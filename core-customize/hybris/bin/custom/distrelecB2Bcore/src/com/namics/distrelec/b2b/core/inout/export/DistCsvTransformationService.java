/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.export;

import java.io.InputStream;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * Interface with methods to transform data to CSV file format.
 * 
 * @author ceberle, Namics AG
 * @since Namics Distrelec 1.1
 */
public interface DistCsvTransformationService {

    InputStream transform(ResultSet resultSet,char seperator);

    InputStream transform(String[] header, List<String[]> arrayList);

    InputStream transform(String flexibleSearchQuery, final Map<String, Object> flexibleSearchParameters,char seperator);

}
