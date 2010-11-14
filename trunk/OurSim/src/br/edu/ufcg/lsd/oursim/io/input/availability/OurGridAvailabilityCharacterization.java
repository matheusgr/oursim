package br.edu.ufcg.lsd.oursim.io.input.availability;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import umontreal.iro.lecuyer.randvar.LognormalGen;
import umontreal.iro.lecuyer.randvar.RandomVariateGen;
import umontreal.iro.lecuyer.rng.MRG31k3p;
import br.edu.ufcg.lsd.oursim.entities.Machine;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.io.input.Input;
import br.edu.ufcg.lsd.oursim.util.TimeUtil;

public class OurGridAvailabilityCharacterization implements Input<AvailabilityRecord> {

	private long startingTime;
	private long quantDeSegundos;

	private Map<Machine, Long> machine2Time;
	private Map<String, Machine> machines;
	private Map<Machine, RandomVariateGen> machine2AvRandomVariate;
	private Map<Machine, RandomVariateGen> machine2NaRandomVariate;

	private AvailabilityRecord nextAV;
	private Queue<AvailabilityRecord> nextAvailabilityRecords;
	private boolean shouldStop = false;

	private double[] wt = new double[] { 0.004181871, 0.003738143, 0.003333431, 0.002965424, 0.002631788, 0.002330196, 0.002058353, 0.001814015, 0.001595011,
			0.001399249, 0.030019328, 0.031149866, 0.032020006, 0.032641937, 0.033028974, 0.033195470, 0.033156675, 0.032928558, 0.032527618, 0.031970677,
			0.031274689, 0.030456538, 0.029532865, 0.028519897, 0.027433302, 0.026288055, 0.025098330, 0.023877415, 0.022637632, 0.021390295, 0.020145678,
			0.018912992, 0.017700397, 0.016515008, 0.015362928, 0.014249285, 0.013178279, 0.012153237, 0.011176673, 0.010250354, 0.009375364, 0.008552175,
			0.007780713, 0.007060428, 0.006390354, 0.005769178, 0.005195297, 0.004666869, 0.004181871 };

	private double wm = 0.01681250;

	public OurGridAvailabilityCharacterization(Map<String, Peer> peers, long quantDeSegundos, long startingTime) throws FileNotFoundException {
		assert quantDeSegundos > 0;
		assert startingTime >= 0;
		this.startingTime = startingTime;
		this.quantDeSegundos = quantDeSegundos;
		this.nextAvailabilityRecords = new PriorityQueue<AvailabilityRecord>();
		this.machine2Time = new HashMap<Machine, Long>();
		this.machine2AvRandomVariate = new HashMap<Machine, RandomVariateGen>();
		this.machine2NaRandomVariate = new HashMap<Machine, RandomVariateGen>();
		this.machine2Time = new HashMap<Machine, Long>();
		this.machines = new HashMap<String, Machine>();
		for (Peer peer : peers.values()) {
			for (Machine machine : peer.getMachines()) {
				assert !this.machines.containsKey(machine);
				this.machines.put(machine.getName(), machine);
				this.machine2AvRandomVariate.put(machine, new LognormalGen(new MRG31k3p(), 7.957307, 2.116613));
				this.machine2NaRandomVariate.put(machine, new LognormalGen(new MRG31k3p(), 7.242198, 1.034311));
				long naDuration = Math.round(this.machine2NaRandomVariate.get(machine).nextDouble());
				this.machine2Time.put(machine, this.startingTime + naDuration);
			}
		}
	}

	@Override
	public void close() {
	}

	@Override
	public AvailabilityRecord peek() {
		if (!shouldStop) {
			if (this.nextAV == null && !this.nextAvailabilityRecords.isEmpty()) {
				this.nextAV = nextAvailabilityRecords.poll();
			} else if (this.nextAvailabilityRecords.isEmpty()) {
				for (Machine machine : machine2Time.keySet()) {
					generateAvailabilityForNextInvocations(machine);
				}
				this.nextAV = nextAvailabilityRecords.poll();
			}
		}

		if (shouldStop || (this.nextAV != null && this.nextAV.getTime() > this.quantDeSegundos)) {
			this.nextAvailabilityRecords.clear();
			this.nextAV = null;
			this.shouldStop = true;
		}

		return this.nextAV;
	}

	@Override
	public AvailabilityRecord poll() {
		AvailabilityRecord polledAV = this.peek();
		this.nextAV = null;

		if (polledAV != null) {
			generateAvailabilityForNextInvocations(machines.get(polledAV.getMachineName()));
		}

		try {
			if (bw != null && polledAV != null) {
				bw.append(polledAV.getTime() + ":AV:" + polledAV.getMachineName() + ":" + polledAV.getDuration()).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return polledAV;
	}

	private void generateAvailabilityForNextInvocations(Machine machine) {
		Long avDuration;
		Long naDuration;

		long time = machine2Time.get(machine);
		int slot = ((int) time % 86400) / 1800;

		// a duração do período de disponibilidade
		do {
			avDuration = Math.round(this.machine2AvRandomVariate.get(machine).nextDouble());
		} while (avDuration == 0);
		avDuration = Math.min(TimeUtil.ONE_WEEK, avDuration);
		avDuration = Math.round(avDuration / (wt[slot] / wm));

		AvailabilityRecord availabilityRecord = new AvailabilityRecord(machine.getName(), time, avDuration);
		this.nextAvailabilityRecords.add(availabilityRecord);

		// agora a duração do período de INdisponibilidade
		do {
			naDuration = Math.round(this.machine2NaRandomVariate.get(machine).nextDouble());
		} while (naDuration == 0);
		this.machine2Time.put(machine, time + avDuration + naDuration);
	}

	public void stop() {
		this.shouldStop = true;
	}

	private BufferedWriter bw = null;

	public void setBuffer(BufferedWriter utilizationBuffer) {
		this.bw = utilizationBuffer;
		try {
			this.bw.append("time:event:machine:duration\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}