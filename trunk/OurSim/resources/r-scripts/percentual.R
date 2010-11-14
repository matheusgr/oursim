#!/usr/bin/Rscript
# Percentual de jobs que apresentam desempenho inferior na nuvem
# 
# R --slave --no-save --no-restore --no-environ --silent --args /local/edigley/traces/oursim/oursim-trace-3.txt < create_spot_workload.R
#
# Author: edigley
###############################################################################

p1menor2 <- function(r, s) {
	#count <- ifelse(s >= (r-(0.05*r)), 1, 0)
	count <- ifelse(s >= r, 1, 0)
	sum(count) / length(count)
}
percentage <- function(grid_tasks, spot_tasks) {
	grid_remote_tasks <- subset(grid_tasks, !is.na(remoteTasks))
	p1menor2(grid_remote_tasks$remoteMakeSpan, spot_tasks$makeSpan)
}

rname <- function(nSites, nRecursos, dir) {
	paste(dir,"oursim-trace-",nRecursos,"_7_dias_",nSites,"_sites.txt",sep="")
}

sname <- function(nSites, nRecursos, dir) {
	paste(dir,"spot-trace-",nRecursos,"_7_dias_",nSites,"_sites_100_spotLimit.txt",sep="")
}

setUpName <- function(nSites, nRecursos, dir, prefix, sufix) {
	paste(dir, prefix, nRecursos, "_7_dias_", nSites, sufix, sep="")
}

plot_percentual <- function(values, dir, new = FALSE, col="green"){

	nRecursos = 25;

	rnames <- mapply(setUpName,values,nRecursos,dir, "oursim-trace-" ,"_sites.txt")
	snames <- mapply(setUpName,values,nRecursos,dir, "spot-trace-"   ,"_sites_100_spotLimit.txt")

	r <- lapply(FUN=read.table,rnames, header=TRUE, sep=":", stringsAsFactors=FALSE)
	s <- lapply(FUN=read.table,snames, header=TRUE, sep=":", stringsAsFactors=FALSE)

	xlab="Quantidade de maquinas em cada peer"
	ylab="Percentual de jobs com makespan menor na grade"
	main="Makespan - Recursos Remotos Vs Spot Instances (max. 100)"

	par(new=new)
	plot(values,mapply(percentage, r, s), ylim=c(0,1),col=col, xlab=xlab,ylab=ylab,main=main)
}