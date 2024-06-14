package com.distrelec.solrfacetsearch.provider.punchout.impl;

import static java.lang.Boolean.FALSE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.namics.distrelec.b2b.core.model.DistCUPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.QualifierProvider;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistPunchOutAllAttributesValueResolverUnitTest {

    private static final String SINGLE_PRODUCT_FILTER = "singleProductFilter";

    private static final String PRODUCT_HIERARCHY_FILTER = "productHierarchyFilter";

    private static final String MANUFACTURER_FILTER = "manufacturerFilter";

    private static final String TYPE_FIELD = "type";

    private static final String PRODUCT_HIERARCHY_CODE_FIELD = "productHierarchyCode";

    private static final String PRODUCT_CODE_FIELD = "productCode";

    private static final String MANUFACTURER_CODE_FIELD = "manufacturerCode";

    private static final String CUSTOMER_ERP_ID_FIELD = "customerId";

    private static final String SALES_ORG_CODE_FIELD = "salesOrgCode";

    private static final String PRODUCT_CODE = "18080220";

    private static final String PRODUCT_HIERARCHY_CODE = "515200";

    private static final String MANUFACTURER_CODE = "man_bel";

    private static final String CUSTOMER_ERP_ID = "0003102000";

    private static final String SALES_ORG_CODE = "7310";

    @InjectMocks
    private DistPunchOutAllAttributesValueResolver distPunchOutAllAttributesValueResolver;

    @Mock
    protected SessionService sessionService;

    @Mock
    protected Session session;

    @Mock
    protected JaloSession jaloSession;

    @Mock
    protected QualifierProvider qualifierProvider;

    @Mock
    protected InputDocument document;

    @Mock
    protected IndexerBatchContext indexerBatchContext;

    protected IndexedProperty cloneableIndexedProperty;

    @Mock
    private ProductModel product;

    @Mock
    private DistManufacturerModel manufacturer;

    @Mock
    private DistSalesOrgModel salesOrg;

    @Mock
    private DistCUPunchOutFilterModel singleProductPunchOutFilter;

    @Mock
    private DistCUPunchOutFilterModel productHierarchyPunchOutFilter;

    @Mock
    private DistManufacturerPunchOutFilterModel manufacturerPunchOutFilter;

    @Mock
    private DistPunchOutFilterModel unconfiguredPunchOutFilter;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);

        when(sessionService.getCurrentSession()).thenReturn(session);
        when(sessionService.getRawSession(session)).thenReturn(jaloSession);

        cloneableIndexedProperty = new IndexedProperty();

        when(qualifierProvider.canApply(cloneableIndexedProperty)).thenReturn(FALSE);

        when(product.getCode()).thenReturn(PRODUCT_CODE);
        when(manufacturer.getCode()).thenReturn(MANUFACTURER_CODE);
        when(salesOrg.getCode()).thenReturn(SALES_ORG_CODE);

        when(singleProductPunchOutFilter.getProduct()).thenReturn(product);
        when(singleProductPunchOutFilter.getSalesOrg()).thenReturn(salesOrg);
        when(singleProductPunchOutFilter.getErpCustomerID()).thenReturn(CUSTOMER_ERP_ID);

        when(productHierarchyPunchOutFilter.getProductHierarchy()).thenReturn(PRODUCT_HIERARCHY_CODE);
        when(productHierarchyPunchOutFilter.getSalesOrg()).thenReturn(salesOrg);
        when(productHierarchyPunchOutFilter.getErpCustomerID()).thenReturn(CUSTOMER_ERP_ID);

        when(manufacturerPunchOutFilter.getManufacturer()).thenReturn(manufacturer);
        when(manufacturerPunchOutFilter.getSalesOrg()).thenReturn(salesOrg);
        when(manufacturerPunchOutFilter.getErpCustomerID()).thenReturn(CUSTOMER_ERP_ID);
    }

    @Test
    public void testResolveSingleProductPunchOutFilter() throws FieldValueProviderException {
        distPunchOutAllAttributesValueResolver.resolve(document, indexerBatchContext, List.of(cloneableIndexedProperty), singleProductPunchOutFilter);

        verify(document, times(1)).addField(refEq(stringIndexedProperty(TYPE_FIELD)), eq(SINGLE_PRODUCT_FILTER), any());
        verify(document, times(1)).addField(refEq(stringIndexedProperty(PRODUCT_CODE_FIELD)), eq(PRODUCT_CODE), any());
        verify(document, times(1)).addField(refEq(stringIndexedProperty(CUSTOMER_ERP_ID_FIELD)), eq(CUSTOMER_ERP_ID), any());
        verify(document, times(1)).addField(refEq(stringIndexedProperty(SALES_ORG_CODE_FIELD)), eq(SALES_ORG_CODE), any());

        verify(document, times(0)).addField(refEq(stringIndexedProperty(PRODUCT_HIERARCHY_CODE_FIELD)), any(), any());
        verify(document, times(0)).addField(refEq(stringIndexedProperty(MANUFACTURER_CODE_FIELD)), any(), any());
    }

    @Test
    public void testResolveProductHierarchyPunchOutFilter() throws FieldValueProviderException {
        distPunchOutAllAttributesValueResolver.resolve(document, indexerBatchContext, List.of(cloneableIndexedProperty), productHierarchyPunchOutFilter);

        verify(document, times(1)).addField(refEq(stringIndexedProperty(TYPE_FIELD)), eq(PRODUCT_HIERARCHY_FILTER), any());
        verify(document, times(1)).addField(refEq(stringIndexedProperty(PRODUCT_HIERARCHY_CODE_FIELD)), eq(PRODUCT_HIERARCHY_CODE), any());
        verify(document, times(1)).addField(refEq(stringIndexedProperty(CUSTOMER_ERP_ID_FIELD)), eq(CUSTOMER_ERP_ID), any());
        verify(document, times(1)).addField(refEq(stringIndexedProperty(SALES_ORG_CODE_FIELD)), eq(SALES_ORG_CODE), any());

        verify(document, times(0)).addField(refEq(stringIndexedProperty(PRODUCT_CODE_FIELD)), any(), any());
        verify(document, times(0)).addField(refEq(stringIndexedProperty(MANUFACTURER_CODE_FIELD)), any(), any());
    }

    @Test
    public void testResolveManufacturerPunchOutFilter() throws FieldValueProviderException {
        distPunchOutAllAttributesValueResolver.resolve(document, indexerBatchContext, List.of(cloneableIndexedProperty), manufacturerPunchOutFilter);

        verify(document, times(1)).addField(refEq(stringIndexedProperty(TYPE_FIELD)), eq(MANUFACTURER_FILTER), any());
        verify(document, times(1)).addField(refEq(stringIndexedProperty(MANUFACTURER_CODE_FIELD)), eq(MANUFACTURER_CODE), any());
        verify(document, times(1)).addField(refEq(stringIndexedProperty(CUSTOMER_ERP_ID_FIELD)), eq(CUSTOMER_ERP_ID), any());
        verify(document, times(1)).addField(refEq(stringIndexedProperty(SALES_ORG_CODE_FIELD)), eq(SALES_ORG_CODE), any());

        verify(document, times(0)).addField(refEq(stringIndexedProperty(PRODUCT_CODE_FIELD)), any(), any());
        verify(document, times(0)).addField(refEq(stringIndexedProperty(PRODUCT_HIERARCHY_CODE_FIELD)), any(), any());
    }

    @Test
    public void testResolveUnconfiguredPunchOutFilter() throws FieldValueProviderException {
        distPunchOutAllAttributesValueResolver.resolve(document, indexerBatchContext, List.of(cloneableIndexedProperty), unconfiguredPunchOutFilter);

        verify(document, times(0)).addField(refEq(stringIndexedProperty(TYPE_FIELD)), any(), any());
        verify(document, times(0)).addField(refEq(stringIndexedProperty(MANUFACTURER_CODE_FIELD)), any(), any());
        verify(document, times(0)).addField(refEq(stringIndexedProperty(CUSTOMER_ERP_ID_FIELD)), any(), any());
        verify(document, times(0)).addField(refEq(stringIndexedProperty(SALES_ORG_CODE_FIELD)), any(), any());
        verify(document, times(0)).addField(refEq(stringIndexedProperty(PRODUCT_CODE_FIELD)), any(), any());
        verify(document, times(0)).addField(refEq(stringIndexedProperty(PRODUCT_HIERARCHY_CODE_FIELD)), any(), any());
    }

    private IndexedProperty stringIndexedProperty(String attributeName) {
        return distPunchOutAllAttributesValueResolver.createNewIndexedProperty(cloneableIndexedProperty,
                                                                               attributeName,
                                                                               SolrPropertiesTypes.STRING.getCode());
    }

}
