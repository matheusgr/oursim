# 
# vide:
# dependencias:
# imports:
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"), "/r_scripts/generators/grid_capacity/generate_peers_machines_description.R", sep="") )
# argumentos de linha de comando
	args.in <- commandArgs(TRUE)
# input:
# output:

siteDescriptionDir <- args.in[1]
mDescriptionFile   <- args.in[2]
#mDescriptionFile  <- "~/Dropbox/mestrado/r_scripts/generators/grid_capacity/machines_ourgrid/machines_ourgrid_speed.txt" 

useEcdf <- ifelse(is.na(mDescriptionFile), FALSE, TRUE)

generate_peers_machines_description(mDescriptionFile, siteDescriptionDir, useEcdf)
