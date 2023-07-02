package io.innospots.workflow.core.app.ftl;

import io.innospots.workflow.core.ftl.SourceTemplateUtils;
import lombok.SneakyThrows;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: innospots-root
 * @description:
 * @author: Alexander
 * @create: 2021-05-03 14:48
 **/

public class SourceTemplateTest {


    @SneakyThrows
    @Test
    public void testIf() {

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("condition", "a < 100 && b > 150");
        String sourceContent = SourceTemplateUtils.output("IfNode.ftl", dataModel);
        System.out.println(sourceContent);
    }


    @Test
    public void testSwitch() {

//        SwitchNode.SwitchCondition[] switchConditions = new SwitchNode.SwitchCondition[];
//        Map<String,Object> dataModel = new HashMap<>();
//        dataModel.put("conditions",switchConditions);
//        SourceTemplateUtils.output("SwitchNode",);
    }


}
