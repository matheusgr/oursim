package oursim.events;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import oursim.entities.Task;

public abstract class TaskTimedEvent extends ComputableElementTimedEvent {

	TaskTimedEvent(long time, int priority, Task task) {
		super(time, priority, task);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("task", (Task) content).toString();
	}

}
