package com.distrelec.solrfacetsearch.provider.product.impl;

import static java.lang.System.currentTimeMillis;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.text.DecimalFormatSymbols;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.google.gson.Gson;
import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;
import com.namics.distrelec.b2b.core.service.unit.UnitConversionService;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistPimWebUseDisplayValueResolver extends AbstractDistPimWebUseValueResolver {

    private static final Logger LOG = LogManager.getLogger(DistPimWebUseDisplayValueResolver.class);

    static {
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
        DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols();
        dfSymbols.setMonetaryDecimalSeparator('.');
        dfSymbols.setDecimalSeparator('.');
        decimalFormat.setDecimalFormatSymbols(dfSymbols);
    }

    protected DistPimWebUseDisplayValueResolver(DistCMSSiteDao distCMSSiteDao, DistProductSearchExportDAO distProductSearchExportDAO,
                                                UnitConversionService unitConversionService, EnumerationService enumerationService, Gson gson) {
        super(distCMSSiteDao, distProductSearchExportDAO, unitConversionService, enumerationService, gson);
    }

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty, ProductModel productModel,
                                  ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        long t0 = currentTimeMillis();
        String pimWebUseJson = productModel.getPimWebUseJson();
        if (isNotBlank(pimWebUseJson)) {
            LOG.debug("generating webUse for product:{}", productModel.getCode());

            document.addField(indexedProperty, pimWebUseJson, resolverContext.getFieldQualifier());
        }
        LOG.debug("Time to process webUse for product[{}] took: {}ms", productModel.getCode(), currentTimeMillis() - t0);

    }

}
