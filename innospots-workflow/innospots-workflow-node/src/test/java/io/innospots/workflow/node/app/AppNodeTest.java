package io.innospots.workflow.node.app;

import com.google.common.base.Enums;
import io.innospots.base.data.enums.DataOperation;
import org.junit.Test;

/**
 * @author Smars
 * @date 2021/4/24
 */
public class AppNodeTest {

    @Test
    public void compile() {
        DataOperation operation = Enums.getIfPresent(DataOperation.class, "null").orNull();
        //DataOperation operation = DataOperation.valueOf("null");
        if (operation == null) {
            System.out.println("operation is null");
        }
    }
}