package com.namics.distrelec.cms.rendering;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import com.namics.distrelec.b2b.core.model.cms.ProductFamilyPageModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cmsfacades.data.AbstractPageData;

@UnitTest
public class DistPageRenderingService_getPageRenderingData_productFamily_Test
        extends AbstractDistPageRenderingServiceTest {

    final String pageTypeCode = ProductFamilyPageModel._TYPECODE;
    final String pageLabelOrId = "/some_prefix/pf/1819332";
    final String code = "1819332";

    @Mock
    AbstractPageData pageData;

    @Before
    public void setUp() throws Exception {
        doReturn(pageData).when(defaultPageRenderingService).getPageRenderingData(pageTypeCode, null, code);
    }

    @Test
    public void testGetPageRenderingDataProductFamily() throws Exception {
        AbstractPageData pageData = renderingService.getPageRenderingData(ContentPageModel._TYPECODE, pageLabelOrId, code);

        assertEquals(this.pageData, pageData);
    }
}
