package io.innospots.workflow.console.task.execution;

import io.innospots.base.events.EventBody;
import io.innospots.base.events.IEventListener;
import io.innospots.libra.base.task.ITaskExecutionExplore;
import io.innospots.libra.base.task.TaskExecution;
import io.innospots.libra.base.task.TaskExecutionStatus;
import io.innospots.workflow.core.execution.FlowExecutionTaskEvent;
import io.innospots.workflow.core.execution.flow.FlowExecution;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Smars
 * @date 2023/8/7
 */
@Component
public class FlowTaskExecutionListener implements IEventListener<FlowExecutionTaskEvent>{

    private ITaskExecutionExplore taskExecutionExplore;

    public static Set<String> taskSources = new HashSet<>();

    public FlowTaskExecutionListener(ITaskExecutionExplore taskExecutionExplore) {
        this.taskExecutionExplore = taskExecutionExplore;
        taskSources.add("CRONTIMER");
    }

    @Override
    public Object listen(FlowExecutionTaskEvent event) {
        if(event.flowExecution().getSource()== null ||
                !taskSources.contains(event.flowExecution().getSource())){
            //not task flow execution
            return null;
        }
        TaskExecution taskExecution = buildTaskExecution(event.flowExecution());

        //save taskExecution
        if(taskExecution.getExecutionStatus() == TaskExecutionStatus.RUNNING){
            taskExecutionExplore.saveTaskExecution(taskExecution);
        }else{
            taskExecutionExplore.updateTaskExecution(taskExecution);
        }
        return taskExecution;
    }

    public TaskExecution buildTaskExecution(FlowExecution execution) {
        //TODO build
        return null;
    }

    @Override
    public Class<? extends EventBody> eventBodyClass() {
        return FlowExecutionTaskEvent.class;
    }
}
