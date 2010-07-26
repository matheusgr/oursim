#!/bin/sh
rm -rf oursim.zip
rm -rf oursim
rm -rf oursim_trace.txt
scp cororoca:~/workspace/OurSim/dist/oursim.zip . 
unzip oursim.zip -d oursim 
java -jar oursim/oursim.jar -w oursim/resources/trace_filtrado_primeiros_1000_jobs.txt -s replication -r 3 -pd oursim/resources/nordugrid_site_description.txt -synthetic_av -o oursim_trace.txt 
