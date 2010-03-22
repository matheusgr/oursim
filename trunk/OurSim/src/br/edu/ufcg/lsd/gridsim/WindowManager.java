package br.edu.ufcg.lsd.gridsim;

import java.util.Iterator;
import java.util.TreeSet;

import br.edu.ufcg.lsd.gridsim.events.StartJobEvent;
import br.edu.ufcg.lsd.gridsim.events.TimeQueue;

public class WindowManager {

	public class NewWindow implements Comparable<NewWindow> {
		private int size;
		private TreeSet<Job> jobs;
		private int endTime;
		private int startTime;
		private TimeQueue timeQueue;
		public NewWindow(int size2, int startTime, TimeQueue timeQueue) {
			this.size = size2;
			this.endTime = -1;
			this.startTime = startTime;
			this.jobs = new TreeSet<Job>();
			this.timeQueue = timeQueue;
		}

		@Override
		public int compareTo(NewWindow o) {
			return this.startTime - o.startTime;
		}
		public void allocate(Job job) {
			size -= job.getNProc();
			jobs.add(job);
			job.cancelStart();
			StartJobEvent startJobEvent = new StartJobEvent(this.startTime, job, "GL");
			timeQueue.addEvent(startJobEvent);
		}
		@Override
		public String toString() {
			return "NewWindow [endTime=" + endTime + ", jobs=" + jobs
					+ ", size=" + size + ", startTime=" + startTime + "]";
		}
	}

	public static enum Strategy {
		FCFS, EBF;
	}
	
	private TreeSet<NewWindow> windows;
	private int size;
	private Strategy strategy;
	private TreeSet<Job> jobs;
	private TimeQueue tq;

	public WindowManager(Strategy strategy, int size, TimeQueue tq) {
		this.strategy = strategy;
		this.windows = new TreeSet<NewWindow>();
		this.size = size;
		this.jobs = new TreeSet<Job>();
		this.tq = tq;
	}
	
	private void clear(int currentTime) {
		for (Iterator<NewWindow> iterator = windows.iterator(); iterator.hasNext();) {
			NewWindow w = iterator.next();
			if (w.endTime != -1 && w.endTime <= currentTime) {
				iterator.remove();
			} else {
				break;
			}
		}
		if (windows.isEmpty()) {
			windows.add(new NewWindow(this.size, currentTime, this.tq));
		} else {
			windows.first().startTime = currentTime;
		}
	}
	
	public void finishJob(Job job, int currentTime) {
		clear(currentTime);
		this.jobs.remove(job);
		if (windows.size() > 1) {
			// Trying to backfill again
			if (strategy == Strategy.EBF) {
				TreeSet<Job> remaingJobs = (new TreeSet<Job>(jobs));
				remaingJobs.removeAll(windows.first().jobs);
				for (Job j : remaingJobs) {
					if (waitTimeFor(j, currentTime) == 0) {
						fillingTime(j, currentTime);
					}
				}
			}
		}
	}
	
	public int waitTimeFor(Job j, int currentTime) {

		if (j.getNProc() > this.size) {
			return Integer.MAX_VALUE; // It will never fit!
		} 
		
		if (windows.isEmpty()) {
			return 0;
		}

		NewWindow selectedWindow = windows.last();
		
		if (strategy == Strategy.EBF) {
			if (firstWindowFits(j, currentTime)) {
				return 0;
			}
		}

		// Fit in current Window
		if (selectedWindow.size >= j.getNProc()) {
			return selectedWindow.startTime < currentTime ? 0 : selectedWindow.startTime - currentTime;
		}

		// Need a new window
		return selectedWindow.endTime < currentTime ? 0 : selectedWindow.endTime - currentTime;
	}

	private boolean firstWindowFits(Job j, int currentTime) {
		Iterator<NewWindow> iterator = windows.iterator();
		NewWindow window = null;
		for (; iterator.hasNext();) {
			window = iterator.next();
			if (window.startTime >= currentTime) {
				break;
			}
			window = null;
		}
		if (window == null) {
			return false; // No need for EBF, just run normally
		}
		if (iterator.hasNext()) {
			NewWindow nextWindow = iterator.next();
			if (window.size >= j.getNProc()) {
				if (j.getRunTime() <= window.endTime - currentTime) {
					return true;
				} else if (nextWindow.size >= j.getNProc()) {
					return true;
				}
			}
		}
		return false; // Try to fill failed.
	}
	
	public void allocateJob(Job job, int currentTime) {
		clear(currentTime);
		this.jobs.add(job);

		NewWindow window = windows.last();

		if (strategy == Strategy.EBF) {
			// First window fit and there is more windows?
			if (windows.size() > 1 && firstWindowFits(job, currentTime)) {
				// Filling time!
				fillingTime(job, currentTime);
				return;
			}
		}
		
		// If it not fit at the last window
		if (window.size < job.getNProc()) {
			window = new NewWindow(this.size, window.endTime, this.tq);
			windows.add(window);
		}
		
		splitMerge(window, job, window.startTime, job.getRunTime());
	}

	private void fillingTime(Job job, int currentTime) {
		NewWindow firstWindow = windows.first();
		TreeSet<Job> runningJobs = firstWindow.jobs;
		TreeSet<Job> queuedJobs = new TreeSet<Job>(this.jobs);
		queuedJobs.removeAll(runningJobs);
		queuedJobs.remove(job);
		Iterator<NewWindow> iterator = windows.iterator();
		iterator.next(); // Avoiding first window
		while (iterator.hasNext()) {
			NewWindow w = iterator.next();
			TreeSet<Job> queuedWindowJobs = new TreeSet<Job>(w.jobs);
			queuedWindowJobs.removeAll(runningJobs);
			if (queuedWindowJobs.isEmpty()) {
				iterator.remove();
				break;
			} else {
				for (Job qJob : queuedWindowJobs) {
					w.jobs.remove(qJob);
					w.size += qJob.getNProc();
				}
				if (w.jobs.isEmpty()) {
					iterator.remove();
					break;
				}
			}
		}
		while (iterator.hasNext()) {
			iterator.next();
			iterator.remove();
		}
		allocateJob(job, currentTime);
		for (Job j : queuedJobs) {
			allocateJob(j, currentTime);
		}
	}
	
	private void splitMerge(NewWindow window, Job job, int currentTime, int runtime) {
		if (window.endTime == -1) { // Empty window
			window.endTime = currentTime + runtime;
			window.allocate(job);
		} else if (window.endTime - currentTime > runtime) { // Job under fit
			int oldTime = window.endTime;
			NewWindow w2 = new NewWindow(window.size, currentTime + runtime, this.tq);
			window.endTime = w2.startTime;
			w2.endTime = oldTime;
			for (Job j : window.jobs ) {
				w2.jobs.add(j);
			}
			window.allocate(job);
			windows.add(w2);
		} else if (window.endTime - currentTime == runtime) { // Job perfect fit
			window.allocate(job);
		} else { // job over fit
			window.allocate(job);
			int oldTime = window.endTime;
			NewWindow w2 = new NewWindow(this.size, oldTime, this.tq);
			w2.endTime = runtime + currentTime;
			w2.allocate(job);
			windows.add(w2);
		}
	}

	public int freeSize(int time) {
		clear(time);
		return windows.first().size;
	}
}