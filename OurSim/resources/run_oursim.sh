#!/bin/sh
rm -rf oursim.zip
rm -rf oursim
rm -rf oursim_trace.txt
scp cororoca:~/workspace/OurSim/dist/oursim.zip . 
unzip oursim.zip -d oursim 
java -jar oursim/oursim.jar -w oursim/resources/trace_filtrado_primeiros_1000_jobs.txt -s replication -r 3 -pd oursim/resources/nordugrid_site_description.txt -synthetic_av -o oursim_trace.txt 

# -w resources/nordugrid_setembro_2005.txt -wt gwa -pd resources/nordugrid_site_description.txt -d -o oursim_trace.txt
# -w resources/nordugrid_janeiro_2006.txt -s replication -r 3 -pd resources/nordugrid_site_description.txt -d -o oursim_trace.txt
# -w resources/nordugrid_janeiro_2006.txt -s replication -r 3 -pd resources/nordugrid_site_description.txt -synthetic_av -o oursim_trace.txt"
# -spot -bid min -md 3000 -w resources/nordugrid_setembro_2005.txt -av /home/edigley/local/traces/spot_instances/spot-instance-prices/eu-west-1.linux.m1.small.csv -o oursim_trace.txt"
# -spot -bid max -type m1.small -w resources/nordugrid_setembro_2005.txt -av /home/edigley/local/traces/spot_instances/spot-instance-prices/eu-west-1.linux.m1.small.csv -o oursim_trace.txt