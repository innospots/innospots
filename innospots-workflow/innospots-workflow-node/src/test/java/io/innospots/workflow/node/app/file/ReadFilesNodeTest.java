package io.innospots.workflow.node.app.file;

import io.innospots.workflow.core.execution.node.NodeExecution;
import io.innospots.workflow.core.execution.node.NodeExecutionDisplay;
import io.innospots.workflow.core.node.app.BaseAppNode;
import io.innospots.workflow.core.node.instance.NodeInstance;
import io.innospots.workflow.node.app.BaseNodeTest;
import io.innospots.workflow.node.app.NodeExecutionTest;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

/**
 * @author Smars
 * @version 1.2.0
 * @date 2023/2/25
 */
class ReadFilesNodeTest {


    @Test
    void testInstance(){
        NodeExecution nodeExecution = readExecution();
        System.out.println(NodeExecutionDisplay.build(nodeExecution,null));
    }

    static NodeExecution readExecution() {
        NodeInstance nodeInstance = BaseNodeTest.build(ReadFilesNodeTest.class.getSimpleName()+".json");
        System.out.println(nodeInstance);
        System.out.println(System.getenv("HOME"));
        BaseAppNode appNode = BaseNodeTest.baseAppNode(ReadFilesNodeTest.class.getSimpleName());
        NodeExecution nodeExecution = NodeExecutionTest.build("key12345");
        appNode.invoke(nodeExecution);
        return nodeExecution;
    }

    @Test
    void testFile() {
//        String imgDir = "/tmp/abbc/*.img";
        String imgDir = "/tmp";
        System.out.println(new File("/tmp/a.img").isFile());
        System.out.println(new File("/tmp/*.img").getName());
        String pt = new File("/tmp/*").getName().replace(".", "\\.").replace("*", ".*");
        System.out.println(pt);
        File fs = new File(imgDir);
        File[] ffs = fs.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.matches(pt);
            }
        });
        System.out.println(Arrays.toString(ffs));
        System.out.println(fs.isFile());
        System.out.println(fs.isDirectory());
        System.out.println(fs.getAbsolutePath());
        System.out.println(fs.getParent());
        System.out.println(fs.getParentFile().getAbsolutePath());
        ReadFilesNode startNode = new ReadFilesNode();
        //File[] ff =  startNode.selectFiles(imgDir);
    }

    @Test
    void testSelectFiles() {
        ReadFilesNode readFilesNode = new ReadFilesNode();
        File[] files = readFilesNode.selectFiles("/tmp/*.img");
        System.out.println(Arrays.toString(files));
        files = readFilesNode.selectFiles("/tmp/*");
        System.out.println(Arrays.toString(files));
        files = readFilesNode.selectFiles(System.getProperty("user.home") + "/Downloads/*.pdf");
        System.out.println(Arrays.toString(files));
        System.getProperties().forEach((k, v) -> {
            System.out.println(k + ":" + v);
        });
    }

}