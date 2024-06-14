package com.distrelec.solrfacetsearch.provider.product.impl;

import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.PRICEINFO_CONTEXT_FIELD;
import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.SALES_ORG_PRODUCT;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.distrelec.solrfacetsearch.indexer.impl.DistSolrInputDocument;
import com.distrelec.solrfacetsearch.provider.product.DistAbstractValueResolverTest;
import com.google.gson.Gson;
import com.namics.distrelec.b2b.core.jalo.DistPriceRow;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.model.DistSalesUnitModel;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.util.PriceValue;

public class DistAllPriceAttributesValueResolverUnitTest extends DistAbstractValueResolverTest {

    private static final String NET = "Net";

    private static final String GROSS = "Gross";

    private static final String CURRENCY_SEK = "SEK";

    private static final Double SALES_UNIT_AMOUNT = 2d;

    @InjectMocks
    private DistAllPriceAttributesValueResolver distAllPriceAttributesValueResolver;

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
    private UserPriceGroup userPriceGroup;

    @Mock
    private DistSalesOrgProductModel salesOrgProduct;

    @Mock
    private DistSalesStatusModel salesStatus;

    @Mock
    private DistSalesUnitModel salesUnit;

    @Mock
    private CurrencyModel currency;

    @Mock
    private PriceInformation priceInformation1;

    @Mock
    private PriceValue priceValue1;

    @Mock
    private DistPriceRow priceRow1;

    @Mock
    private PriceInformation priceInformation1Special;

    @Mock
    private PriceValue priceValue1Special;

    @Mock
    private DistPriceRow priceRow1Special;

    @Mock
    private PriceInformation priceInformation5;

    @Mock
    private PriceValue priceValue5;

    @Mock
    private DistPriceRow priceRow5;

    List<PriceInformation> priceInformationList;

    private Gson gson = new Gson();

    @Before
    public void init() {
        super.init();

        ReflectionTestUtils.setField(distAllPriceAttributesValueResolver, "gson", gson);

        cloneableIndexedProperty.setName("price");
        cloneableIndexedProperty.setExportId("price");
        cloneableIndexedProperty.setLocalized(false);

        when(userPriceGroup.getCode()).thenReturn("SalesOrg_UPG_7640_01");
        when(currency.getIsocode()).thenReturn(CURRENCY_SEK);
        when(cmsSite.getUserPriceGroup()).thenReturn(userPriceGroup);
        when(cmsSite.getDefaultCurrency()).thenReturn(currency);
        when(indexConfig.getCmsSite()).thenReturn(cmsSite);
        when(facetSearchConfig.getIndexConfig()).thenReturn(indexConfig);
        when(indexerBatchContext.getFacetSearchConfig()).thenReturn(facetSearchConfig);

        when(distProductSearchExportDAO.getTaxFactor(product, cmsSite, salesOrgProduct)).thenReturn(1.25d);

        when(salesUnit.getAmount()).thenReturn(SALES_UNIT_AMOUNT);
        when(product.getSalesUnit()).thenReturn(salesUnit);

        when(salesStatus.getCode()).thenReturn("30");
        when(salesStatus.isBuyableInShop()).thenReturn(true);

        when(salesOrgProduct.getSalesStatus()).thenReturn(salesStatus);
        when(salesOrgProduct.getOrderQuantityMinimum()).thenReturn(1L);
        when(salesOrgProduct.getOrderQuantityStep()).thenReturn(1L);

        indexDocumentContext.put(SALES_ORG_PRODUCT, salesOrgProduct);
        priceInformationList = new ArrayList<>();
        configurePriceInfos();
        indexDocumentContext.put(PRICEINFO_CONTEXT_FIELD, priceInformationList);
        when(distSolrInputDocument.getIndexDocumentContext()).thenReturn(indexDocumentContext);
    }

    private void configurePriceInfos() {
        when(priceRow1.isSpecialPrice()).thenReturn(false);
        when(priceRow1.getUnitFactor()).thenReturn(1);
        when(priceValue1.getValue()).thenReturn(2d);
        when(priceInformation1.getQualifierValue("pricerow")).thenReturn(priceRow1);
        when(priceInformation1.getPriceValue()).thenReturn(priceValue1);
        when(priceInformation1.getValue()).thenReturn(priceValue1);
        when(priceInformation1.getQualifierValue(PriceRow.MINQTD)).thenReturn(1L);

        when(priceRow1Special.isSpecialPrice()).thenReturn(true);
        when(priceValue1Special.getValue()).thenReturn(1.2d);
        when(priceInformation1Special.getQualifierValue("pricerow")).thenReturn(priceRow1Special);
        when(priceInformation1Special.getValue()).thenReturn(priceValue1Special);
        when(priceInformation1Special.getQualifierValue(PriceRow.MINQTD)).thenReturn(1L);

        when(priceRow5.isSpecialPrice()).thenReturn(false);
        when(priceValue5.getValue()).thenReturn(10d);
        when(priceInformation5.getQualifierValue("pricerow")).thenReturn(priceRow5);
        when(priceInformation5.getValue()).thenReturn(priceValue5);
        when(priceInformation5.getQualifierValue(PriceRow.MINQTD)).thenReturn(5L);
    }

