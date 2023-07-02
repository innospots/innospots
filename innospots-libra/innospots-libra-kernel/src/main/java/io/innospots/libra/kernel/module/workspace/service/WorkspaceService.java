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

package io.innospots.libra.kernel.module.workspace.service;

import io.innospots.base.constant.Constants;
import io.innospots.base.json.JSONUtils;
import io.innospots.base.utils.DateTimeUtils;
import io.innospots.base.utils.HttpUtils;
import io.innospots.libra.kernel.module.workspace.model.News;
import io.innospots.libra.kernel.module.workspace.model.NewsInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WorkspaceService {

    public News getActivityInfo() {
        String rspStr = HttpUtils.sendGet(Constants.ACTIVITY_URL);
        if (StringUtils.isNotBlank(rspStr)) {
            List<Map<String, Object>> resultList = JSONUtils.toMapList(rspStr, Map.class);
            if (CollectionUtils.isEmpty(resultList)) {
                return getDefaultActivity();
            }
            Map<String, Object> map = resultList.get(0);
            News news = new News();
            String date = map.get("date").toString().replace("T", " ");
            news.setDate(DateTimeUtils.formatDate(DateTimeUtils.parseDate(date, DateTimeUtils.DEFAULT_DATETIME_PATTERN), DateTimeUtils.DEFAULT_DATE_PATTERN));
            news.setLink(map.get("link").toString());
            LinkedHashMap<String, Object> titleMap = (LinkedHashMap<String, Object>) map.get("title");
            news.setTitle(titleMap.get("rendered").toString());
            return news;
        }
        return getDefaultActivity();
    }

    public NewsInfo getNewsInfo() {
        String rspStr = HttpUtils.sendGet(Constants.NEWS_URL);
        if (StringUtils.isNotBlank(rspStr)) {
            List<Map<String, Object>> resultList = JSONUtils.toMapList(rspStr, Map.class);
            if (CollectionUtils.isEmpty(resultList)) {
                return getDefaultNews();
            }
            NewsInfo newsInfo = new NewsInfo();
            List<News> newsList = new ArrayList<>();
            int i = 0;
            for (Map<String, Object> map : resultList) {
                if (i == 0) {
                    LinkedHashMap<String, Object> embeddedMap = (LinkedHashMap<String, Object>) map.get("_embedded");
                    List<Map<String, Object>> mediaList = (List<Map<String, Object>>) embeddedMap.get("wp:featuredmedia");
                    if (CollectionUtils.isNotEmpty(mediaList)) {
                        Map<String, Object> media = mediaList.get(0);
                        LinkedHashMap<String, Object> mediaDetailMap = (LinkedHashMap<String, Object>) media.get("media_details");
                        LinkedHashMap<String, Object> sizeMap = (LinkedHashMap<String, Object>) mediaDetailMap.get("sizes");
                        LinkedHashMap<String, Object> mediumMap = (LinkedHashMap<String, Object>) sizeMap.get("full");
                        newsInfo.setSourceUrl(mediumMap.get("source_url").toString());
                    }
                }
                News news = new News();
                String date = map.get("date").toString().replace("T", " ");
                news.setDate(DateTimeUtils.formatDate(DateTimeUtils.parseDate(date, DateTimeUtils.DEFAULT_DATETIME_PATTERN), DateTimeUtils.DEFAULT_DATE_PATTERN));
                news.setLink(map.get("link").toString());
                LinkedHashMap<String, Object> titleMap = (LinkedHashMap<String, Object>) map.get("title");
                news.setTitle(titleMap.get("rendered").toString());
                newsList.add(news);
                i++;
            }
            newsInfo.setNews(newsList);
            return newsInfo;
        }
        return getDefaultNews();
    }

    private News getDefaultActivity() {
        News news = new News();
        news.setTitle("参与Innospots v1.0 RC版本测试赢取200元京东购物卡！");
        return news;
    }

    private NewsInfo getDefaultNews() {
        NewsInfo newsInfo = new NewsInfo();
        newsInfo.setSourceUrl("http://innospots.com/wp-content/uploads/2022/04/partner-300x138.jpg");
        List<News> newsList = new ArrayList<>();
        News news = new News();
        news.setDate("2019-07-11");
        news.setTitle("隆重宣布Innospots Libra v1.0 Release Candidate发布！");
        newsList.add(news);
        news = new News();
        news.setDate("2022-09-28");
        news.setTitle("隆重宣布Innospots Libra v1.0 Release Candidate发布！");
        newsList.add(news);
        news = new News();
        news.setDate("2020-10-01");
        news.setTitle("隆重宣布Innospots Libra v1.0 Release Candidate发布！");
        newsList.add(news);
        news = new News();
        news.setDate("2021-01-31");
        news.setTitle("隆重宣布Innospots Libra v1.0 Release Candidate发布！");
        newsList.add(news);
        newsInfo.setNews(newsList);
        return newsInfo;
    }
}