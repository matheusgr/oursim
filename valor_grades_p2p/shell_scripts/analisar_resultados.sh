#!/bin/sh
# Analisar os Resultados (shell script)
#	1 - definir parametros em setup_name.R
#  2 - alterar o diretorio do arquivo de resultados
#  3 - descomentar as linhas de interesse
#
# time sh ~/Dropbox/mestrado/shell_scripts/analisar_resultados.sh 

#. /home/edigley/Dropbox/mestrado/shell_scripts/load_properties.sh
TEMPFILE=$(mktemp)
dos2unix -q -n $VALOR_GRADES_P2P_HOME"/args.properties" $TEMPFILE
. $TEMPFILE

# Diratórios com os scripts
	rScriptsDir=$VALOR_GRADES_P2P_HOME"/r_scripts"
	shellScriptsDir=$VALOR_GRADES_P2P_HOME"/shell_scripts"

# Arquivos com métricas
	dsDir=$resultsDir"/d_metrics/"
	#d_metric_for_all_peers.txt
	allTxt=$resultsDir"/all.txt"
	#average_of_d_metric_for_all_grids.txt
	allMeansTxt=$resultsDir"/all_means.txt"
	#confidence_interval_for_average_of_d_metric_for_all_grids.txt
	cisAllTxt=$resultsDir"/cis_all.txt"
	allSpotResultsTxt=$resultsDir"/all_spot_results.txt"
	allGridResultsTxt=$resultsDir"/all_grid_results.txt"
	#confidence_intervals_for_makespan_and_cost_on_cloud.txt
	cisCloudCostMakespanTxt=$resultsDir"/cis_cloud_cost_makespan.txt"
	#sum_of_makespans_on_grid_and_of_makespans_and_costs_on_cloud.txt
	gridAndCloudAllMakespanTxt=$resultsDir"/grid_and_cloud_all_makespan.txt"
	#confidence_intervals_for_sum_of_makespans_on_grid_and_of_makespans_and_costs_on_cloud.txt
	gridAndCloudAllMakespanCisTxt=$resultsDir"/grid_and_cloud_all_makespan_cis.txt"
	extractGridAllNumberOfPreemptionsTxt=$resultsDir"/extract_grid_all_number_of_preemptions.txt"
	extractGridAllNumberOfPreemptionsCisTxt=$resultsDir"/extract_grid_all_number_of_preemptions_cis.txt"
	gridValueTxt=$resultsDir"/grid_value.txt"
	finalTableTxt=$resultsDir"/final_table.txt"
	finalTable1Txt=$resultsDir"/final_table_1.txt"
	finalTable2Txt=$resultsDir"/final_table_2.txt"
	finalTable3Txt=$resultsDir"/final_table_3.txt"
	cisGridUtilizationTxt=$resultsDir"/cis_grid_utilization.txt"
	

# Figuras resultantes
	cloudLimit=100
	gridVsSpotPng1=$resultsDir"/p2p_grid_growing_up_and_out_vs_spot_instances_limit_"$cloudLimit"_v1.png"
	gridVsSpotPng2=$resultsDir"/p2p_grid_growing_up_and_out_vs_spot_instances_limit_"$cloudLimit"_v2.png"
	barplotCloudCostAndMakespanPng=$resultsDir"/barplot_cloud_cost_and_makespan_limit_"$cloudLimit".png"
	bubbleplotCloudCostAndMakespanPng=$resultsDir"/bubbleplot_cloud_cost_and_makespan_limit_"$cloudLimit".png"
	lineplotCloudCostAndMakespanPng=$resultsDir"/lineplot_cloud_cost_and_makespan_limit_"$cloudLimit".png"

	lineplotVPHMPng=$resultsDir"/lineplot_vphm_limit_"$cloudLimit".png"

	LineplotGridAllNumberOfPreemptionsPng=$resultsDir"/extract_grid_all_number_of_preemptions.png"

# R command
	R="R --slave --no-save --no-restore --no-environ --silent --args"

# Scripts R
	SteadyStateFilterR=$rScriptsDir"/steady_state_filter.R"
	
	GenerateDsDMetricR=$rScriptsDir"/generate_ds_d_metric.R"
	GenerateDsDMetricCisR=$rScriptsDir"/generate_ds_d_metric_cis.R"
	GenerateDsDMetricMeansR=$rScriptsDir"/generate_ds_d_metric_means.R"
	GenerateDsCloudCostAndMakespanCisR=$rScriptsDir"/generate_ds_cloud_cost_and_makespan_cis.R"
	GenerateDsGridUtilizationCisR=$rScriptsDir"/generate_ds_grid_utilization_cis.R"

	XYplotCloudVsGridDmetricR=$rScriptsDir"/plots/xyplot_cloud_vs_grid_d_metric_opt_2.R"
	BarplotCloudCostAndMakespanR=$rScriptsDir"/plots/barplot_cloud_cost_and_makespan.R"
	BubbleplotCloudCostAndMakespanR=$rScriptsDir"/plots/bubbleplot_cloud_cost_and_makespan.R"
	LineplotCloudCostAndMakespanR=$rScriptsDir"/plots/evaluate_cloud_makespan_cost_tradeoff.R"
	
	ExtractGridAllNumberOfPreemptionsR=$rScriptsDir"/cost_and_value/extract_grid_all_number_of_preemptions.R"
	ExtractGridAllNumberOfPreemptionsCisR=$rScriptsDir"/cost_and_value/extract_grid_all_number_of_preemptions_cis.R"
	LineplotGridAllNumberOfPreemptionsR=$rScriptsDir"/plots/grid_all_number_of_preemptions.R"

	ExtractGridAndCloudAllMakespanR=$rScriptsDir"/cost_and_value/extract_grid_and_cloud_all_makespan_v2.R"
	ExtractGridAndCloudAllMakespanCisR=$rScriptsDir"/cost_and_value/extract_grid_and_cloud_all_makespan_cis.R"
	
	CalculateGridValueR=$rScriptsDir"/cost_and_value/calculate_grid_value.R"
	CalculateGridCostR=$rScriptsDir"/cost_and_value/calculate_grid_cost.R"

