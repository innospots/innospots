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

package io.innospots.libra.kernel.module.i18n.service;

import io.innospots.libra.kernel.module.i18n.dao.I18nTransMessageDao;
import io.innospots.libra.kernel.module.i18n.model.LocaleMessage;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/1/23
 */
@Service
public class I18nLocaleResourceService {

    private I18nTransMessageDao i18nTransMessageDao;

    public I18nLocaleResourceService(I18nTransMessageDao i18nTransMessageDao) {
        this.i18nTransMessageDao = i18nTransMessageDao;
    }

    /**
     * get the module locale resource according i18n dictionary module
     * and load this module translation message resources
     *
     * @param module
     * @param locale
     * @return
     */
    @Cacheable(cacheNames = "locale_resource", key = "#module+'-'+#locale")
    public TreeMap<String, String> getLocaleResource(String module, String locale) {
        //load i18nDictionary using module name
        // load tranMessage using dictionaryId and locale
        //return treeMap, which the key is the code of i18nDictionary and the value is the corresponding messages of the transMessage
        TreeMap<String, String> resourceMap = null;
        List<LocaleMessage> list = i18nTransMessageDao.selectLocalMessageByApp(module, locale);
        if (list != null && !list.isEmpty()) {
            resourceMap = new TreeMap<>(list.stream().collect(Collectors.toMap(LocaleMessage::getCode, LocaleMessage::getMessage)));
        }
        return resourceMap;
    }
}
