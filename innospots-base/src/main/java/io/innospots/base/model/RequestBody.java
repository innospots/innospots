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

package io.innospots.base.model;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Smars
 * @version 1.2.0
 * @date 2023/2/1
 */
@Getter
@Setter
public class RequestBody {

    private Integer credentialId;

    private String operation;

    private String targetName;

    private String uri;

    private Map<String,String> headers;

    private Map<String, Object> body;

    private Map<String,Object> query;

    private String content;

    private String connectorName;

    public RequestBody() {
        this.body = new HashMap<>();
    }

    public void add(String key, Object value) {
        this.body.put(key, value);
    }

    public void add(Map<String,? extends Object> item) {
        this.body.putAll(item);
    }

    public void addHeader(String key,Object value){
        if(headers==null){
            this.headers = new HashMap<>();
        }
        if(value == null){
            return;
        }
        String s = value.toString();
        if(StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(s)){
            this.headers.put(key, s);
        }
    }

    public void addQuery(String key, Object value){
        if(query==null){
            this.query = new HashMap<>();
        }
        if(value == null){
            return;
        }
        String s = value.toString();
        if(StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(s)){
            this.query.put(key, s);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestBody that = (RequestBody) o;
        return Objects.equals(credentialId, that.credentialId) && Objects.equals(operation, that.operation) && Objects.equals(targetName, that.targetName) && Objects.equals(uri, that.uri) && Objects.equals(headers, that.headers) && Objects.equals(body, that.body) && Objects.equals(query, that.query) && Objects.equals(content, that.content) && Objects.equals(connectorName, that.connectorName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(credentialId, operation, targetName, uri, headers, body, query, content, connectorName);
    }

    public String key(){
        return DigestUtil.sha1Hex(toString());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("credentialId=").append(credentialId);
        sb.append(", operation='").append(operation).append('\'');
        sb.append(", targetName='").append(targetName).append('\'');
        sb.append(", uri='").append(uri).append('\'');
        sb.append(", headers=").append(headers);
        sb.append(", body=").append(body);
        sb.append(", query=").append(query);
        sb.append(", content='").append(content).append('\'');
        sb.append(", connectorName='").append(connectorName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
