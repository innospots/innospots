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

package io.innospots.libra.base.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/7/5
 */
@Slf4j
public class ClassJarFileLoader {

    /**
     * load a single JAR package
     *
     * @param jarUrl
     */


    public static void loadJar(String jarUrl) {
        try {
            URI uri = new URI(jarUrl);
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);

            method.setAccessible(true);
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

            URL url = uri.toURL();
            method.invoke(classLoader, url);
            log.info("load jar success jraUrl: " + jarUrl);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void closeJar(String jarUrl) {
        try {
            URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[]{new URL(jarUrl)});
            urlClassLoader.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
