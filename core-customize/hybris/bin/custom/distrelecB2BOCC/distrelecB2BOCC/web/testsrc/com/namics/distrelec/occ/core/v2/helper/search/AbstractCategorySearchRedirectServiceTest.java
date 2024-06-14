package com.namics.distrelec.occ.core.v2.helper.search;

import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.customer.DistPunchoutService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import de.hybris.platform.category.model.CategoryModel;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.spy;

public abstract class AbstractCategorySearchRedirectServiceTest {

    protected CategorySearchRedirectService service;

    @Mock
    protected DistUrlResolver<CategoryModel> categoryModelUrlResolver;

    @Mock
    protected DistCategoryFacade distCategoryFacade;

    @Mock
    protected DistCategoryService distCategoryService;

    @Mock
    protected DistPunchoutService distPunchoutService;

    @Mock
    protected SearchRedirectRuleFactory searchRedirectRuleFactory;

    @Before
    public void setUpService() {
        MockitoAnnotations.initMocks(this);
        CategorySearchRedirectService service = new CategorySearchRedirectService();
        service.setCategoryModelUrlResolver(categoryModelUrlResolver);
        service.setDistCategoryFacade(distCategoryFacade);
        service.setDistCategoryService(distCategoryService);
        service.setDistPunchoutService(distPunchoutService);
        service.setSearchRedirectRuleFactory(searchRedirectRuleFactory);
        this.service = spy(service);
    }
}
