package com.namics.distrelec.b2b.occ.core.cms.populators;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.spy;

public abstract class AbstracDistrelecCmsSiteModelToDataPopulatorTest {

    protected DistrelecCmsSiteModelToDataPopulator populator;

    @Mock
    protected ConfigurationService configurationService;

    @Before
    public void setUpPopulator() {
        MockitoAnnotations.initMocks(this);

        DistrelecCmsSiteModelToDataPopulator populator = new DistrelecCmsSiteModelToDataPopulator();
        populator.setConfigurationService(configurationService);

        this.populator = spy(populator);
    }
}
