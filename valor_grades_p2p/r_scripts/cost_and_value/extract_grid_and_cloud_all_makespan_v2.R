# extrai o somatorio do makespan de todos os jobs na cloud
# vide:
#dependencias:
#imports:
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/setup_name.R",sep=""))
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/mestrado_utils.R",sep=""))
cmd_args = commandArgs(TRUE);
#input:
	# diretório com todos os resultados, cloud e grid
	allGridResultsFile <- cmd_args[1]
	allSpotResultsFile <- cmd_args[2]
#output:
	outputFile <- cmd_args[3]

grid <- read.table(allGridResultsFile, header=T )
grid <- subset(grid, nMachines==refNumOfMachinesByPeer)

spot <- read.table(allSpotResultsFile, header=T )
#spot <- subset(spot, nMachines==refNumOfMachinesByPeer & instance==referenceSpotMachine )
spot <- subset(spot, nMachines==refNumOfMachinesByPeer )

bps <- data.frame( nPeers	  = numeric(), 
						 nMachines = numeric(), 
						 instance  = character(),
						 gmakespan = numeric(),
						 cmakespan = numeric(),
						 ccost	  = numeric(),						 
						 rodada	  = numeric()
						)

#calcula a soma dos makespan para todos os resultados da grade e da cloud
#for (i in 1:nrow(files)) {

for (r in rodadas) {
	for (i in instances) {
		for (p in numOfPeers) {
			for (m in numOfMachinesByPeer) {

				instShortName <- toShortInstanceName(i)

				gridDS  <- subset(grid, nPeers == p & nMachines == m & rodada == r )
				cloudDS <- subset(spot, nPeers == p & nMachines == m & rodada == r & instance == instShortName)

				if (nrow(gridDS)==1 && nrow(cloudDS)) {
					bps <- rbind( bps, data.frame(   
																nPeers     = p, 
																nMachines  = m, 
																instance   = i,
																gmakespan  = gridDS$sumOfJobsMakespan,
																cmakespan  = cloudDS$sumOfJobsMakespan,
																ccost	     = cloudDS$totalCost,
																rodada     = r
															)
									)
				} else {
					#print(paste("Erro:", p, r, i))
				}
			}
		}
	}
}


write.table(format(bps,format="d",digits=22), outputFile, row.names=F, quote=F)
