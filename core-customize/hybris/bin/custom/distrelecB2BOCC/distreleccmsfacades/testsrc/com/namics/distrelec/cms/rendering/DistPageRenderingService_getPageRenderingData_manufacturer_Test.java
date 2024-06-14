package com.namics.distrelec.cms.rendering;

import com.namics.distrelec.b2b.core.model.pages.DistManufacturerPageModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@UnitTest
public class DistPageRenderingService_getPageRenderingData_manufacturer_Test
        extends AbstractDistPageRenderingServiceTest {

    final String pageTypeCode = DistManufacturerPageModel._TYPECODE;
    final String pageLabelOrId = "/en/manufacturer/man_desc/man_flu";
    final String code = "man_flu";

    @Mock
    AbstractPageData pageData;

    @Before
    public void setUp() throws Exception {
        doReturn(pageData).when(defaultPageRenderingService).getPageRenderingData(pageTypeCode, null, code);
    }

    @Test
    public void testGetPageRenderingDataManufacturer() throws Exception {
        AbstractPageData pageData = renderingService.getPageRenderingData(ContentPageModel._TYPECODE, pageLabelOrId, code);

        assertEquals(this.pageData, pageData);
    }
}
