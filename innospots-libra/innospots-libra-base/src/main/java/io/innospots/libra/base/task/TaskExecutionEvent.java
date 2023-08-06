package io.innospots.libra.base.task;

import org.springframework.context.ApplicationEvent;

/**
 * @author Smars
 * @date 2023/8/6
 */
public class TaskExecutionEvent extends ApplicationEvent {

    public TaskExecutionEvent(Object source) {
        super(source);
    }

    public TaskExecution getTaskExecution() {
        if(source!=null){
            return (TaskExecution) source;
        }
        return null;
    }
}
