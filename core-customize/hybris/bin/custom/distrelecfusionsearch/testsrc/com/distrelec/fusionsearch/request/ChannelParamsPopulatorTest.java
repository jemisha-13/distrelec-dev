package com.distrelec.fusionsearch.request;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.FusionSearchParameters.CHANNEL_PARAM;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.namics.distrelec.b2b.core.service.site.DistrelecBaseStoreService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.site.BaseSiteService;

@UnitTest
public class ChannelParamsPopulatorTest extends AbstractParamsPopulatorTest {

    @InjectMocks
    ChannelParamsPopulator channelParamsPopulator;

    @Mock
    BaseSiteService baseSiteService;

    @Mock
    DistrelecBaseStoreService distBaseStoreService;

    @Test
    public void testPopulateCurrentSiteChannel() {
        SiteChannel channel = SiteChannel.B2C;

        BaseSiteModel currentBaseSite = mock(BaseSiteModel.class);

        when(baseSiteService.getCurrentBaseSite()).thenReturn(currentBaseSite);
        when(distBaseStoreService.getCurrentChannel(currentBaseSite)).thenReturn(channel);

        channelParamsPopulator.populate(searchRequestTuple, params);

        Collection<String> values = params.get(CHANNEL_PARAM);
        assertEquals(1, values.size());
        assertEquals(channel.getCode(), values.iterator().next());
    }
}
