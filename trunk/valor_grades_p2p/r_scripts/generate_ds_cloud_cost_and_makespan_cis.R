#calcula intervalos de confianca do custo e do makespan total das simulações spot
#dependencias:
	library(lattice)
#imports:
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/setup_name.R",sep=""))
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/comparison_metrics.R",sep=""))
cmd_args = commandArgs(TRUE);
#input:
	spotSummaryFile <- cmd_args[1]
#output:
	outputFile      <- cmd_args[2]

bps <- read.table(spotSummaryFile, header=T )

bps$metricM <- bps$sumOfJobsMakespan/bps$finished
bps$metricC <- bps$costByTask

cis <- data.frame( 
			mean=double(), 
			lower=numeric(), 
			upper=numeric(), 
			nPeers=numeric(), 
			nMachines=numeric(), 
			instance=character()
		)

for (i in instancesTypes) {
	for (p in numOfPeers) {
		m <- refNumOfMachinesByPeer
		metricsM <- subset(bps, nPeers == p & nMachines == m & instance==i, select = c(metricM))$metricM
		tM<-t.test(metricsM)

		metricsC <- subset(bps, nPeers == p & nMachines == m & instance==i, select = c(metricC))$metricC
		tC<-t.test(metricsC)

		cis <- rbind( 
				cis, 
				data.frame( 
						mmean     = tM$estimate[[1]],
						mlower    = tM$conf.int[1],
						mupper    = tM$conf.int[2],
						cmean     = tC$estimate[[1]],
						clower    = tC$conf.int[1],  
						cupper    = tC$conf.int[2],  
						nPeers    = p,
						nMachines = m,
						instance  = i
					)
			)
	}
}

write.table(
		format(cis,format="d",digits=22), 
		outputFile, 
		row.names=F, 
		quote=F
	)
