package com.namics.distrelec.cms.populators;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;
import de.hybris.platform.converters.Populator;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@UnitTest
public class DistrelecNavigationNodeModelToDataRenderingPopulatorUnitTest {

    @InjectMocks
    private DistrelecNavigationNodeModelToDataRenderingPopulator distrelecNavigationNodeModelToDataRenderingPopulator;

    @Mock
    private Populator<CMSNavigationNodeModel, NavigationNodeData> cmsNavigationNodeModelToDataRenderingPopulator;

    @Mock
    private CMSNavigationNodeModel source;

    @Mock
    private NavigationNodeData target;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        doReturn("1").when(source).getSortingNumber();
    }

    @Test
    public void testPopulate() {
        distrelecNavigationNodeModelToDataRenderingPopulator.populate(source, target);

        verify(cmsNavigationNodeModelToDataRenderingPopulator, times(1)).populate(source, target);
        verify(target, times(1)).setSortingNumber(source.getSortingNumber());
    }
}
