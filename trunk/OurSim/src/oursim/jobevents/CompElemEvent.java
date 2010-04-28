package oursim.jobevents;

import java.util.EventObject;

import oursim.entities.ComputableElement;

public class CompElemEvent extends EventObject {

	private static final long serialVersionUID = 1338458542592060286L;

	protected long time = -1;

	public CompElemEvent(ComputableElement source) {
		super(source);
	}

	public CompElemEvent(long time, ComputableElement source) {
		super(source);
		this.time = time;
	}

	public long getTime() {
		assert time >= 0;
		return time;
	}

}
