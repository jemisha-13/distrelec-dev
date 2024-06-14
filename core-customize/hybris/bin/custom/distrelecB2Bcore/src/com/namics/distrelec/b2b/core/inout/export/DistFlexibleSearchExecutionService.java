/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.export;

import java.sql.ResultSet;
import java.util.Map;

/**
 * Interface that defines methods to execute a FlexibleSearch query as a raw JDBC statement.
 * 
 * @author ceberle, Namics AG
 * @since Namics Distrelec 1.1
 */
public interface DistFlexibleSearchExecutionService {

    String QUERY_TIMEOUT_PARAM = "queryTimeout";

    /**
     * Executes a FlexibleSearch query with the given parameters. Ensure the returned ResultSet will be closed when data was read.
     * 
     * @param flexibleSearchQuery
     *            the FlexibleSearch query
     * @param flexibleSearchParameters
     *            the FlexibleSearch parameters
     * @return a JDBC ResultSet
     */
    ResultSet execute(String flexibleSearchQuery, Map<String, Object> flexibleSearchParameters);

    ResultSet executionPlan(String flexibleSearchQuery, Map<String, Object> flexibleSearchParameters);

    void closeResultSet(ResultSet resultSet);

}
