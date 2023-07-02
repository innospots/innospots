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

package io.innospots.libra.security.operator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.innospots.base.exception.AuthenticationException;
import io.innospots.libra.security.auth.basic.AuthUserDao;
import io.innospots.libra.security.auth.model.AuthUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author chenc
 * @date 2022/1/13
 */
@Service
public class AuthUserOperator extends ServiceImpl<AuthUserDao, AuthUser> {

    /**
     * view by id or user_name
     *
     * @param user
     * @return
     */
    public AuthUser view(AuthUser user) {
        QueryWrapper<AuthUser> query = new QueryWrapper<>();
        LambdaQueryWrapper<AuthUser> lambda = query.lambda();
        if (user.getUserId() != null) {
            lambda.eq(AuthUser::getUserId, user.getUserId());
        }
        if (StringUtils.isNotBlank(user.getUserName())) {
            lambda.eq(AuthUser::getUserName, user.getUserName());
        }
        user = super.getOne(query);
        if (user == null) {
            throw AuthenticationException.buildUserException(this.getClass());
        }
        return user;
    }

    /**
     * get by id
     *
     * @param userId
     * @return
     */
    public AuthUser get(Integer userId) {
        AuthUser user = super.getById(userId);
        user.setPassword(null);
        return user;
    }

    /**
     * get by userName
     *
     * @param userName
     * @return
     */
    public AuthUser getByUserName(String userName) {
        if (StringUtils.isBlank(userName)) {
            return null;
        }
        QueryWrapper<AuthUser> query = new QueryWrapper<>();
        query.lambda().eq(AuthUser::getUserName, userName);
        query.last("limit 1");
        AuthUser user = super.getOne(query);
        user.setPassword(null);
        return user;
    }
}