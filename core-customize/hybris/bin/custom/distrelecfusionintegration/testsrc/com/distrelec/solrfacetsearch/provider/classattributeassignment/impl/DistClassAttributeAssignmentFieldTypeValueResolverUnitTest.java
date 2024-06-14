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

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;

public class DistClassAttributeAssignmentFieldTypeValueResolverUnitTest extends DistAbstractValueResolverTest {

    @InjectMocks
    private DistClassAttributeAssignmentFieldTypeValueResolver distClassAttributeAssignmentFieldTypeValueResolver;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    @Before
    public void init() {
        super.init();

        cloneableIndexedProperty.setName("fieldType");
        cloneableIndexedProperty.setExportId("fieldType");
        cloneableIndexedProperty.setLocalized(false);
    }

    @Test
    public void testResolveFieldTypeDouble() throws FieldValueProviderException {
        when(classAttributeAssignment.getAttributeType()).thenReturn(ClassificationAttributeTypeEnum.NUMBER);

        distClassAttributeAssignmentFieldTypeValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty),
                                                                   classAttributeAssignment);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(SolrPropertiesTypes.DOUBLE.getCode()), any());
    }

    @Test
    public void testResolveFieldTypeString() throws FieldValueProviderException {
        when(classAttributeAssignment.getAttributeType()).thenReturn(ClassificationAttributeTypeEnum.STRING);

        distClassAttributeAssignmentFieldTypeValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty),
                                                                   classAttributeAssignment);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(SolrPropertiesTypes.STRING.getCode()), any());
    }

}
