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

package io.innospots.base.data.minder;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import io.innospots.base.data.schema.ConnectionCredential;
import io.innospots.base.data.schema.SchemaRegistry;
import io.innospots.base.data.schema.config.ConnectionMinderSchema;
import io.innospots.base.data.schema.config.ConnectionMinderSchemaLoader;
import io.innospots.base.data.schema.config.CredentialFormConfig;
import io.innospots.base.data.schema.reader.IConnectionCredentialReader;
import io.innospots.base.data.schema.reader.ISchemaRegistryReader;
import io.innospots.base.exception.ResourceException;
import io.innospots.base.exception.data.DataConnectionException;
import io.innospots.base.model.PageBody;
import io.innospots.base.utils.ApplicationContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * manage the datasource connection instance
 * data connection use spi interface, this manager will load these class that implement the IDataConnectionMinder interface
 *
 * @author Raydian
 * @date 2021/1/31
 */
public class DataConnectionMinderManager {

    private static final Logger logger = LoggerFactory.getLogger(DataConnectionMinderManager.class);

    static {
//        loadInitialMinder();
    }

    private Cache<String, IDataConnectionMinder> connectionPoolCache;
    /**
     * key:connectorName, value: dataConnectionDriver
     */
//    private static Map<String, Class<? extends IDataConnectionMinder>> connectionClazz;

    private IConnectionCredentialReader connectionCredentialReader;

    private ISchemaRegistryReader dataSchemaReader;

    public DataConnectionMinderManager(
            IConnectionCredentialReader connectionCredentialReader,
            ISchemaRegistryReader dataSchemaReader,
            int cacheTimeoutSecond) {
        this.connectionCredentialReader = connectionCredentialReader;
        this.dataSchemaReader = dataSchemaReader;
        connectionPoolCache = build(cacheTimeoutSecond);
    }

    /*
    private static void loadInitialMinder() {
        connectionClazz = new HashMap<>(7);
        ServiceLoader<IDataConnectionMinder> serviceLoader = ServiceLoader.load(IDataConnectionMinder.class);
        Iterator<IDataConnectionMinder> iterator = serviceLoader.iterator();

        while (iterator.hasNext()) {
            IDataConnectionMinder cPool = iterator.next();
            connectionClazz.put(cPool.minderName(), cPool.getClass());
            logger.debug("Loading data connection driver:{}", cPool.getClass());
        }

        logger.debug("Loaded data connection driver size:{}", connectionClazz.size());
    }
     */

    public static IDataConnectionMinder getCredentialMinder(Integer credentialId) {
        return ApplicationContextUtils.getBean(DataConnectionMinderManager.class).getMinder(credentialId);
    }

    public static IQueueConnectionMinder getCredentialQueueMinder(Integer credentialId) {
        return (IQueueConnectionMinder) ApplicationContextUtils.getBean(DataConnectionMinderManager.class).getMinder(credentialId);
    }


    /*
    public static Collection<Class<? extends IDataConnectionMinder>> getMinderClasses() {
        return connectionClazz.values();
    }

     */

    /*
    public static Class<? extends IDataConnectionMinder> getMinderClass(String minderName) {
        return connectionClazz.get(connectorName);
    }

     */

