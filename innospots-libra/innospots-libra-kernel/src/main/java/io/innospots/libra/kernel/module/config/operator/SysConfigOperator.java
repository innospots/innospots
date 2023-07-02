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

package io.innospots.libra.kernel.module.config.operator;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.innospots.base.json.JSONUtils;
import io.innospots.base.model.system.OrganizationInfo;
import io.innospots.base.utils.BeanUtils;
import io.innospots.base.utils.CCH;
import io.innospots.libra.kernel.module.config.dao.SysConfigDao;
import io.innospots.libra.kernel.module.config.entity.SysConfigEntity;
import io.innospots.libra.kernel.module.config.enums.ConfigGroup;
import io.innospots.libra.kernel.module.config.model.EmailServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/1/15
 */
@Slf4j
@Component
public class SysConfigOperator extends ServiceImpl<SysConfigDao, SysConfigEntity> {

    private static final String CFG_PASSWORD = "password";

    public OrganizationInfo getOrganization() {
        return getModel(ConfigGroup.ORG, OrganizationInfo.class);
    }

    public EmailServerInfo getEmailServerInfo() {
        EmailServerInfo emailServerInfo = getModel(ConfigGroup.EMAIL, EmailServerInfo.class);
        return emailServerInfo;
    }


    @Transactional
    public Integer saveOrganizationConfig(OrganizationInfo organizationInfo) {
        Map<String, Object> mapBean = JSONUtils.objectToMap(organizationInfo);
        //remove logo and
        mapBean.remove("favIcon");
        mapBean.remove("logo");
        mapBean.put(CCH.PROJECT_ID_KEY, CCH.projectId());
        mapBean.put(CCH.ORGANIZATION_ID_KEY, CCH.organizationId());
        return saveConfigItem(ConfigGroup.ORG, mapBean);
    }

    @Transactional
    public Integer saveEmailServerConfig(EmailServerInfo emailServerInfo) {
        // pwd is encrypt
        String pwd = emailServerInfo.getPassword();
        Map<String, Object> mapBean = JSONUtils.objectToMap(emailServerInfo);
        if (!StringUtils.isEmpty(pwd)) {
            mapBean.put(CFG_PASSWORD, pwd);
        } else {
            //密码字段为空不更新保存
            if (mapBean.containsKey(CFG_PASSWORD)) {
                mapBean.remove(CFG_PASSWORD);
            }
        }
        mapBean.put(CCH.PROJECT_ID_KEY, CCH.projectId());
        mapBean.put(CCH.ORGANIZATION_ID_KEY, CCH.organizationId());
        return saveConfigItem(ConfigGroup.EMAIL, mapBean);
    }

    private <T> T getModel(ConfigGroup configGroup, Class<T> clazz) {
        QueryWrapper<SysConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysConfigEntity::getConfigGroup, configGroup);
        List<SysConfigEntity> sysConfigEntities = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(sysConfigEntities)) {
            Map<String, Object> bean = new HashMap<>(sysConfigEntities.size());
            for (SysConfigEntity configEntity : sysConfigEntities) {
                bean.put(configEntity.getConfigCode(), configEntity.getConfigValue());
            }
            return BeanUtils.toBean(bean, clazz);
        }
        return null;
    }

    private Integer saveConfigItem(ConfigGroup configGroup, Map<String, Object> configItems) {
        SysConfigEntity sysConfig = null;
        QueryWrapper<SysConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysConfigEntity::getConfigGroup, configGroup);
        List<SysConfigEntity> configEntities = this.list(queryWrapper);

        Map<String, SysConfigEntity> oldConfigMap = null;
        if (CollectionUtils.isNotEmpty(configEntities)) {
            oldConfigMap = configEntities.stream().collect(Collectors.toMap(SysConfigEntity::getConfigCode, Function.identity()));
        }

        Map<String, Object> addMap = new HashMap<>();
        List<SysConfigEntity> updateList = new ArrayList<>();

        for (String code : configItems.keySet()) {
            if (oldConfigMap != null && !oldConfigMap.containsKey(code)) {
                addMap.put(code, configItems.get(code));
            } else {
                SysConfigEntity configEntity = oldConfigMap.get(code);
                // value change to update
                if (configEntity.getConfigValue() != null && !configEntity.getConfigValue().equals(String.valueOf(configItems.get(configEntity.getConfigCode())))) {
                    configEntity.setConfigCode(String.valueOf(configItems.get(configEntity.getConfigCode())));
                    configEntity.setUpdatedTime(LocalDateTime.now());
                    updateList.add(configEntity);
                }
            }
        }
        if (!updateList.isEmpty()) {
            this.updateBatchById(updateList);
        }

        if (addMap != null && !addMap.isEmpty()) {
            List<SysConfigEntity> addList = new ArrayList<>();
            for (Map.Entry<String, Object> objectEntry : addMap.entrySet()) {
                sysConfig = new SysConfigEntity();
                sysConfig.setConfigGroup(configGroup.name());
                sysConfig.setConfigName(objectEntry.getKey());
                sysConfig.setConfigCode(objectEntry.getKey());
                sysConfig.setConfigValue(String.valueOf(objectEntry.getValue()));
                addList.add(sysConfig);
            }
            this.saveBatch(addList);
        }

        return 1;
    }


}
