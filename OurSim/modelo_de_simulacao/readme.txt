Para rodar as simulações referentes à grade, basta executar:

	sh run_oursim.sh

Já para as simulações da nuvem:

	sh run_spotsim.sh

Os scripts se valem dos argumentos referentes a cada modelo, respectivamente, oursim_args.txt e spotsim_args.txt.
Os argumentos dizem respeito aos arquivos de entrada, presentes no diretório input-files/, arquivos de saída, que serão gerados no diretório corrente, e também às opções passadas diretamente na linha de comando.

Cada arquivo, seja oursim_args.txt ou spotsim_args.txt, são passados para o agente de execução xargs, com nível de paralelismo igual a 2. 
Caso se queira um nível maior de paralelismo, basta alterar a opção -P, presente na chamada ao xargs nos arquivos run_oursim.sh e run_spotsim.sh.
