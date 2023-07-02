package io.innospots.base.condition;

import com.google.common.collect.Lists;
import io.innospots.base.json.JSONUtils;
import io.innospots.base.model.field.FieldValueType;
import org.junit.jupiter.api.Test;


/**
 * @author Smars
 * @date 2021/6/25
 */
public class EmbedConditionTest {

    @Test
    public void rebuild() {
        EmbedCondition ec = condition();
        ec.initialize();
        String json = JSONUtils.toJsonString(ec);

        System.out.println(json);
    }

    @Test
    public void rc() {
        EmbedCondition ec = condition();
        EmbedCondition ec2 = condition();
        EmbedCondition ea = new EmbedCondition();
        ea.setMode(Mode.SCRIPT);
        ea.setRelation(Relation.OR);
        ea.addCondition(ec);
        ea.addCondition(ec2);
        ea.initialize();
        String json = JSONUtils.toJsonString(ea);
        System.out.println(json);
    }

    private EmbedCondition condition() {
        EmbedCondition ec = new EmbedCondition();
        ec.setMode(Mode.SCRIPT);
        ec.setRelation(Relation.AND);
        Factor factor = new Factor();
        factor.setCode("f1");
        factor.setName("字段1");
        factor.setOpt(Opt.EQUAL);
        factor.setValueType(FieldValueType.STRING);
        factor.setValue("v1");
        ec.addFactor(factor);

        Factor factor1 = new Factor(
                "f2", Opt.GREATER, 1, FieldValueType.INTEGER);
        factor1.setName("字段2");
        ec.addFactor(factor1);

        factor = new Factor(
                "f3", Opt.LESS, 45.8f, FieldValueType.DOUBLE);
        factor.setName("字段3");
        ec.addFactor(factor);
        factor = new Factor("f4", Opt.BETWEEN, Lists.newArrayList("333", "999"), FieldValueType.STRING);
        ec.addFactor(factor);
        return ec;
    }

    @Test
    public void testStmt() {
        EmbedCondition ec = condition();
        ec.initialize();
        String stmt = ec.getStatement();
        System.out.println(stmt);
    }

}