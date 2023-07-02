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

package io.innospots.libra.kernel.module.system.operator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.innospots.libra.kernel.module.system.dao.UserRoleDao;
import io.innospots.libra.kernel.module.system.entity.UserRoleEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author chenc
 * @date 2021/5/29 16:58
 */
@Service
public class UserRoleOperator extends ServiceImpl<UserRoleDao, UserRoleEntity> {

    /**
     * create
     *
     * @param userIds
     * @param roleIds
     */
    public boolean saveUserRoles(List<Integer> userIds, List<Integer> roleIds) {
        if (CollectionUtils.isEmpty(userIds) && CollectionUtils.isEmpty(roleIds)) {
            return false;
        }

        if (CollectionUtils.isEmpty(userIds)) {
            return this.deleteByRoleIds(roleIds);
        }

        if (CollectionUtils.isEmpty(roleIds)) {
            return this.deleteByUserIds(userIds);
        }

        List<UserRoleEntity> dbUrs = null;
        QueryWrapper<UserRoleEntity> query = new QueryWrapper<>();
        query.lambda().in(UserRoleEntity::getUserId, userIds);
        dbUrs = this.list(query);

        ArrayListValuedHashMap<Integer, UserRoleEntity> userRoles = new ArrayListValuedHashMap<>();
        for (UserRoleEntity dbUr : dbUrs) {
            userRoles.put(dbUr.getUserId(), dbUr);
        }

        Set<Integer> removed = new HashSet<>();
        List<UserRoleEntity> newRoles = new ArrayList<>();


        for (Integer userId : userIds) {
            List<UserRoleEntity> storedRoles = userRoles.get(userId);
            if (CollectionUtils.isEmpty(storedRoles)) {
                for (Integer roleId : roleIds) {
                    newRoles.add(new UserRoleEntity(userId, roleId));
                }
            } else {
                List<Integer> addRoleIds = new ArrayList<>(roleIds);
                for (UserRoleEntity storedRole : storedRoles) {
                    if (!addRoleIds.contains(storedRole.getRoleId())) {
                        removed.add(storedRole.getUserRoleId());
                    } else {
                        addRoleIds.remove(storedRole.getRoleId());
                    }
                }//end for
                if (!addRoleIds.isEmpty()) {
                    for (Integer addRoleId : addRoleIds) {
                        newRoles.add(new UserRoleEntity(userId, addRoleId));
                    }
                }
            }
        }//end for

        boolean up = true;

        if (!newRoles.isEmpty()) {
            up = this.saveBatch(newRoles);
        }

        if (!removed.isEmpty()) {
            up = this.removeByIds(removed) && up;
        }

        return up;
    }

    public boolean saveUserRoles(Integer userId, List<Integer> roleIds) {
        return saveUserRoles(Collections.singletonList(userId), roleIds);
    }

    public boolean saveUserRoles(List<Integer> userIds, Integer roleId) {
        return saveUserRoles(userIds, Collections.singletonList(roleId));
    }

    /**
     * delete
     *
     * @param roleIds
     */
    public boolean deleteByRoleIds(List<Integer> roleIds) {
        QueryWrapper<UserRoleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(UserRoleEntity::getRoleId, roleIds);
        return this.remove(queryWrapper);
    }

    public boolean deleteByUserIds(List<Integer> userIds) {
        QueryWrapper<UserRoleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(UserRoleEntity::getUserId, userIds);
        return this.remove(queryWrapper);
    }

    public boolean delete(Integer userId, Integer roleId) {
        QueryWrapper<UserRoleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserRoleEntity::getUserId, userId).eq(UserRoleEntity::getRoleId, roleId);
        return this.remove(queryWrapper);
    }

    public List<UserRoleEntity> selectRoleByUserId(Integer userId) {
        QueryWrapper<UserRoleEntity> query = new QueryWrapper<>();
        query.lambda().eq(UserRoleEntity::getUserId, userId);
        return this.list(query);
    }

    /**
     * list by userId&roleId
     *
     * @param userIds
     * @param roleIds
     * @return
     */
    private List<UserRoleEntity> selectByUserIdAndRoleId(List<Integer> userIds, List<Integer> roleIds) {
        if (CollectionUtils.isEmpty(userIds) && CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        QueryWrapper<UserRoleEntity> query = new QueryWrapper<>();
        LambdaQueryWrapper<UserRoleEntity> lambda = query.lambda();
        if (CollectionUtils.isNotEmpty(userIds)) {
            lambda.in(UserRoleEntity::getUserId, userIds);
        }
        if (CollectionUtils.isNotEmpty(roleIds)) {
            lambda.in(UserRoleEntity::getRoleId, roleIds);
        }
        return super.list(query);
    }

    public List<UserRoleEntity> selectRoleByUserIds(List<Integer> userIds) {
        return selectByUserIdAndRoleId(userIds, null);
    }

    public List<UserRoleEntity> selectByRoleIds(List<Integer> roleIds) {
        return selectByUserIdAndRoleId(null, roleIds);
    }

    public List<Map<String, Object>> selectCountByRoleId() {
        return super.listMaps(
                new QueryWrapper<UserRoleEntity>()
                        .select("ROLE_ID, COUNT(*) NUM ")
                        .groupBy("ROLE_ID"));
    }


    public boolean hashRole(Integer userId, Integer roleId) {
        QueryWrapper<UserRoleEntity> query = new QueryWrapper<>();
        query.lambda().eq(UserRoleEntity::getRoleId, roleId)
                .eq(UserRoleEntity::getUserId, userId);
        return this.count(query) > 0;
    }
}