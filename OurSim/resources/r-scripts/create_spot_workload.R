#!/usr/bin/Rscript
# Pós-processamento do trace oursim
# 
# R --slave --no-save --no-restore --no-environ --silent --args /local/edigley/traces/oursim/oursim-trace-3.txt < create_spot_workload.R
#
# Author: edigley
###############################################################################

create_spot_workload <- function(inputFileName) {
	#inputFileName = "/local/edigley/workspace/OurSim/oursim_trace.txt"
	outputFileName = paste(inputFileName,"-remote-tasks.txt",sep="")
	trace <- read.table(inputFileName,header=TRUE, sep=":", stringsAsFactors=FALSE)
	rT <- subset(trace, !is.na(remoteTasks))
	# submissionTime, jobId, numberOfTasks, avgRuntime, tasks, userId
	#remoteWorkload = data.frame(time=rT$submissionTime, jobId=rT$jobId, jobSize=rT$remoteTasksSize, runtime=round(mean(unlist(lapply(strsplit(rT$remoteTasks,";"),as.integer)))), tasks=rT$remoteTasks, user=rT$userId)
	remoteWorkload = data.frame(time=rT$submissionTime, jobId=rT$jobId, jobSize=length(unlist(lapply(strsplit(rT$remoteTasks,";"),as.integer))), runtime=round(mean(unlist(lapply(strsplit(rT$remoteTasks,";"),as.integer)))), tasks=rT$remoteTasks, user=rT$userId)
	remoteWorkloadOrdered <- remoteWorkload[ order(remoteWorkload$time),  ]
	write.table(remoteWorkloadOrdered, outputFileName, row.names=F, quote=F)
	# Para carregar de volta:
	#workload2 <- read.table( outputFileName, header=TRUE, stringsAsFactors=FALSE )
}

cmd_args = commandArgs(TRUE);

fileName = print(cmd_args[1])

create_spot_workload(fileName)
