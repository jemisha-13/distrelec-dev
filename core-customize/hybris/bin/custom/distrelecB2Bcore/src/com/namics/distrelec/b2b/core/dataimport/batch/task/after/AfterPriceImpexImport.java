/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.dataimport.batch.task.after;

import java.io.File;
import java.util.List;

import com.namics.distrelec.b2b.core.dataimport.batch.util.ErpSequenceIdParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.dataimport.batch.task.AfterImpexImport;
import com.namics.distrelec.b2b.core.model.DistPriceRowModel;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.util.SequenceIdParser;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

/**
 * Cleans all the price rows that were not part of the last import.
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 3.0
 * 
 */
public class AfterPriceImpexImport implements AfterImpexImport {

    private static final Logger LOG = LogManager.getLogger(AfterPriceImpexImport.class);
    private static final String SELECT_DISTPRICEMODEL_FOR_REMOVAL_SEQID = "SELECT {" + DistPriceRowModel.PK + "} FROM {" + DistPriceRowModel._TYPECODE + "} WHERE {"
            + DistPriceRowModel.SEQUENCEID + "}!=?" + DistPriceRowModel.SEQUENCEID + " AND {" + DistPriceRowModel.PRICECONDITIONIDERP
            + "} IN ({{ SELECT DISTINCT {" + DistPriceRowModel.PRICECONDITIONIDERP + "} FROM {" + DistPriceRowModel._TYPECODE + "} WHERE {"
            + DistPriceRowModel.SEQUENCEID + "}=?" + DistPriceRowModel.SEQUENCEID + " }})";

    private static final String SELECT_DISTPRICEMODEL_FOR_REMOVAL_ERPSEQID = "SELECT {" + DistPriceRowModel.PK + "} FROM {" + DistPriceRowModel._TYPECODE + "} WHERE {"
        + DistPriceRowModel.ERPSEQUENCEID + "}!=?" + DistPriceRowModel.ERPSEQUENCEID + " AND {" + DistPriceRowModel.PRICECONDITIONIDERP
        + "} IN ({{ SELECT DISTINCT {" + DistPriceRowModel.PRICECONDITIONIDERP + "} FROM {" + DistPriceRowModel._TYPECODE + "} WHERE {"
        + DistPriceRowModel.ERPSEQUENCEID + "}=?" + DistPriceRowModel.ERPSEQUENCEID + " }})";

    private ErpSequenceIdParser erpSequenceIdParser;
    private SequenceIdParser sequenceIdParser;
    private FlexibleSearchService flexibleSearchService;
    private ModelService modelService;
    private CatalogService catalogService;

    @Override
    public void after(final ImportConfig importConfig, final BatchHeader batchHeader, final File originalFile, final File impexFile) {
        Long sequenceId = getSequenceIdParser().getSequenceId(originalFile);
        String erpSequenceId = null;;
        if (sequenceId == null) {
            // parse erp sequence id if hybris sequence id is unknown
            erpSequenceId = getErpSequenceIdParser().getErpSequenceId(originalFile);
        }
        removePriceConditions(batchHeader, sequenceId, erpSequenceId);
        if (importConfig != null) {
            importConfig.setLegacyMode(Boolean.FALSE);
        }
    }

    protected void removePriceConditions(final BatchHeader batchHeader, Long sequenceId, String erpSequenceId) {
        final FlexibleSearchQuery query;
        if (sequenceId != null) {
            query = new FlexibleSearchQuery(SELECT_DISTPRICEMODEL_FOR_REMOVAL_SEQID);
            query.addQueryParameter(DistPriceRowModel.SEQUENCEID, sequenceId);
        } else {
            query = new FlexibleSearchQuery(SELECT_DISTPRICEMODEL_FOR_REMOVAL_ERPSEQID);
            query.addQueryParameter(DistPriceRowModel.ERPSEQUENCEID, erpSequenceId);
        }
        query.setCatalogVersions(getCatalogService().getCatalogForId(batchHeader.getCatalog()).getActiveCatalogVersion());

        final List<DistPriceRowModel> result = getFlexibleSearchService().<DistPriceRowModel> search(query).getResult();
        LOG.info("Found " + result.size() + " prices to delete!");

        for (final DistPriceRowModel priceRow : result) {
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Delete price row " + priceRow.getPriceConditionIdErp());
                }
                getModelService().remove(priceRow);
            } catch (final ModelRemovalException e) {
                LOG.error("Can not remove price row " + priceRow);
            }
        }
    }

    public ErpSequenceIdParser getErpSequenceIdParser() {
        return erpSequenceIdParser;
    }

    @Required
    public void setErpSequenceIdParser(final ErpSequenceIdParser erpSequenceIdParser) {
        this.erpSequenceIdParser = erpSequenceIdParser;
    }

    public SequenceIdParser getSequenceIdParser() {
        return sequenceIdParser;
    }

    @Required
    public void setSequenceIdParser(final SequenceIdParser sequenceIdParser) {
        this.sequenceIdParser = sequenceIdParser;
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
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public CatalogService getCatalogService() {
        return catalogService;
    }

    @Required
    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

}
