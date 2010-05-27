# TODO: Add comment
# 
# Author: edigley
###############################################################################

# carregar o dataset
	ev <- read.table("events_oursim.txt", header=FALSE,col.names=c("event","time","task","job","peer","makespan","runningTime","queueingTime"),sep =" ",na.strings="null" )
# colocar variáveis no path
	attach(ev)
# jobs submetidos por peer
	summary(peer[event=="SubmitJob"])
# boxplot queuing time de job por peer
	boxplot(queueingTime[peer=="P1" & event=="FinishJob"])
# boxplot running time de job por peer	
	boxplot(runningTime[peer=="P1" & event=="FinishJob"])
# boxplot makespan time de job por peer	
	boxplot(makespan[peer=="P1" & event=="FinishJob"])