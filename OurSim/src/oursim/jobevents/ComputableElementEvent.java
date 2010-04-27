package oursim.jobevents;

import java.util.EventObject;

import oursim.entities.ComputableElement;

public class ComputableElementEvent extends EventObject {

	private static final long serialVersionUID = 1338458542592060286L;

	private long time = -1;

	public ComputableElementEvent(ComputableElement source) {
		super(source);
	}

	public ComputableElementEvent(long time, ComputableElement source) {
		super(source);
		this.time = time;
	}

	public long getTime() {
		assert time >= 0;
		return time;
	}

}
