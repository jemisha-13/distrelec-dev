package com.namics.distrelec.occ.core.v2.helper.search;

import com.namics.distrelec.b2b.facades.manufacturer.DistManufacturerFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.spy;

public abstract class AbstractManufacturerSearchRedirectServiceTest {

    protected ManufacturerSearchRedirectService service;

    @Mock
    protected DistManufacturerFacade distManufacturerFacade;

    @Mock
    protected DistrelecProductFacade productFacade;

    @Mock
    protected SearchRedirectRuleFactory searchRedirectRuleFactory;

    @Before
    public void setUpService() {
        MockitoAnnotations.initMocks(this);

        ManufacturerSearchRedirectService service = new ManufacturerSearchRedirectService();
        service.setDistManufacturerFacade(distManufacturerFacade);
        service.setProductFacade(productFacade);
        service.setSearchRedirectRuleFactory(searchRedirectRuleFactory);
        this.service = spy(service);
    }
}
