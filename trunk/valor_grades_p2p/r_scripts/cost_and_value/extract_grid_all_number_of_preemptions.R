# extrai o somatorio do makespan de todos os jobs na cloud
# vide:
#dependencias:
#imports:
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/setup_name.R",sep=""))
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/mestrado_utils.R",sep=""))
cmd_args = commandArgs(TRUE);
#input:
	# diretório com todos os resultados, cloud e grid
#output:
	outputFile <- cmd_args[1]

#files <- getGridDatasetsNames()
files <- getDatasetsNames()
files <- subset(files, type=="c1.xlarge")

bps <- data.frame( nPeers	      = numeric(), 
						 nMachines     = numeric(), 
						 gmakespan     = numeric(),
						 npreemptions  = numeric(),
						 npreemptionsN = numeric(),
						 nslmedio      = numeric(),
						 rtsmedio      = numeric(),
						 rrmedia       = numeric(),
						 nJobs         = numeric(),
						 nTasks        = numeric(),
						 rodada	      = numeric()
						)

#calcula a soma dos makespan para todos os resultados da grade e da cloud
for (i in 1:nrow(files)) {

	gfile <- as.character(files[i,]$gname)
	gridDS <- read.table(gfile, header=TRUE, sep=":", stringsAsFactors=FALSE)

	bps <- rbind( bps, data.frame(   nPeers        = files[i,]$nPeers, 
												nMachines     = files[i,]$nMachines, 
												gmakespan     = sum(gridDS$makeSpan),
												npreemptions  = sum(gridDS$numberOfPreemption),
												npreemptionsN = sum(gridDS$numberOfPreemption/gridDS$size),
												nslmedio      = mean(gridDS$nsl),
												rtsmedio      = mean(gridDS$remoteTasksSize),
												rrmedia       = mean(gridDS$remoteRate),
												nJobs         = length(gridDS$jobId),
												nTasks        = sum(gridDS$size),
												rodada        = files[i,]$rodada
											)
					)

}

write.table(format(bps,format="d",digits=22), outputFile, row.names=F, quote=F)
