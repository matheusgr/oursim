package oursim.availability;

public class AvailabilityRecord implements Comparable<AvailabilityRecord> {

	private String machineName;
	private long time;
	private long duration;

	public AvailabilityRecord(String machineName, long timestamp, long duration) {
		this.machineName = machineName;
		this.time = timestamp;
		this.duration = duration;
	}

	public String getMachineName() {
		return machineName;
	}

	public long getTime() {
		return time;
	}

	public long getDuration() {
		return duration;
	}

	@Override
	public int compareTo(AvailabilityRecord o) {
		return (int) (this.time - o.time);
	}

}
