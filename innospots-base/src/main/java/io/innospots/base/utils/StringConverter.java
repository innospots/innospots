/*
 *  Copyright © 2021-2023 Innospots (http://www.innospots.com)
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.innospots.base.utils;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author Smars
 * @date 2022/2/23
 */
public class StringConverter {

    private static final String KEY_SEED = "123456789abcdefghijklmnopqrstuvwxyz";


    public static String camelToUnderscore(String camelStr) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camelStr);
    }

    public static String underscoreToCamel(String underscoreStr) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, underscoreStr);
    }

    public static String randomKey(int count) {
        return RandomStringUtils.random(count, KEY_SEED);
    }
}
