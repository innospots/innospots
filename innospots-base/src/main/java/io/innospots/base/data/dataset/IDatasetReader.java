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

package io.innospots.base.data.dataset;

import io.innospots.base.constant.ServiceConstant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * data source schema reader
 *
 * @author Raydian
 * @date 2021/2/5
 */
public interface IDatasetReader {


    @GetMapping(ServiceConstant.PATH_ROOT_DATA_API + "{credentialId}/data-set/list")
    List<Dataset> listDatasets(@PathVariable("credentialId") Integer credentialId);

    @PostMapping(ServiceConstant.PATH_ROOT_DATA_API + "/data-set/list")
    List<Dataset> listDatasets(@RequestBody List<String> ids);


}
