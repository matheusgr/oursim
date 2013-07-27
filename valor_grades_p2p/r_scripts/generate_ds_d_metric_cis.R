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
		mean	   = double(), 
		lower      = numeric(), 
		upper      = numeric(), 
		nPeers     = numeric(), 
		nMachines  = numeric(), 
		instance   = character(),
		type       = character(),
		typeNumber = numeric(),
		family     = character(),
		limit      = numeric()
	)

for (i in instances) {
	for (p in numOfPeers) {
		for (m in numOfMachinesByPeer) {

			metrics <- subset(bps, nPeers == p & nMachines == m & instance==i, select = c(metric))$metric

			ic <- calculateIC(metrics)
			mean  <- ic[1]
			lower <- ic[2]
			upper <- ic[3]

			type <- toShortInstanceName(i)
			typeNumber <- toInstanceIndex(type)
			familyName <- toFamilyName(i)

			cis <- rbind( 
					cis, 
					data.frame( 
							mean       = mean,
							lower      = lower,
							upper      = upper,
							nPeers     = p,
							nMachines  = m,
							instance   = i,
							type       = type,
							typeNumber = typeNumber,
							family     = familyName,
							limit      = spotLimit
						)
				)
		}
	}
}

write.table( 
		format(cis, format="d", digits=22), 
		outputFile, 
		row.names=F, 
		quote=F
	   )
