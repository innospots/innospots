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

import java.util.Map;

/**
 * @author Raydian
 * @date 2020/12/31
 */
public interface IExpression<E> {


    E execute(Map<String, Object> env) throws ScriptException;

    E execute(Object... args) throws ScriptException;

    String[] arguments();

    default boolean executeBoolean(Map<String,Object> env){
        Object v = execute(env);
        boolean o = false;
        if (v instanceof String) {
            o = Boolean.parseBoolean((String) v);
        }

        if (v instanceof Boolean) {
            o = (Boolean) v;
        }//end if
        return o;
    }

}
