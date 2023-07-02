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

package io.innospots.libra.kernel.module.i18n.service;

import io.innospots.base.exception.ResourceException;
import io.innospots.libra.base.utils.ExcelUtil;
import io.innospots.libra.kernel.module.i18n.model.I18nDictionary;
import io.innospots.libra.kernel.module.i18n.model.I18nTransMessageGroup;
import io.innospots.libra.kernel.module.i18n.operator.I18nDictionaryOperator;
import io.innospots.libra.kernel.module.i18n.operator.I18nTransMessageOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * I18nTransMessageService
 *
 * @author Wren
 * @date 2022/2/6-15:51
 */
@Slf4j
@Service
public class I18nTransMessageService {

    private I18nDictionaryOperator i18nDictionaryOperator;

    private I18nTransMessageOperator i18nTransMessageOperator;

    public I18nTransMessageService(I18nDictionaryOperator i18nDictionaryOperator,
                                   I18nTransMessageOperator i18nTransMessageOperator) {
        this.i18nDictionaryOperator = i18nDictionaryOperator;
        this.i18nTransMessageOperator = i18nTransMessageOperator;
    }

    /**
     * import trans message file
     *
     * @param filePath
     * @return
     */
    @CacheEvict(cacheNames = "locale_resource", allEntries = true)
    public Map<String, String> importCsv(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            log.error("import Csv file {} is not exist", filePath);
            throw ResourceException.buildCreateException(this.getClass(), "import Csv file {} is not exist", filePath);
        }