    @Test
    public void testPricesResolve() throws FieldValueProviderException {
        priceInformationList.add(priceInformation1);
        priceInformationList.add(priceInformation5);

        distAllPriceAttributesValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        // there is no special prices so this is created from regular prices -> equal for custom and list types in volumePricesMap
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(stringIndexedProperty("scalePrices" + NET)), eq("{\"1\":2.0,\"5\":10.0}"), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(stringIndexedProperty("scalePrices" + GROSS)), eq("{\"1\":2.5,\"5\":12.5}"), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("singleMinPrice" + NET)), eq(2d), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("singleMinPrice" + GROSS)), eq(2.5d), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("singleUnitPrice" + NET)), eq(2d / SALES_UNIT_AMOUNT), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("singleUnitPrice" + GROSS)), eq(2.5d / SALES_UNIT_AMOUNT), any());

        // there is no special prices so this is not filled with regular prices
        verify(distSolrInputDocument, times(0))
                                               .addField(refEq(doubleIndexedProperty("standardPrice" + NET)), any(), any());
        verify(distSolrInputDocument, times(0))
                                               .addField(refEq(doubleIndexedProperty("standardPrice" + GROSS)), any(), any());
        verify(distSolrInputDocument, times(0))
                                               .addField(refEq(integerIndexedProperty("percentageDiscount")), any(), any());

        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(stringIndexedProperty("currency")), eq(CURRENCY_SEK), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("itemMin")), eq(1L), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("itemStep")), eq(1L), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(booleanIndexedProperty("buyable")), eq(true), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(integerIndexedProperty("salesStatus")), eq("30"), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(booleanIndexedProperty("visibleForSearch")), eq(true), any());
    }

    @Test
    public void testPricesWithSpecialResolve() throws FieldValueProviderException {
        priceInformationList.add(priceInformation1);
        priceInformationList.add(priceInformation1Special);

        distAllPriceAttributesValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        // created from special prices to show discounted ones - custom type in volumePricesMap
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(stringIndexedProperty("scalePrices" + NET)), eq("{\"1\":1.2}"), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(stringIndexedProperty("scalePrices" + GROSS)), eq("{\"1\":1.5}"), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("singleMinPrice" + NET)), eq(1.2d), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("singleMinPrice" + GROSS)), eq(1.5d), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("singleUnitPrice" + NET)), eq(1.2d / SALES_UNIT_AMOUNT), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("singleUnitPrice" + GROSS)), eq(1.5d / SALES_UNIT_AMOUNT), any());

        // created from regular/original prices to show original price and discount - list type in volumePricesMap
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(doubleIndexedProperty("standardPrice" + NET)), eq(2d), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(doubleIndexedProperty("standardPrice" + GROSS)), eq(2.5d), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(integerIndexedProperty("percentageDiscount")), eq(40), any());

        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("itemMin")), eq(1L), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("itemStep")), eq(1L), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(booleanIndexedProperty("buyable")), eq(true), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(integerIndexedProperty("salesStatus")), eq("30"), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(booleanIndexedProperty("visibleForSearch")), eq(true), any());
    }

    @Test
    public void testPricesWithSpecialResolveDiscountPercentage() throws FieldValueProviderException {
        when(priceValue1.getValue()).thenReturn(608d);
        when(priceValue1Special.getValue()).thenReturn(505.8d);
        priceInformationList.add(priceInformation1);
        priceInformationList.add(priceInformation1Special);

        distAllPriceAttributesValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(integerIndexedProperty("percentageDiscount")), eq(17), any());
    }

    private IndexedProperty stringIndexedProperty(String attributeName) {
        return distAllPriceAttributesValueResolver.createNewIndexedProperty(cloneableIndexedProperty,
                                                                            attributeName,
                                                                            SolrPropertiesTypes.STRING.getCode());
    }

    private IndexedProperty booleanIndexedProperty(String attributeName) {
        return distAllPriceAttributesValueResolver.createNewIndexedProperty(cloneableIndexedProperty,
                                                                            attributeName,
                                                                            SolrPropertiesTypes.BOOLEAN.getCode());
    }

    private IndexedProperty integerIndexedProperty(String attributeName) {
        return distAllPriceAttributesValueResolver.createNewIndexedProperty(cloneableIndexedProperty,
                                                                            attributeName,
                                                                            SolrPropertiesTypes.INT.getCode());
    }

    private IndexedProperty doubleIndexedProperty(String attributeName) {
        return distAllPriceAttributesValueResolver.createNewIndexedProperty(cloneableIndexedProperty,
                                                                            attributeName,
                                                                            SolrPropertiesTypes.DOUBLE.getCode());
    }

    private IndexedProperty createNewIndexedProperty(String attributeName) {
        return distAllPriceAttributesValueResolver.createNewIndexedProperty(cloneableIndexedProperty,
                                                                            attributeName);
    }

}
