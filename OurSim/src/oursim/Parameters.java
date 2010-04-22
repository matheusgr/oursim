package oursim;

import java.util.Random;

public class Parameters {

	public static Random RANDOM = new Random(9354269l);

	private static String argsString =
	// execTime execTimeVar subInterval numJobs numberOfPeers peerSize
	"       100         50 	      5  100000           10        10";

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

	public static int NUM_PEERS = Integer.parseInt(args[i++]);

	// número de nodos do peer
	public static int PEER_SIZE = Integer.parseInt(args[i++]);

}
