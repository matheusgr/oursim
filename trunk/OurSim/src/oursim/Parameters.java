package oursim;

import java.util.Random;

public class Parameters {

    public static Random RANDOM = new Random(9354269l);

    // int execTime = 100;tempo base de execução do job
    //
    // int execTimeVar = 50;variância máxima do tempo base de execução (sempre
    // positiva)
    //
    // int submissionInterval = 1;intervalo de submissão entre jobs subsequentes
    //
    // int numJobs = 100000;quantidade total de jobs do workload
    //
    // int numberOfPeers = 100;
    //
    // peerNodeSize = 10;número de nodos do peer

    private static String argsString =
    // execTime execTimeVar subInterval numJobs numberOfPeers peerSize
    "       100         50 	      5  100000           10        10";

    public static String[] args = argsString.trim().split("\\s+");

}
