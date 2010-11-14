empirical_distribution <- function( n, values ) {
	distValues <- length(levels(factor(values)))
	h <- hist(values,n=round((max(values)-min(values))/1e5), plot=FALSE)
	empdist <- data.frame(speed=h$mids,counts=h$counts,cumfreq=cumsum(h$counts)/sum(h$counts))
	values=numeric(n) 
	for (j in 1:n) {
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

generate_machines_speed <- function(nOfMachinesByPeer, nPeers, outputDir) {
	#m <- read.table("/local/edigley/traces/oursim/machines_lsd_speed.txt",header=T)
	m <- read.table("~/Desktop/machines_lsd_speed.txt",header=T)
	nOfMachines <- nPeers * nOfMachinesByPeer
	pnames <- mapply(paste, "p_", 1:nPeers, sep="")
	mnames <- mapply(paste, "m_", 1:nOfMachines, sep="")
	sourcePeers <- (0:(nOfMachines-1) %% nPeers) + 1
	speeds <- empirical_distribution(nOfMachines,m$speed)
	speeds <- speeds/1e6
	machines_speeds <- data.frame(name=mnames, speed=speeds, peer=sourcePeers)
	outputFileName <- paste(outputDir,"machines_speeds_",nPeers,"_sites_",nOfMachinesByPeer,"_machines_by_site.txt", sep="");
	write.table(machines_speeds, outputFileName, row.names=F, quote=F)

	#m_speeds <- read.table( outputFileName, header=TRUE, stringsAsFactors=FALSE )
	#hist(m_speeds$speed)
}