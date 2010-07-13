package br.edu.ufcg.lsd.oursim.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.policy.ResourceSharingPolicy;

public final class GWAFormat {

	public final static Job createJobFromGWAFormat(String line, Map<String, Peer> peers) {
		return createJobFromGWAFormat(line, peers, 0);
	}

	public final static Job createJobFromGWAFormat(String line, Map<String, Peer> peers, long startingTime) {
		Scanner scLine = new Scanner(line);
		long JobID = scLine.nextLong();
		long SubmitTime = scLine.nextLong() - startingTime;
		long WaitTime = scLine.nextLong();
		long RunTime = scLine.nextLong();
		long NProc = scLine.nextLong();
		long AverageCPUTimeUsed = scLine.nextLong();
		String UsedMemory = scLine.next();
		long ReqNProcs = scLine.nextLong();
		long ReqTime = scLine.nextLong();
		long ReqMemory = scLine.nextLong();
		long Status = scLine.nextLong();
		String UserID = scLine.next();
		String GroupID = scLine.next();
		long ExecutableID = scLine.nextLong();
		String QueueID = scLine.next();
		long PartitionID = scLine.nextLong();
		String OrigSiteID = scLine.next();
		String LastRunSiteID = scLine.next();
		long UNKNOW = scLine.nextLong();
		long JobStructure = scLine.nextLong();
		long JobStructureParams = scLine.nextLong();
		long UsedNetwork = scLine.nextLong();
		long UsedLocalDiskSpace = scLine.nextLong();
		long UsedResources = scLine.nextLong();
		long ReqPlatform = scLine.nextLong();
		long ReqNetwork = scLine.nextLong();
		long RequestedLocalDiskSpace = scLine.nextLong();
		long RequestedResources = scLine.nextLong();
		long VirtualOrganizationID = scLine.nextLong();
		long ProjectID = scLine.nextLong();
		return new Job(JobID, SubmitTime, RunTime, peers.get(OrigSiteID));
	}

	public final static Map<String, Peer> extractPeersFromGWAFile(String workloadFilePath, int numberOfResourcesByPeer, ResourceSharingPolicy sharingPolicy)
			throws FileNotFoundException {
		Map<String, Peer> peers = new HashMap<String, Peer>();

		Scanner sc = new Scanner(new File(workloadFilePath));
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			// skip the 16 firsts tokens. The site's
			// token is the 17th in the gwa format.
			for (int i = 0; i < 16; i++) {
				scLine.next();
			}
			String OrigSiteID = scLine.next();

			if (!peers.containsKey(OrigSiteID)) {
				Peer peer = (numberOfResourcesByPeer > 0) ? new Peer(OrigSiteID, numberOfResourcesByPeer, sharingPolicy) : new Peer(OrigSiteID, sharingPolicy);
				peers.put(peer.getName(), peer);
			}
		}

		return peers;

	}

}
