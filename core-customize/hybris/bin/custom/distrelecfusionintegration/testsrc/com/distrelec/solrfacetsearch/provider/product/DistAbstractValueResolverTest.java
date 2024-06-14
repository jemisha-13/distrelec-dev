package com.distrelec.solrfacetsearch.provider.product;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.ExpressionEvaluator;
import de.hybris.platform.solrfacetsearch.provider.QualifierProvider;

@Ignore
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public abstract class DistAbstractValueResolverTest {

    @Mock
    protected DistCMSSiteDao distCMSSiteDao;

    @Mock
    protected DistProductSearchExportDAO distProductSearchExportDAO;

    @Mock
    protected EnumerationService enumerationService;

    @Mock
    protected ModelService modelService;

    @Mock
    protected TypeService typeService;

    @Mock
    protected ExpressionEvaluator expressionEvaluator;

    @Mock
    protected QualifierProvider qualifierProvider;

    @Mock
    protected SessionService sessionService;

    @Mock
    protected Session session;

    @Mock
    protected JaloSession jaloSession;

    // parameters
    @Mock
    protected InputDocument document;

    @Mock
    protected IndexerBatchContext indexerBatchContext;

    @Mock
    protected IndexedProperty indexedProperty;

    @Mock
    protected ProductModel product;

    @Mock
    protected DistManufacturerModel manufacturer;

    @Mock
    protected CategoryModel category;

    @Mock
    protected ClassAttributeAssignmentModel classAttributeAssignment;

    protected IndexedProperty cloneableIndexedProperty;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);

        cloneableIndexedProperty = new IndexedProperty();
        when(sessionService.getCurrentSession()).thenReturn(session);
        when(sessionService.getRawSession(session)).thenReturn(jaloSession);
    }

}
