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

package io.innospots.data.calcite;

import org.apache.calcite.linq4j.Enumerator;

import java.util.List;
import java.util.Map;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/10/23
 */
public class MemoryEnumerator implements Enumerator<Object[]> {

    private List<Map<String, Object>> items;

    private List<Column> columns;


    private int i = -1;

    private int length;

    public MemoryEnumerator(List<Map<String, Object>> items, List<Column> columns) {
        this.items = items;
        this.columns = columns;
        length = items.size();
    }

    @Override
    public Object[] current() {
        Object[] outs = new Object[items.size()];
        for (int j = 0; j < columns.size(); j++) {
            outs[j] = items.get(i).get(columns.get(j).getCode());
        }
        return outs;
    }

    @Override
    public boolean moveNext() {
        if (i < length - 1) {
            i++;
            return true;
        }
        return false;
    }

    @Override
    public void reset() {
        i = 0;
    }

    @Override
    public void close() {

    }

}
