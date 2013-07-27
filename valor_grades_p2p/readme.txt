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

As simula��es conduzidas no trabalho de mestrado seguiram o fluxo de trabalho ilustrado na figura abaixo.

Antes de partir para a execu��o propriamente dita, � preciso definir a demanda e a oferta dos sistemas simulados.
Por demanda, deve-se entender a carga de trabalho (workload) que ser� submetida � grade.
J� a oferta � a quantidade de recursos dispon�veis na grade, bem como a quantidade de peers que comp�em a grade e capacidade computacional dos recursos.
Al�m disto, por se tratar de uma grade oportunista, tamb�m � necess�rio caracterizar a volatilidade dos recursos disponibilizados para a grade.

Por quest�o de simplicidade, considerou-se nas simula��es que os peers possuem uma mesma quantidade de recursos (30 m�quinas cada um).
J� para a capacidade computacional de cada m�quina, foi considerada uma distribui��o normal com m�dia de 2.5 GHz com desvio padr�o de 0.33.

Par�metros de Simula��o

Os par�metros utilizados na simula��o se encontram no arquivo args.properties.
Este arquivo ser� lido por todos os scripts que precisarem das vari�veis a� definidas.

Ferramental

Para a caracteriza��o da oferta e da demanda, bem como para a s�ntese e a an�lise dos resultados, foram utilizados scripts shell e R.
J� o simulador foi implementado na linguagem Java, sendo necess�rio uma jvm 1.6 ou superior.
Para gerar um pacote para execu��o, se faz necess�rio a ferramenta ant, para automatiza��o de tarefas.

Depend�ncias
	Para instalar as depend�ncias em R, estando devidamente conectado � Internet, deve-se executar o seguinte comando: 
	
		time R --slave --no-save --no-restore --no-environ --silent < $VALOR_GRADES_P2P_HOME/r_scripts/install_dependencies.R

Reprodu��o dos resultados

Se o objetivo for apenas reproduzir as simula��es que serviram como base para a disserta��o, basta utilizar a caracteriza��o de oferta e demanda disponibilizados neste pacote.
Caso se queira alterar a oferta e demanda, podem ser utilizados os scripts para a gera��o de novos arquivos caracterizando oferta e demanda.
Para tanto, basta executar o fluxo abaixo.

0. Definir os par�metors dos experimentos

	Exportar uma vari�vel de ambient $VALOR_GRADES_P2P_HOME apontando para o diret�rio com os arquivos do projeto
		export VALOR_GRADES_P2P_HOME=/local/$USER
	Editar o arquivo 
		$VALOR_GRADES_P2P_HOME/args.properties
	E definir as vari�veis 
		numOfPeers
			Quantos peers comp�em a grade.
		numOfMachinesByPeer
			O n�mero de m�quinas disponibilizadas por peer.
		rodadas
			O n�mero de repeti��es do experimento para a an�lise estat�stica dos resultados.

1. Gerar workload

	Para gerar a carga de trabalho considerando os par�metros definidos anteriormente, basta executar o comando seguinte:

		time R --slave --no-save --no-restore --no-environ --silent --args $WORKLOAD_DIR < $VALOR_GRADES_P2P_HOME/r_scripts/generators/workload/generate_new_marcus_workload_cli_all.R

		Para o qual:

			$WORKLOAD_DIR � o diret�rio em que ser�o gerados os arquivos com a caracteriza��o do workload.

	Caso se queira passar os par�metros diretamente, basta executar:

		time R --slave --no-save --no-restore --no-environ --silent --args $numOfPeers $upp, $nDias, $rodada $WORKLOAD_DIR < $VALOR_GRADES_P2P_HOME/r_scripts/generators/workload/generate_new_marcus_workload_cli.R

		Para o qual:
		
			$numOfPeers
				Quantos peers que comp�em a grade.
			$upp 
				O n�mero de usu�rios por peer. 
			$nDias
				O n�mero de dias referentes ao workload.
			$rodada
				O identificador da repeti��o do experimento.

