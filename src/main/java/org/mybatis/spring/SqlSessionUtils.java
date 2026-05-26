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
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Handles MyBatis SqlSession life cycle. It can register and get SqlSessions from Spring
 * {@code TransactionSynchronizationManager}. Also works if no transaction is active.
 *
 * @author Hunter Presnall
 * @author Eduardo Macarron
 */
public final class SqlSessionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlSessionUtils.class);

    private static final String NO_EXECUTOR_TYPE_SPECIFIED = "No ExecutorType specified";

    private static final String NO_SQL_SESSION_FACTORY_SPECIFIED = "No SqlSessionFactory specified";

    private static final String NO_SQL_SESSION_SPECIFIED = "No SqlSession specified";

    /**
     * This class can't be instantiated, exposes static utility methods only.
     */
    private SqlSessionUtils() {
        // do nothing
    }

    /**
     * Creates a new MyBatis {@code SqlSession} from the {@code SqlSessionFactory} provided as a parameter and using its
     * {@code DataSource} and {@code ExecutorType}
     *
     * @param sessionFactory
     *          a MyBatis {@code SqlSessionFactory} to create new sessions
     *
     * @return a MyBatis {@code SqlSession}
     *
     * @throws TransientDataAccessResourceException
     *           if a transaction is active and the {@code SqlSessionFactory} is not using a
     *           {@code SpringManagedTransactionFactory}
     */
    public static SqlSession getSqlSession(SqlSessionFactory sessionFactory) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets an SqlSession from Spring Transaction Manager or creates a new one if needed. Tries to get a SqlSession out of
     * current transaction. If there is not any, it creates a new one. Then, it synchronizes the SqlSession with the
     * transaction if Spring TX is active and <code>SpringManagedTransactionFactory</code> is configured as a transaction
     * manager.
     *
     * @param sessionFactory
     *          a MyBatis {@code SqlSessionFactory} to create new sessions
     * @param executorType
     *          The executor type of the SqlSession to create
     * @param exceptionTranslator
     *          Optional. Translates SqlSession.commit() exceptions to Spring exceptions.
     *
     * @return an SqlSession managed by Spring Transaction Manager
     *
     * @throws TransientDataAccessResourceException
     *           if a transaction is active and the {@code SqlSessionFactory} is not using a
     *           {@code SpringManagedTransactionFactory}
     *
     * @see SpringManagedTransactionFactory
     */
    public static SqlSession getSqlSession(SqlSessionFactory sessionFactory, ExecutorType executorType, PersistenceExceptionTranslator exceptionTranslator) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Register session holder if synchronization is active (i.e. a Spring TX is active).
     * <p>
     * Note: The DataSource used by the Environment should be synchronized with the transaction either through
     * DataSourceTxMgr or another tx synchronization. Further assume that if an exception is thrown, whatever started the
     * transaction will handle closing / rolling back the Connection associated with the SqlSession.
     *
     * @param sessionFactory
     *          sqlSessionFactory used for registration.
     * @param executorType
     *          executorType used for registration.
     * @param exceptionTranslator
     *          persistenceExceptionTranslator used for registration.
     * @param session
     *          sqlSession used for registration.
     */
    private static void registerSessionHolder(SqlSessionFactory sessionFactory, ExecutorType executorType, PersistenceExceptionTranslator exceptionTranslator, SqlSession session) {
        SqlSessionHolder holder;
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            var environment = sessionFactory.getConfiguration().getEnvironment();
            if (environment.getTransactionFactory() instanceof SpringManagedTransactionFactory) {
                LOGGER.debug(() -> "Registering transaction synchronization for SqlSession [" + session + "]");
                holder = new SqlSessionHolder(session, executorType, exceptionTranslator);
                TransactionSynchronizationManager.bindResource(sessionFactory, holder);
                TransactionSynchronizationManager.registerSynchronization(new SqlSessionSynchronization(holder, sessionFactory));
                holder.setSynchronizedWithTransaction(true);
                holder.requested();
            } else if (TransactionSynchronizationManager.getResource(environment.getDataSource()) == null) {
                LOGGER.debug(() -> "SqlSession [" + session + "] was not registered for synchronization because DataSource is not transactional");
            } else {
                throw new TransientDataAccessResourceException("SqlSessionFactory must be using a SpringManagedTransactionFactory in order to use Spring transaction synchronization");
            }
        } else {
            LOGGER.debug(() -> "SqlSession [" + session + "] was not registered for synchronization because synchronization is not active");
        }
    }

    private static SqlSession sessionHolder(ExecutorType executorType, SqlSessionHolder holder) {
        SqlSession session = null;
        if (holder != null && holder.isSynchronizedWithTransaction()) {
            if (holder.getExecutorType() != executorType) {
                throw new TransientDataAccessResourceException("Cannot change the ExecutorType when there is an existing transaction");
            }
            holder.requested();
            LOGGER.debug(() -> "Fetched SqlSession [" + holder.getSqlSession() + "] from current transaction");
            session = holder.getSqlSession();
        }
        return session;
    }

    /**
     * Checks if {@code SqlSession} passed as an argument is managed by Spring {@code TransactionSynchronizationManager}
     * If it is not, it closes it, otherwise it just updates the reference counter and lets Spring call the close callback
     * when the managed transaction ends
     *
     * @param session
     *          a target SqlSession
     * @param sessionFactory
     *          a factory of SqlSession
     */
    public static void closeSqlSession(SqlSession session, SqlSessionFactory sessionFactory) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns if the {@code SqlSession} passed as an argument is being managed by Spring
     *
     * @param session
     *          a MyBatis SqlSession to check
     * @param sessionFactory
     *          the SqlSessionFactory which the SqlSession was built with
     *
     * @return true if session is transactional, otherwise false
     */
    public static boolean isSqlSessionTransactional(SqlSession session, SqlSessionFactory sessionFactory) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Callback for cleaning up resources. It cleans TransactionSynchronizationManager and also commits and closes the
     * {@code SqlSession}. It assumes that {@code Connection} life cycle will be managed by
     * {@code DataSourceTransactionManager} or {@code JtaTransactionManager}
     */
    private static final class SqlSessionSynchronization implements TransactionSynchronization {

        private final SqlSessionHolder holder;

        private final SqlSessionFactory sessionFactory;

        private boolean holderActive = true;

        public SqlSessionSynchronization(SqlSessionHolder holder, SqlSessionFactory sessionFactory) {
            notNull(holder, "Parameter 'holder' must be not null");
            notNull(sessionFactory, "Parameter 'sessionFactory' must be not null");
            this.holder = holder;
            this.sessionFactory = sessionFactory;
        }

        @Override
        public int getOrder() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public void suspend() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public void resume() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public void beforeCommit(boolean readOnly) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public void beforeCompletion() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public void afterCompletion(int status) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }
}
