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

import io.innospots.base.enums.ScriptType;
import io.innospots.base.exception.ScriptException;
import io.innospots.base.model.field.ParamField;
import io.innospots.base.re.aviator.AviatorScriptExpressionEngine;
import io.innospots.base.re.java.JavaExpression;
import io.innospots.base.re.java.JavaExpressionEngine;
import io.innospots.base.re.javascript.JavaScriptExpressionEngine;
import io.innospots.base.re.jit.MethodBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用表达式引擎，底层使用JVM做基础构建引擎，兼容其他脚本在jvm的执行
 *
 * @author Smars
 * @date 2021/5/16
 */
public class GenericExpressionEngine extends JavaExpressionEngine {

    private static final Logger logger = LoggerFactory.getLogger(GenericExpressionEngine.class);

    public static GenericExpressionEngine build(String identifier) {
        return new GenericExpressionEngine(identifier);
    }

    public GenericExpressionEngine(String identifier) {
        super(identifier);
    }

    @Override
    public void reload() throws ScriptException {
        try {
            classLoader(true);
            Class<?> clazz = classForName();
            Map<String, IExpression<Object>> tmpExp = new HashMap<>(5);
            for (Method method : clazz.getDeclaredMethods()) {
                String methodName = method.getName();
                if (!methodName.startsWith("_")) {
                    tmpExp.put(methodName, new JavaExpression(method));
                } else if (methodName.startsWith("_" + ScriptType.JAVASCRIPT)) {
                    tmpExp.put(methodName.replace("_" + ScriptType.JAVASCRIPT + "_", ""),
                            JavaScriptExpressionEngine.scriptExpression(method));
                } else if (methodName.startsWith("_" + ScriptType.FORMULA)) {
                    tmpExp.put(methodName.replace("_" + ScriptType.FORMULA + "_", ""),
                            AviatorScriptExpressionEngine.scriptExpression(method));
                } else {
                    logger.warn("script expression not support:{}", methodName);
                }
            }
            this.expressions = tmpExp;
            logger.debug("engine:{} , loaded expression size:{}", className(), expressions.size());
        } catch (ClassNotFoundException | MalformedURLException e) {
            logger.warn("engine:{}  reload exception:{}", className(), e.getMessage());
            //throw new ScriptException(ScriptType.JAVA,e.getMessage(),e);
        }
    }


    public synchronized void register(ScriptType scriptType, Class<?> returnType, String methodName, String srcBody) {
        register(scriptType, returnType, methodName, srcBody, null);
    }

    public synchronized void register(ScriptType scriptType, Class<?> returnType, String methodName, String srcBody, List<ParamField> params) {
        if (scriptType == null) {
            logger.warn("script type is null, method:{}, src:{} ", methodName, srcBody);
            return;
        }
        switch (scriptType) {
            case JAVASCRIPT:
                String src = null;
                if (params != null) {
                    src = JavaScriptExpressionEngine.buildMethodBody(returnType, methodName, srcBody, params.toArray(new ParamField[]{}));
                } else {
                    src = JavaScriptExpressionEngine.buildMethodBody(returnType, methodName, srcBody);
                }
                registerScriptMethod(scriptType, methodName, src);
                break;
            case SCALA:
                //TODO coming soon
                break;
            case GROOVY:
                //TODO coming soon
                break;
            case PYTHON:
                //TODO coming soon
                break;
            case FORMULA:
                registerScriptMethod(scriptType, methodName, srcBody);
                break;
            case CONDITION:
            case JAVA:
            default:
                super.register(returnType, methodName, srcBody, params);
                break;
        }

    }

    @Override
    public void register(MethodBody methodBody) {
        this.register(methodBody.getScriptType(), methodBody.getReturnType(),
                methodBody.getMethodName(), methodBody.getSrcBody(),
                methodBody.getParams());
    }
}
