# plot grafico de barras com intervalos de confianca do custo e do makespan das simulações spot
# vide:
#	custo_makespan_cis.R
#dependencias:
# imports:
	source( "/local/edigley/traces/oursim/r_scripts/setup_name.R")
#cmd_args = commandArgs(TRUE)
#input:
	#spotCisFile <- cmd_args[1]
	spotCisFile<-"/local/edigley/traces/oursim/r_scripts/resultados_dissertacao_14-10-2011/final_table.txt_ds.txt"
#output:
	#outputFile  <- cmd_args[2]
	outputFile<-"/tmp/bla"

# [1] "np"       "nmbp"     "instance" "uRatio"   "costmin"  "costmax" 
# [7] "value"    "valueE1"  "valueE2"  "effVg1"   "effVg2"   "vbm0"    
#[13] "vbm1"     "vbm2"

ylimBarplot <- c(0,23000)

intercalate <- function(x,y) {
	r=numeric(length(x)+length(y)) 
	xi=1
	yi=1
	for (i in 1:length(r)) {
		if (i%%2!=0) {
			r[i] = x[xi]
			xi <- xi+1
		} else {
			r[i] = y[yi]
			yi <- yi+1
		}
	}
	r
}

error.bar <- function(x, y, upper, lower, length=0.1, ...){
	if( length(x) != length(y) | length(y) != length(lower) | length(lower) != length(upper) )
		stop("vectors must be same length")
	arrows(x, upper, x, lower, angle=90, code=3, length=length, ...)
}

doPlot <- function(refInstance, outputFile) {

	FIGURE <- TRUE
	pngFilePath <- outputFile

	if (FIGURE){
		png(pngFilePath)
	}

	sp <- read.table(spotCisFile, header=T )
	sp <- subset(sp, instance==refInstance)
	sp <- sp[ order(sp$np), ]
	sp$cost <- (sp$costmin + sp$costmax) / 2

	print(sp)

	metricComparisonFactor = 1

	y <- matrix(c((sp$value/(1)),sp$cost*metricComparisonFactor), nrow=2, ncol=15, byrow=TRUE)

	eu <- matrix(c((sp$valuemin/(1)),sp$costmax*metricComparisonFactor), nrow=2, ncol=15, byrow=TRUE)
	el <- matrix(c((sp$valuemax/(1)),sp$costmin*metricComparisonFactor), nrow=2, ncol=15, byrow=TRUE)

	colnames(y) <- sp$np
	rownames(y) <- c( "Valor da Grade", "Custo da Grade" )
	#bpcol<-c("green","red")
	bpcol<-c("gray90","gray50")

	oldMar <- par("mar")
	op <- par(
			cex.axis = .8, 
			cex.lab=.8,
			las=1,
			mar = c(oldMar[1],oldMar[2],oldMar[3],5) + 0.1
		)

	mp <- barplot( 
			y, 
			horiz=F, 
			beside=T, 
			ylim=ylimBarplot,
			axisnames=T,
			axes=F,
			xlab="Tamanho da Grade em N\u{FA}mero de Peers",
			xaxt = "n",
			las=1,
			legend.text=F,
			col=bpcol
		)

	legend( "top", rownames(y), fill=bpcol )#density=c(0,1000)) 

	#text(	
	#	mp, 
	#	par("usr")[3] - 0.1, 
	#	srt = 0, 
	#	adj = 1, 
	#	labels = intercalate(
	#				rep("",length(colnames(y))),
	#				colnames(y)
	#				), 
	#	xpd = T, 
	#	cex = par("cex.axis")
	#)

	#Adds the tickmark labels
	# adj = 1 will place the text at the end ot the tick marks
	# xpd = TRUE will "clip" text outside the plot region

	#handsideticks right-hand
	hst = seq(0,ylimBarplot[2],5000);

	axis(		
		side = 2, 
		at = hst,
		labels = format(hst,format="d",digits=1), 
		cex.axis=.6,
		)

	mtext(	
		text = "Valor da Grade (em USD)", 
		side = 2, 
		line = 2.5,
		cex = par("cex.axis"),
		las=0
		)

	axis(		
		side = 4, 
		at = hst,
		labels = format(hst,format="d",digits=1),
		cex.axis=.6,
		)

	mtext(	
		text = "Custo da Grade (em USD)", 
		side = 4, 
		line = 2.5,
		cex = par("cex.axis"),
		las=0,
	)

	error.bar( mp, y, eu, el, .03)

	grid( nx=NA, ny=10)

	box()

	par(op)

	if (FIGURE){
		dev.off()
	}

}

doPlot( "us-east-1.linux.c1.medium.csv", "/tmp/grid_value_c1medium.png" )
doPlot( "us-east-1.linux.c1.xlarge.csv", "/tmp/grid_value_c1xlarge.png" )
