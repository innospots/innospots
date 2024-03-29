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

package io.innospots.base.data.schema;

import io.innospots.base.constant.Constants;
import io.innospots.base.model.BaseModelInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * the catalog in db schema
 *
 * @author Raydian
 * @version 1.0.0
 * @date 2020/11/3
 */
@Getter
@Setter
@Schema(title = "schema catalog")
public class SchemaCatalog extends BaseModelInfo {


    @Size(max = 32, message = "name length max 64")
    @NotBlank(message = "Name cannot be blank")
    @Schema(title = "name")
    @Pattern(regexp = Constants.NAME_REGEX, message = "name, only supports: contains a maximum of 32 characters, including letters, digits, and underscores (_), and chinese.")
    protected String name;

    @Schema(title = "code")
    protected String code;

    /*
    @Size(max = 32, message = "config code length max 32")
    @NotBlank(message = "config code cannot be blank")
    @Schema(title = "app credential config code")
    @Pattern(regexp = Constants.CODE_REGEX, message = "code, only supports: contains a maximum of 32 characters, including letters, digits, and underscores (_), and chinese. It can't all be numbers.")
    protected String configCode;
     */

    //    @NotNull(message = "credentialId cannot be empty")
    @Schema(title = "credential id")
    protected Integer credentialId;

    @Schema(title = "description")
    protected String description;

    @Schema(title = "schema field list")
    protected List<SchemaField> schemaFields;

    @Column
    protected Integer categoryId;

}
