package com.namics.distrelec.occ.core.v2.controller;

import com.namics.distrelec.b2b.core.service.DistCmsPageService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistContentPageMappingControllerTest {

    @InjectMocks
    DistContentPageMappingController controller;

    @Mock
    DistUrlResolver<ContentPageModel> contentPageUrlResolver;

    @Mock
    DistCmsPageService distCmsPageService;

    @Test
    public void testNotResolvesUrlIfThereIsNoContentPage() throws Exception {
        String pageLabel = "pageLabel";
        String urlPath = "prefix/cms/pageLabel?someParam=value";
        when(distCmsPageService.getPageForLabel(pageLabel)).thenThrow(new CMSItemNotFoundException(""));
        String newPath = controller.checkContentPageUrl(urlPath);
        assertNull(newPath);
    }

    @Test
    public void testCheckContentPageUrlNotMatchPattern() {
        try {
            controller.checkContentPageUrl("/invalidUrl");
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testCheckContentPageUrlRedirect() throws Exception {
        String pageLabel = "pageLabel";
        String urlPath = "prefix/cms/pageLabel?someParam=value";
        String redirectedUrlPath = "newPath";

        ContentPageModel contentPageModel = mock(ContentPageModel.class);
        when(distCmsPageService.getPageForLabel(pageLabel)).thenReturn(contentPageModel);
        when(contentPageUrlResolver.resolve(contentPageModel)).thenReturn(redirectedUrlPath);

        String newPath = controller.checkContentPageUrl(urlPath);

        assertEquals(redirectedUrlPath, newPath);
    }

    @Test
    public void testCheckContentPageUrlShouldNotRedirect() throws Exception {
        String pageLabel = "pageLabel";
        String urlPath = "prefix/cms/pageLabel?someParam=value";

        ContentPageModel contentPageModel = mock(ContentPageModel.class);
        when(distCmsPageService.getPageForLabel(pageLabel)).thenReturn(contentPageModel);
        when(contentPageUrlResolver.resolve(contentPageModel)).thenReturn(urlPath);

        String newPath = controller.checkContentPageUrl(urlPath);

        assertNull(newPath);
    }
}
