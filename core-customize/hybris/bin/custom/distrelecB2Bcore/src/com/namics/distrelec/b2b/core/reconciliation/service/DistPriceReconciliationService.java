package com.namics.distrelec.b2b.core.reconciliation.service;

import java.io.InputStream;
import java.sql.ResultSet;

public interface DistPriceReconciliationService {

    ResultSet getAllPriceRows(String salesOrg);

    InputStream transform(ResultSet resultSet);

}
