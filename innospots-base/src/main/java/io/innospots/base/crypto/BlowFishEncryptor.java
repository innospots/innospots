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
import java.util.Arrays;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/9/28
 */
@Slf4j
public class BlowFishEncryptor implements IEncryptor {

    // Implement ZeroPadding
    public static final String TRANSFORMATION = "Blowfish/ECB/NoPadding";
    public static final String ALGORITHM = "Blowfish";


    private SecretKey secretKey;

    public BlowFishEncryptor(String key) {
        this.secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
    }

    @Override
    public byte[] encode(byte[] value) {
        try {
            int length = value.length;
            int blockSize = 8;
            if (length % blockSize != 0) {
                length = length + (blockSize - (length % blockSize));
            }
            byte[] fillValue = new byte[length];
            System.arraycopy(value, 0, fillValue, 0, value.length);
            return IEncryptor.encrypt(fillValue, secretKey, TRANSFORMATION);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public byte[] decode(byte[] value) {
        try {
            byte[] doFinal = IEncryptor.decrypt(value, secretKey, TRANSFORMATION);
            int zeroIndex = doFinal.length;
            for (int i = doFinal.length - 1; i > 0; i--) {
                if (doFinal[i] == (byte) 0) {
                    zeroIndex = i;
                } else {
                    break;
                }
            }
            // Delete the padded 0 at the end
            doFinal = Arrays.copyOf(doFinal, zeroIndex);
            return doFinal;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
