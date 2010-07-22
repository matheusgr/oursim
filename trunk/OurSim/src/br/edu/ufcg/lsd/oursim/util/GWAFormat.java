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

	public final static GWAJobDescription createGWAJobDescription(String line) {
		GWAJobDescription gwaJob = new GWAJobDescription();
		Scanner scLine = new Scanner(line);
		gwaJob.JobID = scLine.nextLong();
		gwaJob.SubmitTime = scLine.nextLong();
		gwaJob.WaitTime = scLine.nextLong();
		gwaJob.RunTime = scLine.nextLong();
		gwaJob.NProc = scLine.nextLong();
		gwaJob.AverageCPUTimeUsed = scLine.nextLong();
		gwaJob.UsedMemory = scLine.next();
		gwaJob.ReqNProcs = scLine.nextLong();
		gwaJob.ReqTime = scLine.nextLong();
		gwaJob.ReqMemory = scLine.nextLong();
		gwaJob.Status = scLine.nextLong();
		gwaJob.UserID = scLine.next();
		gwaJob.GroupID = scLine.next();
		gwaJob.ExecutableID = scLine.nextLong();
		gwaJob.QueueID = scLine.next();
		gwaJob.PartitionID = scLine.nextLong();
		gwaJob.OrigSiteID = scLine.next();
		gwaJob.LastRunSiteID = scLine.next();
		gwaJob.UNKNOW = scLine.nextLong();
		gwaJob.JobStructure = scLine.nextLong();
		gwaJob.JobStructureParams = scLine.nextLong();
		gwaJob.UsedNetwork = scLine.nextLong();
		gwaJob.UsedLocalDiskSpace = scLine.nextLong();
		gwaJob.UsedResources = scLine.nextLong();
		gwaJob.ReqPlatform = scLine.nextLong();
		gwaJob.ReqNetwork = scLine.nextLong();
		gwaJob.RequestedLocalDiskSpace = scLine.nextLong();
		gwaJob.RequestedResources = scLine.nextLong();
		gwaJob.VirtualOrganizationID = scLine.nextLong();
		gwaJob.ProjectID = scLine.nextLong();
		return gwaJob;

	}

	public final static Job createJobFromGWAFormat(String line, Map<String, Peer> peers) {
		return createJobFromGWAFormat(line, peers, 0);
	}

	public final static Job createJobFromGWAFormat(String line, Map<String, Peer> peers, long startingTime) {
		GWAJobDescription gwaJob = createGWAJobDescription(line);
		return new Job(gwaJob.JobID, gwaJob.SubmitTime - startingTime, gwaJob.RunTime, peers.get(gwaJob.OrigSiteID));
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

	public static final long extractSubmissionTimeFromFirstJob(String workloadFilePath) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(workloadFilePath));
		String firstLine = sc.nextLine();
		GWAJobDescription gwajob = GWAFormat.createGWAJobDescription(firstLine);
		sc.close();
		return gwajob.SubmitTime;
	}

}