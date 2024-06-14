package com.distrelec.solrfacetsearch.provider.classattributeassignment.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.distrelec.solrfacetsearch.indexer.impl.DistSolrInputDocument;
import com.distrelec.solrfacetsearch.provider.product.DistAbstractValueResolverTest;

import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistClassAttributeAssignmentCodeValueResolverUnitTest extends DistAbstractValueResolverTest {

    private static final String CLASSIFICATION_ATTRIBUTE_CODE = "aB-09_";

    private static final String CLASSIFICATION_ATTRIBUTE_CODE_CLEARED = "ab09";

    @InjectMocks
    private DistClassAttributeAssignmentCodeValueResolver distClassAttributeAssignmentCodeValueResolver;

    @Mock
    private ClassificationAttributeModel classificationAttribute;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    @Before
    public void init() {
        super.init();

        cloneableIndexedProperty.setName("code");
        cloneableIndexedProperty.setExportId("code");
        cloneableIndexedProperty.setLocalized(false);

        when(classAttributeAssignment.getClassificationAttribute()).thenReturn(classificationAttribute);
        when(classificationAttribute.getCode()).thenReturn(CLASSIFICATION_ATTRIBUTE_CODE);
    }

    @Test
    public void testResolveCode() throws FieldValueProviderException {
        distClassAttributeAssignmentCodeValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty),
                                                              classAttributeAssignment);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(CLASSIFICATION_ATTRIBUTE_CODE_CLEARED), any());
    }

    @Test
    public void testResolveCodeNull() throws FieldValueProviderException {
        when(classificationAttribute.getCode()).thenReturn(null);

        distClassAttributeAssignmentCodeValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty),
                                                              classAttributeAssignment);

        verify(distSolrInputDocument, times(0)).addField(any(), any(), any());
    }

}
