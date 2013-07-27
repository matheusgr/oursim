# 
# vide:
# dependencias:
# imports:
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"), "/r_scripts/setup_name.R", sep="") )
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"), "/r_scripts/generators/workload/generate_new_marcus_workload.R", sep="") )
# argumentos de linha de comando
	args.in <- commandArgs(TRUE)
# input:
# output:

outputDir <- args.in[1]

for (rodada in rodadas) {
	for (nPeers in numOfPeers){
		generate_new_marcus_workload( nPeers, usersPerPeer, numOfDays, rodada, outputDir )
	}
}
