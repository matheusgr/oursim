# Compara desempenho e custos da grade e da nuvem.
#vide:
#dependencias:
#imports:
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/setup_name.R",sep=""))
cmd_args = commandArgs(TRUE)
#input:
	# os resultados de todas as simulações do oursim
	inputFile           <- cmd_args[1]
#output:
	outputFile          <- cmd_args[2]

createV <- function(vec){
	paste("[",paste(vec, collapse=","),"]",sep="")
}

df <- read.table(inputFile)
names(df) <- c( "nPeers", "rodada", "runOursim", "runSpotsim", "instance")
rr <- aggregate( df$rodada, by=list(df$nPeers), FUN=unique )
names(rr) <- c("nPeers","rodadas")

print(rr)

rr$rodadas <- createV(rr$rodadas)
rr$cmd <- "sh build_oursim.sh"
rr$toRun <- "oursim"

rr <- subset(rr, select=c("cmd","nPeers","rodadas","toRun"))

write.table( rr, outputFile, col.names=F, row.names=F, quote=F)

# time R --slave --no-save --no-restore --no-environ --silent --args "/local/edigley/workspaces/simulacao/OurSim/rerun_spotsim.txt" "/tmp/teste.txt" < ~/Dropbox/mestrado/r_scripts/prepare_rerunnings.R
