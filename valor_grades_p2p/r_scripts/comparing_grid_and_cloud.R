# Compara desempenho e custos da grade e da nuvem.
#vide:
#dependencias:
#imports:
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/setup_name.R",sep=""))
cmd_args = commandArgs(TRUE)
#input:
	# os resultados de todas as simula��es do oursim
	inputFile           <- cmd_args[1]
#output:
	outputFile          <- cmd_args[2]

# P   : Quantidade de processamento necess�rio para executar uma aplica��o.
# g   : Capacidade de processamento m�dia do grid.
# C_i : Capacidade de processamento das inst�ncias do tipo i na nuvem.
# C_g : Custo m�dio aproximado do grid, sem considerar badput.
#	C_g = ( P/g ) * C_p
# C_p : � o custo m�dio da energia por unidade de tempo consumida pelas m�quinas do grid quando elas est�o processando as tarefas da aplica��o.
#	Exemplo pr�tico de C_p
#		Pega o consumo m�dio das m�quinas e multiplica pelo valor da tarifa de energia.
# C_c : Custo na nuvem quando s�o utilizados apenas inst�ncias do tipo i.
#	C_c = ( P/C_i ) * T_i
# T_i : Tarifa m�dia cobrada por unidade de tempo de uso de uma inst�ncia do tipo i no spot market. 
#	Se assumido um gr�o suficientemente pequeno, o custo na nuvem independe do n�mero de inst�ncias utilizadas.
#		Embora este n�mero possa impactar diretamente o makespan.
# V_g : Valor do grid.
# k   : Coeficiente que indica qu�o mais importante � o custo em rela��o � urg�ncia.


# P   : Quantidade de processamento necess�rio para executar uma aplica��o.
# B   : Badput m�dio.
# b   : Taxa de badput para o workload considerado.
#	b = ( B/P )
# T_i : Tarifa m�dia cobrada por unidade de tempo de uso de uma inst�ncia do tipo i no spot market. 
# C_i : Capacidade de processamento das inst�ncias do tipo i na nuvem.
# O custo do grid � menor para
#	C_p < ( 1 + b ) * g * ( T_i/C_i )
# C_g : Custo m�dio do grid, considerando badput.
#	C_g = ( ( P + B )/g ) * C_p


#source("/home/edigley/Dropbox/mestrado/r_scripts/args.R")
convertGHz2Mips <- function(nGHz) {
	oneGHzInMips=3000
	nGHz * oneGHzInMips
}

kwh <- 0.05
idlenessRatio <- 0.7
oneMachineCapacity <- 2.5
instanceCapacity <- 2.8
nCores <- 8
instanceCost <- 0.34
C_i <- nCores * convertGHz2Mips(instanceCapacity)
T_i <- instanceCost/3600
efficientProcessorFullConsumption <- 115
inefficientProcessorFullConsumption <- 198
grain<-1/3600

totalConsumptionKwh <- (grain * inefficientProcessorFullConsumption)/1000
costByProcessor <- kwh * totalConsumptionKwh

#C_p <- grain * inefficientProcessorFullConsumption * kwh

df <- read.table(inputFile, header=T)

P <- aggregate(df$goodput, by=list(df$nPeers), FUN=mean)
B <- aggregate(df$badput,  by=list(df$nPeers), FUN=mean)

names(P) <- c("nPeers", "goodput")
names(B) <- c("nPeers", "badput")

ds <- merge(P,B)

ds$g <- ds$nPeers * numOfMachinesByPeer * idlenessRatio * convertGHz2Mips(2.5)

ds$C_p <- ds$nPeers * numOfMachinesByPeer * costByProcessor

#C_g = ( ( P + B )/g ) * C_p
ds$C_g <- ((ds$goodput + ds$badput)/ds$g) * ds$C_p

ds$b <- ds$badput / ds$goodput

ds$C_eff <- ( ds$C_p < (( 1 + ds$b ) * ds$g * (T_i/C_i)) )


write.table(
		ds, 
		outputFile, 
		row.names=F, 
		quote=F, 
		col.names=T
	)

print(ds)

# time R --slave --no-save --no-restore --no-environ --silent --args "/local/edigley/mestrado/resultados/28_09_2011_3upp/all_grid_results.txt" "/tmp/grid_eff.txt" < ~/Dropbox/mestrado/r_scripts/comparing_grid_and_cloud.R
