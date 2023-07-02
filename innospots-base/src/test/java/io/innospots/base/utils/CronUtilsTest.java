package io.innospots.base.utils;

import io.innospots.base.quartz.TimePeriod;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.CronExpression;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * CronUtilsTest
 *
 * @author zwg
 * @date 2022/4/3-16:46
 */
@Slf4j
public class CronUtilsTest {
    @Test
    public void createCronExpressionTest() {
        TimePeriod timePeriod = TimePeriod.MONTH;
        List<String> periodTimes = new ArrayList<>();
        periodTimes.add("2");
        periodTimes.add("4");
        LocalTime localTime = LocalTime.parse("12:23", DateTimeFormatter.ofPattern("HH:mm"));
        String expression = CronUtils.createCronExpression(timePeriod, periodTimes, localTime);
        System.out.println("expression:" + expression);
        Assert.assertTrue("expression error:" + expression, CronExpression.isValidExpression(expression));
    }
}
