package io.innospots.base.re.function;

import org.junit.Test;

import static io.innospots.base.re.function.HttpFunc.httpPost;

/**
 * @author Smars
 * @date 2021/7/3
 */
public class HttpFuncTest {

    @Test
    public void testPost() {
        httpPost("", null, "");
    }
}