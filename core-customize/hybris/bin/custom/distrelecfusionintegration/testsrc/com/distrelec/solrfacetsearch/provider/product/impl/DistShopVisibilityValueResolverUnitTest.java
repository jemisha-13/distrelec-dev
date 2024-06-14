package com.distrelec.solrfacetsearch.provider.product.impl;

import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.PRICEINFO_CONTEXT_FIELD;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.refEq;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.distrelec.solrfacetsearch.indexer.impl.DistSolrInputDocument;
import com.distrelec.solrfacetsearch.provider.product.DistAbstractValueResolverTest;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;

public class DistShopVisibilityValueResolverUnitTest extends DistAbstractValueResolverTest {

    private final static String VISIBLE_IN_SHOP_PROPERTY = "visibleInShop";

    @InjectMocks
    private DistShopVisibilityValueResolver distShopVisibilityValueResolver;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    private Map<String, Object> indexDocumentContext = new HashMap<>();

    @Mock
    private FacetSearchConfig facetSearchConfig;

    @Mock
    private IndexConfig indexConfig;

    @Mock
    private CMSSiteModel cmsSite;

    @Mock
    private CountryModel mainCountry;

    @Mock
    private CountryModel additionalCountry;

    @Mock
    private DistSalesOrgModel salesOrg;

    @Mock
    private PriceInformation priceInformation;

    @Mock
    private CategoryModel category;

    @Before
    public void init() {
        super.init();

        cloneableIndexedProperty.setName(VISIBLE_IN_SHOP_PROPERTY);
        cloneableIndexedProperty.setExportId(VISIBLE_IN_SHOP_PROPERTY);
        cloneableIndexedProperty.setLocalized(false);

        when(distSolrInputDocument.getIndexDocumentContext()).thenReturn(indexDocumentContext);

        when(mainCountry.getIsocode()).thenReturn("CH");
        when(additionalCountry.getIsocode()).thenReturn(("LI"));

        when(cmsSite.getSalesOrg()).thenReturn(salesOrg);
        when(cmsSite.getCountry()).thenReturn(mainCountry);
        when(indexConfig.getCmsSite()).thenReturn(cmsSite);
        when(indexConfig.getAllCountries()).thenReturn(List.of(mainCountry, additionalCountry));
        when(facetSearchConfig.getIndexConfig()).thenReturn(indexConfig);
        when(indexerBatchContext.getFacetSearchConfig()).thenReturn(facetSearchConfig);

        when(distProductSearchExportDAO.isVisibleInSalesOrg(product, salesOrg)).thenReturn(true);
        indexDocumentContext.put(PRICEINFO_CONTEXT_FIELD, List.of(priceInformation));
        when(product.getSupercategories()).thenReturn(List.of(category));
        when(distProductSearchExportDAO.hasActivePunchOutFilter(cmsSite, mainCountry, product)).thenReturn(false);
    }

    @Test
    public void testResolveVisibleInShop() throws FieldValueProviderException {
        distShopVisibilityValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(true), any());
    }

    @Test
    public void testResolveVisibleInShopNotVisibleInSalesOrg() throws FieldValueProviderException {
        when(distProductSearchExportDAO.isVisibleInSalesOrg(product, salesOrg)).thenReturn(false);

        distShopVisibilityValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(false), any());
    }

    @Test
    public void testResolveVisibleInShopNoCategories() throws FieldValueProviderException {
        when(product.getSupercategories()).thenReturn(null);

        distShopVisibilityValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(false), any());
    }

    @Test
    public void testResolveVisibleInShopNotVisibleInShop() throws FieldValueProviderException {
        when(distProductSearchExportDAO.isVisibleInSalesOrg(product, salesOrg)).thenReturn(false);

        distShopVisibilityValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(false), any());
    }

    @Test
    public void testResolveVisibleInShopIsPunchout() throws FieldValueProviderException {
        when(distProductSearchExportDAO.hasActivePunchOutFilter(cmsSite, mainCountry, product)).thenReturn(true);
        when(distProductSearchExportDAO.hasActivePunchOutFilter(cmsSite, additionalCountry, product)).thenReturn(true);

        distShopVisibilityValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(false), any());
        String mainCountryProperty = VISIBLE_IN_SHOP_PROPERTY + "_" + mainCountry.getIsocode().toUpperCase();
        verify(distSolrInputDocument, times(1))
          .addField(refEq(createNewIndexedProperty(mainCountryProperty)), eq(false), any());
        String additionalCountryProperty = VISIBLE_IN_SHOP_PROPERTY + "_" + additionalCountry.getIsocode().toUpperCase();
        verify(distSolrInputDocument, times(1))
          .addField(refEq(createNewIndexedProperty(additionalCountryProperty)), eq(false), any());
    }

    @Test
    public void testResolveVisibleInShopIsNotPunchout() throws FieldValueProviderException {
        when(distProductSearchExportDAO.hasActivePunchOutFilter(cmsSite, mainCountry, product)).thenReturn(false);
        when(distProductSearchExportDAO.hasActivePunchOutFilter(cmsSite, additionalCountry, product)).thenReturn(false);

        distShopVisibilityValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(true), any());
        String mainCountryProperty = VISIBLE_IN_SHOP_PROPERTY + "_" + mainCountry.getIsocode().toUpperCase();
        verify(distSolrInputDocument, times(1))
          .addField(refEq(createNewIndexedProperty(mainCountryProperty)), eq(true), any());
        String additionalCountryProperty = VISIBLE_IN_SHOP_PROPERTY + "_" + additionalCountry.getIsocode().toUpperCase();
        verify(distSolrInputDocument, times(1))
          .addField(refEq(createNewIndexedProperty(additionalCountryProperty)), eq(true), any());
    }

    @Test
    public void testResolveVisibleInShopIsPunchoutButProductIsNotVisible() throws FieldValueProviderException {
        when(product.getSupercategories()).thenReturn(null);

        distShopVisibilityValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(false), any());
        String mainCountryProperty = VISIBLE_IN_SHOP_PROPERTY + "_" + mainCountry.getIsocode().toUpperCase();
        verify(distSolrInputDocument, times(1))
          .addField(refEq(createNewIndexedProperty(mainCountryProperty)), eq(false), any());
        String additionalCountryProperty = VISIBLE_IN_SHOP_PROPERTY + "_" + additionalCountry.getIsocode().toUpperCase();
        verify(distSolrInputDocument, times(1))
          .addField(refEq(createNewIndexedProperty(additionalCountryProperty)), eq(false), any());
        //verify that visible per country is not call if product is generally not visible
        verify(distProductSearchExportDAO, times(0)).hasActivePunchOutFilter(any(), any(), any());
    }


    private IndexedProperty createNewIndexedProperty(String attributeName) {
        return distShopVisibilityValueResolver.createNewIndexedProperty(cloneableIndexedProperty,
                                                                        attributeName,
                                                                        SolrPropertiesTypes.BOOLEAN.toString());
    }
}
