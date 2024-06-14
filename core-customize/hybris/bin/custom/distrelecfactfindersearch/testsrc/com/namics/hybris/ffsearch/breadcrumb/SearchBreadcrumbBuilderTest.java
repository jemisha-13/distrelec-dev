package com.namics.hybris.ffsearch.breadcrumb;

import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@UnitTest
public class SearchBreadcrumbBuilderTest {

    @Mock
    private FactFinderProductSearchPageData<SearchStateData, ProductData> factFinderProductSearchPageData;
    @Mock
    private SearchStateData searchStateData;
    @Mock
    private CommerceCategoryService commerceCategoryService;
    @InjectMocks
    private SearchBreadcrumbBuilder builder;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNullCategoriesAreFilteredOutForCategories(){
        when(factFinderProductSearchPageData.getCurrentQuery()).thenReturn(null);
        when(commerceCategoryService.getCategoryForCode(anyString())).thenReturn(null);
        final List<Breadcrumb> result =  builder.getCategoryBreadcrumbs(factFinderProductSearchPageData);
        assertEquals(0, result.size());
    }
}
