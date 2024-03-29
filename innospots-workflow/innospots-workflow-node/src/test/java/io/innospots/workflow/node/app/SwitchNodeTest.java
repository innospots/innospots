package io.innospots.workflow.node.app;

import io.innospots.base.exception.ScriptException;
import io.innospots.workflow.core.execution.ExecutionInput;
import io.innospots.workflow.core.execution.node.NodeExecution;
import io.innospots.workflow.core.node.app.BaseAppNode;
import io.innospots.workflow.core.node.instance.NodeInstance;
import io.innospots.workflow.node.app.logic.SwitchNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Smars
 * @date 2021/5/26
 */
@Slf4j
public class SwitchNodeTest extends BaseNodeTest {

    @Test
    public void invoke() throws ScriptException {

        SwitchNode appNode = (SwitchNode) baseAppNode(SwitchNodeTest.class.getSimpleName());
        NodeExecution nodeExecution = nodeExecution(appNode);
        appNode.invoke(nodeExecution);
        log.info("nodeExecution:{}", nodeExecution);
    }

    private NodeExecution nodeExecution(BaseAppNode appNode) {
        NodeExecution execution = NodeExecution.buildNewNodeExecution(appNode.nodeKey(), 22L, 1, "21", false);
        Map<String, Object> data = new HashMap<>();
        data.put("total_level", "1");
        data.put("user_level", "v5");
        data.put("user_age", 10);

        ExecutionInput input = new ExecutionInput();
        input.addInput(data);

//        execution.addInput(input);
        return execution;
    }

    public static NodeInstance build() {
        return build(SwitchNodeTest.class.getSimpleName() + ".json");
    }
}