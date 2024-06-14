/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.setup;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;

import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.util.Config;

/**
 * This class provides hooks into the system's initialization and update processes.
 * 
 * @see "https://wiki.hybris.com/display/release4/Hooks+for+Initialization+and+Update+Process"
 */
@SystemSetup(extension = DistrelecfactfindersearchConstants.EXTENSIONNAME)
public class DistFactFindersearchSystemSetup extends AbstractSystemSetup {

    private static final Logger LOG = LoggerFactory.getLogger(DistFactFindersearchSystemSetup.class);

    private static final String ORACLE_STATEMENT_SEPARATOR = "[\n\r]+\\s*/\\s*[\n\r]+";

    @Override
    @SystemSetupParameterMethod
    public List<SystemSetupParameter> getInitializationOptions() {
        return Lists.newArrayList();
    }

    @SystemSetup(type = Type.ESSENTIAL, process = Process.ALL)
    public void createEssentialData(final SystemSetupContext context) {
        importImpexFile(context, "/distrelecfactfindersearch/impex/Distrelec_TechUserFactFinder.impex", true);
        String suffix = null;
        boolean errorIfMissing = false;
        if (Config.isMySQLUsed()) {
            suffix = "Mysql";
        } else if (Config.isHSQLDBUsed()) {
            suffix = "Hsqldb";
        } else if (Config.isOracleUsed()) {
            suffix = "Oracle";
            errorIfMissing = true;
        } else if (Config.isSQLServerUsed()) {
            suffix = "Sqlserver";
        } else if (Config.isHanaUsed()) {
            suffix = "Sap";
        }

        importImpexFile(context, "/distrelecfactfindersearch/impex/Distrelec_FactFinderExport.impex", true);
    }

    /**
     * Treats the given filename as a relative resource and executes its content as an Oracle SQL script on the database for the current
     * tenant.
     * 
     * @param sqlFileName
     *            sqlFileName - does not need a leading '/', resources are resolved relatively
     * @param errorIfMissing
     *            flag, set to true to error if the file is not found
     * 
     */
    private void executeSqlFile(final String sqlFileName, final boolean errorIfMissing) {
        Connection connection = null;
        try {
            final URL sqlFile = Resources.class.getClassLoader().getResource(sqlFileName);
            if (sqlFile == null) {
                logFileNotFound(sqlFileName, errorIfMissing);
            } else {
                final String sql = Resources.toString(sqlFile, Charsets.UTF_8);
                connection = Registry.getCurrentTenant().getDataSource().getConnection();
                separateAndRunStatements(connection, sql);
                LOG.info("Successfully imported SQL file [{}].", sqlFileName);
            }
        } catch (final SQLException e) {
            LOG.error("Failed executing SQL file [{}].", sqlFileName, e);
        } catch (final IOException e) {
            LOG.error("Failed reading SQL file [{}].", sqlFileName, e);
        } finally {
            DbUtils.closeQuietly(connection);
        }
    }

    private void separateAndRunStatements(final Connection connection, final String sql) throws SQLException {
        final String[] statements = sql.split(ORACLE_STATEMENT_SEPARATOR);
        for (final String statement : statements) {
            if (StringUtils.isNotBlank(statement)) {
                connection.createStatement().execute(statement);
            }
        }
    }

    private void logFileNotFound(final String sqlFileName, final boolean errorIfMissing) {
        if (errorIfMissing) {
            LOG.error("Importing [{}]... ERROR (MISSING FILE)", sqlFileName);
        } else {
            LOG.info("Importing [{}]... SKIPPED (Optional File Not Found)", sqlFileName);
        }
    }

}
