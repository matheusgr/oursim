#calcula intervalos de confianca do custo e do makespan total das simulações spot
#dependencias:
	library(lattice)
#imports:
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/setup_name.R",sep=""))
cmd_args = commandArgs(TRUE)
#input:
	gridSummaryFile <- cmd_args[1]
#output:
	outputFile      <- cmd_args[2]

bps <- read.table(gridSummaryFile, header=T )

bps$metricM <- bps$sumOfJobsMakespan
bps$metricU <- bps$utilization

cis <- data.frame( mmean=double(), 
						 mlower=numeric(), 
						 mupper=numeric(),
						 umean=double(),  
						 ulower=numeric(),
						 uupper=numeric(),
 						 nPeers=numeric(), 
						 nMachines=numeric()
					)

for (p in numOfPeers) {
	for (m in numOfMachinesByPeer) {

		metricsM <- subset(bps, nPeers == p & nMachines == m, select = c(metricM))$metricM
		tM<-t.test(metricsM)

		metricsU <- subset(bps, nPeers == p & nMachines == m, select = c(metricU))$metricU
		tU<-t.test(metricsU)

		cis <- rbind( cis, data.frame( mmean     = tM$estimate[[1]],
												 mlower    = tM$conf.int[1],
												 mupper    = tM$conf.int[2],
												 umean     = tU$estimate[[1]],
												 ulower    = tU$conf.int[1],  
												 uupper    = tU$conf.int[2],  
												 nPeers    = p,
												 nMachines = m
												)
						)
	}
}

u <- data.frame()

for (i in instances){
	u <- rbind(u, data.frame(instance=i, cis))
}

cis <- u

cis <- cis[ order(cis$instance, cis$nPeers),]

write.table(format(cis,format="d",digits=22), outputFile, row.names=F, quote=F)
