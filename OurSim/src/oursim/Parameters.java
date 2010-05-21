package oursim;

import java.util.Random;

public class Parameters {

	public static final boolean USE_NOF = false;
	
	public static final boolean DEDICATED_RESOURCES = true;
	
	public static final String AVAILABILITY_CHARACTERIZATION_FILE_PATH = "trace_mutka_100-machines_10-hours.txt";

	public static final boolean LOG = false;

	public static Random RANDOM = new Random(9354269l);

	private static String argsString =
	// execTime execTimeVar subInterval   #Jobs  #TasksByJob #Peers #nodesByPeer nodeMIPSRating
	"       100         50            5    1000           50     10 		  10           3000";

	// 100000

	private static String[] args = argsString.trim().split("\\s+");

	private static int i = 0;

	// tempo base de execução do job
	public static int EXEC_TIME = Integer.parseInt(args[i++]);

	// variância máxima do tempo base de execução (sempre positiva)
	public static int EXEC_TIME_VAR = Integer.parseInt(args[i++]);

	// intervalo de submissão entre jobs subsequentes
	public static int SUBMISSION_INTERVAL = Integer.parseInt(args[i++]);

	// quantidade total de jobs do workload
	public static int NUM_JOBS = Integer.parseInt(args[i++]);

	public static int NUM_TASKS_BY_JOB = Integer.parseInt(args[i++]);

	public static int NUM_PEERS = Integer.parseInt(args[i++]);

	// número de nodos do peer
	public static int NUM_RESOURCES_BY_PEER = Integer.parseInt(args[i++]);

	public static int NODE_MIPS_RATING = Integer.parseInt(args[i++]);

}
