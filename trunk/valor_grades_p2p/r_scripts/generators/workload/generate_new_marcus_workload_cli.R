# 
# vide:
# dependencias:
# imports:
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/generators/workload/generate_new_marcus_workload.R",sep=""))
# argumentos de linha de comando
	args.in <- commandArgs(TRUE)
# input:

# output:

nmaxofpeers   <- as.integer(   args.in[1] )
usersperpeer  <- as.integer(   args.in[2] )
nDias         <- as.integer(   args.in[3] )
rodada		  <- as.integer(   args.in[4] )
outputDir     <- as.character( args.in[5] )

generate_new_marcus_workload( nmaxofpeers, usersperpeer, nDias, rodada,	outputDir )
