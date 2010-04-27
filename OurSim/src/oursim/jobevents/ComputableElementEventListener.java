package oursim.jobevents;

import java.util.EventListener;

public interface ComputableElementEventListener extends EventListener {

	void submitted(ComputableElementEvent computableElement);

	void started(ComputableElementEvent computableElement);

	void finished(ComputableElementEvent computableElement);

	void preempted(ComputableElementEvent computableElement);

}
