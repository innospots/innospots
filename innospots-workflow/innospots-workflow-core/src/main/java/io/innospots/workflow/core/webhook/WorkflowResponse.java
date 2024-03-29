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

package io.innospots.workflow.core.webhook;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author Smars
 * @date 2021/3/3
 */
@Setter
@Getter
public class WorkflowResponse {

    private String contextId;

    private LocalDateTime responseTime;

    private long consume;

    private String flowKey;

    private int revision;

    private Object body;

    private String code;

}
