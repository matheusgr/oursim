/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package br.edu.ufcg.lsd.oursim.ui;

import static br.edu.ufcg.lsd.oursim.ui.CLIUTil.showMessageAndExit;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import br.edu.ufcg.lsd.oursim.entities.Machine;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.entities.Processor;
import br.edu.ufcg.lsd.oursim.policy.ResourceSharingPolicy;

public class SystemConfigurationCommandParser {

	public static Map<String, Peer> readPeersDescription(File siteDescription, int numberOfResourcesByPeer, ResourceSharingPolicy sharingPolicy)
			throws FileNotFoundException {
		Map<String, Peer> peers = new HashMap<String, Peer>();
		Scanner sc = new Scanner(siteDescription);
		sc.nextLine(); // skip header
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			String peerName = scLine.next();
			int peerSize = scLine.nextInt();
			if (!peers.containsKey(peerName)) {
				Peer peer = (numberOfResourcesByPeer > 0) ? new Peer(peerName, numberOfResourcesByPeer, sharingPolicy) : new Peer(peerName, sharingPolicy);
				peers.put(peer.getName(), peer);
				if (numberOfResourcesByPeer == 0) {
					for (int i = 0; i < peerSize; i++) {
						String machineFullName = peer.getName() + ".m_" + i;
						peer.addMachine(new Machine(machineFullName, Processor.EC2_COMPUTE_UNIT.getSpeed()));
					}
				}
				peers.put(peer.getName(), peer);
			} else {
				showMessageAndExit("Já foi adicionado um peer com esse nome: " + peerName);
			}
		}
		return peers;
	}

	@Deprecated
	public static void addResourcesToPeers(Map<String, Peer> peersMap, String peersDescriptionFilePath) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(peersDescriptionFilePath));
		sc.nextLine();// desconsidera o cabeçalho
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			String peerName = scLine.next();
			if (peersMap.containsKey(peerName)) {
				int peerSize = scLine.nextInt();
				Peer peer = peersMap.get(peerName);
				for (int i = 0; i < peerSize; i++) {
					String machineFullName = peer.getName() + "_m_" + i;
					peer.addMachine(new Machine(machineFullName, Processor.EC2_COMPUTE_UNIT.getSpeed()));
				}
			}
		}
	}

}
