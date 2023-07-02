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

import com.google.common.collect.Multimap;
import org.apache.calcite.schema.Function;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Smars
 * @date 2021/10/13
 */
public class MemorySchema extends AbstractSchema implements Serializable {

    private Map<String, Table> metaTableMap = new HashMap<>();

    public void registerTable(MemoryTable memoryTable) {
        this.metaTableMap.put(memoryTable.getTableName(), memoryTable);
    }

    public static MemorySchema create(String tableName, List<Map<String, Object>> items) {
        MemorySchema memorySchema = new MemorySchema();
        MemoryTable memoryTable = new MemoryTable(tableName, items);
        memorySchema.registerTable(memoryTable);
        return memorySchema;
    }

    public static MemorySchema create(String tableName) {
        MemorySchema memorySchema = new MemorySchema();
        MemoryTable memoryTable = new MemoryTable(tableName);
        memorySchema.registerTable(memoryTable);
        return memorySchema;
    }

    @Override
    protected Map<String, Table> getTableMap() {
        return metaTableMap;
    }

    @Override
    protected Multimap<String, Function> getFunctionMultimap() {
        return super.getFunctionMultimap();
    }
}
