package io.innospots.workflow.console.task.execution;

import io.innospots.base.events.EventBody;
import io.innospots.base.events.IEventListener;
import io.innospots.libra.base.task.ITaskExecutionExplore;
import io.innospots.libra.base.task.TaskExecution;
import io.innospots.workflow.core.execution.NodeExecutionTaskEvent;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Smars
 * @date 2023/8/7
 */
@Component
public class TaskNodeExecutionListener implements IEventListener<NodeExecutionTaskEvent>{

    private ITaskExecutionExplore taskExecutionExplore;

    public static Set<String> taskSources = new HashSet<>();

    public TaskNodeExecutionListener(ITaskExecutionExplore taskExecutionExplore) {
        this.taskExecutionExplore = taskExecutionExplore;
        taskSources.add("CRONTIMER");
    }

    @Override
    public Object listen(NodeExecutionTaskEvent event) {
        if(event.flowExecution().getSource()== null ||
                !taskSources.contains(event.flowExecution().getSource())){
            //not task flow execution
            return null;
        }
        //update taskExecution process percent
        TaskExecution taskExecution = buildTaskExecution(event);

        taskExecutionExplore.updateTaskExecution(taskExecution.getTaskExecutionId()
                ,taskExecution.getPercent());

        return taskExecution;
    }

    public TaskExecution buildTaskExecution(NodeExecutionTaskEvent event){

        return null;
    }

    @Override
    public Class<? extends EventBody> eventBodyClass() {
        return NodeExecutionTaskEvent.class;
    }
}
