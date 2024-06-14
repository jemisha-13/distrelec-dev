/*
 * Copyright 2000-2009 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

import com.namics.hybris.toolbox.FileUtils;

import de.hybris.platform.core.Registry;

/**
 * <p>
 * Executes a list of SQL query scripts (files) directly to the JDBC driver (not via flexisearch).
 * </p>
 * 
 * <p>
 * Use this class only if no other way via hybris is provided!
 * </p>
 * 
 * Examples, where this can be useful:
 * <ul>
 * <li>http://dev.hybris.de/display/forum/Hybris+Platform+-+SQL+column+datatype+definition+when+using+java.lang.String+ attributes</li>
 * <li>http://dev.hybris.de/pages/viewpage.action?pageId=45942702</li>
 * </ul>
 * 
 * @author jonathan.weiss, namics ag
 * @since MGB PIM 1.0
 * 
 */
public class DirectSqlAccessTask {

    private static final Logger LOG = Logger.getLogger(DirectSqlAccessTask.class.getName());

    /**
     * <p>
     * The list of files with sql configuration scripts.
     * </p>
     * <p>
     * This map holds a filelist to execute per tenant. The key is the tenant's id. This list has a key that is matching the tenant name or
     * tenant id. Only entries that are matching are imported.
     * </p>
     * 
     */
    protected Map<String, List<String>> sqlTenantFilelist;

    /**
     * Executes the sql configuration scripts.
     * 
     * @throws Exception
     *             exception like <code>FileNotFoundException</code>.
     */
    public void performTask() throws Exception {
        final List<String> sqlFilelist = sqlTenantFilelist.get(getTenantId());

        LOG.info("Start importing " + sqlFilelist.size() + " sql configuration files for tenant '" + getTenantId() + "' ...");
        for (final String filepath : sqlFilelist) {
            LOG.info("Import " + filepath);
            final Resource resource = FileUtils.createResourceFromFilepath(filepath, true);

            final String fileContent = FileUtils.readFile(resource.getInputStream());
            executeSqlQuery(fileContent);

            LOG.info("Import of sql configuration '" + filepath + "' finished.");

        }
        LOG.info("Importing of " + sqlFilelist.size() + " sql configuration files finished.");
    }

    /**
     * Returns the tenant id.
     * 
     * @return Returns the tenant id.
     */
    protected String getTenantId() {
        try {
            return Registry.getCurrentTenant().getTenantID().toLowerCase();
        } catch (final NullPointerException e) {
            return "master";
        }
    }

    /**
     * Executes an SQL query.
     * 
     * @param sqlquery
     *            The sql query like inserts, updates, deletes, alters, etc.
     * @return The number of affected rows.
     * @throws SQLException
     *             If an exception occured.
     */
    public static int executeSqlQuery(final String sqlquery) throws SQLException {
        // open a database connection
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        int rowcount;
        try {
            final DataSource ds = Registry.getCurrentTenant().getDataSource();
            conn = ds.getConnection();

            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (stmt.execute(sqlquery)) {
                rs = stmt.getResultSet();
                rs.afterLast();
                rowcount = rs.getRow() - 1;
            } else {
                rowcount = stmt.getUpdateCount();
            }

            return rowcount;

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (final SQLException sqlCloseException) {
                LOG.warn(sqlCloseException);
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (final SQLException sqlCloseException) {
                LOG.warn(sqlCloseException);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException sqlCloseException) {
                LOG.warn(sqlCloseException);
            }
        }
    }

    /**
     * the SqlTenantFilelist to set.
     * 
     * @param sqlTenantFilelist
     *            the sqlTenantFilelist to set
     */
    public void setSqlTenantFilelist(final Map<String, List<String>> sqlTenantFilelist) {
        this.sqlTenantFilelist = sqlTenantFilelist;
    }

}
