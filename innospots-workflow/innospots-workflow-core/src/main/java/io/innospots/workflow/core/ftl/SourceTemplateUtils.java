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

package io.innospots.workflow.core.ftl;

import freemarker.cache.NullCacheStorage;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;
import java.io.StringWriter;

/**
 * @author Smars
 * @date 2021/4/18
 */
public class SourceTemplateUtils {

    private static final Configuration CONFIGURATION = new Configuration(Configuration.VERSION_2_3_30);

    static {
        CONFIGURATION.setDefaultEncoding("UTF-8");
        CONFIGURATION.setClassForTemplateLoading(SourceTemplateUtils.class, "/node/templates");
        CONFIGURATION.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        CONFIGURATION.setCacheStorage(NullCacheStorage.INSTANCE);
    }

    public static Template getTemplate(String templateName) throws IOException {
        try {

            return CONFIGURATION.getTemplate(templateName);
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * output script source by template source, and config template name
     *
     * @param templateName
     * @param templateSource
     * @param dataModel
     * @return
     * @throws IOException
     * @throws TemplateException
     */
    public static String output(String templateName, String templateSource, Object dataModel) throws IOException, TemplateException {
        Template template = new Template(templateName, templateSource, CONFIGURATION);
        StringWriter stringWriter = new StringWriter();
        template.process(dataModel, stringWriter);
        String source = stringWriter.toString();
        stringWriter.close();
        return source;
    }

    public static String output(String templateName, Object dataModel) throws IOException, TemplateException {
        Template template = getTemplate(templateName);
        StringWriter stringWriter = new StringWriter();
        template.process(dataModel, stringWriter);
        String source = stringWriter.toString();
        stringWriter.close();
        return source;
    }

    public static void clearCache() {
        CONFIGURATION.clearTemplateCache();
    }

}
