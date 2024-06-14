package com.namics.distrelec.b2b.core.service.product.impl;

import com.namics.distrelec.b2b.core.model.DistPimCategoryTypeModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.customer.strategies.CheckoutCustomerStrategy;
import com.namics.distrelec.b2b.core.service.i18n.NamicsCommonI18NService;
import com.namics.distrelec.b2b.core.service.product.dao.DistProductDao;
import com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.site.DistrelecBaseStoreService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDistProductServiceTest {

    @Mock
    private FlexibleSearchService flexibleSearchService;

    @Mock
    private UserService userService;

    @Mock
    private DistSalesOrgService distSalesOrgService;

    @Mock
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;

    @Mock
    private DistrelecBaseStoreService baseStoreService;

    @Mock
    private CheckoutCustomerStrategy customerStrategy;

    @Mock
    private DistProductDao distProductDao;

    @Mock
    private NamicsCommonI18NService namicsCommonI18NService;

    @InjectMocks
    private DefaultDistProductService defaultDistProductService;

    private DistSalesOrgModel mockedSalesOrg;

    // Products must be mocked because of the dynamic attribute EndOfLifeDate
    private ProductModel mockedOriginalProduct;
    private ProductModel mockedSimilarProduct;
    private ProductModel mockedSimilarEolProduct;
    private ProductModel mockedSimilarProductWithFutureEol;

    private void mockProduct(ProductModel mockedProduct, String code, long pk) {
        when(mockedProduct.getCode()).thenReturn(code);
        when(mockedProduct.getCodeErpRelevant()).thenReturn(code);
        when(mockedProduct.getPk()).thenReturn(PK.fromLong(pk));
    }

    @Before
    public void setUp() {
        long l = 1;
        mockedOriginalProduct = mock(ProductModel.class);
        mockProduct(mockedOriginalProduct, "mockedOriginalProduct", l++);

        mockedSimilarProduct = mock(ProductModel.class);
        mockProduct(mockedSimilarProduct, "mockedSimilarProduct", l++);

        mockedSimilarEolProduct = mock(ProductModel.class);
        mockProduct(mockedSimilarEolProduct, "mockeSimilarEolProduct", l++);

        mockedSimilarProductWithFutureEol = mock(ProductModel.class);
        mockProduct(mockedSimilarProductWithFutureEol, "mockeSimilarProductWithFutureEol", l++);

        when(flexibleSearchService.search(any(FlexibleSearchQuery.class))).thenReturn(new SearchResultImpl<>(Collections.singletonList(mockedSimilarProduct), 1, 10, 0));
        when(userService.getCurrentUser()).thenReturn(new B2BCustomerModel());

        mockedSalesOrg = mock(DistSalesOrgModel.class);
        mockedSalesOrg.setCode("7310");
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(mockedSalesOrg);

        B2BUnitModel unit = new B2BUnitModel();
        when(b2BUnitService.getParent(any(B2BCustomerModel.class))).thenReturn(unit);

        final BaseStoreModel baseStoreModel = mockBaseStore();
        when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);

        // this prevents filtering by punchout filter
        when(distProductDao.productIsListedForSalesOrg(any(ProductModel.class), any(DistSalesOrgModel.class))).thenReturn(true);
        when(distProductDao.findPunchOutFilters(any(DistSalesOrgModel.class), any(B2BUnitModel.class), any(SiteChannel.class),
                anyCollectionOf(CountryModel.class), any(ProductModel.class), any(Date.class))).thenReturn(Collections.emptyList());
    }

    @Test
    public void testGetSimilarProducts() {
        // Init
        final DistPimCategoryTypeModel pimCategoryType = new DistPimCategoryTypeModel();
        pimCategoryType.setCategoryWithSimilarProducts(Boolean.TRUE);

        final CategoryModel pimCategory = new CategoryModel();
        pimCategory.setPimCategoryType(pimCategoryType);

        when(mockedOriginalProduct.getSupercategories()).thenReturn(Collections.singletonList(pimCategory));
        when(mockedSimilarProduct.getSupercategories()).thenReturn(Collections.singletonList(pimCategory));
        when(mockedSimilarProduct.getEndOfLifeDate()).thenReturn(null);

        pimCategory.setProducts(Arrays.asList(mockedOriginalProduct, mockedSimilarProduct));

        // Action
        final List<ProductModel> similarProducts = defaultDistProductService.getSimilarProducts(mockedOriginalProduct);

        // Evaluation
        assertEquals(1, similarProducts.size());
        assertEquals(mockedSimilarProduct, similarProducts.get(0));
    }

    /**
     * if a similar product is end of life, it must not be included in the list.
     */
    @Test
    public void testGetSimilarProducts_Eol() {
        // Init
        final DistPimCategoryTypeModel pimCategoryType = new DistPimCategoryTypeModel();
        pimCategoryType.setCategoryWithSimilarProducts(Boolean.TRUE);

        final CategoryModel pimCategory = new CategoryModel();
        pimCategory.setPimCategoryType(pimCategoryType);

        when(mockedOriginalProduct.getSupercategories()).thenReturn(Collections.singletonList(pimCategory));
        when(mockedSimilarProduct.getSupercategories()).thenReturn(Collections.singletonList(pimCategory));
        when(mockedSimilarProduct.getEndOfLifeDate()).thenReturn(null);

        when(mockedSimilarEolProduct.getSupercategories()).thenReturn(Collections.singletonList(pimCategory));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        when(mockedSimilarEolProduct.getEndOfLifeDate()).thenReturn(cal.getTime());

        pimCategory.setProducts(Arrays.asList(mockedOriginalProduct, mockedSimilarProduct, mockedSimilarEolProduct));

        // Action
        final List<ProductModel> similarProducts = defaultDistProductService.getSimilarProducts(mockedOriginalProduct);

        // Evaluation
        assertEquals(1, similarProducts.size());
        assertEquals(mockedSimilarProduct, similarProducts.get(0));
    }

    /**
     * products with a future end of life date should be included
     */
    @Test
    public void testGetSimilarProducts_Future_Eol() {
        // Init
        final DistPimCategoryTypeModel pimCategoryType = new DistPimCategoryTypeModel();
        pimCategoryType.setCategoryWithSimilarProducts(Boolean.TRUE);

        final CategoryModel pimCategory = new CategoryModel();
        pimCategory.setPimCategoryType(pimCategoryType);

        when(mockedOriginalProduct.getSupercategories()).thenReturn(Collections.singletonList(pimCategory));
        when(mockedSimilarProduct.getSupercategories()).thenReturn(Collections.singletonList(pimCategory));
        when(mockedSimilarProduct.getEndOfLifeDate()).thenReturn(null);

        when(mockedSimilarEolProduct.getSupercategories()).thenReturn(Collections.singletonList(pimCategory));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        when(mockedSimilarEolProduct.getEndOfLifeDate()).thenReturn(cal.getTime());

        when(mockedSimilarProductWithFutureEol.getSupercategories()).thenReturn(Collections.singletonList(pimCategory));
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        when(mockedSimilarProductWithFutureEol.getEndOfLifeDate()).thenReturn(cal.getTime());

        pimCategory.setProducts(Arrays.asList(mockedOriginalProduct, mockedSimilarProduct, mockedSimilarEolProduct, mockedSimilarProductWithFutureEol));

        when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class)))
                .thenReturn(new SearchResultImpl<>(Arrays.asList(mockedSimilarProduct, mockedSimilarProductWithFutureEol), 1, 10, 0));

        // Action
        final List<ProductModel> similarProducts = defaultDistProductService.getSimilarProducts(mockedOriginalProduct);

        // Evaluation
        assertEquals(2, similarProducts.size());
        assertTrue(isProductInList(similarProducts, mockedSimilarProduct));
        assertTrue(isProductInList(similarProducts, mockedSimilarProductWithFutureEol));
    }

    /**
     * if a similar product is end of life, it must not be included in the list.
     */
    @Test
    public void testGetSimilarProducts_PunchoutFilter() {
        // Init
        final DistPimCategoryTypeModel pimCategoryType = new DistPimCategoryTypeModel();
        pimCategoryType.setCategoryWithSimilarProducts(Boolean.TRUE);

        final CategoryModel pimCategory = new CategoryModel();
        pimCategory.setPimCategoryType(pimCategoryType);

        when(mockedOriginalProduct.getSupercategories()).thenReturn(Collections.singletonList(pimCategory));
        when(mockedSimilarProduct.getSupercategories()).thenReturn(Collections.singletonList(pimCategory));
        when(mockedSimilarProduct.getEndOfLifeDate()).thenReturn(null);

        when(mockedSimilarEolProduct.getSupercategories()).thenReturn(Collections.singletonList(pimCategory));
        when(mockedSimilarEolProduct.getEndOfLifeDate()).thenReturn(null); // Eol-Product should not be block by end of life date

        pimCategory.setProducts(Arrays.asList(mockedOriginalProduct, mockedSimilarProduct, mockedSimilarEolProduct));

        // overwrite punchout rule for all products
        when(distProductDao.findPunchOutFilters(any(DistSalesOrgModel.class), any(B2BUnitModel.class), any(SiteChannel.class), anyCollectionOf(CountryModel.class), any(ProductModel.class), any(Date.class)))
                .thenReturn(Collections.singletonList(new PunchoutFilterResult()));

        // Action
        final List<ProductModel> similarProducts = defaultDistProductService.getSimilarProducts(mockedOriginalProduct);

        // Evaluation
        assertEquals(0, similarProducts.size()); // all products have a punchout filter applied
    }

    /**
     * If a product is not listed for a salesorg it should not appear.
     */
    @Test
    public void testGetSimilarProducts_ListedForSalesorg() {
        // Init
        final DistPimCategoryTypeModel pimCategoryType = new DistPimCategoryTypeModel();
        pimCategoryType.setCategoryWithSimilarProducts(Boolean.TRUE);

        final CategoryModel pimCategory = new CategoryModel();
        pimCategory.setPimCategoryType(pimCategoryType);

        when(mockedOriginalProduct.getSupercategories()).thenReturn(Collections.singletonList(pimCategory));
        when(mockedSimilarProduct.getSupercategories()).thenReturn(Collections.singletonList(pimCategory));
        when(mockedSimilarProduct.getEndOfLifeDate()).thenReturn(null);

        when(mockedSimilarEolProduct.getSupercategories()).thenReturn(Collections.singletonList(pimCategory));
        when(mockedSimilarEolProduct.getEndOfLifeDate()).thenReturn(null); // EolProduct should appear

        pimCategory.setProducts(Arrays.asList(mockedOriginalProduct, mockedSimilarProduct, mockedSimilarEolProduct));

        // overwrite punchout rule
        when(distProductDao.productIsListedForSalesOrg(mockedOriginalProduct, mockedSalesOrg)).thenReturn(true);
        // mockedSimilarProduct should not appear because it is not listed
        when(distProductDao.productIsListedForSalesOrg(mockedSimilarProduct, mockedSalesOrg)).thenReturn(false);
        when(distProductDao.productIsListedForSalesOrg(mockedSimilarEolProduct, mockedSalesOrg)).thenReturn(true);

        when(flexibleSearchService.search(any(FlexibleSearchQuery.class)))
                .thenReturn(new SearchResultImpl<>(Collections.singletonList(mockedSimilarEolProduct), 1, 10, 0));

        // Action
        final List<ProductModel> similarProducts = defaultDistProductService.getSimilarProducts(mockedOriginalProduct);

        // Evaluation
        assertEquals(1, similarProducts.size());
        assertEquals(mockedSimilarEolProduct, similarProducts.get(0));
    }

    private BaseStoreModel mockBaseStore() {
        final BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
        baseStoreModel.setChannel(SiteChannel.B2B);
        baseStoreModel.setDeliveryCountries(Collections.singletonList(mockCountry()));
        return baseStoreModel;
    }

    private CountryModel mockCountry() {
        final CountryModel country = mock(CountryModel.class);
        country.setActive(Boolean.TRUE);
        country.setIsocode("CH");
        country.setName("Switzerland");
        return country;
    }

    private boolean isProductInList(List<ProductModel> list, ProductModel product) {
        for (ProductModel listItem : list) {
            if (product.equals(listItem)) {
                return true;
            }
        }
        return false;
    }
}
