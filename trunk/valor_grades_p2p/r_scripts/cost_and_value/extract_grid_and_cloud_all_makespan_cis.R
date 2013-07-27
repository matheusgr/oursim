# calcula os intervalos de confianca do somatorio do makespan de todos os jobs na cloud
# vide:
#	extract_grid_and_cloud_all_makespan.R
#dependencias:
#imports:
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/setup_name.R",sep=""))
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/mestrado_utils.R",sep=""))
cmd_args = commandArgs(TRUE)
#input:
	inputFile <- 	cmd_args[1]
#output:
	outputFile <-  cmd_args[2]

bps <- read.table(inputFile, header=T)

cis <- data.frame( gmean     = double(), 
						 glower    = numeric(), 
						 gupper    = numeric(), 
						 cmean     = double(), 
						 clower    = numeric(), 
						 cupper    = numeric(), 
						 ccost     = numeric(), 
 						 nPeers    = numeric(), 
						 nMachines = numeric(),
						 instance  = character()
					)
for (p in numOfPeers) {
	for (m in numOfMachinesByPeer) {
		for (i in instances) {
			metricsG     <- subset(bps, nPeers == p & nMachines == m & instance == i, select = c(gmakespan))$gmakespan
			gm <- calculateIC(metricsG)

			metricsC     <- subset(bps, nPeers == p & nMachines == m & instance == i, select = c(cmakespan))$cmakespan
			cm <- calculateIC(metricsC)

			metricsCCost <- subset(bps, nPeers == p & nMachines == m & instance == i, select = c(ccost)    )$ccost
			cc <- calculateIC(metricsCCost)

			cis <- rbind( cis, data.frame( 
													gmean     = gm[1],
													glower    = gm[2],
													gupper    = gm[3],
													cmean     = cm[1],
													clower    = cm[2],
													cupper	 = cm[3],
													ccost		 = cc[1],
													nPeers    = p,
													nMachines = m,
													instance  = i
													)
							)
		}
	}
}

write.table(format(cis,format="d",digits=22), outputFile, row.names=F, quote=F)
