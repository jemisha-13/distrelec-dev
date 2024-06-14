package com.namics.distrelec.b2b.facades.order.converters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.type.TypeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistCardTypePopulatorUnitTest {

    @Mock
    private Converter<MediaModel, ImageData> imageConverter;

    @Mock
    private TypeService typeService;

    @InjectMocks
    private DistCardTypePopulator distCardTypePopulator;


    @Test
    public void testPopulate() {
        // given
        CreditCardType source = mock(CreditCardType.class);
        CardTypeData target = mock(CardTypeData.class);
        EnumerationValueModel enumerationValueModel = mock(EnumerationValueModel.class);
        MediaModel iconMedia = mock(MediaModel.class);

        // when
        when(enumerationValueModel.getIcon()).thenReturn(iconMedia);
        when(typeService.getEnumerationValue(source)).thenReturn(enumerationValueModel);
        when(imageConverter.convert(iconMedia)).thenReturn(mock(ImageData.class));

        distCardTypePopulator.populate(source, target);

        // then
        verify(imageConverter).convert(iconMedia);
        verify(target).setIcon(any(ImageData.class));
    }
}
