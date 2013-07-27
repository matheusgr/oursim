# estima o custo energetico da grade e compara com o custo da cloud
# vide:
#	custo_makespan_cis.R
#dependencias:
#imports:
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/setup_name.R",sep=""))
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/mestrado_utils.R",sep=""))
cmd_args = commandArgs(TRUE)
#input:
	gridValueFile   <- cmd_args[1]
#output:
	#tabela final utilizada no paper sbrc
	outputFile      <- cmd_args[2]
	outputFile1     <- cmd_args[3]
	outputFile2     <- cmd_args[4]
	outputFile3     <- cmd_args[5]
	outputFile4     <- cmd_args[6]

gridValue <- read.table(gridValueFile, header=T)
gridValue <- subset( gridValue, instance %in% costCompareReferenceSpotMachines )

kwh <- 0.05
workHour <- 10
compHour <- 24 - workHour
timeSpan <- ( TS - (WARM_UP + DRAIN) ) / ONE_DAY
partialGradeCostHours <- timeSpan * workHour
fullGradeCostHours <- timeSpan * compHour
idlenessRatio <- 0.7
efficientProcessorConsumption <- 38
inefficientProcessorConsumption <- 78

efficientProcessorFullConsumption <- 115
inefficientProcessorFullConsumption <- 198
efficientProcessorOverheadConsumption <- efficientProcessorFullConsumption - 3.33
inefficientProcessorOverheadConsumption <- inefficientProcessorFullConsumption - 3.33
#efficientProcessorOverheadConsumption <- 38
#inefficientProcessorOverheadConsumption <- 78
#efficientProcessorIdleConsumption <- efficientProcessorFullConsumption - efficientProcessorOverheadConsumption
#inefficientProcessorIdleConsumption <- inefficientProcessorFullConsumption - inefficientProcessorOverheadConsumption
processorStandbyConsumption <- 3.33
efficientProcessorStandbyConsumption <- processorStandbyConsumption
inefficientProcessorStandbyConsumption <- processorStandbyConsumption
efficientProcessorIdleConsumption <- efficientProcessorStandbyConsumption
inefficientProcessorIdleConsumption <- inefficientProcessorStandbyConsumption

processorsF <- c(efficientProcessorFullConsumption, inefficientProcessorFullConsumption)
processorsO <- c(efficientProcessorOverheadConsumption, inefficientProcessorOverheadConsumption)
processorsI <- c(efficientProcessorIdleConsumption, inefficientProcessorIdleConsumption)

grids <- data.frame( 
			nPeers	   = numeric(), 
			nMachines  = numeric(), 
			instance   = character(),
			uRatio     = numeric(), 
			gasto      = numeric(),
			value      = numeric(),
			valuemin   = numeric(),
			valuemax   = numeric(),
			costmin    = numeric(),
			costmax    = numeric()
		)

df <- gridValue
for (i in 1:nrow(df)) {

	uRatio <- df[i,]$utilization

	partialGradeCostHoursConsumption <- uRatio                                          * idlenessRatio * partialGradeCostHours * processorsO
	#partialGradeCostHoursConsumption <- partialGradeCostHoursConsumption + (1 - uRatio) * idlenessRatio * partialGradeCostHours * processorsI
	fullGradeCostHoursConsumption <- uRatio * fullGradeCostHours * processorsF
	fullGradeCostHoursConsumption <- fullGradeCostHoursConsumption + (1 - uRatio) * fullGradeCostHours * processorsI
	totalConsumption <- partialGradeCostHoursConsumption + fullGradeCostHoursConsumption
	totalConsumptionKwh <- totalConsumption/1000
	costByProcessor <- kwh * totalConsumptionKwh

	np <- df[i,]$nPeers
	nmbp <- df[i,]$nMachines
	instance <- df[i,]$instance
	g <- df[i,]$ccost
	value <- df[i,]$vgrade
	valuemin <- df[i,]$vgradelower
	valuemax <- df[i,]$vgradeupper
	costmin <- np * nmbp * costByProcessor[1]
	costmax <- np * nmbp * costByProcessor[2]
	grids <- rbind( 
			grids, 
			data.frame(
					nPeers     = np, 
					nMachines  = nmbp,
					instance   = instance,
					uRatio     = uRatio,
					gasto      = g, 
					value	   = value,
					valuemin   = valuemin,
					valuemax   = valuemax,
					costmin    = costmin, 
					costmax    = costmax
				)
		)

}

