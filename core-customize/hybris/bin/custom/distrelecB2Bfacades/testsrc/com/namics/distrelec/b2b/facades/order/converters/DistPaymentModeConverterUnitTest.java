package com.namics.distrelec.b2b.facades.order.converters;

import com.namics.distrelec.b2b.core.model.payment.AbstractDistPaymentModeModel;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistPaymentModeConverterUnitTest {

    @Mock
    private Converter<MediaModel, ImageData> imageConverter;

    @Mock
    private SessionService sessionService;

    @InjectMocks
    private DistPaymentModeConverter<AbstractDistPaymentModeModel> distPaymentModeConverter;

    @Test
    public void testPopulate() {
        // given
        AbstractDistPaymentModeModel source = mock(AbstractDistPaymentModeModel.class);
        ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
        DistPaymentModeData target = new DistPaymentModeData();

        // when
        when(source.getCode()).thenReturn("code");
        when(source.getName()).thenReturn("name");
        when(source.getPaymentInfoType()).thenReturn(composedTypeModel);
        when(composedTypeModel.getCode()).thenReturn(CreditCardPaymentInfoModel._TYPECODE);
        when(source.getIcons()).thenReturn(new ArrayList<>());
        when(sessionService.getAttribute("paymentMode#code")).thenReturn(null);

        distPaymentModeConverter.populate(source, target);

        // then
        assertThat(target.getCode(), equalTo("code"));
        assertThat(target.getName(), equalTo("name"));
        assertThat(target.getSelectable(), is(true));
        assertThat(target.getCreditCardPayment(), is(true));
        assertThat(target.getInvoicePayment(), is(false));
        assertThat(target.getIcons(), is(nullValue()));
    }

    @Test
    public void testPopulateWithIcons() {
        // given
        AbstractDistPaymentModeModel source = mock(AbstractDistPaymentModeModel.class);
        ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
        ImageData imageData = mock(ImageData.class);
        DistPaymentModeData target = new DistPaymentModeData();

        // when
        when(source.getIcons()).thenReturn(List.of(mock(MediaModel.class), mock(MediaModel.class)));
        when(source.getPaymentInfoType()).thenReturn(composedTypeModel);
        when(composedTypeModel.getCode()).thenReturn(CreditCardPaymentInfoModel._TYPECODE);
        when(imageConverter.convert(any(MediaModel.class))).thenReturn(imageData);

        distPaymentModeConverter.populate(source, target);

        // then
        assertThat(target.getIcons(), hasSize(2));
    }
}
