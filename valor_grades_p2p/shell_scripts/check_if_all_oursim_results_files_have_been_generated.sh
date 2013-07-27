#!/bin/sh

TEMPFILE=$(mktemp)
dos2unix -q -n $VALOR_GRADES_P2P_HOME"/args.properties" $TEMPFILE
. $TEMPFILE

for r in $rodadas 
do
	for s in $numOfPeers 
	do
		for m in $numOfMachinesByPeer
		do 
			file=$gridResultsDir"/oursim-trace-persistent_"$m"_machines_7_dias_"$s"_sites_"$r".txt"
			if [ ! -f $file ] 
			then 
				echo "Nao existe "$file
			fi 
		done
	done
done
