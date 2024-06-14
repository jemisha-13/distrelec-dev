/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.export;

import com.namics.distrelec.b2b.core.service.export.data.AbstractDistExportData;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Service for export list of products to CSV or XLS.
 *
 * @author pbueschi, Namics AG
 */
public interface DistExportService {

    File getDownloadExportFile(final List<? extends AbstractDistExportData> exportData, final String exportFormat, final String exportFileNamePrefix);

    void saveExportData(InputStream exportDataStream, String exportMediaName, String exportFolder, String fileType, boolean exportAsZip);
}
