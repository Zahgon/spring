/*
 * Copyright 2010-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mybatis.spring;

import static org.springframework.util.Assert.notNull;
import static org.springframework.util.Assert.state;
import static org.springframework.util.ObjectUtils.isEmpty;
import static org.springframework.util.StringUtils.hasLength;
import static org.springframework.util.StringUtils.tokenizeToStringArray;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.util.ClassUtils;

/**
 * {@code FactoryBean} that creates a MyBatis {@code SqlSessionFactory}. This is the usual way to set up a shared
 * MyBatis {@code SqlSessionFactory} in a Spring application context; the SqlSessionFactory can then be passed to
 * MyBatis-based DAOs via dependency injection.
 * <p>
 * Either {@code DataSourceTransactionManager} or {@code JtaTransactionManager} can be used for transaction demarcation
 * in combination with a {@code SqlSessionFactory}. JTA should be used for transactions which span multiple databases or
 * when container managed transactions (CMT) are being used.
 *
 * @author Putthiphong Boonphong
 * @author Hunter Presnall
 * @author Eduardo Macarron
 * @author Eddú Meléndez
 * @author Kazuki Shimizu
 * @author Jens Schauder
 *
 * @see #setConfigLocation
 * @see #setDataSource
 */
public class SqlSessionFactoryBean implements FactoryBean<SqlSessionFactory>, InitializingBean, ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlSessionFactoryBean.class);

    private static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();

    private static final MetadataReaderFactory METADATA_READER_FACTORY = new CachingMetadataReaderFactory();

    private Resource configLocation;

    private Configuration configuration;

    private Resource[] mapperLocations;

    private DataSource dataSource;

    private TransactionFactory transactionFactory;

    private Properties configurationProperties;

    private SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();

    private SqlSessionFactory sqlSessionFactory;

    // EnvironmentAware requires spring 3.1
    private String environment = SqlSessionFactoryBean.class.getSimpleName();

    private boolean failFast;

    private Interceptor[] plugins;

    private TypeHandler<?>[] typeHandlers;

    private String typeHandlersPackage;

    @SuppressWarnings("rawtypes")
    private Class<? extends TypeHandler> defaultEnumTypeHandler;

    private Class<?>[] typeAliases;

    private String typeAliasesPackage;

    private Class<?> typeAliasesSuperType;

    private LanguageDriver[] scriptingLanguageDrivers;

    private Class<? extends LanguageDriver> defaultScriptingLanguageDriver;

    // issue #19. No default provider.
    private DatabaseIdProvider databaseIdProvider;

    private Class<? extends VFS> vfs;

    private Cache cache;

    private ObjectFactory objectFactory;

    private ObjectWrapperFactory objectWrapperFactory;

    /**
     * Sets the ObjectFactory.
     *
     * @since 1.1.2
     *
     * @param objectFactory
     *          a custom ObjectFactory
     */
    public void setObjectFactory(ObjectFactory objectFactory) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the ObjectWrapperFactory.
     *
     * @since 1.1.2
     *
     * @param objectWrapperFactory
     *          a specified ObjectWrapperFactory
     */
    public void setObjectWrapperFactory(ObjectWrapperFactory objectWrapperFactory) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the DatabaseIdProvider
     *
     * @since 1.1.0
     *
     * @return a specified DatabaseIdProvider
     */
    public DatabaseIdProvider getDatabaseIdProvider() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the DatabaseIdProvider. As of version 1.2.2 this variable is not initialized by default.
     *
     * @since 1.1.0
     *
     * @param databaseIdProvider
     *          a DatabaseIdProvider
     */
    public void setDatabaseIdProvider(DatabaseIdProvider databaseIdProvider) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the VFS.
     *
     * @return a specified VFS
     */
    public Class<? extends VFS> getVfs() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the VFS.
     *
     * @param vfs
     *          a VFS
     */
    public void setVfs(Class<? extends VFS> vfs) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the Cache.
     *
     * @return a specified Cache
     */
    public Cache getCache() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the Cache.
     *
     * @param cache
     *          a Cache
     */
    public void setCache(Cache cache) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Mybatis plugin list.
     *
     * @since 1.0.1
     *
     * @param plugins
     *          list of plugins
     */
    public void setPlugins(Interceptor... plugins) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Packages to search for type aliases.
     * <p>
     * Since 2.0.1, allow to specify a wildcard such as {@code com.example.*.model}.
     *
     * @since 1.0.1
     *
     * @param typeAliasesPackage
     *          package to scan for domain objects
     */
    public void setTypeAliasesPackage(String typeAliasesPackage) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Super class which domain objects have to extend to have a type alias created. No effect if there is no package to
     * scan configured.
     *
     * @since 1.1.2
     *
     * @param typeAliasesSuperType
     *          super class for domain objects
     */
    public void setTypeAliasesSuperType(Class<?> typeAliasesSuperType) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Packages to search for type handlers.
     * <p>
     * Since 2.0.1, allow to specify a wildcard such as {@code com.example.*.typehandler}.
     *
     * @since 1.0.1
     *
     * @param typeHandlersPackage
     *          package to scan for type handlers
     */
    public void setTypeHandlersPackage(String typeHandlersPackage) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Set type handlers. They must be annotated with {@code MappedTypes} and optionally with {@code MappedJdbcTypes}
     *
     * @since 1.0.1
     *
     * @param typeHandlers
     *          Type handler list
     */
    public void setTypeHandlers(TypeHandler<?>... typeHandlers) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Set the default type handler class for enum.
     *
     * @since 2.0.5
     *
     * @param defaultEnumTypeHandler
     *          The default type handler class for enum
     */
    public void setDefaultEnumTypeHandler(@SuppressWarnings("rawtypes") Class<? extends TypeHandler> defaultEnumTypeHandler) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * List of type aliases to register. They can be annotated with {@code Alias}
     *
     * @since 1.0.1
     *
     * @param typeAliases
     *          Type aliases list
     */
    public void setTypeAliases(Class<?>... typeAliases) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * If true, a final check is done on Configuration to assure that all mapped statements are fully loaded and there is
     * no one still pending to resolve includes. Defaults to false.
     *
     * @since 1.0.1
     *
     * @param failFast
     *          enable failFast
     */
    public void setFailFast(boolean failFast) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Set the location of the MyBatis {@code SqlSessionFactory} config file. A typical value is
     * "WEB-INF/mybatis-configuration.xml".
     *
     * @param configLocation
     *          a location the MyBatis config file
     */
    public void setConfigLocation(Resource configLocation) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Set a customized MyBatis configuration.
     *
     * @param configuration
     *          MyBatis configuration
     *
     * @since 1.3.0
     */
    public void setConfiguration(Configuration configuration) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Set locations of MyBatis mapper files that are going to be merged into the {@code SqlSessionFactory} configuration
     * at runtime.
     * <p>
     * This is an alternative to specifying "&lt;sqlmapper&gt;" entries in an MyBatis config file. This property being
     * based on Spring's resource abstraction also allows for specifying resource patterns here: e.g.
     * "classpath*:sqlmap/*-mapper.xml".
     *
     * @param mapperLocations
     *          location of MyBatis mapper files
     */
    public void setMapperLocations(Resource... mapperLocations) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Set optional properties to be passed into the SqlSession configuration, as alternative to a
     * {@code &lt;properties&gt;} tag in the configuration xml file. This will be used to resolve placeholders in the
     * config file.
     *
     * @param sqlSessionFactoryProperties
     *          optional properties for the SqlSessionFactory
     */
    public void setConfigurationProperties(Properties sqlSessionFactoryProperties) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Set the JDBC {@code DataSource} that this instance should manage transactions for. The {@code DataSource} should
     * match the one used by the {@code SqlSessionFactory}: for example, you could specify the same JNDI DataSource for
     * both.
     * <p>
     * A transactional JDBC {@code Connection} for this {@code DataSource} will be provided to application code accessing
     * this {@code DataSource} directly via {@code DataSourceUtils} or {@code DataSourceTransactionManager}.
     * <p>
     * The {@code DataSource} specified here should be the target {@code DataSource} to manage transactions for, not a
     * {@code TransactionAwareDataSourceProxy}. Only data access code may work with
     * {@code TransactionAwareDataSourceProxy}, while the transaction manager needs to work on the underlying target
     * {@code DataSource}. If there's nevertheless a {@code TransactionAwareDataSourceProxy} passed in, it will be
     * unwrapped to extract its target {@code DataSource}.
     *
     * @param dataSource
     *          a JDBC {@code DataSource}
     */
    public void setDataSource(DataSource dataSource) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the {@code SqlSessionFactoryBuilder} to use when creating the {@code SqlSessionFactory}.
     * <p>
     * This is mainly meant for testing so that mock SqlSessionFactory classes can be injected. By default,
     * {@code SqlSessionFactoryBuilder} creates {@code DefaultSqlSessionFactory} instances.
     *
     * @param sqlSessionFactoryBuilder
     *          a SqlSessionFactoryBuilder
     */
    public void setSqlSessionFactoryBuilder(SqlSessionFactoryBuilder sqlSessionFactoryBuilder) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Set the MyBatis TransactionFactory to use. Default is {@code SpringManagedTransactionFactory}.
     * <p>
     * The default {@code SpringManagedTransactionFactory} should be appropriate for all cases: be it Spring transaction
     * management, EJB CMT or plain JTA. If there is no active transaction, SqlSession operations will execute SQL
     * statements non-transactionally.
     * <p>
     * <b>It is strongly recommended to use the default {@code TransactionFactory}.</b> If not used, any attempt at
     * getting an SqlSession through Spring's MyBatis framework will throw an exception if a transaction is active.
     *
     * @see SpringManagedTransactionFactory
     *
     * @param transactionFactory
     *          the MyBatis TransactionFactory
     */
    public void setTransactionFactory(TransactionFactory transactionFactory) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * <b>NOTE:</b> This class <em>overrides</em> any {@code Environment} you have set in the MyBatis config file. This is
     * used only as a placeholder name. The default value is {@code SqlSessionFactoryBean.class.getSimpleName()}.
     *
     * @param environment
     *          the environment name
     */
    public void setEnvironment(String environment) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Set scripting language drivers.
     *
     * @param scriptingLanguageDrivers
     *          scripting language drivers
     *
     * @since 2.0.2
     */
    public void setScriptingLanguageDrivers(LanguageDriver... scriptingLanguageDrivers) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Set a default scripting language driver class.
     *
     * @param defaultScriptingLanguageDriver
     *          A default scripting language driver class
     *
     * @since 2.0.2
     */
    public void setDefaultScriptingLanguageDriver(Class<? extends LanguageDriver> defaultScriptingLanguageDriver) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Add locations of MyBatis mapper files that are going to be merged into the {@code SqlSessionFactory} configuration
     * at runtime.
     * <p>
     * This is an alternative to specifying "&lt;sqlmapper&gt;" entries in an MyBatis config file. This property being
     * based on Spring's resource abstraction also allows for specifying resource patterns here: e.g.
     * "classpath*:sqlmap/*-mapper.xml".
     *
     * @param mapperLocations
     *          location of MyBatis mapper files
     *
     * @see #setMapperLocations(Resource...)
     *
     * @since 3.0.2
     */
    public void addMapperLocations(Resource... mapperLocations) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Add type handlers.
     *
     * @param typeHandlers
     *          Type handler list
     *
     * @since 3.0.2
     */
    public void addTypeHandlers(TypeHandler<?>... typeHandlers) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Add scripting language drivers.
     *
     * @param scriptingLanguageDrivers
     *          scripting language drivers
     *
     * @since 3.0.2
     */
    public void addScriptingLanguageDrivers(LanguageDriver... scriptingLanguageDrivers) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Add Mybatis plugins.
     *
     * @param plugins
     *          list of plugins
     *
     * @since 3.0.2
     */
    public void addPlugins(Interceptor... plugins) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Add type aliases.
     *
     * @param typeAliases
     *          Type aliases list
     *
     * @since 3.0.2
     */
    public void addTypeAliases(Class<?>... typeAliases) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private <T> T[] appendArrays(T[] oldArrays, T[] newArrays, IntFunction<T[]> generator) {
        if (oldArrays == null) {
            return newArrays;
        }
        if (newArrays == null) {
            return oldArrays;
        }
        List<T> newList = new ArrayList<>(Arrays.asList(oldArrays));
        newList.addAll(Arrays.asList(newArrays));
        return newList.toArray(generator.apply(0));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Build a {@code SqlSessionFactory} instance.
     * <p>
     * The default implementation uses the standard MyBatis {@code XMLConfigBuilder} API to build a
     * {@code SqlSessionFactory} instance based on a Reader. Since 1.3.0, it can be specified a {@link Configuration}
     * instance directly(without config file).
     *
     * @return SqlSessionFactory
     *
     * @throws Exception
     *           if configuration is failed
     */
    protected SqlSessionFactory buildSqlSessionFactory() throws Exception {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public SqlSessionFactory getObject() throws Exception {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public Class<? extends SqlSessionFactory> getObjectType() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public boolean isSingleton() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private Set<Class<?>> scanClasses(String packagePatterns, Class<?> assignableType) throws IOException {
        Set<Class<?>> classes = new HashSet<>();
        var packagePatternArray = tokenizeToStringArray(packagePatterns, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        for (String packagePattern : packagePatternArray) {
            var resources = RESOURCE_PATTERN_RESOLVER.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(packagePattern) + "/**/*.class");
            for (Resource resource : resources) {
                try {
                    var classMetadata = METADATA_READER_FACTORY.getMetadataReader(resource).getClassMetadata();
                    Class<?> clazz = Resources.classForName(classMetadata.getClassName());
                    if (assignableType == null || assignableType.isAssignableFrom(clazz)) {
                        classes.add(clazz);
                    }
                } catch (Throwable e) {
                    LOGGER.warn(() -> "Cannot load the '" + resource + "'. Cause by " + e.toString());
                }
            }
        }
        return classes;
    }
}
