export VALOR_GRADES_P2P_HOME=/local/$USER

sudo mkdir -p $VALOR_GRADES_P2P_HOME
sudo chown -R $USER. $VALOR_GRADES_P2P_HOME

export WORKLOAD_DIR=$VALOR_GRADES_P2P_HOME/workloads
mkdir $WORKLOAD_DIR

export CAPACITY_DIR=$VALOR_GRADES_P2P_HOME/machines
mkdir $CAPACITY_DIR

export OUTPUT_DIR=$VALOR_GRADES_P2P_HOME/outputs
mkdir $OUTPUT_DIR

export SPOTPRICES_DIR=$VALOR_GRADES_P2P_HOME/outputs


cd $VALOR_GRADES_P2P_HOME
wget http://redmine.lsd.ufcg.edu.br/attachments/download/1185/OurSim.zip
wget http://redmine.lsd.ufcg.edu.br/attachments/download/1186/SpotInstancesSimulator.zip
wget http://redmine.lsd.ufcg.edu.br/attachments/download/1184/scripts.zip
wget http://redmine.lsd.ufcg.edu.br/attachments/download/1183/readme.txt

unzip OurSim.zip 
unzip SpotInstancesSimulator.zip 
unzip scripts.zip 



Estimativa do valor de grades entre-pares

As simulações conduzidas no trabalho de mestrado seguiram o fluxo de trabalho ilustrado na figura abaixo.

Antes de partir para a execução propriamente dita, é preciso definir a demanda e a oferta dos sistemas simulados.
Por demanda, deve-se entender a carga de trabalho (workload) que será submetida à grade.
Já a oferta é a quantidade de recursos disponíveis na grade, bem como a quantidade de peers que compõem a grade e capacidade computacional dos recursos.
Além disto, por se tratar de uma grade oportunista, também é necessário caracterizar a volatilidade dos recursos disponibilizados para a grade.

Por questão de simplicidade, considerou-se nas simulações que os peers possuem uma mesma quantidade de recursos (30 máquinas cada um).
Já para a capacidade computacional de cada máquina, foi considerada uma distribuição normal com média de 2.5 GHz com desvio padrão de 0.33.

Parâmetros de Simulação

Os parâmetros utilizados na simulação se encontram no arquivo args.properties.
Este arquivo será lido por todos os scripts que precisarem das variáveis aí definidas.

Ferramental

Para a caracterização da oferta e da demanda, bem como para a síntese e a análise dos resultados, foram utilizados scripts shell e R.
Já o simulador foi implementado na linguagem Java, sendo necessário uma jvm 1.6 ou superior.
Para gerar um pacote para execução, se faz necessário a ferramenta ant, para automatização de tarefas.

Dependências
	Para instalar as dependências em R, estando devidamente conectado à Internet, deve-se executar o seguinte comando: 
	
		time R --slave --no-save --no-restore --no-environ --silent < $VALOR_GRADES_P2P_HOME/r_scripts/install_dependencies.R

Reprodução dos resultados

Se o objetivo for apenas reproduzir as simulações que serviram como base para a dissertação, basta utilizar a caracterização de oferta e demanda disponibilizados neste pacote.
Caso se queira alterar a oferta e demanda, podem ser utilizados os scripts para a geração de novos arquivos caracterizando oferta e demanda.
Para tanto, basta executar o fluxo abaixo.

0. Definir os parâmetors dos experimentos

	Exportar uma variável de ambient $VALOR_GRADES_P2P_HOME apontando para o diretório com os arquivos do projeto
		export VALOR_GRADES_P2P_HOME=/local/$USER
	Editar o arquivo 
		$VALOR_GRADES_P2P_HOME/args.properties
	E definir as variáveis 
		numOfPeers
			Quantos peers compõem a grade.
		numOfMachinesByPeer
			O número de máquinas disponibilizadas por peer.
		rodadas
			O número de repetições do experimento para a análise estatística dos resultados.

