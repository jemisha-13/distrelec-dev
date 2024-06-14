package com.namics.distrelec.b2b.core.bomtool;

import com.namics.distrelec.b2b.core.bomtool.data.BomToolImportData;
import com.namics.distrelec.b2b.core.model.bomtool.BomImportModel;

import java.util.List;

public interface BomToolService {

    BomImportModel createBomFile(BomToolImportData data);

    void updateBomFile(BomToolImportData data);

    String renameBomFile(String currentName, String newName);

    BomImportModel cloneBomFile(String originalName);

    void removeBomImportEntry(String productCode, String fileName);

    BomImportModel findBomImportByNameForCurrentCustomer(String name);

    long getCountOfFilesUploadedForCurrentCustomer();

    List<BomImportModel> findBomImportsForCurrentCustomer();

    BomToolSearchResult searchBomProducts(List<BomToolSearchRow> searchRows);

	void updateCartTimestamp(BomToolImportData importData);

	void notifyUsersAboutBomFiles();

	void deleteUnusedBomFiles();
}
