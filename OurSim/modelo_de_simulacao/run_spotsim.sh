#!/bin/sh

cat spotsim_args.txt | xargs -n17 -t -P2 java -Xms250M -Xmx1000M -XX:-UseGCOverheadLimit -jar spotsim.jar
