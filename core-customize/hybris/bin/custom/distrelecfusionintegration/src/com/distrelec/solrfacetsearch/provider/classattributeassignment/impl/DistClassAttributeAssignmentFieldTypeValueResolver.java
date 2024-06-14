package com.distrelec.solrfacetsearch.provider.classattributeassignment.impl;

import com.distrelec.solrfacetsearch.provider.impl.AbstractDistValueResolver;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistClassAttributeAssignmentFieldTypeValueResolver extends AbstractDistValueResolver<ClassAttributeAssignmentModel> {

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty,
                                  ClassAttributeAssignmentModel classAttributeAssignment,
                                  ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        String fieldType = isNumericField(classAttributeAssignment) ? SolrPropertiesTypes.DOUBLE.getCode() : SolrPropertiesTypes.STRING.getCode();
        addFieldValue(document, indexerBatchContext, indexedProperty, fieldType, resolverContext.getFieldQualifier());
    }

    private boolean isNumericField(ClassAttributeAssignmentModel classAttributeAssignment) {
        return ClassificationAttributeTypeEnum.NUMBER.equals(classAttributeAssignment.getAttributeType());
    }
}
