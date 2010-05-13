package oursim.output;

import oursim.dispatchableevents.jobevents.JobEventListener;

public interface Output extends JobEventListener {

	void close();

}
