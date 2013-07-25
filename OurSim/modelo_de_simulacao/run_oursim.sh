#!/bin/sh

cat oursim_args.txt | xargs -n25 -t -P2 java -Xms500M -Xmx4000M -XX:-UseGCOverheadLimit -jar oursim.jar
