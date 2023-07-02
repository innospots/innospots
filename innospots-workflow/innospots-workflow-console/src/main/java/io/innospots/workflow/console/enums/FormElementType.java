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

package io.innospots.workflow.console.enums;

/**
 * @author Raydian
 * @date 2020/11/28
 */
public enum FormElementType {

    /**
     * 表单元素
     */

    INPUT("文本输入框"),
    INPUT_NUMBER("数字输入框"),
    TEXTAREA("文本text输入框"),
    RADIO("单选radio组件"),
    CHECKBOX("多选checkbox组件"),
    SWITCH("切换按钮"),
    SELECT("下拉选择和标签选择器"),
    SELECT_NODE_FIELD("下拉选择和标签选择器"),
    SELECT_NODE_FIELD_CONDITION("下拉选择和标签选择器"),
    TIME_PICKER("时间选择"),
    DATE_PICKER("日期选择"),
    MONTH_PICKER("月份选择"),
    YEAR_PICKER("年份选择"),
    DATE_TIME_PICKER("日期时间选择"),
    RANGE_TIME_PICKER("时间范围"),
    RANGE_DATE_PICKER("日期范围"),
    RANGE_MONTH_PICKER("月份范围"),
    RANGE_YEAR_PICKER("年份范围"),
    RANGE_DATE_TIME_PICKER("日期时间范围");

    private String desc;

    FormElementType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
