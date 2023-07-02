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

package io.innospots.base.data.ap;

import io.innospots.base.condition.Factor;
import io.innospots.base.constant.ServiceConstant;
import io.innospots.base.model.DataBody;
import io.innospots.base.model.PageBody;
import io.innospots.base.model.response.InnospotResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * TODO 未使用，保留接口
 *
 * @author Raydian
 * @date 2021/1/11
 */
public interface IDataAccessPoint {


    @GetMapping(ServiceConstant.PATH_ROOT_DATA_API + "/ap/find/{credentialId}/{tableName}/{page}/{size}")
    InnospotResponse<PageBody<Map<String, Object>>> find(@PathVariable("credentialId") Integer credentialId,
                                                         @PathVariable("tableName") String tableName,
                                                         @RequestBody List<Factor> condition,
                                                         @PathVariable("page") int page,
                                                         @PathVariable("size") int size);

    @GetMapping(ServiceConstant.PATH_ROOT_DATA_API + "/ap/get/{credentialId}/{tableName}")
    InnospotResponse<DataBody<Map<String, Object>>> getOne(@PathVariable("credentialId") Integer credentialId,
                                                           @PathVariable("tableName") String tableName,
                                                           @RequestBody List<Factor> condition);

}
