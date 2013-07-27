require(msm)
require(foreach)
require(doMC)
registerDoMC()

new_marcus_workload <- function( tmax, rtmax, nmaxofpeers, peerspertrace, usersperpeer, fileName ) {

	modelFile <- paste(
				Sys.getenv("VALOR_GRADES_P2P_HOME"),
				"/r_scripts/generators/workload/clust-results-log2/wl_best_fitdist.txt",
				sep=""
			)

	buffer <- 10

	dists.fun  <- list(normal=rnorm, lognormal=rlnorm, gamma=rgamma, exponential=rexp, weibull=rweibull)
	dists.qfun <- list(normal=qnorm, lognormal=qlnorm, gamma=qgamma, exponential=qexp, weibull=qweibull)

	rt.dists <- c( function(n){ rtnorm( n, mean=0.6978,  sd=0.47284987, lower=0,       upper=2.2640  )},
						function(n){ rtnorm( n, mean=0.26240, sd=0.08009413, lower=0.03786, upper=0.50710 )}
						)
	df <- read.table( modelFile, header=T )

	p.i <- 0
	u.i <- 0

	nextTaskId <- 1

	nextJobId  <- 1

	nofpeers <- 0

	for(trace in unique(as.character(df$Trace))) {
				
		iat.dist <- subset(df, stats.attr=="bot_user_iat" & Trace==trace) 

		iat.clusters <- sample(iat.dist$Cluster, peerspertrace*usersperpeer, prob=iat.dist$Fraction, replace=TRUE)
			 
		iat.cl.order   <- order(table(iat.clusters), decreasing=TRUE)

		rtsum.dist     <- subset(df, stats.attr=="bot_runtime_sum" & Trace==trace)

		rtsum.clusters <- sample(rtsum.dist$Cluster, peerspertrace*usersperpeer, prob=rtsum.dist$Fraction, replace=TRUE) 

		rtsum.cl.order <- order(table(rtsum.clusters), decreasing=TRUE)

		iat.means   <- foreach(iat.clust=1:5, .combine=rbind) %do% {
					iat.params <- subset(iat.dist, Cluster==iat.clusters[iat.clust])
					d <- as.character(iat.params$distribution)
					#print(paste(iat.clust, iat.clusters[iat.clust], d))
					if (d != "exponential"){
							mean(dists.fun[[as.character(iat.params$distribution)]](1000, iat.params$Param1, iat.params$Param2))
					}
					else {
							mean(dists.fun[[as.character(iat.params$distribution)]](1000, iat.params$Param1))
					}
				}

		rtsum.means <- foreach(rtsum.clust=1:5, .combine=rbind) %do% {
					rtsum.params <- subset(rtsum.dist, Cluster==rtsum.clusters[rtsum.clust])
					d <- as.character(rtsum.params$distribution)
					if (d != "exponential"){
							 mean(dists.fun[[as.character(rtsum.params$distribution)]](1000, rtsum.params$Param1, rtsum.params$Param2))
					} else {
							 mean(dists.fun[[as.character(rtsum.params$distribution)]](1000, rtsum.params$Param1))
					}
				}

		iat.cl.order   <- order(iat.means)
		rtsum.cl.order <- order(rtsum.means)

		cl.i <- 0	 

		for(peer in 1:peerspertrace) {

			p.i <- p.i + 1

			if (nofpeers < nmaxofpeers) {

				for(user in 1:usersperpeer) {

					gc() 
					cl.i <- cl.i + 1
					u.i  <- u.i  + 1
					df2  <- subset(df, Trace == trace)
					iat.dist <- subset(df2, stats.attr=="bot_user_iat" & Cluster==iat.clusters[cl.i])
					d <- as.character(iat.dist$distribution)

					#lastSubmissionTime
					t <- 0

					#interArrivalTime
					iat <- vector()
					#submissionTime
					st  <- vector()
					while (t < tmax) {
						if (d != "exponential") {
							iat <- c(iat, 2^dists.fun[[d]](buffer, iat.dist$Param1, iat.dist$Param2))
						} else {
							iat <- c(iat, 2^dists.fun[[d]](buffer, iat.dist$Param1))
						}
						st <- cumsum(iat)
						t  <- st[length(st)]
					}
					njobs      <- length(st)
					rtsum.dist <- subset(df2, stats.attr=="bot_runtime_sum" & Cluster==rtsum.clusters[cl.i])
					d          <- as.character(rtsum.dist$distribution)

					rtsum <- vector()
					jobs.len <- 0
					while (jobs.len < njobs) {

						if (d != "exponential") {
							rtsum <- c(rtsum,2^dists.fun[[d]](buffer, rtsum.dist$Param1, rtsum.dist$Param2))
						} else {
							rtsum <- c(rtsum,2^dists.fun[[d]](buffer, rtsum.dist$Param1))
						}
						rtsum <- rtsum[rtsum <= rtmax]
						jobs.len <- length(rtsum)

					}
					length(rtsum) <- njobs

					ntasks <- vector() 

					all_rt_tasks <- foreach(bot_runtime=rtsum, .combine=c) %do% {
								agg_runtime <- 0
								job_size    <- 0
								rt_tasks    <- vector(mode="numeric")
								task.i      <- 1
								loop        <- TRUE
								while(loop) {
										rt_tasks <- c(rt_tasks, rt.dists[[sample(1:2,1)]](500) )
										rt.sum   <- cumsum(rt_tasks[(job_size+1):length(rt_tasks)]) + agg_runtime
										task.i   <- max(0, min(which(rt.sum >= bot_runtime))-1)
										if (!is.infinite(task.i)) {
											  length(rt_tasks) <- job_size + task.i + 1
											  rt_tasks[length(rt_tasks)] <- ifelse(task.i > 0, bot_runtime-rt.sum[task.i], bot_runtime-agg_runtime)
											  agg_runtime <- bot_runtime
											  loop <- FALSE
										} else {
											  agg_runtime <- rt.sum[length(rt.sum)]
										}

										job_size <- length(rt_tasks)
								}
								ntasks <- c(ntasks, job_size)
								return(rt_tasks)
										}
					  
					uid       <- u.i #paste("U", u.i, sep="")
					pid       <- p.i #paste("P", p.i, sep="")
					iat.cid   <- paste("C", which(iat.cl.order   == iat.clusters[cl.i]),   sep="")
					rtsum.cid <- paste("C", which(rtsum.cl.order == rtsum.clusters[cl.i]), sep="")

					tasksIds   <- nextTaskId:(nextTaskId+length(all_rt_tasks)-1)
					nextTaskId <- tasksIds[length(tasksIds)] + 1

					wl <- subset( 
							data.frame( 
								taskId      = tasksIds,
								time        = rep( round(st*3600),   ntasks), 
								jobId       = rep( nextJobId:(nextJobId+length(ntasks)-1), ntasks),
								jobSize	    = rep( ntasks, ntasks), 
								runtime     = round( all_rt_tasks*3600 ), 
								user        = uid, 
								peer        = pid, 
								TraceID     = trace, 
								Cluster.IAT = iat.cid, 
								Cluster.RT  = rtsum.cid
								), 
							time <= tmax*3600 & runtime > 0
						)

					nextJobId <- nextJobId+length(ntasks)

					isFirst  <- u.i == 1

					write.table( 
						wl, 
						fileName, 
						col.names = isFirst, 
						append    = !isFirst, 
						quote     = FALSE, 
						row.names = FALSE
								  )
				}
			}
			nofpeers <- nofpeers + 1

		}
	}

}
