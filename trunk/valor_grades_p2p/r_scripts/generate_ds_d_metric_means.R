# Calcula a métrica D para todos os cenários considerando o intervalo de confiança para diferentes rodadas
# vide:
# dependencias:
# imports:
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/setup_name.R",sep=""))
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/comparison_metrics.R",sep=""))
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/mestrado_utils.R",sep=""))
cmd_args = commandArgs(TRUE);
# input:
	inputFile  <- cmd_args[1]
# output:
	outputFile <- cmd_args[2]

bps <- read.table( inputFile, header=T)

#bps$metric <- as.numeric(levels(bps$metric))[bps$metric]

cis <- data.frame( 
			metric     = numeric(),
			nPeers     = numeric(), 
			nMachines  = numeric(), 
			instance   = character(),
			type       = character(),
			typeNumber = numeric(),
			limit      = numeric(),
			rodada     = numeric()
		)
for (i in instances) {
	for (p in numOfPeers) {
		for (m in numOfMachinesByPeer) {
			for (r in rodadas) {
				metrics  <- subset(bps, nPeers == p & nMachines == m & instance==i & rodada==r, select = c(metric))$metric
				if ( length(metrics) != 0 ) {
					d_metric <- mean(metrics)
					type <- toShortInstanceName(i)
					typeNumber <- instancesTypesToTypeNumber[[type]]
					cis <- rbind( 
							cis, 
							data.frame( 
									 metric     = d_metric,
									 nPeers     = p,
									 nMachines  = m,
									 instance   = i,
									 type       = type,
									 typeNumber = typeNumber,
									 limit      = spotLimit,
									 rodada     = r
								)
						)
				}
			}
		}
	}
}

write.table(
		format(cis,format="d",digits=22), 
		outputFile, 
		row.names=F, 
		quote=F
	)
