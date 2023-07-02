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

package io.innospots.base.watcher;

/**
 * @author Raydian
 * @date 2020/12/14
 */
public interface IWatcher extends Runnable {

    /**
     * 执行前检查，返回true的时候才会执行execute方法
     * 否则不执行
     *
     * @return
     */
    boolean check();

    /**
     * 监听器的执行
     *
     * @return
     */
    int execute();

    /**
     * 监听器终止
     */
    void stop();

}
