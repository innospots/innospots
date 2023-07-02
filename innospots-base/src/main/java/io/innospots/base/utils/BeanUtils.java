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

import io.innospots.base.exception.BaseException;
import io.innospots.base.model.response.ResponseCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.CollectionUtils;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

/**
 * @program: innospots-root
 * @description: beanUtils
 * @author: Alexander
 * @create: 2021-01-22 22:27
 **/
@Slf4j
public class BeanUtils extends org.springframework.beans.BeanUtils {

    public static void copyProperties(Object source, Object target) {

        org.springframework.beans.BeanUtils.copyProperties(source, target, getNullPropertyNames(source));

    }

    public static <T> T copyProperties(Object source, Class<T> targetClass) {

        T targetBean = createBean(targetClass);
        copyProperties(source, targetBean, getNullPropertyNames(source));
        return targetBean;
    }

    /**
     * Batch of multiple object properties
     *
     * @param sourceCollection Source list
     * @param targetClass      Target class
     * @param <S>              source
     * @param <T>              target
     * @return Target collection
     */
    public static <S, T> List<T> copyProperties(Collection<S> sourceCollection, Class<T> targetClass) {
        if (CollectionUtils.isEmpty(sourceCollection)) {
            return new ArrayList<>();
        }
        List<T> result = new ArrayList<>();
        try {
            for (S t : sourceCollection) {
                T entity = targetClass.newInstance();
                org.springframework.beans.BeanUtils.copyProperties(t, entity);
                result.add(entity);
            }
        } catch (Exception e) {
            log.error("An exception occurred in batch processing of multiple object properties", e);
            throw new BaseException(BeanUtils.class, ResponseCode.BEAN_CONVERT_ERROR, e, "An exception occurred in batch processing of multiple object properties");
        }
        return result;
    }

