package oursim.availability;

public class AvailabilityRecord {

	public String machineName;
	public long time;
	public long duration;
	
	public AvailabilityRecord(String machineName, long timestamp, long duration) {
		this.machineName = machineName;
		this.time = timestamp;
		this.duration = duration;
	}

}
