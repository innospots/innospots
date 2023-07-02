package io.innospots.base.json;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/8/5
 */
class JSONUtilsTest {

    @Test
    void toJsonString() {
        Map<String, Object> m = new HashMap<>();
        m.put("dateTime", LocalDateTime.now());
        m.put("time", LocalTime.now());
        m.put("date", LocalDate.now());
        m.put("date2", new Date());
        m.put("abc", 12223);
        System.out.println(JSONUtils.toJsonString(m));
    }

    @Test
    void test1() {
        String ss = "{'Alice': 112, 'Beth': '9102', 'Cecil': '3258'}";

        Map m = JSONUtils.parseObject(ss, Map.class);
        System.out.println(m);
        String sss = "{\"Alice\": 112, \"Beth\": \"9102\", \"Cecil\": \"3258\"}";
        Map<String, Object> mm = JSONUtils.parseObject(sss, Map.class);
        System.out.println(mm);

        for (Map.Entry<String, Object> stringObjectEntry : mm.entrySet()) {
            System.out.println(stringObjectEntry.getValue().getClass().getSimpleName());
        }


        String js = "[{'Alice': 112, 'Beth': '9102', 'Cecil': '3258'}, {'Alice': 112, 'Beth': '9102', 'Cecil': '3258'}]";
        List lls = JSONUtils.toList(js, Map.class);
        System.out.println(lls);
    }
}