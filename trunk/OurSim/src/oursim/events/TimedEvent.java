package oursim.events;

public abstract class TimedEvent implements Comparable<TimedEvent> {

	private boolean cancel;

	protected long time;

	// quanto menor, maior a preferência para ser executado antes
	// range -> [1-10]
	private int priority;

	protected TimedEvent(long time) {
		this(time, 5);
	}

	protected TimedEvent(long time, int priority) {
		this.time = time;
		this.priority = priority;
		this.cancel = false;
	}

	protected abstract void doAction();

	public String getType() {
		String thisClassSimpleName = this.getClass().getSimpleName();
		// TODO: following a implicit name convention
		String eventName = thisClassSimpleName.substring(0, thisClassSimpleName.indexOf("Event"));
		return eventName;
	}

	public void cancel() {
		this.cancel = true;
	}

	public final void action() {
		if (!cancel) {
			doAction();
		}
	}

	public boolean isCancelled() {
		return this.cancel;
	}

	public long getTime() {
		return this.time;
	}

	@Override
	public int compareTo(TimedEvent ev) {
		long diffTime = this.time - ev.time;
		if (diffTime == 0) {
			if (this.priority >= ev.priority) {
				return 1;
			} else {
				return -1;
			}
		} else if (diffTime > 0) {
			return 2;
		} else {
			return -2;
		}
	}

}