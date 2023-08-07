package io.innospots.workflow.core.execution;

import io.innospots.base.events.EventBody;
import io.innospots.workflow.core.execution.flow.FlowExecution;
import io.innospots.workflow.core.execution.node.NodeExecution;

/**
 * @author Smars
 * @date 2023/8/7
 */
public class ExecutionEvent extends EventBody {

    private FlowExecution flowExecution;

    public static ExecutionEvent build(FlowExecution flowExecution,NodeExecution nodeExecution){
        return new ExecutionEvent(nodeExecution,flowExecution);
    }

    public ExecutionEvent(Object body, FlowExecution flowExecution) {
        super(body);
        this.flowExecution = flowExecution;
    }

    public FlowExecution flowExecution(){
        return this.flowExecution;
    }

    public NodeExecution nodeExecution(){
        if(this.body!=null){
            return (NodeExecution) this.body;
        }
        return null;
    }
}
