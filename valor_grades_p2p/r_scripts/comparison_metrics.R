f_grid_metric <- function(gw){
	# gw <- subset(gw, remoteTasks != "NA")
	# aggsum      <- aggregate(gw$remoteMakeSpan, by=list(gw$peerId), FUN=sum)
	aggsum        <- aggregate(gw$makeSpan, by=list(gw$peerId), FUN=sum)
	aggsum$njobs  <- aggregate(gw$jobId,    by=list(gw$peerId), FUN=length)$x
	aggsum$ntasks <- aggregate(gw$size,     by=list(gw$peerId), FUN=sum)$x
	names(aggsum) <- c("peer", "makespan", "njobs", "ntasks")
	aggsum
}

f_cloud_metric <- function(cw){
	aggsum <- aggregate(cw$makeSpan,by=list(cw$peerId),FUN=sum)
	aggsum$njobs  <- aggregate(cw$jobId,    by=list(cw$peerId), FUN=length)$x
	aggsum$ntasks <- aggregate(cw$size,     by=list(cw$peerId), FUN=sum)$x
	names(aggsum) <- c("peer", "makespan", "njobs", "ntasks")
	aggsum
}

f_metric <- function (gw, cw) {
	g_metric <- f_grid_metric(gw)
	c_metric <- f_cloud_metric(cw)
	metric <- data.frame(peer=g_metric$peer, metric=g_metric$makespan/c_metric$makespan, njobs=g_metric$njobs, ntasks=g_metric$ntasks)
	metric
}

f_metric_bp <- function (gw, cw, nPeers) {
	g_metric <- f_grid_metric(gw)
	c_metric <- f_cloud_metric(cw)
	metric <- data.frame(peer=g_metric$peer, metric=g_metric$makespan/c_metric$makespan, nPeers=nPeers)
	metric
}

f_metric_bp2 <- function (gw, cw, nPeers) {
	g_metric <- sum(gw$remoteMakeSpan)
	c_metric <- sum(cw$makeSpan)
	metric <- data.frame(metric=g_metric/c_metric, nPeers=nPeers)
	metric
}

f_metric_2 <- function (gw, cw, type) {
	g_metric <- f_grid_metric(gw)
	c_metric <- f_cloud_metric(cw)
	metric <- NULL
	if (type=="mean") {
		metric <- mean(g_metric$makespan/c_metric$makespan)
	} else if (type=="min") {
		metric <- min(g_metric$makespan/c_metric$makespan)
	} else if (type=="max") {
		metric <- max(g_metric$makespan/c_metric$makespan)
	}
	metric
}

f_metric_mean <- function (gw, cw) {
	f_metric_2(gw,cw,"mean")
}

d_metric_mean <- function (nPeers, nMachinesByPeer, instance, rodada) {
	dir <- "/local/edigley/traces/oursim/xpto13_03_2011/"
	gw <- read.table(paste(dir,"oursim-trace-persistent_",nMachinesByPeer,"_machines_7_dias_",nPeers,"_sites_",rodada,".txt",sep=""),header=T,sep=":")
	cw <- read.table(paste("spot-trace-persistent_",nMachinesByPeer,"_machines_7_dias_",nPeers,"_sites_100_spotLimit_groupedbypeer_false_av_us-east-1.linux.",instance,".csv_",rodada,".txt",sep=""),header=T,sep=":")
	f_metric_2(cw,gw,"mean")
}

f_metric_min <- function (gw, cw) {
	f_metric_2(gw,cw,"min")
}

f_metric_max <- function (gw, cw) {
	f_metric_2(gw,cw,"max")
}

cope_with_original_metric <- function(){
	rnames <- mapply(setUpName2, values, nRecursos, dir, "oursim-trace-" , "_sites.txt")
	snames <- mapply(setUpName2, values, nRecursos, dir, "spot-trace-"   , "_sites_100_spotLimit.txt")
	g <- lapply(FUN=read.table,rnames, header=TRUE, sep=":", stringsAsFactors=FALSE)
	c <- lapply(FUN=read.table,snames, header=TRUE, sep=":", stringsAsFactors=FALSE)

	res <- mapply(f_metric, g, c)

	df <- NULL;
	for (i in 1:ncol(res)) {
		values <- res[,i][[2]]
		df <- cbind(df,values)
	}

	for(i in 1:ncol(res)){
		for(j in 1:length(res[,i][[1]])) {
			peer <- res[,i][[1]][j]
			metric <- res[,i][[2]][j]
			par(new=T)
			plot(peer, metric)
		}
	}
}