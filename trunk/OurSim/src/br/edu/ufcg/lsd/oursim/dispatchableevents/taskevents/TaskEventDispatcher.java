package br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.EventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * A dispatcher to the task's related events.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 19/05/2010
 * 
 * @see {@link TaskEventListener}
 * @see {@link TaskEventFilter}
 * 
 */
public class TaskEventDispatcher extends EventDispatcher<Task, TaskEventListener, TaskEventFilter> {

	/**
	 * 
	 * An enumeration of all the types of the events that could be dispatched by
	 * this dispatcher. For each type there is an method responsible to dispatch
	 * it. For example, to {@link TYPE_OF_DISPATCHING#submitted} there is
	 * {@link TaskEventDispatcher#dispatchTaskSubmitted(Task)
	 * 
	 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
	 * @since 19/05/2010
	 * 
	 */
	protected enum TYPE_OF_DISPATCHING {
		submitted, started, preempted, finished
	};

	private static TaskEventDispatcher instance = null;

	private TaskEventDispatcher() {
		super();
	}

	public static TaskEventDispatcher getInstance() {
		return instance = (instance != null) ? instance : new TaskEventDispatcher();
	}

	@Override
	public void addListener(TaskEventListener listener) {
		if (!this.getListeners().contains(listener)) {
			this.getListeners().add(listener);
			this.getListenerToFilter().put(listener, TaskEventFilter.ACCEPT_ALL);
		} else {
			assert false;
		}
	}

	@Override
	public boolean removeListener(TaskEventListener listener) {
		return this.getListeners().remove(listener);
	}

	/**
	 * @see {@link TaskEventListener#taskSubmitted(Event)
	 * @param task
	 */
	public void dispatchTaskSubmitted(Task task) {
		dispatch(TYPE_OF_DISPATCHING.submitted, task);
	}

	/**
	 * @see {@link TaskEventListener#taskStarted(Event)
	 * @param task
	 */
	public void dispatchTaskStarted(Task task) {
		dispatch(TYPE_OF_DISPATCHING.started, task);
	}

	/**
	 * @see {@link TaskEventListener#taskFinished(Event)
	 * @param task
	 */
	public void dispatchTaskFinished(Task task) {
		dispatch(TYPE_OF_DISPATCHING.finished, task);
	}

	/**
	 * @see {@link TaskEventListener#taskPreempted(Event)
	 * @param task
	 * @param preemptionTime
	 */
	public void dispatchTaskPreempted(Task task, long preemptionTime) {
		dispatch(TYPE_OF_DISPATCHING.preempted, task, preemptionTime);
	}

	private void dispatch(TYPE_OF_DISPATCHING type, Task task, long preemptionTime) {
		dispatch(type, new Event<Task>(preemptionTime, task));
	}

	private void dispatch(TYPE_OF_DISPATCHING type, Task task) {
		dispatch(type, new Event<Task>(task));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void dispatch(Enum type, Event<Task> taskEvent) {
		for (TaskEventListener listener : this.getListeners()) {
			// submitted, started, preempted, finished
			if (this.getListenerToFilter().get(listener).accept(taskEvent)) {
				switch ((TYPE_OF_DISPATCHING) type) {
				case submitted:
					listener.taskSubmitted(taskEvent);
					break;
				case started:
					listener.taskStarted(taskEvent);
					break;
				case preempted:
					listener.taskPreempted(taskEvent);
					break;
				case finished:
					listener.taskFinished(taskEvent);
					break;
				default:
					assert false;
				}
			}
		}
	}

}
