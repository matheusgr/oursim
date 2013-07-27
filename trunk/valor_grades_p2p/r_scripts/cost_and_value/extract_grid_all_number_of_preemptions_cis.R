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

cis <- data.frame( 
						 gmakespanmean     = double() , 
						 gmakespanlower    = numeric(), 
						 gmakespanupper    = numeric(), 
						 npreemptionsmean  = double() , 
						 npreemptionslower = numeric(), 
						 npreemptionsupper = numeric(), 
						 npbyjobNmean      = double() , 
						 npbyjobNlower     = numeric(), 
						 npbyjobNupper     = numeric(), 
						 nslmediomean      = double() , 
						 nslmediolower     = numeric(), 
						 nslmedioupper     = numeric(), 
						 rtsmediomean      = double() , 
						 rtsmediolower     = numeric(), 
						 rtsmedioupper     = numeric(), 
						 rrmediamean       = double() , 
						 rrmedialower      = numeric(), 
						 rrmediaupper      = numeric(), 
						 nJobsmean         = double() , 
						 nJobslower        = numeric(), 
						 nJobsupper        = numeric(), 
						 nTasksmean        = double() , 
						 nTaskslower       = numeric(), 
						 nTasksupper       = numeric(), 
						 nPeers            = numeric(), 
						 nMachines         = numeric()
					)
for (p in numOfPeers) {
	for (m in numOfMachinesByPeer) {

		gmakespan <- subset(bps, nPeers == p & nMachines == m , select = c(gmakespan))$gmakespan
		gmakespanIC <- calculateIC(gmakespan)

		npreemptions <- subset(bps, nPeers == p & nMachines == m , select = c(npreemptions))$npreemptions
		npreemptionsIC <- calculateIC(npreemptions)

		tmp <- subset(bps, nPeers == p & nMachines == m , select = c(npreemptions, nTasks))
		npbyjobN <- tmp$npreemptions/tmp$nTasks
		npbyjobNIC <- calculateIC(npbyjobN)

		nslmedio <- subset(bps, nPeers == p & nMachines == m , select = c(nslmedio))$nslmedio
		nslmedioIC <- calculateIC(nslmedio)

		rtsmedio <- subset(bps, nPeers == p & nMachines == m , select = c(rtsmedio))$rtsmedio
		rtsmedioIC <- calculateIC(rtsmedio)

		rrmedia <- subset(bps, nPeers == p & nMachines == m , select = c(rrmedia))$rrmedia
		rrmediaIC <- calculateIC(rrmedia)

		nJobs <- subset(bps, nPeers == p & nMachines == m , select = c(nJobs))$nJobs
		nJobsIC <- calculateIC(nJobs)

		nTasks <- subset(bps, nPeers == p & nMachines == m , select = c(nTasks))$nTasks
		nTasksIC <- calculateIC(nTasks)

		cis <- rbind( cis, data.frame( 
												 gmakespanmean     = gmakespanIC[1], 
												 gmakespanlower    = gmakespanIC[2], 
												 gmakespanupper    = gmakespanIC[3], 
												 npreemptionsmean  = npreemptionsIC[1], 
												 npreemptionslower = npreemptionsIC[2], 
												 npreemptionsupper = npreemptionsIC[3], 
												 npbyjobNmean      = npbyjobNIC[1], 
												 npbyjobNlower     = npbyjobNIC[2], 
												 npbyjobNupper     = npbyjobNIC[3], 
												 nslmediomean      = nslmedioIC[1], 
												 nslmediolower     = nslmedioIC[2], 
												 nslmedioupper     = nslmedioIC[3], 
												 rtsmediomean      = rtsmedioIC[1], 
												 rtsmediolower     = rtsmedioIC[2], 
												 rtsmedioupper     = rtsmedioIC[3], 
												 nJobsmean         = nJobsIC[1], 
												 nJobslower        = nJobsIC[2], 
												 nJobsupper        = nJobsIC[3], 
												 nTasksmean        = nTasksIC[1], 
												 nTaskslower       = nTasksIC[2], 
												 nTasksupper       = nTasksIC[3], 
												 nPeers            = p, 
												 nMachines         = m
												)
						)
	}
}

write.table(format(cis,format="d",digits=22), outputFile, row.names=F, quote=F)
