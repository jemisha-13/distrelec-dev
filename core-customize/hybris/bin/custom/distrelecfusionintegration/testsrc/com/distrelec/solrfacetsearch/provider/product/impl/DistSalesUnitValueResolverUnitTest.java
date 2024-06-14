package com.distrelec.solrfacetsearch.provider.product.impl;

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
import com.namics.distrelec.b2b.core.model.DistSalesUnitModel;

import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistSalesUnitValueResolverUnitTest extends DistAbstractValueResolverTest {

    private static final String SALES_UNIT_NAME = "salesUnitName";

    private static final String SALES_UNIT_NAME_ERP = "salesUnitNameErp";

    private static final String UNIT_NAME = "salesUnitNameErp";

    @InjectMocks
    private DistSalesUnitValueResolver distSalesUnitValueResolver;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    @Mock
    private DistSalesUnitModel salesUnit;

    @Mock
    private UnitModel unit;

    @Before
    public void init() {
        super.init();

        cloneableIndexedProperty.setName("salesUnit");
        cloneableIndexedProperty.setExportId("salesUnit");
        cloneableIndexedProperty.setLocalized(true);
    }

    @Test
    public void testGetSalesUnitFieldName() throws FieldValueProviderException {
        when(salesUnit.getName()).thenReturn(SALES_UNIT_NAME);
        when(product.getSalesUnit()).thenReturn(salesUnit);

        distSalesUnitValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(SALES_UNIT_NAME), any());
    }

    @Test
    public void testGetSalesUnitFieldNameErp() throws FieldValueProviderException {
        when(salesUnit.getName()).thenReturn(null);
        when(salesUnit.getNameErp()).thenReturn(SALES_UNIT_NAME_ERP);
        when(product.getSalesUnit()).thenReturn(salesUnit);

        distSalesUnitValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(SALES_UNIT_NAME_ERP), any());
    }

    @Test
    public void testGetSalesUnitFieldUnitName() throws FieldValueProviderException {
        when(product.getSalesUnit()).thenReturn(null);
        when(product.getUnit()).thenReturn(unit);
        when(unit.getName()).thenReturn(UNIT_NAME);

        distSalesUnitValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(UNIT_NAME), any());
    }

}
