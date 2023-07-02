package io.innospots.workflow.node.app.file;

import io.innospots.workflow.core.execution.ExecutionInput;
import io.innospots.workflow.core.execution.ExecutionResource;
import io.innospots.workflow.core.execution.node.NodeExecution;
import io.innospots.workflow.core.execution.node.NodeExecutionDisplay;
import io.innospots.workflow.core.execution.node.NodeOutput;
import io.innospots.workflow.core.node.app.BaseAppNode;
import io.innospots.workflow.core.node.instance.NodeInstance;
import io.innospots.workflow.node.app.BaseNodeTest;
import io.innospots.workflow.node.app.NodeExecutionTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

/**
 * @author Smars
 * @version 1.2.0
 * @date 2023/2/25
 */
class WriteFilesNodeTest {


    @Test
    void testInstance(){
        NodeInstance nodeInstance = BaseNodeTest.build(WriteFilesNodeTest.class.getSimpleName()+".json");
        System.out.println(nodeInstance);
        System.out.println(System.getenv("HOME"));
        BaseAppNode appNode = BaseNodeTest.baseAppNode(WriteFilesNodeTest.class.getSimpleName());
        NodeExecution ne2 = ReadFilesNodeTest.readExecution();
        NodeExecution nodeExecution = NodeExecutionTest.build("key12345");

        ExecutionInput executionInput = new ExecutionInput();
        for (NodeOutput output : ne2.getOutputs()) {
            for (List<ExecutionResource> value : output.getResources().values()) {
                executionInput.addResource(value);
            }
        }//end for

        nodeExecution.addInput(executionInput);

        appNode.invoke(nodeExecution);
        System.out.println(NodeExecutionDisplay.build(nodeExecution));
    }

    @Test
    void testFile() {

    }

}