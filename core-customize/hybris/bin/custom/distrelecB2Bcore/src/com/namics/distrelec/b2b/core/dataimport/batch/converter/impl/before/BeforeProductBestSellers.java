/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.dataimport.batch.converter.impl.before;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.dataimport.batch.converter.BeforeConverting;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.util.CSVReader;

/**
 * Deletes all existing best seller labels.
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 3.0
 * 
 */
public class BeforeProductBestSellers implements BeforeConverting {

    private final static Logger LOG = Logger.getLogger(BeforeProductBestSellers.class);

    private FlexibleSearchService flexibleSearchService;
    private ModelService modelService;

    @Override
    public void before(final BatchHeader header, final CSVReader csvReader) {
        // delete all existing best sellers
        final FlexibleSearchQuery query = new FlexibleSearchQuery("SELECT {" + DistSalesOrgProductModel.PK + "} FROM {" + DistSalesOrgProductModel._TYPECODE
                + "} WHERE {" + DistSalesOrgProductModel.SHOWBESTSELLERLABELFROMDATE + "} IS NOT NULL");
        final List<DistSalesOrgProductModel> result = getFlexibleSearchService().<DistSalesOrgProductModel> search(query).getResult();
        for (final DistSalesOrgProductModel bestSeller : result) {
            try {
                bestSeller.setShowBestsellerLabelFromDate(null);
                bestSeller.setShowBestsellerLabelUntilDate(null);
                getModelService().save(bestSeller);
            } catch (final ModelSavingException e) {
                LOG.error("Can not delete bestseller label for product " + bestSeller.getProduct().getCode() + "!", e);
            }
        }
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    @Required
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }
}