    private static String[] getNullPropertyNames(Object source) {

        final BeanWrapper src = new BeanWrapperImpl(source);
        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : src.getPropertyDescriptors()) {
            if (src.getPropertyValue(pd.getName()) == null) {
                emptyNames.add(pd.getName());
            }
        }
        return new HashSet<String>().toArray(new String[emptyNames.size()]);
    }


    @SneakyThrows
    private static <T> T createBean(Class<T> clazz) {
        return clazz.newInstance();
    }

    public static Map<String, Object> toMap(Object object, boolean underscore, boolean ignoreNull) {
        Map<String, Object> beanMap = new HashMap<>();
        PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(object.getClass());
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String properName = propertyDescriptor.getName();
            if ("class".equals(properName)) {
                continue;
            }
            String beanKey = underscore ? StringConverter.camelToUnderscore(properName) : properName;
            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod == null) {
                continue;
            } else if (!readMethod.isAccessible()) {
                readMethod.setAccessible(true);
            }
            try {
                Object value = readMethod.invoke(object);
                if (value != null || !ignoreNull) {
                    beanMap.put(beanKey, value);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error(e.getMessage(), e);
            }
        }

        return beanMap;
    }


    public static <T> List<T> toBean(Collection<Map<String, Object>> beanCollection, Class<T> targetClass) {
        List<T> targets = new ArrayList<>();

        if (beanCollection != null && !beanCollection.isEmpty()) {
            for (Map<String, Object> beanMap : beanCollection) {
                targets.add(toBean(beanMap, targetClass));
            }
        }
        return targets;
    }

    public static <T> T toBean(Map<String, Object> beanMap, Class<T> valueType) {
        return toBean(beanMap, valueType, false);
    }

    public static <T> T toBean(Map<String, Object> beanMap, Class<T> valueType, boolean underscore) {
        T bean = org.springframework.beans.BeanUtils.instantiateClass(valueType);
        PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(valueType);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String properName = propertyDescriptor.getName();
            Class properType = propertyDescriptor.getPropertyType();
            if ("class".equals(properName)) {
                continue;
            }
            String beanKey = underscore ? StringConverter.camelToUnderscore(properName) : properName;
            if (beanMap.containsKey(beanKey)) {
                Method writeMethod = propertyDescriptor.getWriteMethod();
                if (null == writeMethod) {
                    continue;
                }
                Object value = beanMap.get(beanKey);
                if (value != null && !"".equals(value.toString().trim())) {
                    if (!(value instanceof Enum) && propertyDescriptor.getPropertyType().isEnum()) {
                        Enum[] objects = (Enum[]) propertyDescriptor.getPropertyType().getEnumConstants();
                        for (Enum object : objects) {
                            if (value.equals(object.name())) {
                                value = object;
                                break;
                            }
                        }
                    }
                    try {
                        if (properType.equals(int.class) || properType.equals(Integer.class)) {
                            value = Integer.valueOf(value.toString());
                        }
                        if (properType.equals(byte.class) || properType.equals(Byte.class)) {
                            value = Byte.valueOf(value.toString());
                        }
                        if (properType.equals(long.class) || properType.equals(Long.class)) {
                            value = Long.valueOf(value.toString());
                        }
                        if (properType.equals(short.class) || properType.equals(Short.class)) {
                            value = Short.valueOf(value.toString());
                        }
                        if (properType.equals(float.class) || properType.equals(Float.class)) {
                            value = Float.valueOf(value.toString());
                        }
                        if (properType.equals(double.class) || properType.equals(Double.class)) {
                            value = Double.valueOf(value.toString());
                        }
                        if (properType.equals(boolean.class) || properType.equals(Boolean.class)) {
                            value = Boolean.valueOf(value.toString());
                        }
                        if (properType.equals(LocalDateTime.class) && value instanceof Timestamp) {
                            value = ((Timestamp) value).toLocalDateTime();
                        }
                        if (properType.equals(LocalDate.class) && value instanceof Timestamp) {
                            value = ((Timestamp) value).toLocalDateTime().toLocalDate();
                        }

                    } catch (Exception e) {
                        log.error("value format error properType:{} data:{}", properType.getName(), value, e);
                        value = null;
                    }

                }
                if (value != null) {
                    if ((properType.equals(char.class) || properType.equals(Character.class)) &&
                            (!char.class.equals(value.getClass()) || !Character.class.equals(value.getClass()))) {
                        String strValue = value.toString();
                        value = strValue.subSequence(0, strValue.length()).charAt(0);
                    }
                }


                if (!writeMethod.isAccessible()) {
                    writeMethod.setAccessible(true);
                }
                try {
                    writeMethod.invoke(bean, value);
                } catch (Throwable throwable) {
                    throw new RuntimeException("Could not set property '" + properName + " ' to bean, value " + value + " " + throwable);
                }
            }
        }
        return bean;
    }

    public static void fillNullValue(Map<String, Object> source, Object targetBean) {
        final BeanWrapper src = new BeanWrapperImpl(targetBean);
        for (java.beans.PropertyDescriptor pd : src.getPropertyDescriptors()) {
            if (src.getPropertyValue(pd.getName()) == null) {
                src.setPropertyValue(pd.getName(), source.get(pd.getName()));
            }
        }
    }

    public static <T> String getFieldName(FieldFunction<T, ?> fieldFunction, boolean isUnderscore) {
        try {
            Method funcMethod = fieldFunction.getClass().getDeclaredMethod("writeReplace");
            funcMethod.setAccessible(true);
            SerializedLambda lambda = (SerializedLambda) funcMethod.invoke(fieldFunction);
            String methodName = lambda.getImplMethodName();
            if (methodName.startsWith("get")) {
                methodName = methodName.substring(3);
            } else if (methodName.startsWith("is")) {
                methodName = methodName.substring(2);
            }
            if (StringUtils.isNotBlank(methodName)) {
                methodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
                if (isUnderscore) {
                    methodName = StringConverter.camelToUnderscore(methodName);
                }
                return methodName;
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }

    public interface FieldFunction<T, R> extends Function<T, R>, Serializable {
    }

}
