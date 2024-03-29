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

package io.innospots.libra.kernel.module.i18n.operator;

import io.innospots.libra.kernel.module.i18n.dao.I18nTransMessageDao;
import io.innospots.libra.kernel.module.i18n.model.LocaleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * @author Smars
 * @date 2021/12/20
 */
@Component
public class I18nMessageOperator {

    @Autowired
    I18nTransMessageDao i18nTransMessageDao;

    public List<LocaleMessage> localeMessages() {
        return null;
    }

    public LocaleMessage getLocaleMessage(String code, Locale locale) {
        return i18nTransMessageDao.selectLocalMessageByCode(code, locale.toString());
    }
}
