package com.namics.distrelec.b2b.core.service.user.impl;

import com.namics.distrelec.b2b.core.service.user.daos.DistCustomerDao;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.spy;

public abstract class AbstractDefaultDistUserServiceTest {

    protected DefaultDistUserService distUserService;

    @Mock
    protected ConfigurationService configurationService;

    @Mock
    protected DistCustomerDao distCustomerDao;

    @Before
    public void setUpDistUserService() {
        MockitoAnnotations.initMocks(this);

        DefaultDistUserService distUserService = new DefaultDistUserService();
        distUserService.setConfigurationService(configurationService);
        distUserService.setDistCustomerDao(distCustomerDao);
        this.distUserService = spy(distUserService);
    }
}
