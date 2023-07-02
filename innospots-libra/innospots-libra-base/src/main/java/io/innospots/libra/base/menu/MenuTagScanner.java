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

package io.innospots.libra.base.menu;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/6/27
 */
public class MenuTagScanner {


    public static List<ModuleMenuOperation> scan(Object[] packages) {
        List<ModuleMenuOperation> menuTags = new ArrayList<>();
        Reflections reflections = new Reflections(packages);
        Set<Class<?>> clazzSet = reflections.getTypesAnnotatedWith(ModuleMenu.class);
        for (Class<?> aClass : clazzSet) {
            ModuleMenu moduleMenu = AnnotationUtils.findAnnotation(aClass, ModuleMenu.class);
            if (moduleMenu != null) {
                RequestMapping mapping = AnnotationUtils.findAnnotation(aClass, RequestMapping.class);
                String keyPrefix = aClass.getSimpleName().replace("Controller", "-");
                String path = "";
                if (mapping != null && ArrayUtils.isNotEmpty(mapping.value())) {
                    path = mapping.value()[0];
                }
                ModuleMenuOperation menuTag = new ModuleMenuOperation();
                menuTag.setControllerKey(aClass.getName());
                menuTag.setPath(path);
                menuTag.setModuleMenu(moduleMenu);
                for (Method declaredMethod : aClass.getDeclaredMethods()) {
                    ResourceItemOperation resourceItemOperation = AnnotationUtils.findAnnotation(declaredMethod, ResourceItemOperation.class);
                    if (resourceItemOperation != null) {
                        OptElement optElement = convertOptElement(path, resourceItemOperation, declaredMethod, keyPrefix);
                        menuTag.addOptElement(optElement);
                    }
                }//end for method
                menuTag.build();
                menuTags.add(menuTag);
            }
        }//end for class

        return menuTags;
    }


    private static OptElement convertOptElement(String path, ResourceItemOperation resourceItemOperation, Method method, String keyPrefix) {
        OptElement optElement = new OptElement();
        optElement.setIcon(resourceItemOperation.icon());
        optElement.setName(resourceItemOperation.name());
        optElement.setItemKey(resourceItemOperation.key());
        optElement.setOpenMode(BaseItem.OpenMode.INTERNAL);
        optElement.setItemType(resourceItemOperation.type());
        optElement.setLabel(StringUtils.isEmpty(resourceItemOperation.label()) ? resourceItemOperation.name() : resourceItemOperation.label());

        if (StringUtils.isEmpty(optElement.getItemKey())) {
            optElement.setItemKey(keyPrefix + method.getName());
            if (optElement.getItemKey().length() >= 64) {
                optElement.setItemKey(optElement.getItemKey().substring(0, 64));
            }
        }
        if (path.endsWith("/")) {
            path = path.substring(0, keyPrefix.length() - 1);
        }
        GetMapping getMapping = AnnotationUtils.getAnnotation(method, GetMapping.class);
        if (getMapping != null) {
            if (!ArrayUtils.isEmpty(getMapping.value())) {
                if (!getMapping.value()[0].startsWith("/")) {
                    path += "/";
                }
                path += getMapping.value()[0];
            }
            optElement.setMethod(OptElement.UriMethod.GET);
            optElement.setUri(path);
            return optElement;
        }
        PostMapping postMapping = AnnotationUtils.getAnnotation(method, PostMapping.class);
        if (postMapping != null) {
            if (ArrayUtils.isNotEmpty(postMapping.value())) {
                if (!postMapping.value()[0].startsWith("/")) {
                    path += "/";
                }
                path += postMapping.value()[0];
            }

            optElement.setMethod(OptElement.UriMethod.POST);
            optElement.setUri(path);
            return optElement;
        }
        PutMapping putMapping = AnnotationUtils.getAnnotation(method, PutMapping.class);
        if (putMapping != null) {
            if (ArrayUtils.isNotEmpty(putMapping.value())) {
                if (!putMapping.value()[0].startsWith("/")) {
                    path += "/";
                }
                path += putMapping.value()[0];
            }
            optElement.setMethod(OptElement.UriMethod.PUT);
            optElement.setUri(path);
            return optElement;
        }
        DeleteMapping deleteMapping = AnnotationUtils.getAnnotation(method, DeleteMapping.class);
        if (deleteMapping != null) {
            if (ArrayUtils.isNotEmpty(deleteMapping.value())) {
                if (!deleteMapping.value()[0].startsWith("/")) {
                    path += "/";
                }
                path += deleteMapping.value()[0];
            }

            optElement.setMethod(OptElement.UriMethod.DELETE);
            optElement.setUri(path);
            return optElement;
        }
        RequestMapping requestMapping = AnnotationUtils.getAnnotation(method, RequestMapping.class);
        if (requestMapping != null) {
            if (ArrayUtils.isNotEmpty(requestMapping.value())) {
                if (!requestMapping.value()[0].startsWith("/")) {
                    path += "/";
                }
                path += requestMapping.value()[0];
            }
            optElement.setMethod(OptElement.UriMethod.valueOf(requestMapping.method()[0].name()));
            optElement.setUri(path);
            return optElement;
        }
        return optElement;
    }
}
