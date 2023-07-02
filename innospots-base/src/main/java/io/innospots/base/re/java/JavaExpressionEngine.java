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
import io.innospots.base.exception.ScriptException;
import io.innospots.base.model.field.FieldValueType;
import io.innospots.base.model.field.ParamField;
import io.innospots.base.re.IExpression;
import io.innospots.base.re.IExpressionEngine;
import io.innospots.base.re.jit.FileClassLoader;
import io.innospots.base.re.jit.JavaSourceFileCompiler;
import io.innospots.base.re.jit.JavaSourceFileStaticBuilder;
import io.innospots.base.re.jit.MethodBody;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Smars
 * @date 2021/4/1
 */
public class JavaExpressionEngine implements IExpressionEngine {

    private static final Logger logger = LoggerFactory.getLogger(JavaExpressionEngine.class);

    public static final String DEFAULT_EXP_PKG = "live.re.scripts";

    public static final String CLASSPATH_ENV = "live.classpath";
    public static final String SOURCEPATH_EVN = "live.sourcepath";


    /**
     * 构建源码路径
     */
    protected Path sourcePath;

    protected Path classPath;

    protected String identifier;

    protected JavaSourceFileCompiler compiler;

    protected JavaSourceFileStaticBuilder sourceBuilder;

    protected String packageName;

    protected Map<String, IExpression<Object>> expressions;

    protected FileClassLoader classLoader;


    public static void setPath(String sourcePath, String classPath) {
        System.setProperty(CLASSPATH_ENV, classPath);
        System.setProperty(SOURCEPATH_EVN, sourcePath);
    }

    public static String getClassPath() {
        return System.getProperty(CLASSPATH_ENV);
    }

    public static String getSourcePath() {
        return System.getProperty(SOURCEPATH_EVN);
    }

    public static JavaExpressionEngine build(String identifier) {
        return new JavaExpressionEngine(DEFAULT_EXP_PKG, identifier);
    }


    public JavaExpressionEngine(String packageName, String identifier) {
        this.packageName = packageName;
        this.identifier = identifier;
    }

    public JavaExpressionEngine(String identifier) {
        this(DEFAULT_EXP_PKG, identifier);
    }

    public void prepare() {
        this.sourceBuilder();
    }

    /**
     * 删除class文件
     *
     * @return
     */
    @Override
    public void deleteBuildFile() {
        File clsFile = new File(classPath().toFile(), className().replace(".", File.separator) + ".class");
        logger.debug("remove class file:{}", clsFile.getPath());
        if (clsFile.exists()) {
            clsFile.delete();
        }
        sourceBuilder().deleteSourceFile();
        /*
        File sourceFile = new File(sourcePath().toFile(),identifier+".java");
        logger.debug("remove source file:{}",sourceFile);
        if(sourceFile.exists()){
            sourceFile.delete();
        }

         */
    }

    @Override
    public String identifier() {
        return identifier;
    }

