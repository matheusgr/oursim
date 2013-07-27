# Funções para gerar uma distribuição empírica das velocidades das máquinas

empirical_distribution <- function( nOfMachines, values ) {
	distValues <- length(levels(factor(values)))
	h <- hist(values,n=round((max(values)-min(values))/1e5), plot=FALSE)
	empdist <- data.frame(speed=h$mids,counts=h$counts,cumfreq=cumsum(h$counts)/sum(h$counts))
	values=numeric(nOfMachines) 
	for (j in 1:nOfMachines) {
		random <- runif(1)
		for (i in 1:length(empdist$speed)){
			if (empdist[i,]$cumfreq >= random) {
				values[j] <- empdist[i,]$speed
				break
			}
		}
	}
	values
}

generate_machines_speed <- function(mSpeeds, nOfMachinesByPeer, nPeers, outputDir, r) {

	nOfMachines <- nPeers * nOfMachinesByPeer
	#pnames <- mapply(paste, "p_", 1:nPeers, sep="")
	#sourcePeers <- (0:(nOfMachines-1) %% nPeers) + 1
	sourcePeers <- rep(1:nPeers, rep(nOfMachinesByPeer, nPeers))
	mnames <- character(nOfMachines)

	i <- 0
	for (peer in 1:nPeers){
		for (machine in 1:nOfMachinesByPeer) {
			#sourcePeer <- (machine %% nPeers) + 1
			i <- i + 1
			mnames[i] <- paste("p_", peer, "-", "m_", machine, sep="")
		}
	}

	speeds <- empirical_distribution(nOfMachines, mSpeeds)
	speeds <- speeds/1e6
	machines_speeds <- data.frame(name=mnames, speed=speeds, peer=sourcePeers)
	outputFileName  <- paste(outputDir,"machines_speeds_",nPeers,"_sites_",nOfMachinesByPeer,"_machines_by_site_",r,".txt", sep="");
	write.table(machines_speeds, outputFileName, row.names=F, quote=F)

}