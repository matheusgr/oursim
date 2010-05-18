package oursim.policy;

import java.util.List;

public abstract class RequestPolicy<T,R> {

	protected T requester;

	public RequestPolicy(T requester) {
		this.requester = requester;
	}

	public abstract void prioritize(List<R> resources);
	
}
