package oursim.output;

import java.util.EventObject;

import oursim.entities.Job;

public class JobEvent extends EventObject {
    
    private static final long serialVersionUID = 481672427365120073L;

    public JobEvent(Job source) {
	super(source);
    }


}
