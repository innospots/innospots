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

package io.innospots.workflow.core.webhook;


import io.innospots.base.model.field.ParamField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author Alfred
 * @date 2021-07-12
 */
@Getter
@Setter
public class FlowWebhookConfig {

    public static final String BASIC_AUTH_USERNAME = "userName";
    public static final String BASIC_AUTH_PASSWORD = "password";
    public static final String BEARER_AUTH_TOKEN = "token";

    private String path;

    private RequestMethod requestMethod;

    private String responseCode;

    private AuthType authType;


    private Map<String, Object> authBody;

    private List<ParamField> responseFields;


    private ResponseMode responseMode;

    private ResponseData responseData;


    /**
     * webhook api trigger auth type
     */
    public enum AuthType {
        /**
         *
         */
        NONE,
        BASIC_AUTH,
        BEARER_AUTH;
    }

    public enum ResponseMode {
        /**
         * return immediately, response ack body that define in the responseFields, when the api will be called
         */
        ACK,
        /**
         * response flow result that define in the webhook complete node
         */
        RESULT;
    }

    public enum ResponseData {
        /**
         * all the response data
         */
        ALL,
        /**
         * the first item in the list
         */
        FIRST_ITEM;
    }

    public enum RequestMethod {
        /**
         *
         */
        GET,
        POST;
    }
}
