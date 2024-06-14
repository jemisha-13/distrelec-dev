package com.namics.distrelec.b2b.facades.snapeda.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.namics.distrelec.b2b.core.service.snapeda.SnapEdaService;
import com.namics.distrelec.b2b.core.service.snapeda.exception.SnapEdaApiException;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistrelecSnapEdaFacadeImplUnitTest {

    @InjectMocks
    private DistrelecSnapEdaFacadeImpl distrelecSnapEdaFacade;

    @Mock
    private SnapEdaService snapEdaService;

    @Test
    public void testFlagProduct() throws SnapEdaApiException {
        ProductModel product = mock(ProductModel.class);

        distrelecSnapEdaFacade.flagProduct(product);

        verify(snapEdaService, times(1))
                                        .flagProduct(product);
    }

}
