/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.ElementPath;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

/**
 * Element handler for "root" XML element. Simply used to log XML export time.
 * 
 * @author ascherrer, Namics AG
 * @since Distrelec 1.1
 */
public class RootElementHandler extends AbstractPimImportElementHandler {
    private static final Logger LOG = LogManager.getLogger(RootElementHandler.class);

    private static final String EXPORT_TIME = "ExportTime";
    private static final String CONTEXT_ID = "ContextID";

    @Autowired
    private CommonI18NService commonI18NService;

    @Override
    public void onPimImportElementStart(final ElementPath elementPath) {
        final Element element = elementPath.getCurrent();

        final String exportDate = element.attributeValue(EXPORT_TIME);
        LOG.info("Stibo export time: [{}]", exportDate);

        getImportContext().setStiboExportDate(convertDate(exportDate));

        final String contextId = element.attributeValue(CONTEXT_ID);
        int index = -1;
        if (StringUtils.isNotBlank(contextId) && (index = contextId.indexOf("-")) > 0) {
            try {
                getImportContext().setCountry(commonI18NService.getCountry(contextId.substring(0, index)));
            } catch (final UnknownIdentifierException uie) {
                // NOP
            }
        }
    }

    private Date convertDate(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            return formatter.parse(dateString);
        } catch (ParseException e) {
            LOG.error("Unable to parse Stibo export date.", e);
            return null;
        }
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }
}
