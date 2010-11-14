library(msm)

create_workload <- function(x, P){
	t <- 0:48
	t <- ifelse(t < 10, t + 48, t)
	wt <- pweibull(t+1/2, shape=1.79, scale=24.16) - pweibull(t-1/2, shape=1.79, scale=24.16)
	wm <- mean(wt)

	#define peer de cada usuario
		N <- 368
		#P <- 20
		user2peer <- floor(runif(N,min=1, max=P))

	times=numeric(1000) 
	users=numeric(1000) 
	jobs_id=numeric(1000) 
	jobs_size=numeric(1000) 
	runtimes=numeric(1000) 
	tasks=character(1000) 
	time <- 0
	vlen <- 0
	while (time < x) {
		
		#define o usuario
			# N <- 368
			# N <- 70
			alpha <- 1.31
			i0 <- (1:N)
			p <- 1/(i0^alpha) 
			p <- p/sum(p)
			user <- sample (i0, 1, prob=p) 
			if (vlen == length(users))
				length(users) <- 2*length(users)
			vlen <- vlen+1
			users[vlen] <- user

		#define o tempo de submissão
			slot <- (time %% 86400) %/% 1800
			iat <- rweibull(1, shape=4.25, scale=7.86)
			iat <- round(2^iat)
			iat_s <- iat/(wt[slot+1]/wm)
			time <- time + iat_s
			if (vlen == length(times))
				length(times) <- 2*length(times)
			#vlen <- vlen+1
			times[vlen] <- round(time)

		#job size
			job_size <- rweibull(1, shape=1.76, scale=2.11)
			job_size <- round(2^job_size)
			if (vlen == length(jobs_size))
				length(jobs_size) <- 2*length(jobs_size)
				jobs_size[vlen] <- job_size

		# o id do job
		if (vlen == length(jobs_id))
				length(jobs_id) <- 2*length(jobs_id)
				jobs_id[vlen] <- vlen

		#task runtime
			#runtime_avg <- rtnorm( 1, mean=2.73, sd=6.1, lower=0, upper=11.8 )
			#runtime_avg <- rtnorm( 1, mean=2.73, sd=6.1, lower=0, upper=10.8 )
			#runtime_var <- rweibull(1, shape=2.05, scale=12.25)
			#tasks_runtime <- rtnorm( job_size, mean=runtime_avg, sd=runtime_var, lower=0, upper=10.8 )

			tasks_runtime <- round(rtnorm( job_size, mean=15*60, sd=7*60, lower=1*60, upper=29*60 ))

			#tasks_runtime=numeric(job_size) 
			#for ( i in c(1:job_size) ) {
			#	logRunTime <- rtnorm( 1, mean=runtime_avg, sd=runtime_var, lower=0)
			#	unLogRunTime <- round(2^logRunTime)
			#	tasks_runtime[i] <- min(3600, unLogRunTime)
			#}

			#tasks_runtime <- min(10, round(2^tasks_runtime))
			#tasks_runtime <- round(2^tasks_runtime)
		
			if (vlen == length(tasks))
				length(tasks) <- 2*length(tasks)
			#runtimes[vlen] <- round(2^runtime_avg)
			runtimes[vlen] <- round(mean(tasks_runtime))
			tasks[vlen] <- paste(tasks_runtime, collapse = ';')
		
		#print(runtime_avg)
		
	}
	v_temp <- 1:vlen
	workload = data.frame(time=times[v_temp], jobId=jobs_id[v_temp], jobSize=jobs_size[v_temp], runtime=runtimes[v_temp], tasks=tasks[v_temp], user=users[v_temp], peer=user2peer[users[v_temp]])
}

generate_workloads <- function(nDias, nPeers, outputDir) {
	P <- nPeers
	ONE_HOUR <-60*60; 
	ONE_DAY <- 24*ONE_HOUR; 
	TS <- nDias * ONE_DAY; 
	workload <- create_workload(TS, P);
	outputFileName <- paste(outputDir,"iosup_workload_",TS/(ONE_DAY),"_dias_",P,"_sites.txt", sep="");
	write.table(workload, outputFileName, row.names=F, quote=F)
}