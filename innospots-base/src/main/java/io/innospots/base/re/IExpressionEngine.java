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

package io.innospots.base.re;

import io.innospots.base.exception.ScriptException;
import io.innospots.base.model.field.ParamField;
import io.innospots.base.re.jit.MethodBody;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author Raydian
 * @date 2020/12/31
 */
public interface IExpressionEngine {

    /**
     * engine unique identifier
     *
     * @return
     */
    String identifier();

    IExpression<Object> getExpression(String methodName);

    void reload() throws ScriptException;

    boolean compile() throws ScriptException;

    boolean isLoaded();

    void deleteBuildFile();

    void register(Class<?> returnType, String methodName, String srcBody, ParamField... params);


    default void register(Class<?> returnType, String methodName, String srcBody, List<ParamField> params) {
        if (CollectionUtils.isNotEmpty(params)) {
            register(returnType, methodName, srcBody, params.toArray(new ParamField[]{}));
        } else {
            register(returnType, methodName, srcBody, new ParamField[0]);
        }
    }

    /**
     * input param is map type
     *
     * @param returnType
     * @param methodName
     * @param srcBody
     */

    default void register(Class<?> returnType, String methodName, String srcBody) {
        register(returnType, methodName, srcBody, new ParamField[0]);
    }

    /**
     * use method body
     *
     * @param methodBody
     */
    default void register(MethodBody methodBody) {
        register(methodBody.getReturnType(), methodBody.getMethodName(), methodBody.getSrcBody(), methodBody.getParams());
    }


}
