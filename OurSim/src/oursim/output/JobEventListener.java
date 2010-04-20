package oursim.output;

import java.util.EventListener;

public interface JobEventListener extends EventListener {

    void jobFinished(JobEvent jobEvent);

    void jobStarted(JobEvent jobEvent);

    void jobSubmitted(JobEvent jobEvent);

}
