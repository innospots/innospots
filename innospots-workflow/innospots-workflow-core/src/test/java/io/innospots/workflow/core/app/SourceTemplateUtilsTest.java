package io.innospots.workflow.core.app;

import freemarker.template.TemplateException;
import io.innospots.workflow.core.ftl.SourceTemplateUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Smars
 * @date 2021/4/24
 */
public class SourceTemplateUtilsTest {

    @Test
    public void output() {
    }

    @Test
    public void testOutput() throws IOException, TemplateException {
        String source = "return a;";
        Map<String, Object> m = new HashMap<>();
        String ss = SourceTemplateUtils.output("Test.ftl", source, m);
        System.out.println(ss);
        m.put("asb", 256);
        source = "return ${asb} > vars;";
        ss = SourceTemplateUtils.output("Test.ftl", source, m);
        System.out.println(ss);
    }

    @Test
    public void testIfNode() throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put("condition", "abc < 10");
        String script = SourceTemplateUtils.output("IfNode.ftl", model);
        System.out.println(script);
    }

}