package oursim;

import java.util.Random;

public class Parameters {

    public static Random RANDOM = new Random(9354269l);

    // int execTime = 100;
    // int execTimeVar = 50;
    // int submissionInterval = 1;
    // int numJobs = 100000;
    // int numberOfPeers = 100;
    // peerNodeSize = 10;

    // execTime execTimeVar subInterval numJobs numberOfPeers peerSize
    private static String argsString = "        100         50 	          5   10000            10       10";

    public static String[] args = argsString.trim().split("\\s+");

}
