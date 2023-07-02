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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.innospots.base.utils.BeanUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author Smars
 * @date 2021/11/30
 */
@Getter
@Setter
public class ResourceItem extends BaseItem {

    private int orders;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ResourceItem> items;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<OptElement> opts;

    @JsonIgnore
    private int level = 1;

    /**
     * controller api path
     */
    private String path;

    /**
     * page urls define in the controller
     */
    private Set<String> pageUris = new HashSet<>();

    @JsonIgnore
    private List<ModuleMenuOperation> moduleMenuOperations = new ArrayList<>();

    public ResourceItem moduleItem() {
        ResourceItem item = new ResourceItem();
        BeanUtils.copyProperties(this, item);
        item.fillI18n();
        List<ResourceItem> subItems = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(items)) {
            for (ResourceItem subItem : items) {
                ResourceItem nItem = subItem.moduleItem();
                subItems.add(nItem);
            }
        }
        item.setItems(subItems);
        if (CollectionUtils.isNotEmpty(item.getOpts())) {
            item.setOpts(null);
        }
        item.fillDefaultSubItem();
        return item;
    }

    public ResourceItem cloneI18nItem() {
        ResourceItem item = new ResourceItem();
        BeanUtils.copyProperties(this, item);
        item.fillI18n();
        List<ResourceItem> subItems = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(items)) {
            for (ResourceItem subItem : items) {
                ResourceItem nItem = subItem.cloneI18nItem();
                subItems.add(nItem);
            }
        }
        item.setItems(subItems);
        if (CollectionUtils.isNotEmpty(opts)) {
            List<OptElement> newOpts = new ArrayList<>();
            for (OptElement opt : opts) {
                OptElement subOpt = new OptElement();
                BeanUtils.copyProperties(opt, subOpt);
                subOpt.fillI18n();
                newOpts.add(subOpt);
            }
            item.setOpts(newOpts);
        }
        if (StringUtils.isNotBlank(item.getUri())) {
            item.addPageUri(item.getUri());
        }
        return item;
    }

    public void addPageUri(String pageUri) {
        this.pageUris.add(pageUri);
    }

    private void fillDefaultSubItem() {
        if (level >= 3) {
            return;
        }
        if (this.loadMode == LoadMode.DYNAMIC) {
            return;
        }

        if (CollectionUtils.isEmpty(this.items)) {
            ResourceItem subItem = new ResourceItem();
            BeanUtils.copyProperties(this, subItem);
            subItem.setName("默认");
            subItem.setI18nNames(null);
            subItem.setLevel(level + 1);
            List<ResourceItem> subItems = new ArrayList<>();
            subItems.add(subItem);
            this.items = subItems;
            subItem.fillDefaultSubItem();
        } else {
            for (ResourceItem item : this.items) {
                item.fillDefaultSubItem();
            }
        }
    }

    private List<ResourceItem> cloneSubItems() {
        List<ResourceItem> subItems = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(items)) {
            for (ResourceItem subItem : items) {
                ResourceItem nItem = subItem.cloneI18nItem();
                subItems.add(nItem);
            }
        }
        return subItems;
    }

    public void addOpt(OptElement optElement) {
        if (opts == null) {
            opts = new ArrayList<>();
        }
        opts.add(optElement);
    }

    public void addMenuOperation(ModuleMenuOperation moduleMenuOperation) {
        if (this.moduleMenuOperations == null) {
            this.moduleMenuOperations = new ArrayList<>();
        }
        this.moduleMenuOperations.add(moduleMenuOperation);
    }

    public boolean matchPageUrl(String pageUrl) {
        boolean match = false;
        if (this.uri != null) {
            match = pageUrl.startsWith(uri);
        }
        if (match) {
            return true;
        }
        return this.pageUris.stream().anyMatch(pageUrl::startsWith);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("itemKey='").append(itemKey).append('\'');
        sb.append(", path='").append(path).append("'");
        sb.append(", uri='").append(uri).append('\'');
        sb.append(", opt size=").append(opts == null ? 0 : opts.size());
        sb.append(", orders=").append(orders);
        sb.append(", loadMode=").append(loadMode);
        sb.append(", name='").append(name).append('\'');
        sb.append(", icon='").append(icon).append('\'');
        sb.append(", itemType=").append(itemType);
        sb.append(", openMode=").append(openMode);
        sb.append(", subItem size=").append(items == null ? 0 : items.size());
        sb.append('}');
        return sb.toString();
    }

    public String line(boolean opt) {
        StringBuilder line = new StringBuilder();
        line.append(outItemLine(this, null, opt));
        return line.toString();
    }


    private String outItemLine(BaseItem baseItem, BaseItem parent, boolean opt) {
        StringBuilder item = new StringBuilder();
        item.append(baseItem.resourceId).append(",");
        item.append("1").append(",")
                .append(baseItem.name).append(",")
                .append(baseItem.icon).append(",")
                .append(baseItem.openMode).append(",")
                .append("system").append(",")
                //show_menu
                .append("1").append(",");
        if (parent != null) {
            item.append(parent.resourceId).append(",");
        } else {
            item.append("0").append(",");
        }
        //orders
        item.append("0").append(",")
                .append(baseItem.uri).append(",")
                .append(baseItem.itemType == null ? ItemType.MENU : baseItem.itemType).append(",");
        if (baseItem instanceof OptElement) {
            item.append(((OptElement) baseItem).getMethod()).append(",");
        } else {
            item.append("").append(",");
        }
        if (baseItem instanceof ResourceItem) {
            item.append(((ResourceItem) baseItem).loadMode).append(",");
            item.append(items == null ? 0 : items.size()).append(",");
            item.append(opts == null ? 0 : opts.size()).append(",");
        } else {
            item.append("").append(",");
            item.append("0").append(",");
            item.append("0").append(",");
        }

        if (baseItem.i18nNames != null) {
            item.append(baseItem.i18nNames);
        } else {
            item.append(new HashMap<>());
        }

        item.append("\n");

        if (baseItem instanceof ResourceItem) {
            if (((ResourceItem) baseItem).items != null) {
                for (ResourceItem subItem : ((ResourceItem) baseItem).items) {
                    item.append(outItemLine(subItem, baseItem, opt));
                }

            }

            if (((ResourceItem) baseItem).opts != null && opt) {
                for (OptElement optElement : ((ResourceItem) baseItem).opts) {
                    item.append(outItemLine(optElement, baseItem, opt));
                }
            }
        }

        return item.toString();
    }
}
