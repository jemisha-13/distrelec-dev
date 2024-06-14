package com.namics.distrelec.b2b.facades.order.quotation.converters;

import com.namics.distrelec.b2b.core.model.order.SubOrderEntryModel;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationEntry;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.product.ProductModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class QuotationEntries2SubOrderEntryModelConverterUnitTest {

    @Mock
    private DistProductService distProductService;

    @InjectMocks
    private QuotationEntries2SubOrderEntryModelConverter converter;

    @Test
    public void testPopulate() {
        // given
        QuotationEntry source = mock(QuotationEntry.class);
        ProductData productData = mock(ProductData.class);
        SubOrderEntryModel target = new SubOrderEntryModel();
        ProductModel productModel = mock(ProductModel.class);
        MediaContainerModel mediaContainerModel = mock(MediaContainerModel.class);

        // when
        when(source.getProduct()).thenReturn(productData);
        when(productData.getCode()).thenReturn("301154667");
        when(source.getQuantity()).thenReturn(5);
        when(mediaContainerModel.getName()).thenReturn("/bike-battery");
        when(productModel.getGalleryImages()).thenReturn(List.of(mediaContainerModel));
        when(productModel.getName()).thenReturn("Bike battery");
        when(distProductService.getProductForCode("301154667")).thenReturn(productModel);

        converter.populate(source, target);

        // then
        assertThat(target.getMaterialName(),equalTo("Bike battery"));
        assertThat(target.getImageUrl(),equalTo("/bike-battery"));
        assertThat(target.getMaterialNumber(),equalTo("301154667"));
        assertThat(target.getOrderQuantity(), equalTo(5));
    }
}

