package com.namics.distrelec.cms.rendering;

import de.hybris.platform.cmsfacades.rendering.PageRenderingService;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.spy;

public abstract class AbstractDistPageRenderingServiceTest {

    protected DistPageRenderingService renderingService;

    @Mock
    protected PageRenderingService defaultPageRenderingService;

    @Before
    public void setUpRenderingService() {
        MockitoAnnotations.initMocks(this);
        DistPageRenderingService renderingService = new DistPageRenderingService();
        renderingService.setDefaultPageRenderingService(defaultPageRenderingService);
        this.renderingService = spy(renderingService);
    }
}
