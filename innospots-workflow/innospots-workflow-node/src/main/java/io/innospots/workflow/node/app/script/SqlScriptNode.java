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

package io.innospots.workflow.node.app.script;

import io.innospots.base.condition.Factor;
import io.innospots.base.data.ap.ISqlOperatorPoint;
import io.innospots.base.exception.ConfigException;
import io.innospots.base.model.response.InnospotResponse;
import io.innospots.base.utils.ApplicationContextUtils;
import io.innospots.base.utils.BeanUtils;
import io.innospots.workflow.core.execution.ExecutionInput;
import io.innospots.workflow.core.execution.node.NodeExecution;
import io.innospots.workflow.core.execution.node.NodeOutput;
import io.innospots.workflow.core.node.app.BaseAppNode;
import io.innospots.workflow.core.node.instance.NodeInstance;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/12/18
 */
public class SqlScriptNode extends BaseAppNode {


    private static final Logger logger = LoggerFactory.getLogger(SqlScriptNode.class);

    public static final String FIELD_SQL_CLAUSE = "sql_clause";
    public static final String FIELD_DATASOURCE_ID = "datasource_id";
    public static final String FIELD_CONDITION_MAPPING = "condition_mapping";
    /**
     * store field variable name
     */
    public static final String FIELD_VARIABLE = "variable_name";

    /**
     * sql中参数的开始和结束字符
     */
    private static final String SQL_PARAM_START = "${";
    private static final String SQL_PARAM_END = "}";

    private ISqlOperatorPoint sqlOperatorPoint;

    private String sqlQueryClause;


    protected Integer datasourceId;

    /**
     * where clause, when update operation
     */
    protected List<Factor> conditionFields;

    @Override
    protected void initialize(NodeInstance nodeInstance) {
        super.initialize(nodeInstance);
        datasourceId = nodeInstance.valueInteger(FIELD_DATASOURCE_ID);


        List<Map<String, Object>> conditionFieldMapping = (List<Map<String, Object>>) nodeInstance.value(FIELD_CONDITION_MAPPING);
        if (conditionFieldMapping != null) {
            conditionFields = BeanUtils.toBean(conditionFieldMapping, Factor.class);
        }

        sqlOperatorPoint = ApplicationContextUtils.getBean(ISqlOperatorPoint.class);
        sqlQueryClause = nodeInstance.valueString(FIELD_SQL_CLAUSE);
        if (sqlQueryClause != null) {
            sqlQueryClause = sqlQueryClause.replaceAll("\\n", " ");
        }
    }


    private String parseSqlParam(String sql, Map<String, Object> item, boolean checkParam) {
        if (CollectionUtils.isEmpty(this.conditionFields)) {
            return sql;
        }
        for (Factor conditionField : this.conditionFields) {
            if (sql.contains(conditionField.getName())) {
                Object value = conditionField.value(item);
                if (value == null) {
                    throw ConfigException.buildMissingException(this.getClass(), "sql param:" + conditionField.getCode() + " value is null");
                }
                sql = sql.replaceAll("\\$\\{" + conditionField.getCode() + "\\}", value.toString());
            }
        }

        int noReplaceParamIdx = sql.indexOf(SQL_PARAM_START);
        if (checkParam && noReplaceParamIdx > 0) {
            String paramName = sql.substring(noReplaceParamIdx + 2, sql.lastIndexOf(SQL_PARAM_END));
            throw ConfigException.buildMissingException(this.getClass(), "sql param:" + paramName + " not replace");
        }
        return sql;
    }


    @Override
    public void invoke(NodeExecution nodeExecution) {
        NodeOutput nodeOutput = new NodeOutput();
        nodeOutput.addNextKey(this.nextNodeKeys());
        nodeExecution.addOutput(nodeOutput);
        if (CollectionUtils.isNotEmpty(nodeExecution.getInputs())) {
            for (ExecutionInput executionInput : nodeExecution.getInputs()) {
                for (Map<String, Object> item : executionInput.getData()) {
                    String sql = parseSqlParam(sqlQueryClause, item, true);
                    logger.debug("execute sql:{}", sql);
                    InnospotResponse<Integer> innospotResponse = sqlOperatorPoint.executeForSql(datasourceId, sql);
                    if (logger.isDebugEnabled()) {
                        logger.debug("sql query:{}, response:{}", sql, innospotResponse);
                    }
                    nodeOutput.addResult(item);
                }//end item
            }//end execution input
        } else {
            String sql = parseSqlParam(sqlQueryClause, null, true);
            logger.debug("execute sql:{}", sql);
            InnospotResponse<Integer> innospotResponse = sqlOperatorPoint.executeForSql(datasourceId, sql);
            if (logger.isDebugEnabled()) {
                logger.debug("execute sql:{}, response:{}", sql, innospotResponse);
            }
        }
    }


}
