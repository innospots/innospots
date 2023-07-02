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

package io.innospots.base.re.java;

import io.innospots.base.enums.ScriptType;
import io.innospots.base.model.field.FieldValueType;
import io.innospots.base.model.field.ParamField;
import io.innospots.base.re.IExpression;
import io.innospots.base.re.IExpressionEngine;
import io.innospots.base.re.jit.MethodBody;
import javassist.*;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * javaassit 编译方式，后续可过期删除
 *
 * @author Smars
 * @date 2021/4/1
 */
public class JavaExpressionEngineJavaAssit implements IExpressionEngine {

    private static final Logger logger = LoggerFactory.getLogger(JavaExpressionEngineJavaAssit.class);


    public static final String DEFAULT_EXP_PKG = "live.re.scripts";
    public static final String CLASSPATH_EXPRESSION = "live.classpath";

    protected String classPath;

    protected String pkgName;
    protected String identifier;
    protected CtClass ctClass;

    protected boolean newForce;

    protected Map<String, IExpression<Object>> expressions;

    public static void setBuildPath(String path) {
        System.setProperty(CLASSPATH_EXPRESSION, path);
    }

    public static String getBuildPath() {
        return System.getProperty(CLASSPATH_EXPRESSION);
    }

    public static JavaExpressionEngineJavaAssit build(String pkgName, String identifier, boolean newForce) {
        return new JavaExpressionEngineJavaAssit(pkgName, identifier, newForce);
    }

    public static JavaExpressionEngineJavaAssit build(String identifier) {
        return build(identifier, true);
    }

    public static JavaExpressionEngineJavaAssit build(String identifier, boolean newForce) {
        return new JavaExpressionEngineJavaAssit(identifier, newForce);
    }

    public JavaExpressionEngineJavaAssit(String pkgName, String identifier, boolean newForce) {
        this.pkgName = pkgName;
        this.identifier = identifier;
        this.newForce = newForce;
        classPath = getBuildPath();
        if (classPath == null) {
            classPath = "";
        }
        File clzDir = new File(classPath);
        if (!clzDir.exists()) {
            clzDir.mkdirs();
        }
    }

    public JavaExpressionEngineJavaAssit() {

    }

    public JavaExpressionEngineJavaAssit(String identifier, boolean newForce) {
        this(DEFAULT_EXP_PKG, identifier, newForce);
    }

    @Override
    public String identifier() {
        return identifier;
    }

    public String className() {
        return pkgName + "." + identifier;
    }

    @Override
    public IExpression<Object> getExpression(String methodName) {
        if (this.expressions != null) {
            return this.expressions.get(methodName);
        }
        return null;
    }

    @Override
    public boolean isLoaded() {
        return this.expressions != null;
    }

    @Override
    public void deleteBuildFile() {

    }


    protected Class<?> classForName() throws ClassNotFoundException, MalformedURLException {
        classPath = getBuildPath();
        return classPath == null ?
                Class.forName(className()) :
                Class.forName(className(), true, new URLClassLoader(new URL[]{new File(classPath).toURI().toURL()}));
    }

