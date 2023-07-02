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

package io.innospots.workflow.node.app;

import io.innospots.workflow.core.execution.ExecutionStatus;
import io.innospots.workflow.core.execution.node.NodeExecution;
import io.innospots.workflow.core.node.app.BaseAppNode;
import io.innospots.workflow.core.node.instance.NodeInstance;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Smars
 * @date 2021/3/17
 */
@Getter
@Setter
public class StateNode extends BaseAppNode {


    private StateNodeType stateNodeType;

    @Override
    protected void initialize(NodeInstance nodeInstance) {
        super.initialize(nodeInstance);
        stateNodeType = StateNodeType.valueOf(this.ni.getCode());
    }

    @Override
    public void processNextKeys(NodeExecution nodeExecution) {
        switch (stateNodeType) {
            case END:
                nodeExecution.setNext(false);
                nodeExecution.setNextNodeKeys(null);
                nodeExecution.setStatus(ExecutionStatus.COMPLETE);
                break;
            case PAUSE:
                nodeExecution.setNextNodeKeys(null);
                nodeExecution.setNext(false);
                nodeExecution.setStatus(ExecutionStatus.STOPPED);
                break;
            default:
                super.processNextKeys(nodeExecution);
                break;
        }
    }

    public enum StateNodeType {
        /**
         * 开始
         */
        START,
        /**
         * 结束
         */
        END,
        /**
         * 暂停
         */
        PAUSE,
        /**
         * 下一个循环
         */
        NEXT_LOOP;
    }
}
