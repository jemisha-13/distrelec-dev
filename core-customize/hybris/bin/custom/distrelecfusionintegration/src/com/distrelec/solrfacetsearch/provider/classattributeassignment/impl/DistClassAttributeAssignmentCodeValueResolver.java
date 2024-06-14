package com.distrelec.solrfacetsearch.provider.classattributeassignment.impl;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Fusion.ALLOWED_CHARACTERS_FIELDNAME;
import static org.apache.commons.lang.StringUtils.EMPTY;

import com.distrelec.solrfacetsearch.provider.impl.AbstractDistValueResolver;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistClassAttributeAssignmentCodeValueResolver extends AbstractDistValueResolver<ClassAttributeAssignmentModel> {

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty,
                                  ClassAttributeAssignmentModel classAttributeAssignment,
                                  ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        ClassificationAttributeModel classificationAttribute = classAttributeAssignment.getClassificationAttribute();

        String code = removeNonAlphanumeric(classificationAttribute.getCode());
        addFieldValue(document, indexerBatchContext, indexedProperty, code, resolverContext.getFieldQualifier());
    }

    private String removeNonAlphanumeric(String string) {
        return string != null ? string.replaceAll(ALLOWED_CHARACTERS_FIELDNAME, "").toLowerCase() : EMPTY;
    }
}
