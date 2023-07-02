package io.innospots.base.re.cmdline;

import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/11/11
 */
class CmdLineExpressionTest {


    @Test
    void testCmd() {
        String cmd = "sh";
        System.out.println(new File("/tmp/").getAbsolutePath());
        String script = "/tmp/hello.sh";
        CmdLineExpression expression = new CmdLineExpression(cmd, script);
        Object resp = expression.execute("11", "22");
        System.out.println(resp);
    }

}