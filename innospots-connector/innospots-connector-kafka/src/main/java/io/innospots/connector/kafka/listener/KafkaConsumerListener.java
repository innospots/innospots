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

package io.innospots.connector.kafka.listener;

import io.innospots.base.condition.Factor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;

/**
 * @author Smars
 * @date 2021/5/8
 */
public class KafkaConsumerListener implements IConsumerListener {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerListener.class);

    private String topic;

    private Properties properties;

    private KafkaConsumer<String, String> consumer;

    private List<String> data;

    private Long endOffsets;

    // TODO 后续优化
    public KafkaConsumerListener(String topic, Properties props) {

        // 默认参数
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "default_" + topic);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "default_" + topic);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "60000");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        this.properties = props;
        this.topic = topic;

        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList(topic));
    }

    @Override
    public List<String> selectForList(List<Factor> condition, int page, int size) {

        // TODO condition
        List<String> data = listener();
        int start = (page - 1) * size;
        int end = (start + size) > data.size() ? data.size() : start + size;

        if (start > data.size()) {
            return Collections.emptyList();
        } else {
            List<String> list = new ArrayList<>();
            for (int i = start; i < end; i++) {
                list.add(data.get(i));
            }
            return list;
        }
    }

    @Override
    public List<String> selectLatest(int size) {
        List<String> data = listener();

        if (size >= data.size()) {
            return data;
        } else {
            List<String> list = new ArrayList<>();
            int start = data.size() - size;
            int end = data.size();

            for (int i = start; i < end; i++) {
                list.add(data.get(i));
            }
            return list;

        }
    }

    @Override
    public Object selectForObject(List<Factor> condition) {
        // TODO
        return null;
    }

    @Override

    public Object selectForObject(String key, String value) {
        // TODO
        return null;
    }

    @Override
    public Long endOffsets() {
        return this.endOffsets;
    }

    private List<String> listener() {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }

        this.data.clear();
        // TODO 暂时先每次都直接从消费者中读取，不提交offset
        consumer.seekToBeginning(consumer.assignment());

        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(5000));
        for (ConsumerRecord<String, String> record : records) {
            logger.info("kafka message : {}", record.toString());
            this.data.add(record.value());
            System.out.println(record.toString());
        }

        Map<TopicPartition, Long> topicPartitionLongMap = consumer.endOffsets(consumer.assignment());


        for (Map.Entry<TopicPartition, Long> entry : topicPartitionLongMap.entrySet()) {
            // TODO 目前都是一个分区 待优化
            if (entry.getKey().toString().contains(this.topic)) {
                this.endOffsets = entry.getValue();
            }
        }


        return this.data;
    }
}
