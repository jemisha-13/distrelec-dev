package com.namics.distrelec.b2b.core.jdbc.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import javax.annotation.PostConstruct;

import com.namics.distrelec.b2b.core.jdbc.DistJdbcService;

import de.hybris.bootstrap.config.tenants.Tenant;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.flexiblesearch.internal.ReadOnlyConditionsHelper;
import de.hybris.platform.jdbcwrapper.HybrisDataSource;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistJdbcServiceImpl implements DistJdbcService {

    private static final Logger LOG = LoggerFactory.getLogger(DistJdbcServiceImpl.class);

    private static final String JDBC_READ_REPLICA_ENABLED_CFG = "jdbc.read-replica.enabled";

    private final ConfigurationService configurationService;

    private final ReadOnlyConditionsHelper readOnlyConditionsHelper = new ReadOnlyConditionsHelper();

    public DistJdbcServiceImpl(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @PostConstruct
    public void onCreate() {
    }

    @Override
    public boolean isReadReplicaEnabled() {
        return configurationService.getConfiguration().getBoolean(JDBC_READ_REPLICA_ENABLED_CFG, false);
    }

    @Override
    public Connection acquireMasterReplicaConnection() throws SQLException {
        return Registry.getCurrentTenant().getDataSource().getConnection(false);
    }

    @Override
    public Connection acquireReadReplicaConnection() throws SQLException {
        Optional<HybrisDataSource> dataSource = readOnlyConditionsHelper.getReadOnlyDataSource(Registry.getCurrentTenant());

        if (dataSource.isEmpty()) {
            LOG.warn("Readonly datasource is not available");
            return acquireMasterReplicaConnection();
        }

        return dataSource.get().getConnection(false);
    }
}
