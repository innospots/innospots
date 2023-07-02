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

import java.lang.annotation.*;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/2/19
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResourceItemOperation {

    /**
     * button name or link name, the name can be empty if type is api
     */
    String name() default "";

    /**
     * operation item key, default key: controller name + '-' + method name
     */
    String key() default "";

    /**
     *
     */
    String label() default "";

    /**
     * operation item icon
     */
    String icon() default "";

    /**
     * if true, the resource access is not restricted by permission
     */
    boolean skipPermission() default false;

    /**
     * operation item type, eg: api, button, link, menu, page, category etc.
     */
    ItemType type() default ItemType.API;

    /**
     * operation item Owning the parent menu item key
     */
    String parent() default "";

}