1. Gerar workload

	Para gerar a carga de trabalho considerando os parâmetros definidos anteriormente, basta executar o comando seguinte:

		time R --slave --no-save --no-restore --no-environ --silent --args $WORKLOAD_DIR < $VALOR_GRADES_P2P_HOME/r_scripts/generators/workload/generate_new_marcus_workload_cli_all.R

		Para o qual:

			$WORKLOAD_DIR é o diretório em que serão gerados os arquivos com a caracterização do workload.

	Caso se queira passar os parâmetros diretamente, basta executar:

		time R --slave --no-save --no-restore --no-environ --silent --args $numOfPeers $upp, $nDias, $rodada $WORKLOAD_DIR < $VALOR_GRADES_P2P_HOME/r_scripts/generators/workload/generate_new_marcus_workload_cli.R

		Para o qual:
		
			$numOfPeers
				Quantos peers que compõem a grade.
			$upp 
				O número de usuários por peer. 
			$nDias
				O número de dias referentes ao workload.
			$rodada
				O identificador da repetição do experimento.

2. Gerar Oferta de Recursos

	Para gerar novos arquivos de descrição das máquinas de cada peer, basta executar o comando seguinte:

		time R --slave --no-save --no-restore --no-environ --silent --args $CAPACITY_DIR r_scripts/generators/grid_capacity/machines_ourgrid/machines_ourgrid_speed.txt < $VALOR_GRADES_P2P_HOME/r_scripts/generators/grid_capacity/generate_peers_machines_description_cli.R

		Para o qual $CAPACITY_DIR é o diretório em que serão gerados os arquivos com a caracterização das máquinas de cada peer.

3. Criar Pacote para Execução

	Para criar o pacote de execução, há o script build_oursim.sh, que recebe como parâmetro a indicação dos cenários a serem executados. 

	Por exemplo, o comando abaixo solicita a geração de um pacote que inclui os cenários com 10, 50 e 100 peers, para as rodadas de 11 a 15 e apenas com a execução da grade.
	
		cd $VALOR_GRADES_P2P_HOME/OurSim
		sh build_oursim.sh "50 1:10 oursim"
		
	Já o comando a seguir faz o mesmo do anterior só que para a simulação da nuvem de instâncias.

		sh build_oursim.sh "50 1:10 spotsim"

	O script build_oursim.sh obedece à seguinte sintaxe:

		sh build_oursim.sh "${nPeers} {rodadaInicio}[:{rodadaFim}] ( oursim | spotsim [instancesTypes*] )"

	Como resultado da execução do script build_oursim.sh, será gerado um pacote .zip no diretório indicado a seguir, referente ao cenário solicitado.

		$VALOR_GRADES_P2P_HOME/OurSim/rodadas

4. Executar

	O pacote gerado no passo 3 oferece algumas opções de execução. 
	Uma delas é o script cmd.txt, que executa sequencialmente os diversos cenários.
	
		time sh cmd.txt
		
	Para executar em paralelo, numa máquina com vários núcleos, por exemplo, pode-se usar os parâmetros gerados no arquivo args.txt e passá-lo para um
	gerenciador de execução xargs. Use-o da sequinte forma, para as execuções da grade:

		cat args.txt | xargs -n25 -t -P0 java -Xms500M -Xmx1500M -XX:-UseGCOverheadLimit -jar oursim.jar

	Ou da forma abaixo, para execução da nuvem de instâncias.

		cat args.txt | xargs -n17 -t -P0 java -Xms500M -Xmx1500M -XX:-UseGCOverheadLimit -jar spotsim.jar

5. Verificar se existem os arquivos de resultados

	Quando os resultados tiverem sido gerados, pode-se utilizar o script utilitário check_files.R, que verifica se os arquivos esperados (cujos parâmetros foram definidos no arquivo args.properties) foram gerados.
	
		time R --slave --no-save --no-restore --no-environ --silent --args rerun_oursim.txt rerun_spotsim.txt rerun_essential.txt torun.txt < $VALOR_GRADES_P2P_HOME/r_scripts/check_files.R && cat torun.txt
		
	Os arquivos rerun_oursim.txt, rerun_spotsim.txt e rerun_essential.txt indicam os cenários que ainda precisam ser executados, enquanto torun.txt exibe os comandos para empacotar novas simulações para a nuvem de instancias spot para as quais já existe um resultado da execução da grade.

6. Analisar os Resultados

	Há um script shell que executa outros scripts para sumarizar e analisar os resultados.
	
	Antes de executá-lo, deve-se alterar as definições de variáveis nele contidas, que indicam os diretorios dos arquivos de entrada, que são os resultados das simulações, e o diretório que conterá os arquivos e gráficos resultantes da análise.
	
		time sh $VALOR_GRADES_P2P_HOME/shell_scripts/analisar_resultados.sh 
		
