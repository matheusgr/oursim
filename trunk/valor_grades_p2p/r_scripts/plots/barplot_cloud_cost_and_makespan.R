# plot grafico de barras com intervalos de confianca do custo e do makespan das simulações spot
# vide:
#	custo_makespan_cis.R
#dependencias:
# imports:
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/setup_name.R",sep=""))
cmd_args = commandArgs(TRUE)
#input:
	spotCisFile <- cmd_args[1]
#output:
	outputFile  <- cmd_args[2]

FIGURE <- TRUE
pngFilePath <- outputFile

if (FIGURE){
	png(pngFilePath)
}

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


sp <- read.table(spotCisFile, header=T )
sp <- subset(sp, nPeers==refNumOfPeers & nMachines==refNumOfMachinesByPeer)
sp <- sp[ order(sp$mmean), ]

metricComparisonFactor = 300

#como estava antes de usar o novo workload de marcus
#y <- matrix(c((sp$mmean/(3600)),sp$cmean*30), nrow=2, ncol=8, byrow=TRUE)
y <- matrix(c((sp$mmean/(3600)),sp$cmean*metricComparisonFactor), nrow=2, ncol=8, byrow=TRUE)

#e <- matrix(c((sp$mlower/(3600)),sp$clower*30), nrow=2, ncol=8, byrow=TRUE)
e <- matrix(c(((sp$mmean-sp$mlower)/(3600)),(sp$cmean-sp$clower)*metricComparisonFactor), nrow=2, ncol=8, byrow=TRUE)

eu <- matrix(c((sp$mupper/(3600)),sp$cupper*metricComparisonFactor), nrow=2, ncol=8, byrow=TRUE)
el <- matrix(c((sp$mlower/(3600)),sp$clower*metricComparisonFactor), nrow=2, ncol=8, byrow=TRUE)


colnames(y)<-sp$instance
rownames(y)<-c("makespan","custo")
bpcol<-c("lightblue","lightgray")

oldMar <- par("mar")
op <- par(
		cex.axis = .8, 
		cex.lab=.8,
		las=1,
		mar = c(oldMar[1],oldMar[2],oldMar[3],5) + 0.1
	)

mp<-barplot( 
		y, 
		horiz=F, 
		beside=T, 
		ylim=ylimBarplot,
		axisnames=T,
		axes=F,
		#xlab="EC2 Instance Types",xaxt = "n",
		xlab="Tipos de Instancias spot",xaxt = "n",
		las=2,
		legend.text=F,
		col=bpcol
	)

legend("top", rownames(y), fill=bpcol)#density=c(0,1000)) 

text(	mp, par("usr")[3] - 0.1, srt = 45, adj = 1, 
		labels = intercalate(	rep("",length(colnames(y))),
										colnames(y)
									), 
		xpd = T, cex = par("cex.axis")
	)

#Adds the tickmark labels
# adj = 1 will place the text at the end ot the tick marks
# xpd = TRUE will "clip" text outside the plot region

#handsideticks right-hand
hst = seq(0,ylimBarplot[2],.5);

axis(		side = 2, 
			at = hst,
			labels = format(hst,format="d",digits=1), 
			cex.axis=.6,
	 )

mtext(	#text = "Average Job Makespan (hours)", 
			text = "Media do makespan dos jobs (horas)", 
			side = 2, 
			line = 2.5,
			cex = par("cex.axis"),
			las=0
	   )

axis(		side = 4, 
			at = hst,
			labels = format(hst/30,format="d",digits=1),
			cex.axis=.6,
			#lwd=.2, lwd.ticks=.2,
			#col=bpcol[2], col.ticks=bpcol[2]
	 )

mtext(	#text = "Average Cost by Task (ACT) in USD", 
			text = "Custo medio por tarefa (ACT) em USD", 
			side = 4, 
			line = 2.5,
			cex = par("cex.axis"),
			las=0,
	   )

error.bar(mp,y,eu,el,.03)

grid(nx=NA,ny=10)

box()

#print(mp)

par(op) ## reset

if (FIGURE){
	dev.off()
}
