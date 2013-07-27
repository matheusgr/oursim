#!/bin/sh

TEMPFILE=$(mktemp)
dos2unix -q -n $VALOR_GRADES_P2P_HOME"/args.properties" $TEMPFILE
. $TEMPFILE

#Lista os que ainda não terminaram
	ls $gridResultsDir/oursim-trace-persistent_30_machines_7_dias_* | awk ' { system("tail -n1 "$NF) } ' | grep -v "#"
#Lista os que já terminaram
	ls spot-trace-* | awk ' { system("tail -n1 "$NF) } ' | grep "#"

head -n1 -c10 spot-trace-*

exit 

cd /tmp && cat ~/cmd_cp_template.txt | sed -e "s/_2.txt/_4.txt/g" | sed -r "s/persistent/replication/g" > new_workload.txt && sh new_workload.txt

ls oursim-trace-persistent_*_machines_7_dias_5_sites_1.txt | awk ' { system("tail -n1 "$NF) } '
ls spot-trace-persistent_30_machines_7_dias_*.txt | awk ' { system("tail -n1 "$NF) } '

sed -i "s/FALSE -1.0 -1.0/FALSE -1.0 -1.0 -1 -1/g" spot-trace-persistent_30_machines_7_dias_10_sites_100_spotLimit_groupedbypeer_false_av_us-east-1.linux.c1.medium.csv_*

grep "# submitted" spot-trace-persistent_30_machines_7_dias_* > re.txt
#grep spot-trace re.txt
#grep -rnwi "spot-trace" re.txt
sed -i "s/:# submitted finished preempted notStarted submittedTasks finishedTasks success sumOfJobsMakespan sumOfTasksMakespan finishedCost preemptedCost totalCost costByTask nPeers nMachines instance limit group groupedCloudUser utilization realUtilization goodput badput hostname ipaddress simulationDuration simulationDurationF//g" re.txt
cat re.txt | awk ' { system("scp "$NF" 150.165.85.109:/local/edigley/mestrado/traces/28_09_2011_3upp/") } '
