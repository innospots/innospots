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

package io.innospots.libra.kernel.module.workspace.mapper;

import io.innospots.base.mapper.BaseConvertMapper;
import io.innospots.libra.kernel.module.page.model.PageDetail;
import io.innospots.libra.kernel.module.workspace.entity.WorkspaceEntity;
import io.innospots.libra.kernel.module.workspace.model.Workspace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author Alfred
 * @date 2022/1/30
 */
@Mapper
public interface WorkspaceMapper extends BaseConvertMapper {

    WorkspaceMapper INSTANCE = Mappers.getMapper(WorkspaceMapper.class);

    @Mapping(source = "id", target = "pageId")
    WorkspaceEntity modelToEntity(Workspace model);

    @Mapping(source = "pageId", target = "id")
    Workspace entityToModel(WorkspaceEntity entity);

    List<Workspace> entitiesToModels(List<WorkspaceEntity> entities);

    PageDetail workspaceToPageDetail(Workspace workspace);

    Workspace pageDetailToWorkspace(PageDetail pageDetail);


}
