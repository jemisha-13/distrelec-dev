package com.namics.distrelec.b2b.facades.product.converters.populator;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.namics.distrelec.b2b.core.model.DistVideoMediaModel;
import com.namics.distrelec.b2b.facades.product.data.DistVideoData;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

@UnitTest
public class DistVideoMediaPopulatorTest {

    @Mock
    private Converter<DistVideoMediaModel, DistVideoData> distVideoMediaConverter;

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private DistVideoMediaModel videoMediaModel;

    @Mock
    private ProductModel source;

    @InjectMocks
    private DistVideoMediaPopulator distVideoMediaPopulator;

    private static final String ENGLISH_ISO_CODE = DistVideoMediaPopulator.ENGLISH_ISO_CODE;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testVideoIsAdded() {
        when(source.getVideoMedias()).thenReturn(createVideoMediaModels());
        when(videoMediaModel.getLanguages()).thenReturn(new ArrayList<>());
        when(distVideoMediaConverter.convert(any(DistVideoMediaModel.class))).thenReturn(new DistVideoData());
        final ProductData b2BProductData = new ProductData();
        distVideoMediaPopulator.populate(source, b2BProductData);
        assertFalse(b2BProductData.getVideos().isEmpty());
    }

    private DistVideoData createDistVideoData() {
        return new DistVideoData();
    }

    private Set<DistVideoMediaModel> createVideoMediaModels() {
        return Set.of(videoMediaModel);
    }

    private Collection<LanguageModel> createLanguageModels() {
        return Arrays.asList(createSingleLanguageModel());
    }

    private LanguageModel createSingleLanguageModel() {
        final LanguageModel model = new LanguageModel();
        model.setIsocode(ENGLISH_ISO_CODE);
        return model;
    }
}