finalTable   <- data.frame( line = character() )

finalTableDF <- data.frame( 
				np	 = numeric(), 
				nmbp	 = numeric(), 
				instance = character(),
				uRatio   = numeric(), 
				costmin  = numeric(),
				costmax  = numeric(), 
				value    = numeric(), 
				valuemin = numeric(),
				valuemax = numeric(),
				valueE1  = numeric(), 
				valueE2  = numeric(),
				vbm0	 = numeric(),
				vbm1	 = numeric(),
				vbm2	 = numeric()
			)

for (i in 1:nrow(grids)) {

	np       <- grids[i,]$nPeers
	nmbp     <- grids[i,]$nMachines
	instance <- grids[i,]$instance
	uRatio   <- grids[i,]$uRatio
	g        <- myformat( grids[i,]$gasto )
	costmin  <- grids[i,]$costmin
	costmax  <- grids[i,]$costmax
	value    <- grids[i,]$value
	valuemin <- grids[i,]$valuemin
	valuemax <- grids[i,]$valuemax
	valueE1  <- grids[i,]$value-grids[i,]$costmin
	valueE2  <- grids[i,]$value-grids[i,]$costmax
	effVg1   <- (value-costmin)/value
	effVg2   <- (value-costmax)/value
	vbm0     <- ( value / (np * nmbp) ) / (numOfDays*24)
	vbm1     <- ( valueE1 / (np * nmbp) ) / (numOfDays*24)
	vbm2     <- ( valueE2 / (np * nmbp) ) / (numOfDays*24)
	finalTableDF <- rbind(
				finalTableDF, 
				data.frame( 
						np       = np,      
						nmbp 	 = nmbp,
						instance = instance,
						uRatio   = uRatio,
						costmin  = costmin,
						costmax  = costmax,
						value    = value,  
						valuemin = valuemin,
						valuemax = valuemax,
						valueE1  = valueE1,
						valueE2  = valueE2,
						effVg1   = effVg1,
						effVg2   = effVg2,
						vbm0 	 = vbm0,   
						vbm1 	 = vbm1,   
						vbm2	 = vbm2    
					)
			)

}

write.table(
		finalTableDF, 
		paste( outputFile, "_ds.txt", sep=""), 
		row.names=F, 
		quote=F, 
		col.names=T
	)

finalTableDF <- finalTableDF[ order(finalTableDF$instance, finalTableDF$effVg2), ]

finalTableF <- data.frame( 
				np	 = numeric(), 
				nmbp	 = numeric(), 
				instance = character(),
				uRatio   = character(), 
				costmin  = character(),
				costmax  = character(), 
				cdpmmin  = character(),
				cdpmmax  = character(),
				value    = character(), 
				valueE1  = character(), 
				valueE2  = character(),
				effVg1   = character(),
				effVg2   = character(),
				vbm0	 = character(),
				vbm1	 = character(),
				vbm2	 = character()
			)
for (i in 1:nrow(finalTableDF)) {

	r <- finalTableDF[i,]

	nm <- r$np * r$nmbp

	finalTableF <- rbind(
				finalTableF, 
				data.frame( 
						np       = r$np,      
						nmbp 	 = r$nmbp,
						instance = r$instance,
						uRatio   = myformat2( r$uRatio ),
						costmin  = myformat( r$costmin ),
						costmax  = myformat( r$costmax ),														
						cdpmmin  = myformat( (r$costmin / nm) / timeSpan ),
						cdpmmax  = myformat( (r$costmax / nm) / timeSpan ),
						value    = myformat( r$value ),  
						valueE1  = myformat( r$value-r$costmin ),
						valueE2  = myformat( r$value-r$costmax ),
						effVg1   = myformat2( r$effVg1 ),
						effVg2   = myformat2( r$effVg2 ),
						vbm0 	 = myformat( ( r$value / (r$np * r$nmbp) ) / (numOfDays*24) ),   
						vbm1 	 = myformat( ( r$valueE1 / (r$np * r$nmbp) ) / (numOfDays*24) ),   
						vbm2	 = myformat( ( r$valueE2 / (r$np * r$nmbp) ) / (numOfDays*24) )    
						)
					)

}

