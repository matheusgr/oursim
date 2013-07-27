# Filtra os resultados que estiverem em steady state
# vide:
#dependencias:
#imports:
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/setup_name.R",sep=""))
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/mestrado_utils.R",sep=""))
#input:
	# diretório com todos os resultados, cloud e grid
#output:
	#TODO verificar essas definicoes
	goutputDir <- "/local/edigley/traces/oursim/g_filtered_ds/"
	coutputDir <- "/local/edigley/traces/oursim/c_filtered_ds/"

filter <- function(files, outputDir) {
	for (i in 1:nrow(files)) {

		file <- as.character(files[i,]$name)
		ds <- read.table(file, header=TRUE, sep=":", stringsAsFactors=FALSE)

		dss <- subset(ds , submissionTime > WARM_UP & submissionTime < (TS - DRAIN) )

		outputFile <- paste( outputDir, getSimpleName(file), sep="")

		write.table(format(dss,format="d",digits=22), outputFile, row.names=F, quote=F, sep=":")

	}
}

gfiles <- getGridDatasetsNames()
filter(gfiles, goutputDir)

cfiles <- getCloudDatasetsNames()
filter(cfiles, coutputDir)

bla_old <- function(){

	gfiles <- getGridDatasetsNames()

	for (i in 1:nrow(gfiles)) {

		gfile <- as.character(gfiles[i,]$gname)
		gridDS <- read.table(gfile, header=TRUE, sep=":", stringsAsFactors=FALSE)

		gs <- subset(gridDS , submissionTime > WARM_UP & submissionTime < (TS - DRAIN) )

		goutputFile <- paste( goutputDir, getSimpleName(gfile), sep="")

		write.table(format(gs,format="d",digits=22), goutputFile, row.names=F, quote=F, sep=":")

	}

	cfiles <- getCloudDatasetsNames()

	for (i in 1:nrow(cfiles)) {

		cfile <- as.character(cfiles[i,]$cname)
		cloudDS <- read.table(cfile, header=TRUE, sep=":", stringsAsFactors=FALSE)

		cs <- subset(cloudDS, submissionTime > ONE_DAY & submissionTime < (TS - ONE_DAY) )

		coutputFile <- paste( coutputDir, getSimpleName(cfile), sep="")

		write.table(format(cs,format="d",digits=22), coutputFile, row.names=F, quote=F, sep=":")

	}

}
