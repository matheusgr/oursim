# plot grafico de barras com intervalos de confianca do custo e do makespan das simulações spot
# vide:
#	custo_makespan_cis.R
#dependencias:
	suppressMessages(library(Rlab))
	suppressPackageStartupMessages(library(Rlab))
# imports:
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/setup_name.R",sep=""))
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/plots/lplot2.R",sep=""))
cmd_args = commandArgs(TRUE)
#input:
	spotCisFile <- cmd_args[1]
#output:
	outputFile  <- cmd_args[2]

FIGURE <- TRUE
pngFilePath <- outputFile

bubblegraph1 <- function(sp) {

	plot( 
		sp$mmean/3600,
		sp$cmean,
		xlim=c(0,50000/3600), 
		ylim=c(0,.08),
		xlab="",
		ylab="",
		col=1,
		#col=as.numeric(sp$instance)
	  )
	par(new=T)
	lplot2( 
		sp$mmean/3600,
		sp$cmean+.0025,
		labels=sp$instance,
		#size=sp$nnucleos,
		xlim=c(0,50000/3600), 
		ylim=c(0,.08),
		col=1,
		xlab="Makespan Medio por Job (em Horas)",
		ylab="Custo Medio por Tarefa (em USD)",
		#col=as.numeric(sp$instance)
	)
	grid(nx=10,ny=10)

	#plot(sp$mmean, sp$cmean)
}

bubblegraph2 <- function(sp) {

	#Bubble Plot
	#xYplot(cmean ~ mmean | "fuc", data=sp,groups=instance, size=nnucleos,xlim=c(0,50000))
	#Key(.75,.70,other=list(title='Tipo de Instancia', cex=.5, cex.title=.5))
	#sKey(.75,.70,other=list(title='Tipo de Instancia', cex=.5, cex.title=.5))
	xYplot(cmean ~ mmean | "fuc", data=sp,groups=instance, size=nnucleos,xlim=c(0,50000))

}

bubblegraph3 <- function(sp) {

	plot(sp$mmean,sp$cmean,xlim=c(0,50000), ylim=c(0,.08) ,col=1:8)
	par(new=T)
	lplot2(sp$mmean,sp$cmean+.0025,labels=sp$instance,xlim=c(0,50000), ylim=c(0,.08),col=1:8)
	grid(nx=10,ny=10)

	xYplot(cmean ~ mmean, data=sp, xlim=c(0,50000), group=instance)

}

if (FIGURE){
	png(pngFilePath)
}

sp <- read.table(spotCisFile, header=T )
sp <- subset(sp, nPeers==refNumOfPeers & nMachines==refNumOfMachinesByPeer)
sp <- sp[ order(sp$mmean), ]

sp$nnucleos <- nnucleos

bubblegraph1(sp)

if (FIGURE){
	dev.off()
}