    public static IDataConnectionMinder newInstanceByConfigCode(String schemaName) {
        try {
            ConnectionMinderSchema minderSchema = ConnectionMinderSchemaLoader.getConnectionMinderSchema(schemaName);
            if (minderSchema == null) {
                return null;
            }
            return (IDataConnectionMinder) Class.forName(minderSchema.getMinder()).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static Boolean testConnection(ConnectionCredential connectionCredential) {
        //Class<? extends IDataConnectionMinder> driverClass = DataConnectionMinderManager.getMinderClass(formConfig.getMinder());
        IDataConnectionMinder dataConnectionDriver = newInstanceByConfigCode(connectionCredential.getConnectorName());
        if (dataConnectionDriver != null) {
            return dataConnectionDriver.test(connectionCredential);
        }
        return false;
        /*
        try {

            return dataConnectionDriver.test(connectionCredential);
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }
        return false;

         */
    }

    public static Object fetchSample(ConnectionCredential connectionCredential) {
        CredentialFormConfig formConfig = ConnectionMinderSchemaLoader.getCredentialFormConfig(connectionCredential.getConnectorName(), connectionCredential.getConfigCode());
        if (formConfig == null) {
            return false;
        }
        //Class<? extends IDataConnectionMinder> driverClass = DataConnectionMinderManager.getMinderClass(formConfig.getMinder());
        IDataConnectionMinder dataConnectionDriver = newInstanceByConfigCode(connectionCredential.getConfigCode());
        if (dataConnectionDriver != null) {
            dataConnectionDriver.open();
            return dataConnectionDriver.fetchSample(connectionCredential, null);
        }
        /*
        dataConnectionDriver.open();
        Class<? extends IDataConnectionMinder> driverClass = DataConnectionMinderManager.getMinderClass(connectionCredential.getConnectorName());
        try {
            IDataConnectionMinder dataConnectionDriver = driverClass.newInstance();
            dataConnectionDriver.open();
            return dataConnectionDriver.fetchSample(connectionCredential, null);
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }

         */
        return null;
    }

    public Object fetchSample(Integer credentialId, String tableName) {
        IDataConnectionMinder minder = this.getMinder(credentialId);
        ConnectionCredential connectionCredential = connectionCredentialReader.readCredential(credentialId);
        return minder.fetchSample(connectionCredential, tableName);
    }

    public PageBody<Map<String, Object>> fetchSamples(Integer credentialId, SchemaRegistry schemaRegistry, int page, int size) {
        IDataConnectionMinder minder = this.getMinder(credentialId);
        ConnectionCredential connectionCredential = connectionCredentialReader.readCredential(credentialId);
        return minder.fetchSamples(connectionCredential, schemaRegistry, page, size);
    }

    private IDataConnectionMinder getMinder(ConnectionCredential connectionCredential) {
        IDataConnectionMinder dataConnectionMinder = connectionPoolCache.getIfPresent(connectionCredential.key());
        if (dataConnectionMinder != null) {
            dataConnectionMinder.open();
            return dataConnectionMinder;
        }

        try {
            dataConnectionMinder = newInstanceByConfigCode(connectionCredential.getConnectorName());
            //Class<? extends IDataConnectionMinder> connectionMinderClass = connectionClazz.get(connectionCredential.getConnectorName());
            if (dataConnectionMinder != null) {
//                dataConnectionMinder = connectionMinderClass.newInstance();
                dataConnectionMinder.initialize(dataSchemaReader, connectionCredential);
                connectionPoolCache.put(connectionCredential.key(), dataConnectionMinder);
                dataConnectionMinder.open();

                if (logger.isDebugEnabled()) {
                    logger.debug("register datasource:{}", connectionCredential);
                }
            } else {
                logger.error("not find connectionCredential, id:{}, type:{} ", connectionCredential.getCredentialId(), connectionCredential.getConfigCode());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (dataConnectionMinder == null) {
            throw DataConnectionException.buildException(this.getClass(), "not find connectionCredential type:" + connectionCredential.getConfigCode());
        }

        return dataConnectionMinder;
    }

    public void unregister(Integer credentialId) {
        connectionPoolCache.invalidate(key(credentialId));
    }

//    public IDataConnectionMinder getMinder(Integer datasourceId) {
//        return getMinder(datasourceId, null);
//    }

    public IDataConnectionMinder getMinder(String credentialCode) {
        ConnectionCredential connectionCredential = connectionCredentialReader.readCredential(credentialCode);
        if (connectionCredential != null) {
            return getMinder(connectionCredential.getCredentialId());
        }
        return null;
    }

    public IQueueConnectionMinder getQueueMinder(Integer credentialId){
        IDataConnectionMinder dataConnectionMinder = getMinder(credentialId);
        if(dataConnectionMinder instanceof IQueueConnectionMinder){
            return (IQueueConnectionMinder) dataConnectionMinder;
        }
        return null;
    }

    public IDataConnectionMinder getMinder(Integer credentialId) {
        IDataConnectionMinder minder =
                connectionPoolCache.getIfPresent(key(credentialId));
        if (minder == null) {
            ConnectionCredential connectionCredential = connectionCredentialReader.readCredential(credentialId);
            if (connectionCredential == null) {
                logger.error("credential config don't exist, {}", credentialId);
                throw ResourceException.buildNotExistException(this.getClass(), "credential config don't exist, credentialId: " + credentialId);
            }
            minder = getMinder(connectionCredential);
        }

        return minder;
    }

    private String key(Integer credentialId) {
        return String.valueOf(credentialId);
    }

    public void close() {
        if (connectionPoolCache != null) {
            connectionPoolCache.invalidateAll();
        }
    }

    private Cache<String, IDataConnectionMinder> build(int timeoutSecond) {

        return Caffeine.newBuilder()
                .removalListener((RemovalListener<String, IDataConnectionMinder>) (s, dataConnectionMinder, removalCause) -> {
                    logger.warn("dataConnection is expired, close the data connection,key:{}", s);
                    if (dataConnectionMinder != null) {
                        dataConnectionMinder.close();
                    }
                }).expireAfterAccess(timeoutSecond, TimeUnit.SECONDS).build();
    }

}
