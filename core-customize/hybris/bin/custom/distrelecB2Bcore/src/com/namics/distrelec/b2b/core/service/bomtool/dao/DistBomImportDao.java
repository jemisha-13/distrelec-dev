package com.namics.distrelec.b2b.core.service.bomtool.dao;


import com.namics.distrelec.b2b.core.model.bomtool.BomImportModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.List;
import java.util.Optional;

public interface DistBomImportDao {

    Optional<BomImportModel> findBomImportByCustomerAndName(CustomerModel customer, String name);

    List<BomImportModel> findBomImportsByCustomer(CustomerModel customer);

    long getCountOfBomFilesByCustomer(CustomerModel customer);

	List<BomImportModel> getUnusedBomFiles(int unusedTimestamp, int deleteTimestamp);

	List<BomImportModel> deleteUnusedBomFiles(int deleteTimestamp);
}
