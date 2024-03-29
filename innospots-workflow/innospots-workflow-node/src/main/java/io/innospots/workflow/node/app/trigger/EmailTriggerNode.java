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

package io.innospots.workflow.node.app.trigger;

import io.innospots.base.data.minder.DataConnectionMinderManager;
import io.innospots.base.data.minder.IDataConnectionMinder;
import io.innospots.base.data.operator.IExecutionOperator;
import io.innospots.base.data.schema.ConnectionCredential;
import io.innospots.base.model.DataBody;
import io.innospots.base.model.RequestBody;
import io.innospots.workflow.core.execution.node.NodeExecution;
import io.innospots.workflow.core.node.instance.NodeInstance;

import java.util.Map;

/**
 * @author Smars
 * @version 1.2.0
 * @date 2023/2/12
 */
public class EmailTriggerNode extends CycleTimerNode {


    public static final String FIELD_CREDENTIAL_ID = "credentialId";
    public static final String FIELD_MAIL_BOX = "mail_folder";

    public static final String FIELD_ACTION = "action";
    public static final String FIELD_HAS_ATTACH = "has_attach";
    public static final String FIELD_ATTACH_PREFIX = "attach_prefix";

    private Integer credentialId;

    private String mailBoxName;

    private String actionName;

    private boolean hasAttachments;

    private String attachPrefix;

    private ConnectionCredential connectionCredential;

    private IExecutionOperator executionOperator;

    @Override
    protected void initialize(NodeInstance nodeInstance) {
        super.initialize(nodeInstance);
        validFieldConfig(nodeInstance, FIELD_CREDENTIAL_ID);
        validFieldConfig(nodeInstance, FIELD_MAIL_BOX);
        credentialId = nodeInstance.valueInteger(FIELD_CREDENTIAL_ID);
        mailBoxName = nodeInstance.valueString(FIELD_MAIL_BOX);
        actionName = nodeInstance.valueString(FIELD_ACTION);
        hasAttachments = nodeInstance.valueBoolean(FIELD_HAS_ATTACH);

        if (hasAttachments) {
            attachPrefix = nodeInstance.valueString(FIELD_ATTACH_PREFIX);
        }

        IDataConnectionMinder connectionMinder = DataConnectionMinderManager.getCredentialMinder(connectionCredential.getCredentialId());
        executionOperator = (IExecutionOperator) connectionMinder.buildOperator();

    }

    @Override
    public void invoke(NodeExecution nodeExecution) {
        super.invoke(nodeExecution);
        RequestBody requestBody = new RequestBody();
        requestBody.add(FIELD_MAIL_BOX, mailBoxName);
        if (actionName != null) {
            requestBody.add(FIELD_ACTION, actionName);
        }
        if (attachPrefix != null) {
            requestBody.add(FIELD_ATTACH_PREFIX, attachPrefix);
        }
        DataBody<Map<String, Object>> dataBody = executionOperator.execute(requestBody);

    }
}
