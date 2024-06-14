package com.namics.distrelec.b2b.facades.product.converters.populator;

import com.namics.distrelec.b2b.core.model.DistAudioMediaModel;
import com.namics.distrelec.b2b.facades.product.data.DistAudioData;
import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class DistAudioMediaPopulator extends AbstractProductPopulator<ProductModel, ProductData> {

    @Autowired
    private Converter<DistAudioMediaModel, DistAudioData> distAudioMediaConverter;

    @Override
    public void populate(ProductModel source, ProductData target) throws ConversionException {
        if (CollectionUtils.isNotEmpty(source.getAudioMedias())) {
            List<DistAudioData> audios = source.getAudioMedias().stream()
                    .map(distAudioMediaConverter::convert)
                    .collect(Collectors.toList());

            target.setAudios(audios);
        }
    }
}
