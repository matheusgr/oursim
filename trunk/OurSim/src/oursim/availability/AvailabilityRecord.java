package oursim.availability;

public class AvailabilityRecord {

	public AvailabilityRecord(String machineName, long timestamp, long duration) {
		this.machineName = machineName;
		this.time = timestamp;
		this.duration = duration;
	}

	public String machineName;
	public long time;
	public long duration;

}
