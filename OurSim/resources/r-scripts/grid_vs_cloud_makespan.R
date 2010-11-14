#Comparação otimizada makespan remoto e makespan spot
compare <- function(r, spot_tasks) {
	r <- subset(r, !is.na(remoteTasks))
	co <- ifelse(spot_tasks$makeSpan >= r$remoteMakeSpan, "red", "green")
	co2 <- ifelse(spot_tasks$makeSpan < r$remoteMakeSpan, "red", "cyan")
	ysup <- max(max(r$remoteMakeSpan),max(spot_tasks$makeSpan))
	plot(r$jobId, r$remoteMakeSpan, type="h", xlim=c(0,2000), ylim=c(-ysup,ysup), col=co2)
	par(new=T);plot(spot_tasks$jobId, -spot_tasks$makeSpan, type="h", xlim=c(0,2000), ylim=c(-ysup,ysup),col=co)
}

r <- read.table("/local/edigley/traces/oursim/trace_media_15s_persistent_heterogeneous_resources/oursim-trace-25_7_dias_10_sites.txt",header=TRUE, sep=":")
spot_tasks <- read.table("/local/edigley/traces/oursim/trace_media_15s_persistent_heterogeneous_resources/spot-trace-25_7_dias_10_sites_100_spotLimit.txt",header=TRUE, sep=":")
compare(r, spot_tasks)