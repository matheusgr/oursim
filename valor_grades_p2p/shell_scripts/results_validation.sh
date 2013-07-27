#ls oursim-trace-persistent_30_machines_7_dias_* | awk ' { system("tail -n1 "$NF) } ' | grep -v "#"
#tail -q -n1 oursim-trace-persistent_30_machines_7_dias_* | grep -v "#"
tail $1 -n1 * | grep -v "#"