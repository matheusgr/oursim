calculateIC <- function(metrics) {
	ic <- numeric()
	if (length(metrics) < 10) {
		m  <- mean(metrics)
		#s  <- sd(metrics)
		s <- ifelse( length(metrics)==1, 0.00001, sd(metrics) )
		ic[1] <- m
		ic[2] <- m - s
		ic[3] <- m + s
	} else {
		t <- t.test(metrics)
		ic[1] <- t$estimate[[1]]
		ic[2] <- t$conf.int[1]
		ic[3] <- t$conf.int[2]
	}
	ic
}

toShortInstanceName <- function(instance) {
	splitName <- unlist(strsplit(instance, split=".", fixed=T))
	type      <- paste(splitName[3], splitName[4], sep=".");
}

toFamilyName <- function(instance) {
	splitName <- unlist(strsplit(instance, split=".", fixed=T))
	#type      <- paste(splitName[3], splitName[4], sep=".");
	splitName[3]
}

getSimpleName <- function(path) {
	splitName <- unlist(strsplit(path, split="/", fixed=T))
	splitName[length(splitName)]
}

generateRodadaID <- function(nPeers, nMachines, instType, limit, rodada) {
	#npeers=[80]_nmacbypeer=[30]_scheduler=persistent_spts=[m1.small]_limit=[100]_gbp=false_rodadas=[30]
	paste("npeers=[",nPeers,"]_nmacbypeer=[",nMachines,"]_scheduler=persistent_spts=[",instType,"]_limit=[",limit,"]_gbp=false_rodadas=[",rodada,"]",sep="")
}

myformat <- function(arg, ...){
	format( arg, format="d",digits=4, nsmall=2, justify = "right", decimal.mark = ",", big.mark = ".")
}

myformat2 <- function(arg, ...){
	valuef <- format( arg * 100, format="d",digits=2, nsmall=2, justify = "right", decimal.mark = ",", big.mark = ".")
	#paste(valuef, "\\%")
}

myformat3 <- function(arg, ...){
	valuef <- format( arg , format="d",digits=4, nsmall=6, justify = "right", decimal.mark = ",", big.mark = ".")
}

getDatasetsNames <- function(){

	files <- data.frame( 
			nPeers    = numeric(), 
			nMachines = numeric(), 
			gname     = character(), 
			cname     = character(), 
			instance  = character(), 
			type      = character(),
			rodada    = numeric()
		)

	#pega os nomes de todos os arquivos com resultados da cloud e da grade
	for (rodada in rodadas) {
		for (instance in instances) {
			for (nPeers in numOfPeers) {
				for (nMachines in numOfMachinesByPeer) {
					gname <- setUpGName(nPeers, nMachines, oursimDir, scheduler, rodada)
					cname <- setUpCName(nPeers, nMachines, spotsimDir, scheduler, rodada, instance, spotLimit, groupedbypeer)
					if ( file.exists(gname) && file.exists(cname) ) {
						splitName <- unlist(strsplit(instance, split=".", fixed=T))
						type      <- paste(splitName[3], splitName[4], sep=".");
						files     <- rbind( 
								files, 
								data.frame( 
									nPeers    = nPeers, 
									nMachines = nMachines, 
									gname     = gname, 
									cname     = cname, 
									instance  = instance, 
									type      = type, 
									rodada    = rodada
								)
							)
					}
				}
			}
		}
	}
	files

}

getGridDatasetsNames <- function(){

	files <- data.frame( nPeers    = numeric(), 
								nMachines = numeric(), 
								rodada    = numeric(),
								name      = character()
							)

	#pega os nomes de todos os arquivos com resultados da grade
	for (rodada in rodadas) {
		for (nPeers in numOfPeers) {
			for (nMachines in numOfMachinesByPeer) {
				gname <- setUpGName(nPeers, nMachines, oursimDirO, scheduler, rodada)
				#print(paste(rodada,nPeers,nMachines,gname,class(files)))
				if ( file.exists(gname) ) {					
					files <- try(
							rbind( files, 
							data.frame( 
								nPeers    = nPeers, 
								nMachines = nMachines, 
								rodada    = rodada,
								name      = gname
							) 
						)
									)
					if (class(files) == "try-error") {
						print(paste("try-error",rodada,nPeers,nMachines,gname))
						#exit
					}
				} else {
						#print(paste(rodada,nPeers,nMachines,gname))
				}
			}
		}
	}

	files

}

getCloudDatasetsNames <- function(nMachines){

	files <- data.frame( 
			nPeers  = numeric(), 
			instance  = character(), 
			type      = character(),
			rodada    = numeric(), 
			name      = character() 
			)

	#pega os nomes de todos os arquivos com resultados da cloud
	for (rodada in rodadas) {
		for (instance in instances) {
			for (nPeers in numOfPeers) {
				if ( file.exists(cname) ) {
					#nMachines <- 50
					cname <- setUpCName(nPeers, nMachines, spotsimDirO, scheduler, rodada, instance,spotLimit,groupedbypeer)
					splitName <- unlist(strsplit(instance, split=".", fixed=T))
					type <- paste(splitName[3], splitName[4], sep=".");
					files <- rbind( files, 
							data.frame( 
								nPeers   = nPeers, 
								instance = instance, 
								type     = type, 
								rodada   = rodada,
								name     = cname
								)
					)
				}
			}
		}
	}

	files

}

extract_d_metric <- function (gfile, cfile, nPeers, nMachines, instance, type, limit, rodada) {

	gridDS  <- read.table(gfile, header=TRUE, sep=":", stringsAsFactors=FALSE)
	cloudDS <- read.table(cfile, header=TRUE, sep=":", stringsAsFactors=FALSE)

	ds <- data.frame( 
			f_metric( gridDS, cloudDS ),
			nPeers    = nPeers, 
			nMachines = nMachines, 
			instance  = instance, 
			type      = type,
			limit     = limit,
			rodada    = rodada 
			)
}
