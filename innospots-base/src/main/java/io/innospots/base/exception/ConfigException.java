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

package io.innospots.base.exception;

import io.innospots.base.model.response.ResponseCode;

/**
 * @author chenc
 * @date 2021/2/7 17:56
 */
public class ConfigException extends BaseException {

    private ConfigException(Class<?> invokeClass, ResponseCode responseCode, Throwable cause, Object... params) {
        super(invokeClass, responseCode, cause, params);
    }

    private ConfigException(Class<?> invokeClass, ResponseCode responseCode, Object... params) {
        super(invokeClass, responseCode, params);
    }

    public static ConfigException buildException(Class<?> invokeClass, ResponseCode responseCode, Object... params) {
        return new ConfigException(invokeClass, responseCode, params);
    }

    public static ConfigException buildMissingException(Class<?> invokeClass, Object... params) {
        return new ConfigException(invokeClass, ResponseCode.CONFIG_MISSING, params);
    }

    public static ConfigException buildTypeException(Class<?> invokeClass, Object... params) {
        return new ConfigException(invokeClass, ResponseCode.CONFIG_TYPE_INVALID, params);
    }

    public static ConfigException buildParamException(Class<?> invokeClass, Object... params) {
        return new ConfigException(invokeClass, ResponseCode.CONFIG_PARAM_ERROR, params);
    }

}
