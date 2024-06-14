/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.jdbcwrapper;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.pool2.PooledObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hybris.platform.jdbcwrapper.ConnectionErrorCheckingJDBCConnectionFactory;
import de.hybris.platform.jdbcwrapper.ConnectionStatus;
import de.hybris.platform.jdbcwrapper.HybrisDataSource;

/**
 * {@code DistConnectionErrorCheckingJDBCConnectionFactory}
 * <p>
 * To measure more efficiently the DB connection time, we override the standard Hybris JDBC connection Factory to log the time to get a new
 * connection.
 * </p>
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.3
 */
public class DistConnectionErrorCheckingJDBCConnectionFactory extends ConnectionErrorCheckingJDBCConnectionFactory {

    private final static Logger LOG = LogManager.getLogger(DistConnectionErrorCheckingJDBCConnectionFactory.class);

    /**
     * Create a new instance of {@code DistConnectionErrorCheckingJDBCConnectionFactory}
     * 
     * @param dataSource
     * @param connectionStatus
     */
    public DistConnectionErrorCheckingJDBCConnectionFactory(final HybrisDataSource dataSource, final ConnectionStatus connectionStatus) {
        super(dataSource, connectionStatus);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.jdbcwrapper.JDBCConnectionFactory#makeObject()
     */
    @Override
    public PooledObject<Connection> makeObject() throws SQLException {
        long time = System.currentTimeMillis();
        // Call the super (default) implementation.
        final PooledObject<Connection> pooledConnection = super.makeObject();
        time = System.currentTimeMillis() - time;
        if (LOG.isInfoEnabled()) {
            final String methodName = this.getClass().getSimpleName() + ".makeObject()";
            LOG.debug("Time to get new connection using method [{}]: {}ms", methodName, time);
        }
        return pooledConnection;
    }
}
