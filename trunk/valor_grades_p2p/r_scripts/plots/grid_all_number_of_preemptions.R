# 
# vide:
#	
# dependencias:
# imports:
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/setup_name.R",sep=""))
cmd_args = commandArgs(TRUE);
# input:
	inputFile  <- cmd_args[1]
# output:
	outputFile <- cmd_args[2]

cisFilePath <- inputFile
FIGURE <- TRUE
pngFilePath <- outputFile

cis <- read.table(cisFilePath,header=T)	

df  <- cis
df$npreemptionsJ <- df$npreemptionsmean/df$nJobsmean
df$npreemptionsJlower <- df$npreemptionslower/df$nJobsmean
df$npreemptionsJupper <- df$npreemptionsupper/df$nJobsmean
df$npreemptionsT <- df$npreemptionsmean/df$nTasksmean
df$npreemptionsTlower <- df$npreemptionslower/df$nTasksmean
df$npreemptionsTupper <- df$npreemptionsupper/df$nTasksmean
df$npreemptionsP <- df$npreemptionsmean/df$nPeers
df$npreemptionsPlower <- df$npreemptionslower/df$nPeers
df$npreemptionsPupper <- df$npreemptionsupper/df$nPeers
#xYplot( npreemptionsJ ~ nPeers | rodada, data=df, layout=c(1,length(unique(df$rodada))), type="h")
#xYplot( npreemptionsJ ~ nPeers | rodada, data=df, layout=c(1,length(unique(df$rodada))))
np0 <- data.frame( nPeers=df$nPeers, 
						 npreemptions=df$npbyjobNmean, 
						 npreemptionslower=df$npbyjobNlower, 
						 npreemptionsupper=df$npbyjobNupper, 
						 type="por jobsN"
					   )
np1 <- data.frame( nPeers=df$nPeers, 
						 npreemptions=df$npreemptionsJ, 
						 npreemptionslower=df$npreemptionsJlower, 
						 npreemptionsupper=df$npreemptionsJupper, 
						 type="por jobs"
					   )
np2 <- data.frame( nPeers=df$nPeers, 
						 npreemptions=df$npreemptionsT, 
						 npreemptionslower=df$npreemptionsTlower, 
						 npreemptionsupper=df$npreemptionsTupper, 
						 type="por tasks"
					   )
np3 <- data.frame( nPeers=df$nPeers, 
						 npreemptions=df$npreemptionsP, 
						 npreemptionslower=df$npreemptionsPlower, 
						 npreemptionsupper=df$npreemptionsPupper, 
						 type="por peers"
					   )
np  <- rbind(np0, np1, np2, np3)
np  <- rbind(np0, np2)
np  <- rbind(np0)

#plot(np2$nPeers, np2$npreemptions, type="l")
#xYplot( npreemptions ~ nPeers, data=np, groups=type, type="l")
#xYplot( npreemptions ~ nPeers | type, subset=type!="por tasks", data=np, type="l", layout=c(1,2))
#xYplot( npreemptions ~ nPeers , groups=type, subset=type!="por tasks", data=np, type="l")
#r <- read.table("/home/edigley/local/traces/oursim/13_03_2011/oursim-trace-persistent_30_machines_7_dias_10_sites_1.txt", header=T, sep=":")

#xYplot( Cbind(npreemptions, npreemptionslower, npreemptionsupper) ~ nPeers, data=np, groups=type, type="l")
#xYplot( Cbind(npreemptions, npreemptionslower, npreemptionsupper) ~ nPeers | type, data=np, type="l", layour=c(1,4))


#xYplot( Cbind(npreemptionsT, npreemptionsTlower, npreemptionsTupper) ~ nPeers , data=df, type="l")
#xYplot( Cbind(npreemptionsJ, npreemptionsJlower, npreemptionsJupper) ~ nPeers , data=df, type="l")
#xYplot( Cbind(npreemptionsP, npreemptionsPlower, npreemptionsPupper) ~ nPeers , data=df, type="l")

if (FIGURE){
	png(pngFilePath)
}

	print(xYplot( Cbind(npreemptions, npreemptionslower, npreemptionsupper) ~ nPeers, #| "30 Maquinas por Peer", 
					  data=np, 
					  type="l", 
					  col=1,
					  layour=c(1,4), 
					  ylim=c(.1, .2),
					  strip=strip.custom(strip.names=TRUE, strip.levels=TRUE,bg="white",fg="white"),
					  xlab=grid_all_number_of_preemptions_xlab,
					  ylab=grid_all_number_of_preemptions_ylab
				  )
		)

if (FIGURE){
	dev.off()
}