2. Gerar Oferta de Recursos

	Para gerar novos arquivos de descri��o das m�quinas de cada peer, basta executar o comando seguinte:

		time R --slave --no-save --no-restore --no-environ --silent --args $CAPACITY_DIR r_scripts/generators/grid_capacity/machines_ourgrid/machines_ourgrid_speed.txt < $VALOR_GRADES_P2P_HOME/r_scripts/generators/grid_capacity/generate_peers_machines_description_cli.R

		Para o qual $CAPACITY_DIR � o diret�rio em que ser�o gerados os arquivos com a caracteriza��o das m�quinas de cada peer.

3. Criar Pacote para Execu��o

	Para criar o pacote de execu��o, h� o script build_oursim.sh, que recebe como par�metro a indica��o dos cen�rios a serem executados. 

	Por exemplo, o comando abaixo solicita a gera��o de um pacote que inclui os cen�rios com 10, 50 e 100 peers, para as rodadas de 11 a 15 e apenas com a execu��o da grade.
	
		cd $VALOR_GRADES_P2P_HOME/OurSim
		sh build_oursim.sh "50 1:10 oursim"
		
	J� o comando a seguir faz o mesmo do anterior s� que para a simula��o da nuvem de inst�ncias.

		sh build_oursim.sh "50 1:10 spotsim"

	O script build_oursim.sh obedece � seguinte sintaxe:

		sh build_oursim.sh "${nPeers} {rodadaInicio}[:{rodadaFim}] ( oursim | spotsim [instancesTypes*] )"

	Como resultado da execu��o do script build_oursim.sh, ser� gerado um pacote .zip no diret�rio indicado a seguir, referente ao cen�rio solicitado.

		$VALOR_GRADES_P2P_HOME/OurSim/rodadas

4. Executar

	O pacote gerado no passo 3 oferece algumas op��es de execu��o. 
	Uma delas � o script cmd.txt, que executa sequencialmente os diversos cen�rios.
	
		time sh cmd.txt
		
	Para executar em paralelo, numa m�quina com v�rios n�cleos, por exemplo, pode-se usar os par�metros gerados no arquivo args.txt e pass�-lo para um
	gerenciador de execu��o xargs. Use-o da sequinte forma, para as execu��es da grade:

		cat args.txt | xargs -n25 -t -P0 java -Xms500M -Xmx1500M -XX:-UseGCOverheadLimit -jar oursim.jar

	Ou da forma abaixo, para execu��o da nuvem de inst�ncias.

		cat args.txt | xargs -n17 -t -P0 java -Xms500M -Xmx1500M -XX:-UseGCOverheadLimit -jar spotsim.jar

5. Verificar se existem os arquivos de resultados

	Quando os resultados tiverem sido gerados, pode-se utilizar o script utilit�rio check_files.R, que verifica se os arquivos esperados (cujos par�metros foram definidos no arquivo args.properties) foram gerados.
	
		time R --slave --no-save --no-restore --no-environ --silent --args rerun_oursim.txt rerun_spotsim.txt rerun_essential.txt torun.txt < $VALOR_GRADES_P2P_HOME/r_scripts/check_files.R && cat torun.txt
		
	Os arquivos rerun_oursim.txt, rerun_spotsim.txt e rerun_essential.txt indicam os cen�rios que ainda precisam ser executados, enquanto torun.txt exibe os comandos para empacotar novas simula��es para a nuvem de instancias spot para as quais j� existe um resultado da execu��o da grade.

6. Analisar os Resultados

	H� um script shell que executa outros scripts para sumarizar e analisar os resultados.
	
	Antes de execut�-lo, deve-se alterar as defini��es de vari�veis nele contidas, que indicam os diretorios dos arquivos de entrada, que s�o os resultados das simula��es, e o diret�rio que conter� os arquivos e gr�ficos resultantes da an�lise.
	
		time sh $VALOR_GRADES_P2P_HOME/shell_scripts/analisar_resultados.sh 
		
