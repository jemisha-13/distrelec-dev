package com.namics.distrelec.b2b.core.reconciliation.dao;

import java.sql.ResultSet;
import java.util.List;


public interface DistPriceReconciliationDao {

	ResultSet fetchAllPriceRows(String salesOrg);

}
