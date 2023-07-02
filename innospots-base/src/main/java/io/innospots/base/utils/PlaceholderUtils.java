
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

package io.innospots.base.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * placeholder utils
 */
public class PlaceholderUtils {

    private static final Logger logger = LoggerFactory.getLogger(PlaceholderUtils.class);

    /**
     * Prefix of the position to be replaced
     */
    public static final String PLACEHOLDER_PREFIX = "${";

    /**
     * The suffix of the position to be replaced
     */

    public static final String PLACEHOLDER_SUFFIX = "}";

    private static final PropertyPlaceholderHelper strictHelper = getPropertyPlaceholderHelper(false);
    private static final PropertyPlaceholderHelper nonStrictHelper = getPropertyPlaceholderHelper(true);

    public static String replacePlaceholders(String value,
                                             Map<String, String> paramsMap){
        return replacePlaceholders(value,paramsMap,true);
    }

    public static String replacePlaceholders(String value,
                                             Map<String, String> paramsMap,
                                             boolean ignoreUnresolvablePlaceholders) {
        //replacement tool， parameter key will be replaced by value,if can't match , will throw an exception
//        PropertyPlaceholderHelper strictHelper = getPropertyPlaceholderHelper(false);

        //Non-strict replacement tool implementation, when the position to be replaced does not get the corresponding value, the current position is ignored, and the next position is replaced.
//        PropertyPlaceholderHelper nonStrictHelper = getPropertyPlaceholderHelper(true);

        PropertyPlaceholderHelper helper = (ignoreUnresolvablePlaceholders ? nonStrictHelper : strictHelper);

        //the PlaceholderResolver to use for replacement
        return helper.replacePlaceholders(value, new PropertyPlaceholderResolver(value, paramsMap));
    }

    public static PropertyPlaceholderHelper getPropertyPlaceholderHelper(boolean ignoreUnresolvablePlaceholders) {

        return new PropertyPlaceholderHelper(PLACEHOLDER_PREFIX, PLACEHOLDER_SUFFIX, null, ignoreUnresolvablePlaceholders);
    }

    /**
     * Placeholder replacement resolver
     */
    private static class PropertyPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {

        private final String value;

        private final Map<String, String> paramsMap;

        public PropertyPlaceholderResolver(String value, Map<String, String> paramsMap) {
            this.value = value;
            this.paramsMap = paramsMap;
        }

        @Override
        public String resolvePlaceholder(String placeholderName) {
            try {
                return paramsMap.get(placeholderName);
            } catch (Exception ex) {
                logger.error("resolve placeholder '{}' in [ {} ]", placeholderName, value, ex);
                return null;
            }
        }
    }

    private static class PropertyPlaceholderHelper {

        private static final Logger logger = LoggerFactory.getLogger(PropertyPlaceholderHelper.class);

        private static final Map<String, String> WELL_KNOWN_SIMPLE_PREFIXES = new HashMap<String, String>(4);

        static {
            WELL_KNOWN_SIMPLE_PREFIXES.put("}", "{");
            WELL_KNOWN_SIMPLE_PREFIXES.put("]", "[");
            WELL_KNOWN_SIMPLE_PREFIXES.put(")", "(");
        }


        private final String placeholderPrefix;

        private final String placeholderSuffix;

        private final String simplePrefix;

        private final String valueSeparator;

        private final boolean ignoreUnresolvablePlaceholders;


        public PropertyPlaceholderHelper(String placeholderPrefix, String placeholderSuffix) {
            this(placeholderPrefix, placeholderSuffix, null, true);
        }


        public PropertyPlaceholderHelper(String placeholderPrefix, String placeholderSuffix,
                                         String valueSeparator, boolean ignoreUnresolvablePlaceholders) {

            notNull(placeholderPrefix, "'placeholderPrefix' must not be null");
            notNull(placeholderSuffix, "'placeholderSuffix' must not be null");
            this.placeholderPrefix = placeholderPrefix;
            this.placeholderSuffix = placeholderSuffix;
            String simplePrefixForSuffix = WELL_KNOWN_SIMPLE_PREFIXES.get(this.placeholderSuffix);
            if (simplePrefixForSuffix != null && this.placeholderPrefix.endsWith(simplePrefixForSuffix)) {
                this.simplePrefix = simplePrefixForSuffix;
            } else {
                this.simplePrefix = this.placeholderPrefix;
            }
            this.valueSeparator = valueSeparator;
            this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
        }


