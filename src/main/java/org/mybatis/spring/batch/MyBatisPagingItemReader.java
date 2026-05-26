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
package org.mybatis.spring.batch;

import static org.springframework.util.Assert.notNull;
import static org.springframework.util.ClassUtils.getShortName;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.batch.infrastructure.item.database.AbstractPagingItemReader;

/**
 * {@code org.springframework.batch.infrastructure.item.ItemReader} for reading database records using MyBatis in a
 * paging fashion.
 * <p>
 * Provided to facilitate the migration from Spring-Batch iBATIS 2 page item readers to MyBatis 3.
 *
 * @author Eduardo Macarron
 *
 * @param <T>
 *          the generic type
 *
 * @since 1.1.0
 */
public class MyBatisPagingItemReader<T> extends AbstractPagingItemReader<T> {

    private String queryId;

    private SqlSessionFactory sqlSessionFactory;

    private SqlSessionTemplate sqlSessionTemplate;

    private Map<String, Object> parameterValues;

    private Supplier<Map<String, Object>> parameterValuesSupplier;

    /**
     * Instantiates a new my batis paging item reader.
     */
    public MyBatisPagingItemReader() {
        setName(getShortName(MyBatisPagingItemReader.class));
    }

    /**
     * Public setter for {@link SqlSessionFactory} for injection purposes.
     *
     * @param sqlSessionFactory
     *          a factory object for the {@link SqlSession}.
     */
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Public setter for the statement id identifying the statement in the SqlMap configuration file.
     *
     * @param queryId
     *          the id for the statement
     */
    public void setQueryId(String queryId) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * The parameter values to be used for the query execution.
     *
     * @param parameterValues
     *          the values keyed by the parameter named used in the query string.
     */
    public void setParameterValues(Map<String, Object> parameterValues) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * The parameter supplier used to get parameter values for the query execution.
     *
     * @param parameterValuesSupplier
     *          the supplier used to get values keyed by the parameter named used in the query string.
     *
     * @since 2.1.0
     */
    public void setParameterValuesSupplier(Supplier<Map<String, Object>> parameterValuesSupplier) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Check mandatory properties.
     *
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    protected void doReadPage() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
