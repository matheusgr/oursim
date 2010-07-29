package br.edu.ufcg.lsd.oursim.policy;

import java.util.Iterator;
import java.util.Map.Entry;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.spotinstances.SpotPriceEventListener;
import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Machine;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.entities.Processor;
import br.edu.ufcg.lsd.oursim.entities.Task;
import br.edu.ufcg.lsd.oursim.entities.TaskExecution;
import br.edu.ufcg.lsd.oursim.io.input.spotinstances.BidValue;
import br.edu.ufcg.lsd.oursim.io.input.spotinstances.SpotPrice;
import br.edu.ufcg.lsd.oursim.io.input.workload.Workload;
import br.edu.ufcg.lsd.oursim.simulationevents.ActiveEntityAbstract;
import br.edu.ufcg.lsd.oursim.util.BidirectionalMap;

public class SpotInstancesScheduler extends ActiveEntityAbstract implements JobSchedulerPolicy, SpotPriceEventListener {

	private BidirectionalMap<BidValue, Machine> allocatedMachines;

	private BidirectionalMap<Machine, Task> machine2Task;

	private SpotPrice currentSpotPrice;

	private long nextMachineId = 0;

	private final Peer thePeer;

	public SpotInstancesScheduler(Peer thePeer, SpotPrice initialSpotPrice) {
		this.machine2Task = new BidirectionalMap<Machine, Task>();
		this.allocatedMachines = new BidirectionalMap<BidValue, Machine>();
		this.currentSpotPrice = initialSpotPrice;
		this.thePeer = thePeer;
	}

	@Override
	public void schedule() {
		Iterator<Entry<BidValue, Machine>> iterator = allocatedMachines.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<BidValue, Machine> entry = iterator.next();
			BidValue bid = entry.getKey();
			if (bid.getValue() < currentSpotPrice.getPrice()) {
				Machine machine = entry.getValue();
				Task task = machine2Task.remove(machine);
				iterator.remove();
				addPreemptedTaskEvent(getCurrentTime(), task);
			}
		}
	}

	@Override
	public void addJob(Job job) {
		for (Task task : job.getTasks()) {
			this.addSubmitTaskEvent(this.getCurrentTime(), task);
		}
	}

	@Override
	public void addWorkload(Workload workload) {
		throw new RuntimeException();
	}

	@Override
	public boolean isFinished() {
		throw new RuntimeException();
	}

	// B-- beginning of implementation of SpotPriceEventListener

	@Override
	public void newSpotPrice(Event<SpotPrice> spotPriceEvent) {
		SpotPrice newSpotPrice = spotPriceEvent.getSource();
		this.currentSpotPrice = newSpotPrice;
	}

	// E-- End of implementation of SpotPriceEventListener

	// B-- beginning of implementation of JobEventListener

	@Override
	public void jobSubmitted(Event<Job> jobEvent) {
		this.addJob(jobEvent.getSource());
	}

	@Override
	public void jobPreempted(Event<Job> jobEvent) {
	}

	@Override
	public void jobFinished(Event<Job> jobEvent) {
	}

	@Override
	public void jobStarted(Event<Job> jobEvent) {
	}

	// E-- end of implementation of JobEventListener

	// B-- beginning of implementation of TaskEventListener

	@Override
	public void taskSubmitted(Event<Task> taskEvent) {
		Task task = taskEvent.getSource();
		assert this.machine2Task.size() == this.allocatedMachines.size();
		if (task.getBidValue() >= currentSpotPrice.getPrice()) {
			String machineName = "m_" + nextMachineId++;
			Machine newMachine = new Machine(machineName, Processor.EC2_COMPUTE_UNIT.getSpeed());
			BidValue bidValue = new BidValue(machineName, getCurrentTime(), task.getBidValue());
			this.machine2Task.put(newMachine, task);
			this.allocatedMachines.put(bidValue, newMachine);
			long currentTime = getCurrentTime();
			Processor defaultProcessor = newMachine.getDefaultProcessor();
			task.setTaskExecution(new TaskExecution(task, defaultProcessor, currentTime));
			task.setStartTime(currentTime);
			task.setTargetPeer(thePeer);
			addStartedTaskEvent(task);
		}
		assert this.machine2Task.size() == this.allocatedMachines.size();
	}

	@Override
	public void taskFinished(Event<Task> taskEvent) {
		Machine machine = this.machine2Task.getKey(taskEvent.getSource());
		this.machine2Task.remove(machine);
		BidValue bidValue = this.allocatedMachines.getKey(machine);
		this.allocatedMachines.remove(bidValue);
	}

	@Override
	public void taskStarted(Event<Task> taskEvent) {
	}

	@Override
	public void taskPreempted(Event<Task> taskEvent) {
	}

	@Override
	public void taskCancelled(Event<Task> taskEvent) {
	}

	// E-- end of implementation of TaskEventListener

	// B-- beginning of implementation of SpotPriceEventListener

	@Override
	public void workerAvailable(Event<String> workerEvent) {
		this.schedule();
	}

	@Override
	public void workerUnavailable(Event<String> workerEvent) {
	}

	@Override
	public void workerUp(Event<String> workerEvent) {
	}

	@Override
	public void workerDown(Event<String> workerEvent) {
	}

	@Override
	public void workerIdle(Event<String> workerEvent) {
	}

	@Override
	public void workerRunning(Event<String> workerEvent) {
	}

	// E-- end of implementation of SpotPriceEventListener
}
