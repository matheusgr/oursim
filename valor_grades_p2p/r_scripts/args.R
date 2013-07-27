source( paste( Sys.getenv("VALOR_GRADES_P2P_HOME"), "/args.properties", sep="" ) )

v <- function(arg) {
	argTmp <- strsplit(arg, split=" ", fixed=T)
	result <- unlist(lapply(FUN=as.integer, argTmp, split=" ", fixed=T))
	result
}

rodadas <- v(rodadas)
numOfPeers <- v(numOfPeers)
refNumOfPeers <- v(refNumOfPeers)
numOfMachinesByPeer <- v(numOfMachinesByPeer)
refNumOfMachinesByPeer <- v(refNumOfMachinesByPeer)
usersPerPeer=v(usersPerPeer)
spotLimits <- v(spotLimits)
spotLimit <- v(spotLimit)
instances <- unlist(strsplit(instances, split=" ", fixed=T))
referenceInstances <- unlist(strsplit(referenceInstances, split=" ", fixed=T))
costCompareReferenceSpotMachines <- unlist(strsplit(costCompareReferenceSpotMachines, split=" ", fixed=T))


groupedbypeer <- "false"
apenasNovasRodadas <- T

oursimDirO  <- gridResultsDir
spotsimDir0 <- cloudResultsDir
figuresDir  <- resultsDir
imagesDir   <- resultsDir

oursimDir   <- oursimDirO
spotsimDir  <- spotsimDir0

filterDS  <- FALSE

scheduler <- "replication"
scheduler <- "persistent"

ONE_HOUR  <- 60 * 60
ONE_DAY   <- 24 * ONE_HOUR
numOfDays <- 7
TS        <- numOfDays * ONE_DAY
WARM_UP   <- ONE_DAY
DRAIN     <- ONE_DAY
WARM_UP   <- 0
DRAIN     <- 0
