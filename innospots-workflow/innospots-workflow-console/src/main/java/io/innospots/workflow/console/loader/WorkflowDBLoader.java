package io.innospots.workflow.console.loader;

import io.innospots.workflow.console.operator.instance.WorkflowBuilderOperator;
import io.innospots.workflow.core.flow.WorkflowBody;
import io.innospots.workflow.core.loader.IWorkflowLoader;

/**
 * @author Smars
 * @date 2023/8/7
 */
public class WorkflowDBLoader implements IWorkflowLoader {

    private WorkflowBuilderOperator workFlowBuilderOperator;

    public WorkflowDBLoader(WorkflowBuilderOperator workFlowBuilderOperator) {
        this.workFlowBuilderOperator = workFlowBuilderOperator;
    }

    @Override
    public WorkflowBody loadFlowInstance(Long workflowInstanceId, Integer revision) {
        return workFlowBuilderOperator.getWorkflowBody(workflowInstanceId, revision, true);
    }

}
