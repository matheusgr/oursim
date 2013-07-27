# Gera os arquivos com as descrições das máquinas dos peers
# vide:
# dependencias:
	library(msm)
# imports:
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"), "/r_scripts/setup_name.R", sep="") )
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"), "/r_scripts/generators/grid_capacity/grid_machines_distribution.R", sep="") )
# input:
# output:

generate_peers_machines_description <- function( mDescriptionFile, siteDescriptionDir, useEcdf ){

	if (useEcdf) {
		m <- read.table(mDescriptionFile, header=T)
		values <- m$speed
	} else {
		values <- rtnorm(10000, mean=2.5, sd=1/3, lower=1, upper=3) * 1e6
	}

	for (rodada in rodadas) {
		for (numMachines in numOfMachinesByPeer) {
			for (nPeers in numOfPeers){
				generate_machines_speed( values, numMachines, nPeers, siteDescriptionDir, rodada )
			}
		}
	}

}
