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

package io.innospots.base.json.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.annotation.*;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2021/12/19
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@JacksonAnnotationsInside
@Target(ElementType.FIELD)
@JsonSerialize(using = MaskValue.MaskValueFieldSerializer.class)
public @interface MaskValue {

    /**
     * default mask character
     */
    char DEFAULT_REPLACE_STRING = '*';

    /**
     * desensitization mode
     */
    Mode mode() default Mode.TOTAL;

    /**
     * mask length of the values, ignore this setting if the value of mode is 'TOTAL'
     */
    int length() default 0;

    /**
     * replace character
     */
    char replaceChar() default DEFAULT_REPLACE_STRING;

    enum Mode {
        /**
         * replace all
         */
        TOTAL,
        /**
         * from left to replace
         */
        LEFT,
        /**
         * from right to replace
         */
        RIGHT
    }


    class MaskValueFieldSerializer extends JsonSerializer<String> implements ContextualSerializer {

        private MaskValue maskValue;

        public MaskValueFieldSerializer(MaskValue maskValue) {
            this.maskValue = maskValue;
        }

        public MaskValueFieldSerializer() {
        }

        @Override
        public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (StringUtils.isBlank(s)) {
                jsonGenerator.writeString(s);
                return;
            }

            if (maskValue != null) {
                final Mode mode = maskValue.mode();
                final int length = maskValue.length();
                final String replaceString = String.valueOf(maskValue.replaceChar());
                jsonGenerator.writeString(getValue(s, mode, length, replaceString));
            } else {
                jsonGenerator.writeString(s);
            }
        }

        @Override
        public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
            MaskValue annotation = beanProperty.getAnnotation(MaskValue.class);

            if (annotation != null) {
                return new MaskValueFieldSerializer(annotation);
            }
            return this;
        }

        private String getValue(String rawStr, MaskValue.Mode mode, int length, String replaceString) {
            switch (mode) {
                case TOTAL:
                    return rawStr.replaceAll("[\\s\\S]", replaceString);
                case LEFT:
                    return replaceByLength(rawStr, length, replaceString, true);
                case RIGHT:
                    return replaceByLength(rawStr, length, replaceString, false);
                default:
                    throw new IllegalArgumentException("Illegal mask mode");
            }
        }

        private String replaceByLength(String rawStr, int length, String replaceString, boolean fromLeft) {
            if (StringUtils.isBlank(rawStr)) {
                return rawStr;
            }
            if (rawStr.length() <= length) {
                return rawStr.replaceAll("[\\s\\S]", replaceString);
            }

            if (fromLeft) {
                return getSpecStringSequence(length, replaceString) + rawStr.substring(length);
            } else {
                return rawStr.substring(0, length) + getSpecStringSequence(length, replaceString);
            }
        }

        private String getSpecStringSequence(int length, String str) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                stringBuilder.append(str);
            }
            return stringBuilder.toString();
        }
    }

}
