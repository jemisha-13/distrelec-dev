/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.service;

import com.namics.hybris.ffsearch.model.DistFactFinderExportChannelModel;
import com.namics.hybris.ffsearch.model.export.DistAbstractFactFinderExportCronJobModel;

/**
 * Service interface with methods to manage FactFinder search index.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public interface FactFinderIndexManagementService {

    void startImport(DistAbstractFactFinderExportCronJobModel cronJob, DistFactFinderExportChannelModel channel) throws Exception;

}
