package io.innospots.libra.base.task;

import org.springframework.context.ApplicationEvent;

/**
 * @author Smars
 * @date 2023/8/7
 */
public class ExecutionStatusEvent extends ApplicationEvent {

    private int percent;

    private TaskExecutionStatus executionStatus;

    public ExecutionStatusEvent(Object source, int percent, TaskExecutionStatus executionStatus) {
        super(source);
        this.percent = percent;
        this.executionStatus = executionStatus;
    }

    public String taskExecutionId(){
        if(this.source!=null){
            return (String) this.source;
        }
        return null;
    }

    public int getPercent() {
        return percent;
    }

    public TaskExecutionStatus getExecutionStatus() {
        return executionStatus;
    }
}
