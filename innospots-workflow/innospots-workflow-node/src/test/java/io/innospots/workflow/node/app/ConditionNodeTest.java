package io.innospots.workflow.node.app;

import io.innospots.base.exception.ScriptException;
import io.innospots.workflow.core.execution.ExecutionInput;
import io.innospots.workflow.core.execution.node.NodeExecution;
import io.innospots.workflow.core.node.instance.NodeInstance;
import io.innospots.workflow.node.app.logic.ConditionNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Smars
 * @date 2021/5/23
 */
@Slf4j
public class ConditionNodeTest extends BaseNodeTest {

    @Test
    public void invoke() throws ScriptException {
        NodeExecution nodeExecution = nodeExecution();
        ConditionNode appNode = (ConditionNode) baseAppNode(ConditionNodeTest.class.getSimpleName());

        appNode.invoke(nodeExecution);

        log.info("nodeExecution:{}", nodeExecution);
    }


    private NodeExecution nodeExecution() {
        NodeExecution execution = NodeExecution.buildNewNodeExecution("abc", 22L, 1, "432", false);
        Map<String, Object> data = new HashMap<>();
        data.put("f1", "v1");
        data.put("f2", 2);
        data.put("f3", 1.0);

        ExecutionInput input = new ExecutionInput();
        input.addInput(data);

//        execution.addInput(input);
        return execution;
    }


    public static NodeInstance build() {
        return build(ConditionNodeTest.class.getSimpleName() + ".json");
    }

}