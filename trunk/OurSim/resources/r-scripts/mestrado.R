Em dado momento gerou o lexema 4e+05 em vez de 400000 para o runtime.

ONE_HOUR<-60*60; ONE_DAY <- 24*ONE_HOUR; TS <- 7 * ONE_DAY; source("/local/edigley/traces/workload-gen/create_workload.R"); workload <- create_workload(TS);hist(workload$time/ONE_HOUR,n=TS/ONE_HOUR);write.table(workload, paste("/local/edigley/workspace/OurSim/resources/iosup_workload_",TS/(ONE_DAY),"_dias_70_sites.txt", sep=""),row.names=F, quote=F)
#deve ser monotonicamente crescente
plot(workload$time)
#deve variar com as amostras
length(workload$jobId)
#deve ter o padrão esperado
hist(workload$time)
#write.table(workload, paste("/local/edigley/workspace/OurSim/resources/iosup_workload_",TS/(ONE_DAY),"_dias_70_sites.txt", sep=""),row.names=F, quote=F)

#Overall job arrival rate during hourly intervals (Para um trace de 30 dias)
#	hist(workload$time/(24*3600),n=30*24)
#	unlist(lapply(strsplit("25;17;3600;24",";"),as.integer))

#numOfPeers <- seq(1,25,5)
numOfPeers <- c(1, seq(5,25,5))
numOfPeers <- 2:10

numOfMachinesByPeer <- 5:10

# criar a descrição das máquinas
	source("/local/edigley/traces/oursim/grid_machines_distribution.R")
	siteDescriptionDir <- "/local/edigley/traces/oursim/sites_description/"
	for (i in numOfMachinesByPeer) {
		mapply(generate_machines_speed, i, numOfPeers, siteDescriptionDir)
	}

# criar o workload 
	numOfDays <- c(7)
	source("/local/edigley/traces/oursim/create_workload.R"); 
	workloadDir <- "/local/edigley/traces/oursim/workloads/"
	mapply(generate_workloads, numOfDays, numOfPeers, workloadDir)

# executar no oursim

# filtrar as tarefas remotas e não esquecer de reordenar

# executar no spotsim

# comparar os resultados
	dir<-"/home/edigley/local/traces/oursim/trace_media_15s_persistent_heterogeneous_resources/"
	dir<-"/home/edigley/local/traces/oursim/trace_media_15s_replication_heterogeneous_resources/"
	source("~/local/traces/oursim/percentual.R")
	plot_percentual(numOfPeers, dir, TRUE, "green")

source("/home/edigley/workspace/cloudsim/r-scripts/availability_distribution.R")
source("/home/edigley/workspace/cloudsim/r-scripts/availability_overview.R")
hist(workload$time/ONE_HOUR,n=TS/ONE_HOUR)