        Map<String, String> result = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            result = importTransMessage(reader);
        } catch (Exception e) {
            log.error("import Csv file {} error", filePath, e);
            throw ResourceException.buildCreateException(this.getClass(), "import Csv file {} is error", filePath, e.getMessage());
        }

        log.info("import Csv file {} result:{}", filePath, result);

        return result;
    }

    @CacheEvict(cacheNames = "locale_resource", allEntries = true)
    public Map<String, String> importCsv(MultipartFile file) {
        Map<String, String> result = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            result = importTransMessage(reader);
        } catch (Exception e) {
            log.error("import Csv file {} error", file.getOriginalFilename(), e);
            throw ResourceException.buildCreateException(this.getClass(), "import Csv file {} is error", file.getOriginalFilename(), e.getMessage());
        }

        log.info("import Csv file {} result:{}", file.getOriginalFilename(), result);

        return result;
    }

    private Map<String, String> importTransMessage(BufferedReader reader) throws IOException {
        Map<String, String> resultMap = new HashMap<>();
        int rowNum = 0;
        int rowSaveNum = 0;
        String headLine = reader.readLine();
        //app,module,code,description,en_US,zh_CN
        String[] headArr = headLine.split(",");
        if (headArr.length < 4) {
            log.error("import TransMessage  error head {}", headLine);
            throw ResourceException.buildCreateException(this.getClass(), "import TransMessage error head {} ", headLine);
        }

        String line = null;
        while ((line = reader.readLine()) != null) {
            rowNum++;
            String[] rowArr = line.split(",");
            if (rowArr.length != headArr.length) {
                log.warn("import Csv file line-{}:{}", rowNum, line);
                if (rowArr.length < 4) {
                    log.error("import Csv file error line-{}:{}", rowNum, line);
                    continue;
                }
            }
            String description = rowArr.length >= 4 ? rowArr[3] : "";
            if (description.length() > 128) {
                description = description.substring(0, 128);
                log.warn("description too long : {}-{}", rowNum, description);
            }

            I18nDictionary i18nDictionary = new I18nDictionary(rowArr[0], rowArr[1], rowArr[2], description);
            //trans message
            Map<String, String> messages = new HashMap<>();
            for (int i = 4; i < headArr.length; i++) {
                if (StringUtils.isBlank(rowArr[i])) {
                    continue;
                }
                String message = rowArr[i];
                if (message.startsWith("\"")) {
                    int j = i;
                    do {
                        message += ",";
                        message += rowArr[++j];
                    } while (!message.endsWith("\""));
                    message = message.substring(1, message.length() - 1);
                }
                if (message.length() > 512) {
                    message = message.substring(0, 512);
                    log.warn("message too long : {}-{}", rowNum, message);
                }
                messages.put(headArr[i], message);
            }
            boolean result = i18nDictionaryOperator.saveOrUpdate(i18nDictionary);
            if (result && !messages.isEmpty()) {
                i18nTransMessageOperator.updateTransMessageGroup(new I18nTransMessageGroup(i18nDictionary, messages));
            } else {
                log.error("import I18nDict error:{}", i18nDictionary);
            }
            rowSaveNum++;
        }
        log.info("import translate message:{}", resultMap);
        resultMap.put("rowNum", "" + rowNum);
        resultMap.put("rowSaveNum", "" + rowSaveNum);
        return resultMap;
    }

    @CacheEvict(cacheNames = "locale_resource", allEntries = true)
    public Map<String, String> importExcel(MultipartFile file) {
        Map<String, String> resultMap = new HashMap<>();
        List<List<Object>> dataList = null;
        try (InputStream inputStream = file.getInputStream()) {
            dataList = ExcelUtil.importExcel(file.getOriginalFilename(), inputStream, 0);
        } catch (Exception e) {
            log.error("import excel error", e);
            throw ResourceException.buildCreateException(this.getClass(), "import i18n trans error");
        }

        if (dataList == null || dataList.isEmpty()) {
            resultMap.put("rowNum", "0");
            resultMap.put("rowSaveNum", "0");
            return resultMap;
        }

        int rowNum = 0;
        int rowSaveNum = 0;
        //app,module,code,description,en_US,zh_CN
        List<Object> headArr = dataList.get(0);
        if (headArr.size() < 4) {
            log.error("import TransMessage  error head {}", headArr);
            throw ResourceException.buildCreateException(this.getClass(), "import TransMessage error head {} ", headArr);
        }
        log.info("upload file header:{}", headArr);

        for (int num = 1; num < dataList.size(); num++) {
            rowNum++;
            List<Object> rowArr = dataList.get(num);
            if (rowArr.size() != headArr.size()) {
                log.warn("import Csv file line-{}:{}", rowNum, rowArr);
                if (rowArr.size() < 4) {
                    log.error("import Csv file error line-{}:{}", rowNum, rowArr);
                    continue;
                }
            }
            String description = rowArr.size() >= 4 && rowArr.get(3) != null ? rowArr.get(3).toString() : "";
            if (description.length() > 128) {
                log.warn("description too long exceed 128 : {}-{}", rowNum, description);
                description = description.substring(0, 128);

            }

            I18nDictionary i18nDictionary = new I18nDictionary(rowArr.get(0).toString(), rowArr.get(1).toString(), rowArr.get(2).toString(), description);
            //trans message
            Map<String, String> messages = new HashMap<>();
            for (int i = 4; i < headArr.size(); i++) {
                if (StringUtils.isBlank(rowArr.get(i).toString())) {
                    continue;
                }
                String message = rowArr.get(i).toString();
                if (message.length() > 512) {
                    log.warn("message too long exceed 512 : {}-{}", rowNum, message);
                    message = message.substring(0, 512);
                }
                messages.put(headArr.get(i).toString(), message);
            }
            if (log.isDebugEnabled()) {
                log.debug("import translate message:{}, rowLine:{}", messages, rowArr);
            }
            boolean result = i18nDictionaryOperator.saveOrUpdate(i18nDictionary);
            if (result && !messages.isEmpty()) {
                i18nTransMessageOperator.updateTransMessageGroup(new I18nTransMessageGroup(i18nDictionary, messages));
            } else {
                log.error("import I18nDict error:{}", i18nDictionary);
            }
            rowSaveNum++;
        }
        resultMap.put("rowNum", "" + rowNum);
        resultMap.put("rowSaveNum", "" + rowSaveNum);
        log.info("import translate message:{}", resultMap);
        return resultMap;
    }
}
