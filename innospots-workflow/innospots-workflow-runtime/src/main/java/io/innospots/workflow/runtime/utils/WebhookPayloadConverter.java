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

package io.innospots.workflow.runtime.utils;

import io.innospots.workflow.core.webhook.FlowWebhookConfig;
import io.innospots.workflow.core.webhook.WebhookPayload;
import org.apache.commons.collections4.MapUtils;
import org.springframework.core.io.InputStreamSource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Smars
 * @version 1.2.0
 * @date 2023/2/19
 */
public class WebhookPayloadConverter {


    public static WebhookPayload convert(String flowKey,
                                         HttpServletRequest httpRequest, Map<String, Object> headers,
                                         Map<String, Object> params, Map<String, Object> body
    ) {
        WebhookPayload payload = new WebhookPayload();
        payload.setRequestMethod(FlowWebhookConfig.RequestMethod.valueOf(httpRequest.getMethod()));
        payload.setUri(httpRequest.getRequestURI());
        payload.setLocation(httpRequest.getRemoteAddr());
        payload.setFlowKey(flowKey);
        if (headers == null) {
            headers = Collections.emptyMap();
        }
        if (params == null) {
            params = Collections.emptyMap();
        }
        if (body == null) {
            body = Collections.emptyMap();
        }

        payload.setHeaders(headers);
        payload.setParams(params);
        payload.setBody(body);
        if (httpRequest instanceof StandardMultipartHttpServletRequest) {
            StandardMultipartHttpServletRequest multipartRequest = (StandardMultipartHttpServletRequest) httpRequest;
            Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
            Map<String, InputStreamSource> fileIos = new HashMap<>();
            if (MapUtils.isNotEmpty(fileMap)) {
                for (Map.Entry<String, MultipartFile> fileEntry : fileMap.entrySet()) {
                    MultipartFile multipartFile = fileEntry.getValue();
                    fileIos.put(fileEntry.getKey(), multipartFile);
                }
            }
            payload.setFiles(fileIos);
        }
        return payload;
    }
}
