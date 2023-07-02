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

package io.innospots.connector.schema.controller;

import io.innospots.base.data.minder.DataConnectionMinderManager;
import io.innospots.base.data.schema.AppCredentialInfo;
import io.innospots.base.data.schema.ConnectionCredential;
import io.innospots.base.data.schema.reader.IConnectionCredentialReader;
import io.innospots.base.exception.ValidatorException;
import io.innospots.base.model.response.InnospotResponse;
import io.innospots.libra.base.operator.SystemTempCacheOperator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Alfred
 * @date 2023/3/6
 */
@Slf4j
@RestController
@RequestMapping("/api/credential/oauth2")
@Tag(name = "App Credential Oauth2")
public class CredentialOauth2Controller {

    private final IConnectionCredentialReader connectionCredentialReader;

    private final SystemTempCacheOperator systemTempCacheOperator;

    public CredentialOauth2Controller(
            IConnectionCredentialReader connectionCredentialReader,
            SystemTempCacheOperator systemTempCacheOperator) {
        this.connectionCredentialReader = connectionCredentialReader;
        this.systemTempCacheOperator = systemTempCacheOperator;
    }

    @GetMapping("callback")
    @Operation(summary = "oauth2 credential callback")
    public String callback(
            @Parameter(name = "code") @RequestParam("code") String code,
            @Parameter(name = "state") @RequestParam("state") String state) {
        if (StringUtils.isBlank(state)) {
            log.warn("oauth2 credential callback state can not be empty");
            throw ValidatorException.buildInvalidException("oauth2-credential", "oauth2 credential callback state can not be empty");
        }
        AppCredentialInfo appCredentialInfo = new AppCredentialInfo();
        appCredentialInfo.setConfigCode("oauth2-api");
        appCredentialInfo.setEncryptFormValues(state);

        ConnectionCredential connectionCredential = connectionCredentialReader.fillCredential(appCredentialInfo);
        connectionCredential.getConfig().put("code", code);
        Object result = DataConnectionMinderManager.fetchSample(connectionCredential);

        Map<String, Object> config = connectionCredential.getConfig();
        if (MapUtils.isNotEmpty(config) && result != null) {
            String clientId = "client_id";
            String clientSecret = "client_secret";
            systemTempCacheOperator.put(config.get(clientId) + "_" + config.get(clientSecret), String.valueOf(result));
        }

        // TODO 返回一个oauth-callback.html 的页面：Got connected. The window can be closed now.
        return "forward:/oauth-callback.html";
    }

    @GetMapping("status/{clientId}/{clientSecret}")
    @Operation(summary = "oauth2 credential status")
    public InnospotResponse<Boolean> result(
            @Parameter(name = "clientId") @PathVariable("clientId") String clientId,
            @Parameter(name = "clientSecret") @PathVariable("clientSecret") String clientSecret
    ) {
        String value = systemTempCacheOperator.get(clientId + "_" + clientSecret);
        return InnospotResponse.success(!StringUtils.isBlank(value));
    }
}