        public String replacePlaceholders(String value, final Properties properties) {
            notNull(properties, "'properties' must not be null");
            return replacePlaceholders(value, new PropertyPlaceholderHelper.PlaceholderResolver() {
                @Override
                public String resolvePlaceholder(String placeholderName) {
                    return properties.getProperty(placeholderName);
                }
            });
        }


        public String replacePlaceholders(String value, PropertyPlaceholderHelper.PlaceholderResolver placeholderResolver) {
            notNull(value, "'value' must not be null");
            return parseStringValue(value, placeholderResolver, new HashSet<String>());
        }

        protected String parseStringValue(
                String value, PropertyPlaceholderHelper.PlaceholderResolver placeholderResolver, Set<String> visitedPlaceholders) {

            StringBuilder result = new StringBuilder(value);

            int startIndex = value.indexOf(this.placeholderPrefix);
            while (startIndex != -1) {
                int endIndex = findPlaceholderEndIndex(result, startIndex);
                if (endIndex != -1) {
                    String placeholder = result.substring(startIndex + this.placeholderPrefix.length(), endIndex);
                    String originalPlaceholder = placeholder;
                    if (!visitedPlaceholders.add(originalPlaceholder)) {
                        throw new IllegalArgumentException(
                                "Circular placeholder reference '" + originalPlaceholder + "' in property definitions");
                    }
                    // Recursive invocation, parsing placeholders contained in the placeholder key.
                    placeholder = parseStringValue(placeholder, placeholderResolver, visitedPlaceholders);
                    // Now obtain the value for the fully resolved key...
                    String propVal = placeholderResolver.resolvePlaceholder(placeholder);
                    if (propVal == null && this.valueSeparator != null) {
                        int separatorIndex = placeholder.indexOf(this.valueSeparator);
                        if (separatorIndex != -1) {
                            String actualPlaceholder = placeholder.substring(0, separatorIndex);
                            String defaultValue = placeholder.substring(separatorIndex + this.valueSeparator.length());
                            propVal = placeholderResolver.resolvePlaceholder(actualPlaceholder);
                            if (propVal == null) {
                                propVal = defaultValue;
                            }
                        }
                    }
                    if (propVal != null) {
                        // Recursive invocation, parsing placeholders contained in the
                        // previously resolved placeholder value.
                        propVal = parseStringValue(propVal, placeholderResolver, visitedPlaceholders);
                        result.replace(startIndex, endIndex + this.placeholderSuffix.length(), propVal);
                        if (logger.isTraceEnabled()) {
                            logger.trace("Resolved placeholder '" + placeholder + "'");
                        }
                        startIndex = result.indexOf(this.placeholderPrefix, startIndex + propVal.length());
                    } else if (this.ignoreUnresolvablePlaceholders) {
                        // Proceed with unprocessed value.
                        startIndex = result.indexOf(this.placeholderPrefix, endIndex + this.placeholderSuffix.length());
                    } else {
                        throw new IllegalArgumentException("Could not resolve placeholder '" +
                                placeholder + "'" + " in value \"" + value + "\"");
                    }
                    visitedPlaceholders.remove(originalPlaceholder);
                } else {
                    startIndex = -1;
                }
            }

            return result.toString();
        }

        private int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
            int index = startIndex + this.placeholderPrefix.length();
            int withinNestedPlaceholder = 0;
            while (index < buf.length()) {
                if (substringMatch(buf, index, this.placeholderSuffix)) {
                    if (withinNestedPlaceholder > 0) {
                        withinNestedPlaceholder--;
                        index = index + this.placeholderSuffix.length();
                    } else {
                        return index;
                    }
                } else if (substringMatch(buf, index, this.simplePrefix)) {
                    withinNestedPlaceholder++;
                    index = index + this.simplePrefix.length();
                } else {
                    index++;
                }
            }
            return -1;
        }


        public interface PlaceholderResolver {

            String resolvePlaceholder(String placeholderName);
        }


        public static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
            for (int j = 0; j < substring.length(); j++) {
                int i = index + j;
                if (i >= str.length() || str.charAt(i) != substring.charAt(j)) {
                    return false;
                }
            }
            return true;
        }


        public static void notNull(Object object, String message) {
            if (object == null) {
                throw new IllegalArgumentException(message);
            }
        }


    }


}
