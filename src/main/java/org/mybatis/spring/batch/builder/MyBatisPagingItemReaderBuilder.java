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
package org.mybatis.spring.batch.builder;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;

/**
 * A builder for the {@link MyBatisPagingItemReader}.
 *
 * @author Kazuki Shimizu
 *
 * @param <T>
 *          the generic type
 *
 * @see MyBatisPagingItemReader
 *
 * @since 2.0.0
 */
public class MyBatisPagingItemReaderBuilder<T> {

    private SqlSessionFactory sqlSessionFactory;

    private String queryId;

    private Map<String, Object> parameterValues;

    private Supplier<Map<String, Object>> parameterValuesSupplier;

    private Integer pageSize;

    private Boolean saveState;

    private Integer maxItemCount;

    /**
     * Set the {@link SqlSessionFactory} to be used by writer for database access.
     *
     * @param sqlSessionFactory
     *          the {@link SqlSessionFactory} to be used by writer for database access
     *
     * @return this instance for method chaining
     *
     * @see MyBatisPagingItemReader#setSqlSessionFactory(SqlSessionFactory)
     */
    public MyBatisPagingItemReaderBuilder<T> sqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Set the query id identifying the statement in the SqlMap configuration file.
     *
     * @param queryId
     *          the id for the query
     *
     * @return this instance for method chaining
     *
     * @see MyBatisPagingItemReader#setQueryId(String)
     */
    public MyBatisPagingItemReaderBuilder<T> queryId(String queryId) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Set the parameter values to be used for the query execution.
     *
     * @param parameterValues
     *          the parameter values to be used for the query execution
     *
     * @return this instance for method chaining
     *
     * @see MyBatisPagingItemReader#setParameterValues(Map)
     */
    public MyBatisPagingItemReaderBuilder<T> parameterValues(Map<String, Object> parameterValues) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Set the parameter supplier to be used to get parameters for the query execution.
     *
     * @param parameterValuesSupplier
     *          the parameter supplier to be used to get parameters for the query execution
     *
     * @return this instance for method chaining
     *
     * @see MyBatisPagingItemReader#setParameterValuesSupplier(Supplier)
     *
     * @since 2.1.0
     */
    public MyBatisPagingItemReaderBuilder<T> parameterValuesSupplier(Supplier<Map<String, Object>> parameterValuesSupplier) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * The number of records to request per page/query. Defaults to 10. Must be greater than zero.
     *
     * @param pageSize
     *          number of items
     *
     * @return this instance for method chaining
     *
     * @see org.springframework.batch.infrastructure.item.database.AbstractPagingItemReader#setPageSize(int)
     */
    public MyBatisPagingItemReaderBuilder<T> pageSize(int pageSize) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Configure if the state of the {@link org.springframework.batch.infrastructure.item.ItemStreamSupport} should be
     * persisted within the {@link org.springframework.batch.infrastructure.item.ExecutionContext} for restart purposes.
     *
     * @param saveState
     *          defaults to true
     *
     * @return The current instance of the builder.
     *
     * @see org.springframework.batch.infrastructure.item.support.AbstractItemCountingItemStreamItemReader#setSaveState(boolean)
     */
    public MyBatisPagingItemReaderBuilder<T> saveState(boolean saveState) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Configure the max number of items to be read.
     *
     * @param maxItemCount
     *          the max items to be read
     *
     * @return The current instance of the builder.
     *
     * @see org.springframework.batch.infrastructure.item.support.AbstractItemCountingItemStreamItemReader#setMaxItemCount(int)
     */
    public MyBatisPagingItemReaderBuilder<T> maxItemCount(int maxItemCount) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns a fully built {@link MyBatisPagingItemReader}.
     *
     * @return the reader
     */
    public MyBatisPagingItemReader<T> build() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
