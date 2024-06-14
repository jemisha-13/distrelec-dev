package com.distrelec.solrfacetsearch.provider.product.impl;

import static de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes.DOUBLE;
import static de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes.STRING;
import static java.lang.System.currentTimeMillis;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.distrelec.b2b.core.search.data.PimWebUseField;
import com.distrelec.b2b.core.search.data.Unit;
import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;
import com.namics.distrelec.b2b.core.service.unit.UnitConversionService;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistPimWebUseIndividualFieldsValueResolver extends AbstractDistPimWebUseValueResolver {

    private static final Logger LOG = LogManager.getLogger(DistPimWebUseIndividualFieldsValueResolver.class);

    static {
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
        DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols();
        dfSymbols.setMonetaryDecimalSeparator('.');
        dfSymbols.setDecimalSeparator('.');
        decimalFormat.setDecimalFormatSymbols(dfSymbols);
    }

    protected DistPimWebUseIndividualFieldsValueResolver(DistCMSSiteDao distCMSSiteDao, DistProductSearchExportDAO distProductSearchExportDAO,
                                                         UnitConversionService unitConversionService, EnumerationService enumerationService, Gson gson) {
        super(distCMSSiteDao, distProductSearchExportDAO, unitConversionService, enumerationService, gson);
    }

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty, ProductModel productModel,
                                  ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        long t0 = currentTimeMillis();
        if (isBlank(productModel.getPimWebUseJson())) {
            return;
        }
        try {
            addPimWebUseFields(document, indexedProperty, productModel, resolverContext);
        } catch (JsonSyntaxException e) {
            LOG.warn("Could not parse PimWebUseNonLocalized-value for product:{}", productModel.getCode());
            return;
        }
        LOG.debug("Time to process webUse for product[{}] took: {}ms", productModel.getCode(), currentTimeMillis() - t0);
    }

    private void addPimWebUseFields(InputDocument document, IndexedProperty indexedProperty, ProductModel productModel,
                                    ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        PimWebUseField[] pimWebUseField = parsePimWebUseJson(productModel.getPimWebUseJson());

        for (PimWebUseField field : Arrays.asList(pimWebUseField)) {
            String attributeName = generatePimWebUseFieldKey(field.getCode());

            if (isNumerical(field)) {
                IndexedProperty newIndexedProperty = createNewIndexedProperty(indexedProperty, newPropertyName(indexedProperty, attributeName),
                                                                              DOUBLE.getCode());
                document.addField(newIndexedProperty, getNumericalValue(field), resolverContext.getFieldQualifier());
            } else {
                IndexedProperty newIndexedProperty = createNewIndexedProperty(indexedProperty, newPropertyName(indexedProperty, attributeName),
                                                                              STRING.getCode());
                document.addField(newIndexedProperty, field.getValue(), resolverContext.getFieldQualifier());
            }
        }
    }

    private Double getNumericalValue(PimWebUseField field) {
        Optional<Unit> unitBySymbol = getUnitConversionService().getUnitBySymbol(field.getUnit());
        if (isNotBlank(field.getUnit()) && unitBySymbol.isPresent()) {
            return getUnitConversionService().convertToBaseUnit(unitBySymbol.get(), field.getValue()).doubleValue();
        } else {
            return Double.valueOf(field.getValue());
        }
    }

    private String newPropertyName(IndexedProperty indexedProperty, String attributeName) {
        return indexedProperty.getExportId() + "_" + attributeName;
    }
}
