# Calcula a métrica D para todos os cenários considerando o intervalo de confiança para diferentes rodadas
# vide:
# dependencias:
# imports:
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/setup_name.R",sep=""))
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/comparison_metrics.R",sep=""))
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/mestrado_utils.R",sep=""))
cmd_args = commandArgs(TRUE);
# input:
	#diretório com todos os resultados, cloud e grid
# output:
	oursimOutputFile    <- cmd_args[1]
	spotsimOutputFile   <- cmd_args[2]
	essentialOutputFile <- cmd_args[3]
	torunOutputFile     <- cmd_args[4]

rerunoursim  <- data.frame( 
				nPeers   = numeric(),
				rodada   = numeric(),
				oursim   = character(),
				spotsim  = character(),
				instance = character()
			)
rerunspotsim <- data.frame( 
				nPeers   = numeric(),
				rodada   = numeric(),
				oursim   = character(),
				spotsim  = character(),
				instance = character()
			)
essential <- data.frame( 
				nPeers   = numeric(),
				rodada   = numeric(),
				oursim   = character(),
				spotsim  = character(),
				instance = character()
			)
for (rodada in rodadas) {
	for (instance in instances) {
		for (nPeers in numOfPeers) {
			for (nMachines in numOfMachinesByPeer) {
				gfile     <- setUpGName(nPeers, nMachines, oursimDir, scheduler, rodada)
				cfile     <- setUpCName(nPeers, nMachines, spotsimDir, scheduler, rodada, instance,spotLimit,groupedbypeer)
				if ( file.exists(gfile) && file.exists(cfile) ) {

				} else {
							
					splitName <- unlist(strsplit(instance, split=".", fixed=T))
					type      <- paste(splitName[3], splitName[4], sep=".");

					if ( !file.exists(gfile) ) {
						rerunoursim  <- rbind(
									rerunoursim, 
									data.frame( 
											nPeers   = nPeers,
											rodada   = rodada,
											oursim   = "oursim",
											spotsim  = "nospotsim",														
											instance = type
										)
									)
					}
					if ( !file.exists(cfile) ) {
						rerunspotsim <- rbind(
									rerunspotsim, 
									data.frame( 
											nPeers   = nPeers,
											rodada   = rodada,
											oursim   = "nooursim",
											spotsim  = "spotsim",														
											instance = type
										)
									)
					}

					if ( file.exists(gfile) && !file.exists(cfile) ) {
						essential <- rbind(
									essential, 
									data.frame( 
											nPeers   = nPeers,
											rodada   = rodada,
											oursim   = "nooursim",
											spotsim  = "spotsim",														
											instance = type
										)
								)
					}

				}
			}
		}
	}
}

write.table(	format(rerunoursim,format="d",digits=22), 
					oursimOutputFile, 
					col.names=F,
					row.names=F, 
					quote=F
				)
write.table(	format(rerunspotsim,format="d",digits=22), 
					spotsimOutputFile, 
					col.names=F,
					row.names=F, 
					quote=F
				)
write.table(	format(essential,format="d",digits=22), 
					essentialOutputFile, 
					col.names=F,
					row.names=F, 
					quote=F
				)

torun <- data.frame ( desc = character() )

for (r in rodadas) {
		for (p in numOfPeers) {
			ss <- subset(essential, nPeers == p & rodada == r )
			if (nrow(ss) > 0) {
				pref <- "cd /local/edigley/workspaces/simulacao/OurSim/; sh build_oursim.sh \""
				suf  <- "\""
				description <- paste( pref, p, r, "spotsim", paste(ss$instance, collapse=" "), suf )
				torun <- rbind( torun, data.frame(desc=description) )
			}
		}
}

write.table(	
		format( torun, format="d", digits=22 ), 
		torunOutputFile, 
		col.names=F,
		row.names=F, 
		quote=F
	)

createV <- function(vec){
	paste("[",paste(vec, collapse=","),"]",sep="")
}

prepareRerunnings <- function(inputFile, toRun, outputFile){
	df <- read.table(inputFile)
	names(df) <- c("nPeers","rodada","runOursim","runSpotsim", "instance")
	if (toRun=="oursim"){
		print("oursim")
		rr <- aggregate(df$rodada, by=list(df$nPeers), FUN=unique )
	} else if (toRun=="spotsim"){
		print("spotsim")
		rr <- aggregate(df$rodada, by=list(df$nPeers, "instance"), FUN=unique )
	}
	names(rr) <- c("nPeers","rodadas")

	rr$rodadas <- createV(rr$rodadas)
	rr$cmd <- "sh build_oursim.sh"
	rr$toRun <- toRun

	rr <- subset(rr, select=c("cmd","nPeers","rodadas","toRun"))

	write.table(rr, outputFile, col.names=F,row.names=F, quote=F)
}

#prepareRerunnings(oursimOutputFile,  "oursim",   "/tmp/oursim.txt")
#prepareRerunnings(spotsimOutputFile, "spotsim", "/tmp/spotsim.txt")