printFinalTable <- function(df, file) {
	table   <- data.frame( line = character() )
	for (i in 1:nrow(df)) {

		r  <- df[i,]

		row <- paste(toShortInstanceName(as.character(r$instance)), r$np, r$uRatio, r$cdpmmin, r$cdpmmax, r$costmin, r$costmax, r$value, r$effVg1, r$effVg2, sep=" & ")
		row <- paste("\t",row, "\\\\", ifelse(i%%5==0,"\\hline",""), sep=" ")

		table <- rbind( table, data.frame( line = row ) ) 

	}

	write.table( table, file, row.names=F, quote=F, col.names=F )
}

printFinalTable(finalTableF, outputFile)

finalTable1F <- subset(finalTableF, instance == costCompareReferenceSpotMachines[1])
printFinalTable(finalTable1F, outputFile1)

finalTable2F <- subset(finalTableF, instance == costCompareReferenceSpotMachines[2])
printFinalTable(finalTable2F, outputFile2)

# Calculo de valor de forma relativa (por hora-máquina)

ndf1 <- subset(finalTableDF, instance == costCompareReferenceSpotMachines[1])
ndf1 <- ndf1[order(ndf1$np),]
ndf2 <- subset(finalTableDF, instance == costCompareReferenceSpotMachines[2])
ndf2 <- ndf2[order(ndf2$np),]

ndf <- data.frame(
			np        = ndf1$np,
			xlargemax = ndf1$vbm1  ,
			xlargemin = ndf1$vbm2  ,
			mediummax = ndf2$vbm1  , 
			mediummin =	ndf2$vbm2  
		)
ndf <- ndf[order(ndf$mediummin),]
finalTableR   <- data.frame( line = character() )
for (i in 1:nrow(ndf)) {

	r  <- ndf[i,]

	#r$instance,
	row  <- paste(r$np, myformat3(r$xlargemax), myformat3(r$xlargemin), myformat3(r$mediummax), myformat3(r$mediummin), sep=" & ")
	row <- paste("\t",row, "\\\\", ifelse(i%%5==0,"\\hline",""), sep=" ")

	finalTableR <- rbind( finalTableR, data.frame( line = row ) ) 

}

write.table(finalTableR, outputFile3, row.names=F, quote=F, col.names=F)

FIGURE <- TRUE
pngFilePath <- outputFile4

if (FIGURE){
	png(pngFilePath)
}


insts <- character()
peers <- numeric()
vbm1  <- numeric()
vbm2  <- numeric()
for (i in 1:nrow(finalTableDF)) {

	r  <- finalTableDF[i,]
	
	insts[i] <- toShortInstanceName(as.character(r$instance))
	peers[i] <- r$np
	vbm1[i]  <- r$vbm1
	vbm2[i]  <- r$vbm2
}

fdf <- data.frame( 
			instance = insts,
			np       = peers,
			vbm1     = vbm1,
			vbm2     = vbm2
		)

fdf <- fdf[order(fdf$instance, fdf$np),]

fdf$vbm <- (fdf$vbm2 + fdf$vbm1)/2

print(xYplot(  
		Cbind(vbm,vbm2,vbm1) ~ np, 
		data=fdf, 
		groups=instance, 
		type="l", 
		#lty=5,
		lty=c(3,5),
		col=c("grey50","black"),
		panel=function(x,y,...){ 
			panel.xYplot(x, y, ...) ;
			panel.abline(	
					h = seq(0,.0265,.0025), 
					col = c("lightgray"), 
					lty=3, 
					cex=.2,
				)
		},
		strip=strip.custom(strip.names=TRUE, strip.levels=TRUE,bg="white",fg="white"),
		ylim=c(-.0005,.0265),
		ylab="VPHM  ( em USD )",
		xlab=grid_all_number_of_preemptions_xlab,
		cex=.7,
	)
)

if (FIGURE){
	dev.off()
}
