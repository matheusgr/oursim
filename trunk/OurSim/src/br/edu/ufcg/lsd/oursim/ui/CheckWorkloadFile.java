package br.edu.ufcg.lsd.oursim.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.io.input.workload.MarcusWorkload2;
import br.edu.ufcg.lsd.oursim.io.input.workload.Workload;
import br.edu.ufcg.lsd.oursim.policy.FifoSharingPolicy;
import br.edu.ufcg.lsd.oursim.util.AB;

public class CheckWorkloadFile {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		int[] nSitesV = new int[] { 50 }; //50, 40, 30, 20, 10 };
		
		int[] nResV   = new int[] { 50, 40, 30, 20, 10 };

		int[] rodadas = AB.cv(11,14);
		
		String workloadPattern = "/local/edigley/traces/oursim/workloads/marcus_workload_7_dias_%s_sites_%s.txt";

		Map<String, Peer> peers = new HashMap<String, Peer>();
		for (int i = 1; i <= 50; i++) {
			Peer peer = new Peer(i + "", FifoSharingPolicy.getInstance());
			peers.put(peer.getName(), peer);
		}
		
		MarcusWorkload2.check = true;
		
		for (int rodada : rodadas) {
			for (int nSites : nSitesV) {
				String workloadFile = String.format(workloadPattern, nSites, rodada);
				//String workloadFile = "/local/edigley/traces/oursim/workloads/marcus_workload_7_dias_50_sites_1.txt";
				
				Workload workload = new MarcusWorkload2(workloadFile, peers, Long.MAX_VALUE);
				
				while(workload.peek()!=null){
					workload.poll();
				}
				
				
				workload.close();
				
				workload = null;
				
				System.gc();
				
				System.out.println(nSites+" sites, rodada " + rodada + " OK");
			}
		}
					
		System.out.println("--------------- Finished!!! ----------------");
		
	}

}
