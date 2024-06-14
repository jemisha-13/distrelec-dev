package com.namics.distrelec.b2b.core.service.eol.job;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.namics.distrelec.b2b.core.model.RemoveEolProductsCronJobModel;
import com.namics.distrelec.b2b.core.service.eol.service.DistEOLProductsRemovalService;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistEOLProductsRemovalJobUnitTest {

    @InjectMocks
    private DistEOLProductsRemovalJob eolProductsRemovalJob;

    @Mock
    private DistEOLProductsRemovalService eolProductsRemovalService;

    @Test
    public void testPerform() {
        RemoveEolProductsCronJobModel cronJobModel = mock(RemoveEolProductsCronJobModel.class);

        eolProductsRemovalJob.perform(cronJobModel);

        verify(eolProductsRemovalService, times(1)).removeEOLProducts(anyInt(), anyInt(), anyInt());
    }

}
