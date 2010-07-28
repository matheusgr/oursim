package br.edu.ufcg.lsd.oursim.spotinstances;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Map.Entry;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventListener;
import br.edu.ufcg.lsd.oursim.dispatchableevents.spotinstances.SpotPriceEventListener;
import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventListener;
import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Machine;
import br.edu.ufcg.lsd.oursim.entities.Processor;
import br.edu.ufcg.lsd.oursim.entities.Task;
import br.edu.ufcg.lsd.oursim.io.input.spotinstances.SpotPriceFluctuation;
import br.edu.ufcg.lsd.oursim.simulationevents.ActiveEntityAbstract;
import br.edu.ufcg.lsd.oursim.util.BidirectionalMap;
import br.edu.ufcg.lsd.oursim.util.SpotInstaceTraceFormat;

public class SpotInstancesSimulator extends ActiveEntityAbstract implements JobEventListener, TaskEventListener, SpotPriceEventListener {

	// private Map<BidValue, Machine> allocatedMachines;
	private BidirectionalMap<BidValue, Machine> allocatedMachines;

	// private Map<Machine, Task> machine2Task;
	private BidirectionalMap<Machine, Task> machine2Task;

	private SpotPrice currentSpotPrice;

	public static void main(String[] args) throws FileNotFoundException, ParseException {
		String spotTraceFilePath = "/home/edigley/local/traces/spot_instances/spot-instance-prices/eu-west-1.linux.m1.small.csv";
		Scanner sc = new Scanner(new File(spotTraceFilePath));
		SpotPrice firestSpotPriceRecord = SpotInstaceTraceFormat.createSpotPriceFromSpotTraceRecord(sc.nextLine(), 0);
		long startingTime = firestSpotPriceRecord.getSimulationTime();
		SpotPriceFluctuation spotTrace = new SpotPriceFluctuation(spotTraceFilePath, startingTime);

		while (spotTrace.peek() != null) {
			System.out.println(spotTrace.poll());
		}
	}

	@Override
	public void newSpotPrice(Event<SpotPrice> spotPriceEvent) {
		SpotPrice newSpotPrice = spotPriceEvent.getSource();
		Iterator<Entry<BidValue, Machine>> iterator = allocatedMachines.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<BidValue, Machine> entry = iterator.next();
			if (entry.getKey().getValue() < newSpotPrice.getPrice()) {
				Machine machine = entry.getValue();
				Task task = machine2Task.remove(machine);
				addPreemptedTaskEvent(getCurrentTime(), task);
			}
			iterator.remove();
		}
		this.currentSpotPrice = newSpotPrice;
	}

	@Override
	public void taskFinished(Event<Task> taskEvent) {
		Machine machine = this.machine2Task.getKey(taskEvent.getSource());
		this.machine2Task.remove(machine);
		BidValue bidValue = this.allocatedMachines.getKey(machine);
		this.allocatedMachines.remove(bidValue);
	}

	private long nextMachineId = 0;

	@Override
	public void taskSubmitted(Event<Task> taskEvent) {
		Task task = taskEvent.getSource();
		if (task.getBidValue() >= currentSpotPrice.getPrice()) {
			Machine newMachine = new Machine("m_" + nextMachineId++, Processor.EC2_COMPUTE_UNIT.getSpeed());
			this.machine2Task.put(newMachine, task);
			BidValue bidValue = new BidValue(null, getCurrentTime(), task.getBidValue());
			this.allocatedMachines.put(bidValue, newMachine);
			addStartedTaskEvent(task);
		}
	}

	@Override
	public void taskCancelled(Event<Task> taskEvent) {
	}

	@Override
	public void taskPreempted(Event<Task> taskEvent) {
	}

	@Override
	public void taskStarted(Event<Task> taskEvent) {
	}

	@Override
	public void jobFinished(Event<Job> jobEvent) {
	}

	@Override
	public void jobPreempted(Event<Job> jobEvent) {
	}

	@Override
	public void jobStarted(Event<Job> jobEvent) {
	}

	@Override
	public void jobSubmitted(Event<Job> jobEvent) {
	}

}
