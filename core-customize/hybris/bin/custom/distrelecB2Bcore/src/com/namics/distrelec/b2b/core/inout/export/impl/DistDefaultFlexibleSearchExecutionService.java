/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.export.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import com.namics.distrelec.b2b.core.jdbc.DistJdbcService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.Config;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchExecutionService;
import com.namics.distrelec.b2b.core.inout.export.exception.DistFlexibleSearchExecutionException;

import de.hybris.platform.persistence.property.JDBCValueMappings;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.TranslationResult;

import static de.hybris.platform.jalo.flexiblesearch.internal.ReadOnlyConditionsHelper.CTX_ENABLE_FS_ON_READ_REPLICA;

/**
 * Default implementation of {@link DistFlexibleSearchExecutionService}.
 *
 * @author ceberle, Namics AG
 * @since Namics Distrelec 1.1
 */
public class DistDefaultFlexibleSearchExecutionService implements DistFlexibleSearchExecutionService {

    private static final Logger LOG = LoggerFactory.getLogger(DistDefaultFlexibleSearchExecutionService.class);

    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private DistJdbcService jdbcService;

    @Autowired
    private SessionService sessionService;

    @Override
    public ResultSet execute(final String flexibleSearchQuery, final Map<String, Object> flexibleSearchParameters) {
        return executeInternal(flexibleSearchQuery, flexibleSearchParameters, false);
    }

    @Override
    public ResultSet executionPlan(String flexibleSearchQuery, Map<String, Object> flexibleSearchParameters) {
        return executeInternal(flexibleSearchQuery, flexibleSearchParameters, true);
    }

    private ResultSet executeInternal(final String flexibleSearchQuery,
                                      final Map<String, Object> flexibleSearchParameters,
                                      boolean executionPlan) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(flexibleSearchQuery, flexibleSearchParameters);
        final TranslationResult translationResult = getFlexibleSearchService().translate(searchQuery);

        while (true) {
            Connection connection = null;
            PreparedStatement statement = null;
            try {
                connection = isReadonlyReplica() ? jdbcService.acquireReadReplicaConnection() : jdbcService.acquireMasterReplicaConnection();

                String sqlQuery;
                if (executionPlan) {
                    sqlQuery = "explain plan for ";
                } else {
                    sqlQuery = "";
                }
                sqlQuery += translationResult.getSQLQuery();
                List<Object> sqlParams = translationResult.getSQLQueryParameters();

                LOG.debug("SQL query statement:{} and parameters: {}", sqlQuery, sqlParams);
                statement = connection.prepareStatement(sqlQuery, ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);

                if (flexibleSearchParameters.containsKey(QUERY_TIMEOUT_PARAM)) {
                    int queryTimeout = (Integer) flexibleSearchParameters.get(QUERY_TIMEOUT_PARAM);
                    statement.setQueryTimeout(queryTimeout);
                }

                JDBCValueMappings.getInstance().fillStatement(statement, sqlParams);
                if (executionPlan) {
                    if (Config.isHanaUsed()) {
                        // clear explain plan table
                        final Statement clearExplainTable = connection.createStatement();
                        clearExplainTable.execute("delete from explain_plan_table");
                    }
                    statement.execute();
                    final Statement followingStmt = connection.createStatement();
                    if (Config.isHanaUsed()) {
                        return followingStmt.executeQuery("select * from explain_plan_table");
                    } else if (Config.isSQLServerUsed()) {
                        return followingStmt.executeQuery("select * from explain_plan_table");
                    }else if (Config.isOracleUsed()) {
                        return followingStmt.executeQuery("select plan_table_output from table(dbms_xplan.display())");
                    } else {
                        throw new IllegalArgumentException("Unsupported db");
                    }
                } else {
                    return statement.executeQuery();
                }

            } catch (final SQLTimeoutException e) {
                DbUtils.closeQuietly(statement);
                DbUtils.closeQuietly(connection);
                LOG.warn("timeout exception - loop", e);
            } catch (final SQLException e) {
                throw new DistFlexibleSearchExecutionException("Could not execute FlexibleSearch statement", e);
            }
        }
    }

    private boolean isReadonlyReplica() {
        boolean readonly = BooleanUtils.isTrue(sessionService.getCurrentSession().getAttribute(CTX_ENABLE_FS_ON_READ_REPLICA));
        return readonly && jdbcService.isReadReplicaEnabled();
    }

    @Override
    public void closeResultSet(final ResultSet resultSet) {
        Statement statement = null;
        Connection connection = null;

        if (resultSet != null) {
            try {
                statement = resultSet.getStatement();
            } catch (final SQLException e) {
                LOG.error("Could not get Statement of ResultSet", e);
            }
        }

        if (statement != null) {
            try {
                connection = statement.getConnection();
            } catch (final SQLException e) {
                LOG.error("Could not get Connection of Statement", e);
            }
        }

        DbUtils.closeQuietly(connection, statement, resultSet);
    }

    // BEGIN GENERATED CODE

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    @Autowired
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    // END GENERATED CODE

}
