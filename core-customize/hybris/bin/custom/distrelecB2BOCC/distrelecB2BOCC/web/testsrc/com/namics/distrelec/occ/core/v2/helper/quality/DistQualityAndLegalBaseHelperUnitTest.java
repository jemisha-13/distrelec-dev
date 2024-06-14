package com.namics.distrelec.occ.core.v2.helper.quality;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistQualityAndLegalBaseHelperUnitTest {

    @InjectMocks
    private DistQualityAndLegalBaseHelper distQualityAndLegalBaseHelper;

    @Mock
    private DistrelecProductFacade mockProductFacade;

    private Collection<ProductOption> options;

    @Before
    public void setup() {
        options = List.of(ProductOption.BASIC,
                          ProductOption.MIN_BASIC,
                          ProductOption.DESCRIPTION,
                          ProductOption.DIST_MANUFACTURER,
                          ProductOption.CLASSIFICATION_AND_PRODUCT_INFORMATION);
    }

    @Test
    public void testGetProductsForReport() {
        List<String> mockProductCodes = Arrays.asList("11111111", "22222222", "33333333");
        List<ProductData> mockProductDataList = List.of(
                                                        mock(ProductData.class),
                                                        mock(ProductData.class),
                                                        mock(ProductData.class));

        when(mockProductFacade.getProductListForCodesAndOptions(mockProductCodes, options)).thenReturn(mockProductDataList);

        List<ProductData> result = distQualityAndLegalBaseHelper.getProductsForReport(mockProductCodes);

        verify(mockProductFacade, times(1))
                                           .getProductListForCodesAndOptions(mockProductCodes, options);

        assertNotNull(result);
    }

    @Test
    public void testGetProductsForReportProductsListIsNull() {
        List<ProductData> result = distQualityAndLegalBaseHelper.getProductsForReport(null);

        verify(mockProductFacade, times(1))
                                           .getProductListForCodesAndOptions(null, options);

        assertNotNull(result);
        assertThat(result.isEmpty(), is(true));
    }

}
