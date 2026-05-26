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

import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.transaction.TransactionException;

/**
 * Default exception translator.
 * <p>
 * Translates MyBatis SqlSession returned exception into a Spring {@code DataAccessException} using Spring's
 * {@code SQLExceptionTranslator} Can load {@code SQLExceptionTranslator} eagerly or when the first exception is
 * translated.
 *
 * @author Eduardo Macarron
 */
public class MyBatisExceptionTranslator implements PersistenceExceptionTranslator {

    private final Supplier<SQLExceptionTranslator> exceptionTranslatorSupplier;

    private SQLExceptionTranslator exceptionTranslator;

    private ReentrantLock lock = new ReentrantLock();

    /**
     * Creates a new {@code PersistenceExceptionTranslator} instance with {@code SQLErrorCodeSQLExceptionTranslator}.
     *
     * @param dataSource
     *          DataSource to use to find metadata and establish which error codes are usable.
     * @param exceptionTranslatorLazyInit
     *          if true, the translator instantiates internal stuff only the first time will have the need to translate
     *          exceptions.
     */
    public MyBatisExceptionTranslator(DataSource dataSource, boolean exceptionTranslatorLazyInit) {
        this(() -> new SQLErrorCodeSQLExceptionTranslator(dataSource), exceptionTranslatorLazyInit);
    }

    /**
     * Creates a new {@code PersistenceExceptionTranslator} instance with specified {@code SQLExceptionTranslator}.
     *
     * @param exceptionTranslatorSupplier
     *          Supplier for creating a {@code SQLExceptionTranslator} instance
     * @param exceptionTranslatorLazyInit
     *          if true, the translator instantiates internal stuff only the first time will have the need to translate
     *          exceptions.
     *
     * @since 2.0.3
     */
    public MyBatisExceptionTranslator(Supplier<SQLExceptionTranslator> exceptionTranslatorSupplier, boolean exceptionTranslatorLazyInit) {
        this.exceptionTranslatorSupplier = exceptionTranslatorSupplier;
        if (!exceptionTranslatorLazyInit) {
            this.initExceptionTranslator();
        }
    }

    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException e) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Initializes the internal translator reference.
     */
    private void initExceptionTranslator() {
        lock.lock();
        try {
            if (this.exceptionTranslator == null) {
                this.exceptionTranslator = exceptionTranslatorSupplier.get();
            }
        } finally {
            lock.unlock();
        }
    }
}
