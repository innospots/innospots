package io.innospots.workflow.console.task.operate;

import io.innospots.libra.base.event.NewAvatarEvent;
import io.innospots.libra.base.task.TaskEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * schedule execute flow task using task event
 * @author Smars
 * @date 2023/8/8
 */
@Component
public class FlowTaskScheduleListener {



    @EventListener(TaskEvent.class)
    public void stop(TaskEvent taskEvent){
        if(taskEvent.taskAction()!= TaskEvent.TaskAction.STOP){
            return;
        }

        //TODO
    }

    @EventListener(TaskEvent.class)
    public void reRun(TaskEvent taskEvent){
        if(taskEvent.taskAction()!= TaskEvent.TaskAction.RERUN){
            return;
        }

        //TODO
    }


}
