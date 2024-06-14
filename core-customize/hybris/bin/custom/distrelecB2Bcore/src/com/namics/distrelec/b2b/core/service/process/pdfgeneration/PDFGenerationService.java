/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.core.service.process.pdfgeneration;

import java.io.File;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;

/**
 * Service for generating a pdf.
 */
public interface PDFGenerationService {

    File transformModelToPDF(BusinessProcessModel businessProcessModel, String xslMediaId, String rendererTemplateCode, BaseSiteModel baseSiteModel);
}
