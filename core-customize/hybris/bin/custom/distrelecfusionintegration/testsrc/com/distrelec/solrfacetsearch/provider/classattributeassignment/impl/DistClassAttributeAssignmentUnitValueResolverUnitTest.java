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
import com.namics.distrelec.b2b.core.service.unit.UnitConversionService;

import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistClassAttributeAssignmentUnitValueResolverUnitTest extends DistAbstractValueResolverTest {

    private static final String UNIT_TYPE = "unece.unit.MTR";

    private static final String UNIT_SYMBOL = "cm";

    private static final String BASE_UNIT_SYMBOL = "m";

    @InjectMocks
    private DistClassAttributeAssignmentUnitValueResolver distClassAttributeAssignmentUnitValueResolver;

    @Mock
    private UnitConversionService unitConversionService;

    @Mock
    private ClassificationAttributeUnitModel classificationAttributeUnit;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    @Before
    public void init() {
        super.init();

        cloneableIndexedProperty.setName("unit");
        cloneableIndexedProperty.setExportId("unit");
        cloneableIndexedProperty.setLocalized(false);

        when(classAttributeAssignment.getUnit()).thenReturn(classificationAttributeUnit);
        when(classificationAttributeUnit.getUnitType()).thenReturn(UNIT_TYPE);
        when(classificationAttributeUnit.getSymbol()).thenReturn(UNIT_SYMBOL);

    }

    @Test
    public void testResolveUnitBaseUnitSymbol() throws FieldValueProviderException {
        when(unitConversionService.getBaseUnitSymbolForUnitType(UNIT_TYPE)).thenReturn(BASE_UNIT_SYMBOL);

        distClassAttributeAssignmentUnitValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty),
                                                              classAttributeAssignment);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(BASE_UNIT_SYMBOL), any());
    }

    @Test
    public void testResolveUnitBaseUnitSymbolEmpty() throws FieldValueProviderException {
        when(unitConversionService.getBaseUnitSymbolForUnitType(UNIT_TYPE)).thenReturn(null);

        distClassAttributeAssignmentUnitValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty),
                                                              classAttributeAssignment);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(UNIT_SYMBOL), any());
    }

    @Test
    public void testResolveUnitNoUnit() throws FieldValueProviderException {
        when(classAttributeAssignment.getUnit()).thenReturn(null);

        distClassAttributeAssignmentUnitValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty),
                                                              classAttributeAssignment);

        verify(distSolrInputDocument, times(0)).addField(any(), any(), any());
    }
}
