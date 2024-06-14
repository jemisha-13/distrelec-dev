package com.namics.distrelec.cms.rendering;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@UnitTest
public class DistPageRenderingService_getPageRenderingData_contentPage_Test
        extends AbstractDistPageRenderingServiceTest {

    final String pageTypeCode = ContentPageModel._TYPECODE;
    final String pageLabelOrId = "pageLabelOrId";
    final String code = "code";
    final String normalizedPageLabelOrId = "normalizedPageLabelOrId";

    @Mock
    AbstractPageData pageData;

    @Before
    public void setUp() throws Exception {
        doReturn(normalizedPageLabelOrId).when(renderingService).normalize(pageLabelOrId);
        doReturn(pageData).when(defaultPageRenderingService).getPageRenderingData(pageTypeCode, normalizedPageLabelOrId, code);
    }

    @Test
    public void testGetPageRenderingDataContentPage() throws Exception{
        AbstractPageData pageData = renderingService.getPageRenderingData(ContentPageModel._TYPECODE, pageLabelOrId, code);

        assertEquals(this.pageData, pageData);
    }
}
