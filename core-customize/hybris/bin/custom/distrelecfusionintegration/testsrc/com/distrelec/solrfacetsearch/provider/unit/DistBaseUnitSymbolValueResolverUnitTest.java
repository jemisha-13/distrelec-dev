package com.distrelec.solrfacetsearch.provider.unit;

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
import com.namics.distrelec.b2b.core.service.classification.dao.DistClassificationDao;

import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistBaseUnitSymbolValueResolverUnitTest extends DistAbstractValueResolverTest {

    private static final String BASE_UNIT_SYMBOL = "m";

    @InjectMocks
    private DistBaseUnitSymbolValueResolver distBaseUnitSymbolValueResolver;

    @Mock
    private DistClassificationDao distClassificationDao;

    @Mock
    private ClassificationAttributeUnitModel unit;

    @Mock
    private ClassificationAttributeUnitModel baseUnit;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    @Before
    public void init() {
        super.init();

        cloneableIndexedProperty.setName("baseUnitSymbol");
        cloneableIndexedProperty.setExportId("baseUnitSymbol");
        cloneableIndexedProperty.setLocalized(true);

        when(distClassificationDao.findBaseUnitForUnit(unit)).thenReturn(baseUnit);
        when(baseUnit.getSymbol()).thenReturn(BASE_UNIT_SYMBOL);
    }

    @Test
    public void testResolve() throws FieldValueProviderException {
        distBaseUnitSymbolValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), unit);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(BASE_UNIT_SYMBOL), any());
    }

    @Test
    public void testResolveNull() throws FieldValueProviderException {
        when(distClassificationDao.findBaseUnitForUnit(unit)).thenReturn(null);

        distBaseUnitSymbolValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), unit);

        verify(distSolrInputDocument, times(0)).addField(any(), any(), any());
    }
}