    @Override
    public void reload() {
        try {
            Class<?> clazz = classForName();
            Map<String, IExpression<Object>> tmpExp = new HashMap<>();
            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.getName().startsWith("_")) {
                    tmpExp.put(method.getName(), new JavaExpression(method));
                }
            }
            this.expressions = tmpExp;
            logger.debug("engine:{} , loaded expresion size:{}", className(), expressions.size());
        } catch (ClassNotFoundException | MalformedURLException e) {
            logger.warn(e.getMessage());
        }
    }

    @Override
    public boolean compile() {
        if (ctClass != null) {
            Map<String, IExpression<Object>> tmpExp = new HashMap<>();
            try {
                logger.info("compile engine:{}, write class file:{}", className(), classPath);
                Class<?> clazz = ctClass.toClass();
                for (Method method : clazz.getDeclaredMethods()) {
                    tmpExp.put(method.getName(), new JavaExpression(method));
                }
                ctClass.writeFile(classPath);
                ctClass.detach();
                ctClass = null;
                this.expressions = tmpExp;
                logger.info("engine class file write complete, classFile:{} , loaded expresion size:{}", className(), expressions.size());
                return true;
            } catch (CannotCompileException | IOException e) {
                logger.error(e.getMessage(), e);
                return false;
            }
        } else if (this.expressions == null) {
            reload();
            return true;
        }

        return false;
    }


    @Override
    public synchronized void register(Class<?> returnType, String methodName, String srcBody, List<ParamField> params) {
        if (CollectionUtils.isNotEmpty(params)) {
            register(returnType, methodName, srcBody, params.toArray(new ParamField[]{}));
        } else {
            register(returnType, methodName, srcBody, new ParamField[0]);
        }

    }

    @Override
    public synchronized void register(Class<?> returnType, String methodName, String srcBody, ParamField... params) {
        buildClass();

        CtMethod ctMethod = null;
        try {
            ctMethod = ctClass.getDeclaredMethod(methodName);
            ctClass.removeMethod(ctMethod);
        } catch (NotFoundException e) {
            //logger.error(e.getMessage(),e);
        }
        try {
            if (ArrayUtils.isEmpty(params)) {
                params = new ParamField[1];
                params[0] = new ParamField("payload", "payload", FieldValueType.MAP);
            }
            srcBody = buildMethodBody(returnType, methodName, srcBody, params);
            System.out.println(srcBody);
            ctMethod = CtNewMethod.make(srcBody, ctClass);
            addAnnotation(ctMethod, params);
            ctClass.addMethod(ctMethod);
        } catch (CannotCompileException | NotFoundException e) {
            logger.error(e.getMessage() + "\nsource: \n" + srcBody, e);
        }
    }

    private void addAnnotation(CtMethod ctMethod, ParamField... paramFields) throws NotFoundException {
        MethodInfo methodInfo = ctMethod.getMethodInfo();
        ConstPool constPool = methodInfo.getConstPool();
        Annotation[][] paramArrays = new Annotation[ctMethod.getParameterTypes().length][];
        ParameterAnnotationsAttribute paramAtrributeInfo = new ParameterAnnotationsAttribute(constPool, ParameterAnnotationsAttribute.visibleTag);
        for (int i = 0; i < ctMethod.getParameterTypes().length; i++) {
            Annotation paramAnnot = new Annotation("io.innospots.base.re.java.ParamName", constPool);
            paramAnnot.addMemberValue("value", new StringMemberValue(paramFields[i].getCode(), constPool));
            paramArrays[i] = new Annotation[1];
            paramArrays[i][0] = paramAnnot;
        }
        paramAtrributeInfo.setAnnotations(paramArrays);
        methodInfo.addAttribute(paramAtrributeInfo);

    }

    public synchronized void registerScriptMethod(ScriptType scriptType, String methodName, String srcMethodBody) {
        buildClass();

        CtMethod ctMethod = null;
        try {
            ctMethod = ctClass.getDeclaredMethod(methodName);
            ctClass.removeMethod(ctMethod);
        } catch (NotFoundException e) {
            //logger.error(e.getMessage(),e);
        }
        try {
            String srcBody = scriptBody(scriptType, methodName, srcMethodBody);
            ctMethod = CtNewMethod.make(srcBody, ctClass);
            ctClass.addMethod(ctMethod);
        } catch (CannotCompileException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void register(Class<?> returnType, String methodName, String srcBody) {
        register(returnType, methodName, srcBody, new ParamField[0]);
    }

    @Override
    public void register(MethodBody methodBody) {

    }

    private void buildClass() {
        if (ctClass == null) {
            try {
                ClassPool.getDefault().insertClassPath(new ClassClassPath(this.getClass()));

                ctClass = ClassPool.getDefault().get(className());
                if (newForce) {
                    URL url = ClassPool.getDefault().find(className());
                    Files.deleteIfExists(Paths.get(url.toURI()));
                    ctClass = null;
                }
            } catch (NotFoundException | URISyntaxException | IOException e) {
                logger.warn(e.getMessage());
            }
            if (ctClass == null) {
                ctClass = ClassPool.getDefault().makeClass(className());

                try {
                    CtField logField = new CtField(ClassPool.getDefault().getOrNull("org.slf4j.Logger"), "logger", ctClass);
                    logField.setModifiers(Modifier.setPrivate(Modifier.STATIC));
                    ctClass.addField(logField, CtField.Initializer.byExpr(" org.slf4j.LoggerFactory.getLogger(\"" + className() + "\")"));
                } catch (CannotCompileException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }//end if
    }

    /**
     * 脚本方法
     *
     * @param methodName
     * @param script
     * @return
     */
    public String scriptBody(ScriptType scriptType, String methodName, String script) {
        StringBuilder buf = new StringBuilder();
        buf.append("public static String");
        buf.append(" _");
        buf.append(scriptType);
        buf.append(" _");
        buf.append(methodName);
        buf.append("() {\n");
        buf.append("String script =\"");
        buf.append(script);
        buf.append("\"");
        buf.append("\nreturn script;");
        buf.append("}");

        return buf.toString();
    }

    public static String buildMethodBody(Class<?> returnType, String mName, String srcBody, ParamField... params) {
        StringBuilder buf = new StringBuilder();
        buf.append("public static ");
        if (returnType.equals(Void.class)) {
            buf.append("void");
        } else if (returnType.equals(Boolean.class)) {
            buf.append("boolean");
        } else {
            buf.append(returnType.getName());
        }
        buf.append(" ");
        buf.append(mName);
        buf.append("(");
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                buf.append(params[i].getValueType().getClazz().getName());
                buf.append(" ");
                buf.append(params[i].getCode());
                if (i < params.length - 1) {
                    buf.append(", ");
                }
            }//end for
        } else {
            buf.append("java.util.Map payload");
        }
        buf.append(") {");
        buf.append("\n");
        buf.append("  try {\n");
        buf.append("  ");
        buf.append(srcBody);
        buf.append("\n");
        buf.append("  }catch (Exception e){\n");
        buf.append("    logger.error(e.getMessage(),e);\n");
        buf.append("  }\n");

        if (!returnType.equals(Void.class)) {
            buf.append("return null;\n");
        }

        buf.append("}");

        return buf.toString();
    }

    public String dump() {
        final StringBuilder sb = new StringBuilder("JavaExpressionEngine{");
        sb.append("classPath='").append(classPath).append('\'');
        sb.append(", pkgName='").append(pkgName).append('\'');
        sb.append(", identifier='").append(identifier).append('\'');
        sb.append(", expression size:").append(this.expressions == null ? 0 : this.expressions.size());
        sb.append(", expressions='").append(this.expressions != null ? this.expressions.keySet().toString() : "").append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JavaExpressionEngine{");
        sb.append("classPath='").append(classPath).append('\'');
        sb.append(", pkgName='").append(pkgName).append('\'');
        sb.append(", identifier='").append(identifier).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
