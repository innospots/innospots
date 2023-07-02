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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.innospots.base.enums.ScriptType;
import io.innospots.base.exception.ScriptException;
import io.innospots.base.re.aviator.AviatorScriptExpressionEngine;
import io.innospots.base.re.java.JavaExpressionEngine;
import io.innospots.base.re.javascript.JavaScriptExpressionEngine;
import io.innospots.base.re.python.PythonExpressionEngine;
import io.innospots.base.re.shell.ShellExpressionEngine;

import java.util.concurrent.TimeUnit;

/**
 * @author Raydian
 * @date 2020/12/31
 */
public class ExpressionEngineFactory {


    private static Cache<String, IExpressionEngine> engineCache =
            Caffeine.newBuilder()
                    .expireAfterAccess(3, TimeUnit.HOURS)
                    .build();

    public static IExpressionEngine buildNewEngine(ScriptType scriptType) {
        return null;
    }

    public static void clear(String identifier) {
        engineCache.invalidate(identifier);
    }

    public static GenericExpressionEngine build(String identifier) throws ScriptException {
        GenericExpressionEngine engine = (GenericExpressionEngine) engineCache.getIfPresent(identifier);
        if (engine == null) {
            engine = GenericExpressionEngine.build(identifier);
            engine.reload();
            engineCache.put(identifier, engine);
        }
        return engine;
    }

    public static IExpressionEngine getEngine(String identifier,ScriptType scriptType) {
        IExpressionEngine engine;
        if(scriptType == ScriptType.JAVA || scriptType==ScriptType.JAVASCRIPT){
            engine = engineCache.getIfPresent(identifier);
        }else{
            engine = engineCache.getIfPresent(identifier+ "_" + scriptType);
        }
        return engine;
    }

    public static IExpressionEngine build(String identifier, ScriptType scriptType) throws ScriptException {
        IExpressionEngine engine = engineCache.getIfPresent(identifier + "_" + scriptType);
        if (engine != null) {
            return engine;
        }

        switch (scriptType) {
            case JAVA:
                engine = JavaExpressionEngine.build(identifier);
                break;
            case FORMULA:
                engine = AviatorScriptExpressionEngine.build(identifier);
                break;
            case CONDITION:
                engine = JavaExpressionEngine.build(identifier);
                engine.reload();
                break;
            case JAVASCRIPT:
                engine = JavaScriptExpressionEngine.build(identifier);
                break;
            case PYTHON:
                String scriptPath = JavaExpressionEngine.getClassPath() + "/script";
                engine = PythonExpressionEngine.build(scriptPath, identifier);
                break;
            case SHELL:
                scriptPath = JavaExpressionEngine.getClassPath() + "/script";
                engine = ShellExpressionEngine.build(scriptPath, identifier);
                break;
            case SCALA:
            case GROOVY:
            default:
                break;
        }

        if (engine != null) {
            engineCache.put(identifier + "_" + scriptType, engine);
        }


        return engine;
    }


}
