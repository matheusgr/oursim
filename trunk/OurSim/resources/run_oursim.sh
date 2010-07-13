#!/bin/sh
scp cororoca:~/workspace/OurSim/dist/oursim.zip . 
unzip oursim.zip 
java -jar oursim.jar -w trace_filtrado1.txt -n 20 -d 
