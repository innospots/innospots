package io.innospots.base.re.java;

import org.junit.jupiter.api.Test;

/**
 * @author Smars
 * @date 2021/9/28
 */
public class JavaExpressionEngineTest2 {


    @Test
    public void test2() {
        JavaExpressionEngine.setPath("target/classes", "target/classes");
        JavaExpressionEngine engine = JavaExpressionEngine.build("Debug_50_draft_fnmWWraZm");

        engine.compile();
    }
}
