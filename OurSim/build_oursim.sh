#!/bin/sh

args=$1
cmd="unzip oursim.zip ; java -Xms500M -Xmx1500M -XX:-UseGCOverheadLimit -jar  oursim.jar"
echo \"$args\"

ant -buildfile build.xml -Dargs=\"$args\" && \
#sed 's/$cmd//g' xargs.txt > args.txt && \
#rm -rf xargs.txt && \
echo "Finished!"
