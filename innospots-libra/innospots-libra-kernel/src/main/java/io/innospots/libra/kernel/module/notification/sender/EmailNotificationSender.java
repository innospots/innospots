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

package io.innospots.libra.kernel.module.notification.sender;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import io.innospots.base.model.user.UserInfo;
import io.innospots.libra.kernel.module.notification.entity.NotificationMessageEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/4/26
 */
@Slf4j
public class EmailNotificationSender implements INotificationSender {


    private MailAccount mailAccount;

    public EmailNotificationSender(MailAccount mailAccount) {
        this.mailAccount = mailAccount;
    }

    @Override
    public void send(NotificationMessageEntity messageLog, UserInfo userInfo) {
        String to = userInfo.getEmail();
        log.info("send email to:{}", to);
        if (StringUtils.isNotBlank(to)) {
            String title = messageLog.getTitle();
            String content = messageLog.getMessage();
            mailAccount.setStarttlsEnable(true);
            mailAccount.setSslEnable(false);
            String res = MailUtil.send(mailAccount, to, title, content, false);
            if (log.isDebugEnabled()) {
                log.debug("send email to:{},title:{},result:{}", to, title, res);
            }
        }
    }
}
