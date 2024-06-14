package com.namics.distrelec.b2b.facades.product.converters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.namics.distrelec.b2b.core.model.DistAudioMediaModel;
import com.namics.distrelec.b2b.facades.product.data.DistAudioData;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.site.BaseSiteService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistAudioMediaConverterUnitTest {

    private static final String AUDIO_FILE_RELATIVE_URL = "/audio/banana.mp3";

    private static final String SITE_BASE_URL = "https://www.banana.com";

    private static final String MP3_MIME_TYPE = "audio/mpeg";

    @InjectMocks
    private DistAudioMediaConverter distAudioMediaConverter;

    @Mock
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    @Mock
    private BaseSiteService baseSiteService;

    private DistAudioMediaModel source;

    private DistAudioData target;

    private BaseSiteModel baseSite;

    @Before
    public void setup() {
        source = mock(DistAudioMediaModel.class);
        target = new DistAudioData();
        baseSite = mock(BaseSiteModel.class);

        when(source.getURL()).thenReturn(AUDIO_FILE_RELATIVE_URL);
        when(source.getMime()).thenReturn(MP3_MIME_TYPE);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
        when(siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSite, true, "")).thenReturn(SITE_BASE_URL);
    }

    @Test
    public void testPopulate() {
        distAudioMediaConverter.populate(source, target);

        assertThat(target).isNotNull();
        verify(baseSiteService, times(1)).getCurrentBaseSite();
        verify(siteBaseUrlResolutionService, times(1)).getWebsiteUrlForSite(baseSite, true, "");
        assertThat(target.getAudioUrl()).isEqualTo(SITE_BASE_URL + AUDIO_FILE_RELATIVE_URL);
        assertThat(target.getMimeType()).isEqualTo(MP3_MIME_TYPE);
    }

}
