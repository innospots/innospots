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

package io.innospots.base.condition.statement;

import io.innospots.base.condition.Mode;

/**
 * Statement factor builder
 *
 * @author Smars
 * @date 2021/8/11
 */
public class FactorStatementBuilder {


    public static IFactorStatement build(Mode mode) {
        IFactorStatement factorStatement;
        switch (mode) {
            case DB:
                factorStatement = new DatabaseFactorStatement();
                break;
            case SCRIPT:
            case JAVA:
            default:
                factorStatement = new ScriptFactorStatement();
                break;
        }
        return factorStatement;
    }
}
