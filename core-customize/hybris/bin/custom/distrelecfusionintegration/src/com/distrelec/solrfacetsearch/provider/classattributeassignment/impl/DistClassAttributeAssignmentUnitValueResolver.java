package com.distrelec.solrfacetsearch.provider.classattributeassignment.impl;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import com.distrelec.solrfacetsearch.provider.impl.AbstractDistValueResolver;
import com.namics.distrelec.b2b.core.service.unit.UnitConversionService;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistClassAttributeAssignmentUnitValueResolver extends AbstractDistValueResolver<ClassAttributeAssignmentModel> {

    private UnitConversionService unitConversionService;

    public DistClassAttributeAssignmentUnitValueResolver(UnitConversionService unitConversionService) {
        this.unitConversionService = unitConversionService;
    }

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty,
                                  ClassAttributeAssignmentModel classAttributeAssignment,
                                  ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        ClassificationAttributeUnitModel unit = classAttributeAssignment.getUnit();
        if (unit != null) {
            String baseUnitSymbolForUnitType = unitConversionService.getBaseUnitSymbolForUnitType(unit.getUnitType());
            String unitSymbol = isNotBlank(baseUnitSymbolForUnitType) ? baseUnitSymbolForUnitType : unit.getSymbol();
            addFieldValue(document, indexerBatchContext, indexedProperty, unitSymbol, resolverContext.getFieldQualifier());
        }
    }

}