# Scripts Shell
	ConcatenateAllSpotResultsSh=$shellScriptsDir"/concatenate_all_spot_results.sh"
	ConcatenateAllGridResultsSh=$shellScriptsDir"/concatenate_all_grid_results.sh"

# Execuções
	#### Filtrar os traces para os resultados com o sistema em regime
	#echo "Filtrar os traces para os resultados com o sistema em regime"
	#$R "none.txt" < $SteadyStateFilterR && \
	#\
	#### Gera o gráfico da métrica D
	echo "Gerar os arquivos dos datasets da métrica D por peer" && \
	mkdir -p $dsDir && \
	$R $dsDir < $GenerateDsDMetricR && \
	echo "Gerar o arquivo do dataset da métrica D por peer para todas as rodadas" && \
	echo "peer metric njobs ntasks nPeers nMachines instance type limit rodada" > $allTxt && \
	eval "cat $dsDir/npeers=*.txt " >> $allTxt && \
	echo "Gerar o dataset da métrica D para todas as rodadas" && \
	$R $allTxt $allMeansTxt < $GenerateDsDMetricMeansR && \
	echo "Gerar o dataset da métrica D com ICS" && \
	$R $allMeansTxt $cisAllTxt < $GenerateDsDMetricCisR && \
	#echo "Plotar o gráfico da métrica D - v1" && \
	#$R $cisAllTxt  1 $gridVsSpotPng1 < $XYplotCloudVsGridDmetricR && \
	echo "Plotar o gráfico da métrica D - v2" && \
	$R $cisAllTxt -1 $gridVsSpotPng2 < $XYplotCloudVsGridDmetricR && \
	\
#	#### Gera o gráfico com efeitos potenciais das preempções
#	echo "Extrair todas as preempções na grade" && \
#	$R $extractGridAllNumberOfPreemptionsTxt < $ExtractGridAllNumberOfPreemptionsR  && \
#	echo "Extrair todas as preempções na grade CIS" && \
#	$R $extractGridAllNumberOfPreemptionsTxt $extractGridAllNumberOfPreemptionsCisTxt < $ExtractGridAllNumberOfPreemptionsCisR  && \
#	echo "Plotar o gráfico com os efeitos potenciais das preempções" && \
#	$R $extractGridAllNumberOfPreemptionsCisTxt $LineplotGridAllNumberOfPreemptionsPng < $LineplotGridAllNumberOfPreemptionsR  && \
#	\
	#### Gera o gráfico com a comparação de custo e desempenho da execução na nuvem
	echo "Concatenar todos os resultados da nuvem" && \
	sh $ConcatenateAllSpotResultsSh $cloudResultsDir $cloudLimit $allSpotResultsTxt && \
	echo "Gerar o dataset cloud-makespan-and-cost-cis" && \
	$R $allSpotResultsTxt $cisCloudCostMakespanTxt < $GenerateDsCloudCostAndMakespanCisR && \
	echo "Plotar o gráfico de barras cloud-makespan-and-cost-cis" && \
	$R $cisCloudCostMakespanTxt $barplotCloudCostAndMakespanPng < $BarplotCloudCostAndMakespanR && \
	echo "Plotar o gráfico de bolhas cloud-makespan-and-cost-cis" && \
	$R $cisCloudCostMakespanTxt $bubbleplotCloudCostAndMakespanPng < $BubbleplotCloudCostAndMakespanR && \
	echo "Plotar o gráfico de linhas cloud-makespan-and-cost-cis" && \
	$R $cisCloudCostMakespanTxt $lineplotCloudCostAndMakespanPng < $LineplotCloudCostAndMakespanR && \
	\
	echo "Concatenar todos os resultados da grade" && \
	sh $ConcatenateAllGridResultsSh $gridResultsDir $allGridResultsTxt && \
	echo "Extrair GridAndCloud all makespans" && \
	#$R  $gridAndCloudAllMakespanTxt < $ExtractGridAndCloudAllMakespanR && \
	$R $allGridResultsTxt $allSpotResultsTxt $gridAndCloudAllMakespanTxt < $ExtractGridAndCloudAllMakespanR && \
	echo "Extrair Grid and Cloud all makespans CIS" && \
	$R $gridAndCloudAllMakespanTxt $gridAndCloudAllMakespanCisTxt < $ExtractGridAndCloudAllMakespanCisR && \
	echo "Gerar o dataset grid utilization CIS" && \
	$R $allGridResultsTxt $cisGridUtilizationTxt < $GenerateDsGridUtilizationCisR && \
	echo "Calcular o valor da Grade" && \
	$R $gridAndCloudAllMakespanCisTxt $cisGridUtilizationTxt $gridValueTxt < $CalculateGridValueR && \
	echo "Calcular o custo da Grade" && \
	$R $gridValueTxt $finalTableTxt $finalTable1Txt $finalTable2Txt $finalTable3Txt $lineplotVPHMPng < $CalculateGridCostR
