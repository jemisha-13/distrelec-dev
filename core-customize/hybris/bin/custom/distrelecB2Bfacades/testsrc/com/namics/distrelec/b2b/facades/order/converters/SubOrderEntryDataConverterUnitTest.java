package com.namics.distrelec.b2b.facades.order.converters;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.order.SubOrderEntryModel;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.facades.order.data.SubOrderEntryData;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SubOrderEntryDataConverterUnitTest {

    @Mock
    private DistProductService distProductService;

    @Mock
    private DistrelecProductFacade productFacade;

    @InjectMocks
    SubOrderEntryDataConverter subOrderEntryDataConverter;

    @Test
    public void testPopulate() {
        // given
        SubOrderEntryModel source = mock(SubOrderEntryModel.class);
        SubOrderEntryData target = new SubOrderEntryData();
        ProductModel productModel = mock(ProductModel.class);
        ProductData productData = mock(ProductData.class);
        DistManufacturerModel manufacturerModel = mock(DistManufacturerModel.class);

        // when
        when(source.getImageUrl()).thenReturn("www.image.com");
        when(source.getMaterialName()).thenReturn("Battery");
        when(source.getMaterialNumber()).thenReturn("300312123");
        when(source.getOrderQuantity()).thenReturn(10);
        when(distProductService.getProductForCode("300312123")).thenReturn(productModel);
        when(productFacade.getProductForCodeAndOptions("300312123", Arrays.asList(ProductOption.BASIC, ProductOption.SUMMARY,
                                                                                  ProductOption.DESCRIPTION, ProductOption.PROMOTION_LABELS,
                                                                                  ProductOption.DIST_MANUFACTURER))).thenReturn(productData);
        when(productModel.getManufacturer()).thenReturn(manufacturerModel);
        when(manufacturerModel.getName()).thenReturn("Distrelec");
        when(productModel.getCode()).thenReturn("300312123");

        subOrderEntryDataConverter.populate(source, target);

        // then
        assertThat(target.getImageUrl(), equalTo("www.image.com"));
        assertThat(target.getMaterialName(), equalTo("Battery"));
        assertThat(target.getMaterialNumber(), equalTo("300312123"));
        assertThat(target.getOrderQuantity(), equalTo(10));
        assertThat(target.getProduct(), equalTo(productData));
        verify(productData).setManufacturer("Distrelec");
    }
}
