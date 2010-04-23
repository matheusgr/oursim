package oursim.output;

import oursim.jobevents.JobEventListener;

public interface Output extends JobEventListener {

	void close();

}
