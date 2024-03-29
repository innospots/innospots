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

package io.innospots.libra.kernel.module.i18n.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/1/9
 */
@Getter
@Setter
@Schema(title = "translation message Group, which show all the national translating dictionary item")
public class I18nTransMessageGroup {

    @Schema(title = "dictionary info ")
    private I18nDictionary dictionary;

    @Schema(title = "key is locale, value is trans message")
    private Map<String, String> messages;

    public I18nTransMessageGroup() {
    }

    public I18nTransMessageGroup(I18nDictionary dictionary, Map<String, String> messages) {
        this.dictionary = dictionary;
        this.messages = messages;
    }
}
