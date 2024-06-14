package com.distrelec.solrfacetsearch.provider.product.impl;

import static java.lang.System.currentTimeMillis;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.distrelec.b2b.core.search.data.PimWebUseField;
import com.distrelec.b2b.core.search.data.Unit;
import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.google.gson.Gson;
import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;
import com.namics.distrelec.b2b.core.service.unit.UnitConversionService;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistPimWebUseFullTextSearchValueResolver extends AbstractDistPimWebUseValueResolver {

    private static final Logger LOG = LogManager.getLogger(DistPimWebUseFullTextSearchValueResolver.class);

    protected DistPimWebUseFullTextSearchValueResolver(DistCMSSiteDao distCMSSiteDao, DistProductSearchExportDAO distProductSearchExportDAO,
                                                       UnitConversionService unitConversionService, EnumerationService enumerationService, Gson gson) {
        super(distCMSSiteDao, distProductSearchExportDAO, unitConversionService, enumerationService, gson);
    }

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty, ProductModel productModel,
                                  ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        long t1 = currentTimeMillis();
        String pimWebUseJson = productModel.getPimWebUseJson();
        if (!isNotBlank(pimWebUseJson)) {
            return;
        }

        PimWebUseField[] pimWebUseField = parsePimWebUseJson(pimWebUseJson);
        List<String> textFields = new ArrayList<>();
        List<String> numericalFields = new ArrayList<>();

        for (PimWebUseField field : pimWebUseField) {
            String value = field.getValue();
            if (isNumerical(field)) {
                Optional<Unit> baseUnit = getUnitConversionService().getUnitBySymbol(field.getUnit());
                if (isNotBlank(field.getUnit()) && baseUnit.isPresent()) {
                    BigDecimal bigDecimal = getUnitConversionService().convertToBaseUnit(baseUnit.get(), value);
                    String baseUnitSymbolForUnitType = getUnitConversionService().getBaseUnitSymbolForUnitType(baseUnit.get().getUnitType());
                    numericalFields.add(bigDecimal.toPlainString() + " " + baseUnitSymbolForUnitType);
                } else if (isNotBlank(field.getUnit())) {
                    numericalFields.add(value + " " + field.getUnit());
                } else {
                    numericalFields.add(value);
                }
            } else {
                String unitSymbol = isNotBlank(field.getUnit()) ? (" " + field.getUnit()) : StringUtils.EMPTY;
                textFields.add(value + unitSymbol);
            }

        }

        if (isNotEmpty(textFields)) {
            IndexedProperty newIndexedProperty = createNewIndexedProperty(indexedProperty, indexedProperty.getExportId() + "_" + "textValues",
                                                                          SolrPropertiesTypes.STRING.getCode());
            document.addField(newIndexedProperty, textFields, resolverContext.getFieldQualifier());
        }

        if (isNotEmpty(numericalFields)) {
            IndexedProperty newIndexedProperty = createNewIndexedProperty(indexedProperty, indexedProperty.getExportId() + "_" + "numericalValues",
                                                                          SolrPropertiesTypes.STRING.getCode());
            document.addField(newIndexedProperty, numericalFields, resolverContext.getFieldQualifier());
        }

        LOG.debug("Time to create pimwebusefulltextsearch-field for product[{}] took: {}ms", productModel.getCode(), (currentTimeMillis() - t1));
    }
}
