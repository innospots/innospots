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

package io.innospots.libra.kernel.module.i18n.loader;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.TreeMap;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/1/23
 */
public class LocaleCountryLoader {


    public static TreeMap<String, String> localeList() {
        TreeMap<String, String> localList = new TreeMap<>();
        for (Locale locale : Locale.getAvailableLocales()) {
            if (StringUtils.isEmpty(locale.getLanguage())) {
                continue;
            }
            String v = locale.getLanguage();
            if (StringUtils.isNotEmpty(locale.getCountry())) {
                v += "_" + locale.getCountry();
            } else {
                //Country code must be included
                continue;
            }
            //update  20220529
            //localList.put(locale.getDisplayName()+"/"+v,v);
            if (!localList.containsKey(v)) {
                localList.put(v, locale.getDisplayName() + "/" + v);
            }

        }
        return localList;
    }
}
