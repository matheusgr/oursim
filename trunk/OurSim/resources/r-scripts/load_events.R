# TODO: Add comment
# 
# Author: edigley
###############################################################################

# carregar o dataset
#	ev <- read.table("oursim_trace.txt", header=FALSE,col.names=c("event","time","task","job","peer","makespan","runningTime","queueingTime"),sep =" ",na.strings="null" )
#	ev <- read.table("oursim_trace.txt", header=TRUE,sep =":")
# colocar variáveis no path
#	attach(ev)
# jobs submetidos por peer
#	summary(peer[event=="SubmitJob"])
# boxplot queuing time de job por peer
#	boxplot(queueingTime[peer=="P1" & event=="FinishJob"])
# boxplot running time de job por peer	
#	boxplot(runningTime[peer=="P1" & event=="FinishJob"])
# boxplot makespan time de job por peer	
#	boxplot(makespan[peer=="P1" & event=="FinishJob"])

library(Hmisc)
library(lattice)
library(ggplot2)

# carregar o dataset
	ev <- read.table("/local/edigley/workspace/OurSim/oursim_trace.txt", header=TRUE,sep =":")
# total de jobs submetidos
	submitted = length(ev$jobId[ev$F=="U"])
# total de jobs concluidos
	finished  = length(ev$jobId[ev$F=="F"])
# total de jobs preempted
	preempted  = length(ev$jobId[ev$F=="P"])
# total de jobs não iniciados
	notStarted  = submitted - (finished + preempted)
# total de jobs iniciados
	started  = submitted - notStarted
# porcentagem de jobs concluidos
	success = finished/submitted
	
	finishedCost = sum(ev$cost[ev$F=="F"])
	preemptedCost = sum(ev$cost[ev$F=="P"])
	totalCost = finishedCost + preemptedCost

# submitted finished preempted notStarted success finishedCost preemptedCost totalCost
resume = c(submitted,finished,preempted,notStarted,success,finishedCost,preemptedCost,totalCost);
write(resume,file="test.txt",ncolumns=length(resume),append=T)

# runtime de todos os jobs por usuário em função do tempo de submissão
	xyplot( RunTime ~ SubmitTime | factor(UserID), data=w, type="h")
# quantidade de submissões de todos os usuários ao long do tempo
	hist((w$SubmitTime-w$SubmitTime[1])/(3600*24), 30, col=3)
# quantidade de submissões por usuário ao long do tempo (o parâmetro n é quantidade de buckets)
	histogram( ~ (SubmitTime-SubmitTime[1])/(3600*24) | factor(UserID), data = w, n = 30)
# como se distribuem as submissões quanto ao tempo (nada que um histrograma não faça melhor)
	print(Ecdf( ~ SubmitTime | UserID,data=w,col=c(1,2),q=0.95))
# como se distribuem os runtimes dos jobs submetidos
	print(Ecdf( ~ RunTime | UserID,data=w,col=c(1,2),q=0.95))
	