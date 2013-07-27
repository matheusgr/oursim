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

evaluateCloudOptions <- function(sp, pond) {
	mincost  <- min(sp$cmean)
	maxcost  <- max(sp$cmean)
	meancost <- mean(sp$cmean)

	minmksp  <- min(sp$mmean)
	maxmksp  <- max(sp$mmean)
	meanmksp <- mean(sp$mmean)

	valuations <- data.frame( 
					instance = character(),
					value    = numeric(),
					valueMin = numeric(),
					valueMax = numeric(),
					pc       = factor(),
					pm       = factor()
				)

	for (p in 1:nrow(pond)) {

		pc <- as.numeric(pond[p,]$pc)
		pm <- as.numeric(pond[p,]$pm)

		for (i in 1:nrow(sp)) {
			r <- sp[i,]
			#Verificar se não está retornando Inf
			m <- ( ( r$mmean - minmksp ) / minmksp ) + 1
			c <- ( ( r$cmean - mincost ) / mincost ) + 1
			v <- 1 / ( c*pc + m*pm )

			m    <- ( ( r$mupper - minmksp ) / minmksp ) + 1
			c    <- ( ( r$cupper - mincost ) / mincost ) + 1
			vmin <- 1 / ( c*pc + m*pm )

			m   <- ( ( r$mlower - minmksp ) / minmksp ) + 1
			c   <- ( ( r$clower - mincost ) / mincost ) + 1
			vmax <- 1 / ( c*pc + m*pm )

			val <- data.frame( instance = r$instance,
	  								 value    = v,
									 valueMin = vmin, 
									 valueMax = vmax, 
									 pc       = pc,
									 pm       = pm
								  ) 
			val <- val[ order(val$value), ]

			valuations <- rbind( valuations, 
										val
									 ) 
		}

	}

	#print(valuations)
	
	valuations

}

sp <- read.table(spotCisFile, header=T )
sp <- subset(sp, nPeers==refNumOfPeers & nMachines==refNumOfMachinesByPeer)
sp <- sp[ order(sp$mmean), ]

pond <- seq(0,1,.005)

pcs <-    pond
pms <- ( 1 - pcs )

p <- data.frame( 
						pc=pcs, 
						pm=pms 
					)

v <- evaluateCloudOptions(sp, p)

#xYplot(Cbind(mean^opt2,lower^opt2,upper^opt2)

xYplot(  value ~ pc, #Cbind(value,valueMin,valueMax) ~ pc, 
			data=v, 
			groups=instance, 
			#auto.key=T,
			type="l",
			col=1,
			#lty=c(1,8),
			ylab=" sat( inst, pc ) "
		)


if (FIGURE){
	dev.off()
}

