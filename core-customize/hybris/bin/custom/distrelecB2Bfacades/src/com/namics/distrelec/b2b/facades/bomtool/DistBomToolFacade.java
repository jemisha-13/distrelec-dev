/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.bomtool;

import com.namics.distrelec.b2b.core.bomtool.data.BomToolImportData;
import com.namics.distrelec.b2b.facades.bomtool.exception.BomToolFacadeException;

import de.hybris.platform.core.model.media.MediaModel;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * DistImportToolFacade
 *
 * @author Ajinkya, Distrelec
 * @since Distrelec 1.0
 */
public interface DistBomToolFacade {
	BomToolSearchResultData searchProductsFromFile(final MediaModel file, final int articleNumberPosition,
			final int quantityPosition, final Integer referencePosition, final boolean ignoreFirstRow)
			throws BomToolFacadeException;

	BomToolSearchResultData searchProductsFromData(final String data) throws BomToolFacadeException;

	BomToolSearchResultData searchProductsFromData(final String data, final int articleNumberPosition,
			final int quantityPosition, final int referencePosition) throws BomToolFacadeException;

	List<String[]> getLinesFromFile(final MediaModel file) throws BomToolFacadeException;

	void deleteBomImportEntry(String productCode, String fileName);

	List<BomToolImportData> getSavedBomToolEntries();

	BomToolSearchResultData loadBomFile(final String fileName) throws BomToolFacadeException;

	void createBomFile(BomToolImportData data);

	boolean deleteBomFile(String filename);

	void editBomFile(BomToolImportData importData);

	String copyBomFile(String filename);

	String renameBomImportFile(String currentName, String newName);

	long getCountOfBomFilesUploadedForCurrentCustomer();

	void saveCartTimestamp(BomToolImportData importData);

	MediaModel saveFileAsMedia(final String fileName, final CommonsMultipartFile uploadFile) throws IOException;

	void removeFileIfAlreadyExists(final String fileName);

	String generateBomToolFileName(final String originalFileName);
}
