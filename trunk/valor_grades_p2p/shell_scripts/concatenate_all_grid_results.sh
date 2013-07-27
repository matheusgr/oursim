#!/bin/sh

TEMPFILE=$(mktemp)
dos2unix -q -n $VALOR_GRADES_P2P_HOME"/args.properties" $TEMPFILE
. $TEMPFILE

dir=$1
fileName=$2

echo $header > $fileName

for r in $rodadas; do
	eval "tail -q -n1 $dir/oursim-trace-persistent_*_machines_7_dias_*_sites_"$r".txt | sed -e 's/#/"$r"/g'" >> $fileName
done

