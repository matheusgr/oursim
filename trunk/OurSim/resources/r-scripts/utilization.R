#u <- read.table(paste(dir,"utilization.txt",sep=""),header=TRUE)

unames <- mapply(setUpName,values,nRecursos,dir, "oursim-trace-utilization-" ,"_sites.txt")
u <- lapply(FUN=read.table,unames, header=TRUE, sep=":", stringsAsFactors=FALSE)

ymax = -1
xmax = -1
for (i in 1:length(u)) {
	ymax = max(ymax, max(u[[i]]$numberOfEnqueuedTasks))
	xmax = max(xmax, max(u[[i]]$time))
}
for (i in 1:length(u)) {
	plot(u[[i]]$time,u[[i]]$numberOfEnqueuedTasks,type="l", xlim=c(0,xmax), ylim=c(0,ymax), col=i,xlab="",ylab="",main="")
	par(new=TRUE)
}
#plot(u$nMachines,u$realUtilization/100,type="h", ylim=c(0,1),col="red",xlim=c(1,33),xlab="",ylab="",main="")
#par(new=TRUE)
#plot(u$nMachines,u$utilization/100,type="h", ylim=c(0,1),col="green",xlim=c(1,33),xlab="",ylab="",main="")

#mapply(create_spot_workload,rnames)