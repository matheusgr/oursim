source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/generators/workload/new_marcus_workload.R",sep=""))

generate_new_marcus_workload <- function( nmaxofpeers, usersperpeer, nDias, rodada, outputDir ){

	ONE_HOUR <- 60 * 60
	ONE_DAY  <- 24 * ONE_HOUR
	TS       <- nDias * ONE_DAY

	# tudo em horas
	rtmax <- 365*24 
	tmax <- TS / 3600

	peerspertrace <- nmaxofpeers %/% 6 + (ifelse(nmaxofpeers%%6==0,0,1))

	fileName <- paste("/tmp/","workload_edigley_clust_", peerspertrace, "ppt_", usersperpeer, "upp.txt", sep="")

	new_marcus_workload( 
				tmax=tmax, 
				rtmax=rtmax, 
				nmaxofpeers=nmaxofpeers, 
				peerspertrace=peerspertrace, 
				usersperpeer=usersperpeer, 
				fileName=fileName
			)

	w <- read.table(fileName,header=T)
	w.sorted <- w[ order(w$time),]

	outputFileName <- paste( outputDir, "/", "marcus_workload_", TS/(ONE_DAY), "_dias_", nmaxofpeers, "_sites_", rodada, ".txt", sep="" );

	write.table( 
			format(w.sorted,format="d",digits=22), 
			outputFileName, 
			row.names=F, 
			quote=F
		 )

}