    public String className() {
        return packageName + "." + identifier;
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


    public Class<?> classForName() throws ClassNotFoundException, MalformedURLException {
        classPath();
//        return classLoader(false).loadClass(className());
        return classPath == null ?
                Class.forName(className()) :
                Class.forName(className(), true, new URLClassLoader(new URL[]{classPath.toUri().toURL()}));
    }

    @Override
    public void reload() throws ScriptException {
        try {
            classLoader(true);
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
            throw ScriptException.buildCompileException(this.getClass(), ScriptType.JAVA, e, e.getMessage());
        }
    }

    private Path sourcePath() {
        if (this.sourcePath != null) {
            return sourcePath;
        }
        String path = getSourcePath();
        if (path == null) {
            classPath();
            sourcePath = Paths.get(classPath.toAbsolutePath().toString(), "src");
            logger.warn("system variable has not been set: live.sourcepath, please setting variable: System.setProperty(SOURCEPATH_ENV,path)");
        } else {
            sourcePath = Paths.get(path);
        }
        File clzDir = sourcePath.toFile();
        if (!clzDir.exists()) {
            clzDir.mkdirs();
        }
        return sourcePath;
    }

    private Path classPath() {
        if (this.classPath != null) {
            return classPath;
        }
        String path = getClassPath();
        if (path == null) {
            classPath = Paths.get("");
            logger.warn("system variable has not been set: live.sourcepath, please setting variable: System.setProperty(CLASSPATH_ENV,path)");
        } else {
            classPath = Paths.get(path);
        }
        File clzDir = classPath.toFile();
        if (!clzDir.exists()) {
            clzDir.mkdirs();
        }
        return classPath;
    }

    protected FileClassLoader classLoader(boolean update) {
        if (this.classLoader == null || update) {
            this.classLoader = new FileClassLoader(classPath());
        }
        return new FileClassLoader(classPath);
    }

    public void clearSrcFile() {
        this.sourceBuilder().deleteSourceFile();
        /*
        Path sourceFile = Paths.get(sourcePath().toAbsolutePath().toString(),identifier+".java");
        if(sourceFile.toFile().exists()){
            sourceFile.toFile().delete();
        }
         */
    }

    @Override
    public boolean compile() throws ScriptException {
        if (this.compiler == null) {
            this.compiler = new JavaSourceFileCompiler(classPath());
        }
        this.sourceBuilder = sourceBuilder();
        if (this.sourceBuilder.hasSourceBody()) {
            try {
                logger.info("compile engine:{}, write source file:{}", className(), sourceBuilder.getSourceFile());
                this.sourceBuilder.writeToFile();
            } catch (IOException e) {
                throw ScriptException.buildCompileException(this.getClass(), ScriptType.JAVA, e, e.getMessage());
            }
        }

        if (sourceBuilder.sourceFileExists()) {
            this.compiler.addSourceFile(sourceBuilder.getSourceFile());
            try {
                this.compiler.compile();
                reload();
                logger.info("engine class file write complete, classFile:{} , loaded expresion size:{}", className(), expressions.size());
                return true;
            } catch (IOException e) {
                throw ScriptException.buildCompileException(this.getClass(), ScriptType.JAVA, e, e.getMessage());
            }
        }

        if (this.expressions == null && sourceBuilder.sourceFileExists()) {
            reload();
            return true;
        }
        /*
        Path sourceFile = Paths.get(sourcePath().toAbsolutePath().toString(),identifier+".java");
        if (sourceBuilder != null) {
            Map<String, IExpression<Object>> tmpExp = new HashMap<>();
            try {
                logger.info("compile engine:{}, write source file:{}", className(), sourceFile);
                this.sourceBuilder.writeToFile();
                this.compiler.addSourceFile(sourceFile.toFile());
                this.compiler.compile();
                reload();
                logger.info("engine class file write complete, classFile:{} , loaded expresion size:{}", className(), expressions.size());
                sourceBuilder.clear();
                sourceBuilder = null;
                return true;
            } catch (IOException e) {
                throw ScriptException.buildCompileException(this.getClass(),ScriptType.JAVA,e,e.getMessage());
            }
        }else if(sourceFile.toFile().exists()){
            this.compiler.addSourceFile(sourceFile.toFile());
            try {
                this.compiler.compile();
                reload();
                logger.info("engine class file write complete, classFile:{} , loaded expresion size:{}", className(), expressions.size());
                return true;
            } catch (IOException e) {
                throw ScriptException.buildCompileException(this.getClass(),ScriptType.JAVA,e,e.getMessage());
            }
        } else if (this.expressions == null) {
            reload();
            return true;
        }

         */

        return false;
    }


    @Override
    public synchronized void register(Class<?> returnType, String methodName, String srcBody, List<ParamField> params) {
        if (CollectionUtils.isNotEmpty(params)) {
            register(returnType, methodName, srcBody, params.toArray(new ParamField[]{}));
        } else {
            register(returnType, methodName, srcBody);
        }

    }

    @Override
    public synchronized void register(Class<?> returnType, String methodName, String srcBody, ParamField... params) {
        sourceBuilder();
        sourceBuilder.addMethod(returnType, srcBody, methodName, params);
    }


    public synchronized void registerScriptMethod(ScriptType scriptType, String methodName, String srcMethodBody) {
        sourceBuilder();
        sourceBuilder.addScriptMethod(scriptType, methodName, srcMethodBody);
    }

    @Override
    public void register(Class<?> returnType, String methodName, String srcBody) {
        register(returnType, methodName, srcBody, new ParamField("item", "item", FieldValueType.MAP));
    }

    @Override
    public void register(MethodBody methodBody) {
        register(methodBody.getReturnType(), methodBody.getMethodName(), methodBody.getSrcBody(), methodBody.getParams());
    }

    private JavaSourceFileStaticBuilder sourceBuilder() {
        if (sourceBuilder == null) {
            try {
                sourceBuilder = JavaSourceFileStaticBuilder.newBuilder(identifier, packageName, sourcePath());
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        }//end if

        return sourceBuilder;
    }


    public String dump() {
        return toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("classPath='").append(classPath).append('\'');
        sb.append(", pkgName='").append(packageName).append('\'');
        sb.append(", identifier='").append(identifier).append('\'');
        sb.append(", expression size:").append(this.expressions == null ? 0 : this.expressions.size());
        sb.append(", expressions='").append(this.expressions != null ? this.expressions.keySet().toString() : "").append('\'');
        sb.append('}');
        return sb.toString();
    }
}
