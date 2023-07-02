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

package io.innospots.base.crypto;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/9/28
 */
@Slf4j
public class AesEncryptor implements IEncryptor {

    public static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    public static final String ALGORITHM = "AES";


    private SecretKey secretKey;

    public AesEncryptor(String key) {
        this.secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
    }

    @Override
    public byte[] encode(byte[] value) {
        try {
            return IEncryptor.encrypt(value, secretKey, TRANSFORMATION);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public byte[] decode(byte[] value) {
        try {
            return IEncryptor.decrypt(value, secretKey, TRANSFORMATION);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
