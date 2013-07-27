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
	outputFile <- cmd_args[1]

dsDir <- outputFile 

files <- getDatasetsNames()

#calcula a métrica d para os resultados
#peer metric njobs ntasks nPeers nMachines instance type limit rodada
for (i in 1:nrow(files)) {
	
	gfile   <- as.character(files[i,]$gname)
	cfile   <- as.character(files[i,]$cname)

	if (file.exists(gfile) & file.exists(cfile)) {

		dsName   <- generateRodadaID(files[i,]$nPeers, files[i,]$nMachines, files[i,]$type, spotLimit, files[i,]$rodada)
		fileName <- paste(dsDir, dsName, ".txt", sep="")

	if ( file.exists(fileName) && apenasNovasRodadas ) {
		#print(paste("Jah existe:", files[i,]$nPeers, files[i,]$type, files[i,]$rodada, sep=" "))
	} else {
		#print(dsName)

		ds <- try(
				extract_d_metric(
							gfile, 
							cfile, 
							files[i,]$nPeers, 
							files[i,]$nMachines, 
							as.character(files[i,]$instance), 
							files[i,]$type, 
							spotLimit, 
							files[i,]$rodada
						)
			)
		if (class(ds) == "try-error") {
			print(past("try-error", dsName))
		} else {
			write.table(
				format(ds, format="d", digits=22), 
				fileName, 
				col.names=F,
				row.names=F, 
				quote=F
				)
		}

	}

	}
}
