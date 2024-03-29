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

package io.innospots.workflow.runtime.flow;

import com.fasterxml.jackson.databind.json.JsonMapper;
import io.innospots.base.enums.ScriptType;
import io.innospots.base.exception.InnospotException;
import io.innospots.base.exception.ResourceException;
import io.innospots.base.exception.ScriptException;
import io.innospots.base.model.response.ResponseCode;
import io.innospots.base.re.ExpressionEngineFactory;
import io.innospots.base.re.GenericExpressionEngine;
import io.innospots.base.re.IExpressionEngine;
import io.innospots.base.re.jit.MethodBody;
import io.innospots.workflow.core.flow.WorkflowBaseBody;
import io.innospots.workflow.core.node.app.BaseAppNode;
import io.innospots.workflow.core.node.instance.NodeInstance;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.innospots.workflow.runtime.loader.FsWorkflowLoader.DIR_FLOW_CONFIG;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/2/5
 */
@Slf4j
public class FlowCompiler {

    private WorkflowBaseBody workflowInstance;

    private GenericExpressionEngine genericExpressionEngine;

    public Map<String, IExpressionEngine> scriptEngines = new HashMap<>();

    private FlowCompiler(WorkflowBaseBody workflowInstance) {
        this(workflowInstance.identifier());
        this.workflowInstance = workflowInstance;
    }

    private FlowCompiler(String identifier){
        genericExpressionEngine = ExpressionEngineFactory.build(identifier);
        genericExpressionEngine.prepare();
    }

    public static FlowCompiler build(String identifier){
        return new FlowCompiler(identifier);
    }

    public static FlowCompiler build(WorkflowBaseBody workflowInstance){
        return new FlowCompiler(workflowInstance);
    }

    public void clear() {
        genericExpressionEngine.deleteBuildFile();
        for (IExpressionEngine expressionEngine : scriptEngines.values()) {
            expressionEngine.deleteBuildFile();
            ExpressionEngineFactory.clear(expressionEngine.identifier());
        }
        scriptEngines.clear();
        ExpressionEngineFactory.clear(genericExpressionEngine.identifier());
    }

    public boolean isCompiled() {
        try {
            genericExpressionEngine.classForName();
            return true;
        } catch (ClassNotFoundException | MalformedURLException e) {
            log.warn("class exception:{},{},{}", e.getClass().getName(), e.getMessage(), workflowInstance.identifier());
            return false;
        }
    }

    public void compile() throws ScriptException {
        if(workflowInstance!=null){
            for (NodeInstance node : workflowInstance.getNodes()) {
                registerToEngine(node);
            }//end for
        }
        genericExpressionEngine.compile();
        scriptEngines.values().stream().filter(Objects::nonNull).forEach(IExpressionEngine::compile);
        outputFlowFile(GenericExpressionEngine.getClassPath() + File.separator + DIR_FLOW_CONFIG);
    }


    /**
     * build node instance
     *
     * @param nodeInstance
     * @return
     */
    public BaseAppNode registerToEngine(NodeInstance nodeInstance) {
        BaseAppNode appNode = null;
        try {
            appNode = BaseAppNode.newInstance(nodeInstance);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException |
                 InvocationTargetException e) {
            log.error(e.getMessage());
            throw InnospotException.buildException(BaseAppNode.class, ResponseCode.INITIALIZING, e);
        }
        List<MethodBody> methodBodies = nodeInstance.expMethods();

        for (MethodBody methodBody : methodBodies) {
            ScriptType scriptType = methodBody.getScriptType();
            IExpressionEngine expressionEngine;
            if (scriptType == null || scriptType == ScriptType.JAVA || scriptType == ScriptType.JAVASCRIPT) {
                expressionEngine = genericExpressionEngine;
            }else{
                expressionEngine = scriptEngines.get(scriptType.name());
                if(expressionEngine == null){
                    expressionEngine = ExpressionEngineFactory.build(genericExpressionEngine.identifier(), scriptType);
                    scriptEngines.put(scriptType.name(),expressionEngine);
                }
            }
            if (expressionEngine != null) {
                expressionEngine.register(methodBody);
            }
        }//end for methodBody

        return appNode;
    }

    /**
     * write workflow instance json to file
     *
     * @param outputPath
     */
    private void outputFlowFile(String outputPath) {
        if(workflowInstance==null){
            return;
        }
        File dir = new File(outputPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File confFile = new File(dir, workflowInstance.identifier() + ".json");
        if (confFile.exists()) {
            confFile.delete();
        }
        log.info("output workflow instance config:{}", confFile.getAbsolutePath());
        try {
            JsonMapper jsonMapper = JsonMapper.builder().findAndAddModules().build();
            jsonMapper.writeValue(confFile, workflowInstance);
        } catch (IOException e) {
            throw ResourceException.buildIOException(this.getClass(), e);
        }
    }

}
