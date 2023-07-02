import io.innospots.data.calcite.CalciteConnectionBuilder;
import io.innospots.data.calcite.MemoryQueryExecutor;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/10/23
 */
class CalciteConnectionBuilderTest {

    @Test
    void buildCalciteConnection() throws SQLException {
        String schema = "sch";
        String table = "t1";
        List<Map<String, Object>> items = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", "user_0" + i);
            item.put("age", 18 + i);
            if (i % 2 == 0) {
                item.put("sex", "male");
            } else {
                item.put("sex", "female");
            }

            item.put("price", RandomUtils.nextDouble(50, 80));
            item.put("order_count", RandomUtils.nextInt(20, 50));
            items.add(item);
            System.out.println(item);
        }

        CalciteConnection con = CalciteConnectionBuilder.buildCalciteConnection(schema, table, items);
        String sql = "select * from sch.t1 limit 2";
        MemoryQueryExecutor queryExecutor = new MemoryQueryExecutor(con);
        Map<String, Object> m = queryExecutor.queryForObject(sql);
        System.out.println(m);
        StopWatch sw = new StopWatch();
        sw.start("counter");
        m = queryExecutor.queryForObject("select count(*) as ct from sch.t1");
        System.out.println(m);
        sw.stop();

        sw.start("age_22");
        List<Map<String, Object>> list = queryExecutor.queryForList("select name,age,(CASE WHEN sex='male' THEN 'n' else 'f' END) as sd from sch.t1 where age > 22");
//        List<Map<String,Object>> list = queryExecutor.queryForList("select name,age,if(age>20,'N','V') from sch.t1 where age > 22");
        sw.stop();
        for (Map<String, Object> map : list) {
            System.out.println(map);
        }

        sw.start("group");
        list = queryExecutor.queryForList("select sex,sum(price) as p from sch.t1 group by sex");
        sw.stop();
        for (Map<String, Object> map : list) {
            System.out.println(map);
        }
        sw.start("group_2");
        list = queryExecutor.queryForList("select sex,sum(price)as p,sum(order_count) as c from sch.t1 group by sex");
        sw.stop();
        for (Map<String, Object> map : list) {
            System.out.println(map);
        }

        sw.start("group_and_age");
        long s = System.currentTimeMillis();
        list = queryExecutor.queryForList("select sex,sum(price) as p, sum(order_count) as c from sch.t1 where age > 22 group by sex");
        long e = System.currentTimeMillis() - s;
        sw.stop();
        System.out.println(e + " ms.");
        System.out.println(sw.prettyPrint());
        for (Map<String, Object> map : list) {
            System.out.println(map);
        }

        queryExecutor.close();
        con = CalciteConnectionBuilder.buildCalciteConnection(schema, table, items);
//        sql = "select * from sch.t1 limit 2";
        queryExecutor = new MemoryQueryExecutor(con);
        s = System.currentTimeMillis();
        list = queryExecutor.queryForList("select sex,sum(price) as p, sum(order_count) as c from sch.t1 where age > 22 group by sex");
//        m = queryExecutor.queryForObject(sql);
        e = System.currentTimeMillis() - s;
        System.out.println(e + " ms.");
        for (Map<String, Object> map : list) {
            System.out.println(map);
        }
    }
}