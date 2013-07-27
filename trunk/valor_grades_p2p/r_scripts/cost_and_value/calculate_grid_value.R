# Calcula o valor da grade
#vide:
#dependencias:
#imports:
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/setup_name.R",sep=""))
cmd_args = commandArgs(TRUE)
#input:
	# os resultados de todas as simulações do oursim
	inputFile           <- cmd_args[1]
	gridUtilizationFile <- cmd_args[2]
#output:
	outputFile          <- cmd_args[3]

gridAndCloudAllMakespanCis <- read.table(inputFile, header=T)

u <- read.table(gridUtilizationFile, header=T)

all <- merge(gridAndCloudAllMakespanCis, u)

k <- 1
all$vgrade <- ((all$cmean*all$ccost)/all$gmean)*k

all$vgradelower <- ((all$clower*all$ccost)/all$glower)*k

all$vgradeupper <- ((all$cupper*all$ccost)/all$gupper)*k

all$utilization <- all$umean

write.table( 
		format(all, format="d", digits=22), 
		outputFile, 
		row.names=F, 
		quote=F
	)

#Constanza Gruber: Momix
