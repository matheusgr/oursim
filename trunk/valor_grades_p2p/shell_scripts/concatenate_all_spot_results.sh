#!/bin/sh

TEMPFILE=$(mktemp)
dos2unix -q -n $VALOR_GRADES_P2P_HOME"/args.properties" $TEMPFILE
. $TEMPFILE

dir=$1
cloudLimit=$2
fileName=$3

echo $header > $fileName

for r in $rodadas; do
	eval "tail -q -n1 $dir/spot-trace-persistent_*_machines_7_dias_*_sites_"$cloudLimit"_spotLimit_groupedbypeer_false_av_*_"$r".txt | sed -e 's/#/"$r"/g'" >> $fileName
done

