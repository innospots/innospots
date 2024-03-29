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

package io.innospots.base.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.innospots.base.model.LocaleContext;
import io.innospots.base.utils.LocaleMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.FastDateFormat;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;


/**
 * @author Smars
 * @version 1.0.0
 * @date 2021/12/21
 */
@Slf4j
public class CustomerDateDeserializer extends StdDeserializer<Date> {


    protected CustomerDateDeserializer(Class<Date> vc) {
        super(vc);
    }

    public static CustomerDateDeserializer getInstance() {
        return new CustomerDateDeserializer(Date.class);
    }

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        LocaleContext localeContext = LocaleMessageUtils.getLocaleContext();
        FastDateFormat sdf = FastDateFormat
                .getInstance(
                        localeContext.getDateFormat() + " " + localeContext.getTimeFormat(),
                        localeContext.getTimeZone());

        try {
            return sdf.parse(p.getText());
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}
