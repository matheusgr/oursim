suppressPackageStartupMessages(library(Hmisc))
suppressPackageStartupMessages(library(lattice))
suppressPackageStartupMessages(require(grid))
library(Hmisc)#,warn.conflicts = FALSE,verbose = FALSE, quietly=TRUE)
library(lattice)

setUpGName <- function(nSites, nRecursos, dir, scheduler, rodada) {
	prefix <- paste( "oursim-trace-", scheduler, "_", sep="")
	sufix <- paste( "_sites_", rodada, ".txt", sep="")
	setUpName(nSites, nRecursos, dir, prefix, sufix, scheduler, rodada)
}

setUpCName <- function(nSites, nRecursos, dir, scheduler, rodada, instance, spotLimit, groupedbypeer) {
	prefix <- paste( "spot-trace-", "persistent", "_", sep="" )
	sufix <- paste( "_sites_",spotLimit,"_spotLimit_groupedbypeer_",groupedbypeer,"_av_",instance,"_", rodada, ".txt",sep="" )
	setUpName(nSites, refNumOfMachinesByPeer, dir, prefix, sufix, scheduler, rodada)
}

setUpGUName <- function(nSites, nRecursos, dir, scheduler, rodada) {
	prefix <- paste( "oursim-trace-utilization-", scheduler, "_", sep="")
	sufix <- paste( "_sites_", rodada, ".txt", sep="")
	setUpName(nSites, nRecursos, dir, prefix, sufix, scheduler, rodada)
}

setUpCUName <- function(nSites, nRecursos, dir, scheduler, rodada, instance) {
	prefix <- paste( "spot-trace-utilization-", scheduler, "_", sep="")
	sufix <- paste( "_sites_100_spotLimit_groupedbypeer_true_av_us-east-1.linux.m1.small.csv_", rodada, ".txt", sep="")
	setUpName(nSites, nRecursos, dir, prefix, sufix, scheduler, rodada)
}

setUpName <- function(nSites, nRecursos, dir, prefix, sufix, scheduler, rodada) {
	paste(dir, prefix, nRecursos, "_machines_7_dias_", nSites, sufix, sep="")
}

source( paste( Sys.getenv("VALOR_GRADES_P2P_HOME"), "/r_scripts/args.R", sep="" ) )

instancesTypes <- c( 
		"m1.small",
		"m1.large",
		"m1.xlarge",
		"c1.medium",
		"c1.xlarge",
		"m2.xlarge",
		"m2.2xlarge",
		"m2.4xlarge"
	)

instancesTypesToTypeNumber <- list( 
		"m1.small"   = 1,
		"m1.large"   = 2,
		"m1.xlarge"  = 3,
		"c1.medium"  = 4,
		"c1.xlarge"  = 5,
		"m2.xlarge"  = 6,
		"m2.2xlarge" = 7,
		"m2.4xlarge" = 8
	)

instancesNumberOfCores <- list( 
		"m1.small"   = 1,
		"m1.large"   = 2,
		"m1.xlarge"  = 4,
		"c1.medium"  = 2,
		"c1.xlarge"  = 8,
		"m2.xlarge"  = 2,
		"m2.2xlarge" = 4,
		"m2.4xlarge" = 8
	)

nnucleos <- c(8, 8, 4, 4, 2, 2, 2, 1)

typeToIndex <- function(type){
	instancesTypesToTypeNumber[[type]]
}

toInstanceIndex <- function(type){
	instancesTypesToTypeNumber[[type]]
}

instancesTypesToColor <- list( 
		"m1.small"   = "black",
		"m1.large"   = "black",
		"m1.xlarge"  = "black",
		"c1.medium"  = "green",
		"c1.xlarge"  = "green",
		"m2.xlarge"  = "blue",
		"m2.2xlarge" = "blue",
		"m2.4xlarge" = "blue"
	)

typeToColor <- function(type){
	instancesTypesToColor[[type]]
}

bwcolors <- c(rep("yellow",3),rep("green",2),rep("lightblue",3))

workload_histogram_xlab <- "time in days"
workload_histogram_ylab <- "number of submitions per hour"
workload_histogram_xlab <- "Tempo em Dias"
workload_histogram_ylab <- "N\u{FA}mero de Submiss\u{F5}es Por Hora"

ecdf_tasks_runtime_header <- "Empirical Cumulative Distribution"
ecdf_tasks_runtime_xlab   <- "task's runtime in hours"
ecdf_tasks_runtime_ylab   <- "number of submitions per hour"
ecdf_tasks_runtime_header <- "Distribui\u{E7}\u{E3}o Acumulada Emp\u{ED}rica"
ecdf_tasks_runtime_xlab   <- "Tempo de Execu\u{E7}\u{E3}o em Horas"
ecdf_tasks_runtime_ylab   <- "Propor\u{E7}\u{E3}o < Tempo de Execu\u{E7}\u{E3}o"
ecdf_tasks_runtime_xlim	  <- c(0,3)

xyplot_cloud_vs_grid_d_metric_xlab   <- "Number of Machines by Peer"
xyplot_cloud_vs_grid_d_metric_ylab   <- "Normalized Aggregated Makespan Deviation"
xyplot_cloud_vs_grid_d_metric_xlab   <- "N\u{FA}mero de M\u{E1}quinas por Peer"
xyplot_cloud_vs_grid_d_metric_ylab   <- "M\u{E9}trica D"
xyplot_cloud_vs_grid_d_metric_xlab   <- "Number of Peers"
xyplot_cloud_vs_grid_d_metric_ylab   <- "Relative Performance"
xyplot_cloud_vs_grid_d_metric_main   <- ""
xyplot_cloud_vs_grid_d_metric_layout <- c(1,length(numOfPeers))#c(1,3)
xyplot_cloud_vs_grid_d_metric_opt_2_layout <- c(1,length(numOfMachinesByPeer))#c(1,3)
xyplot_cloud_vs_grid_d_metric_opt_2_layout <- c(3,1)
xyplot_cloud_vs_grid_d_metric_min_y  <- 0
xyplot_cloud_vs_grid_d_metric_max_y  <- 5.5#3#1.5#10#4

grid_all_number_of_preemptions_xlab   <- "N\u{FA}mero de Peers na Grade"
grid_all_number_of_preemptions_ylab   <- "Impacto Potencial das Preemp\u{E7}\u{F5}es no Makespan"
grid_all_number_of_preemptions_ylab   <- "M\u{E9}trica IPP"


minmax <- T

ylimBarplot <- c(0,20)
