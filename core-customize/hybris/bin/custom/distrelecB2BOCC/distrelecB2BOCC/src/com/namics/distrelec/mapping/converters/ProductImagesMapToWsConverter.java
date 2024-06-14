package com.namics.distrelec.mapping.converters;

import de.hybris.platform.cmsoccaddon.mapping.converters.DataToWsConverter;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercewebservicescommons.dto.product.ImageWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductImagesMapWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Resource;

import ma.glasnost.orika.MapperFactory;

public class ProductImagesMapToWsConverter implements DataToWsConverter<List<Map<String, ImageData>>, List<ProductImagesMapWsDTO>> {

    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

    @Override
    public Class getDataClass() {
        return ImageData.class;
    }

    @Override
    public Class getWsClass() {
        return ImageWsDTO.class;
    }

    @Override
    public Predicate<Object> canConvert() {
        return null;
    }

    @Override
    public List<ProductImagesMapWsDTO> convert(List<Map<String, ImageData>> source, String fields) {
        List<ProductImagesMapWsDTO> target = new ArrayList<ProductImagesMapWsDTO>();
        source.forEach((x) -> {
            x.forEach((k, v) -> {
                ProductImagesMapWsDTO productImagesMapWsDTO = new ProductImagesMapWsDTO();
                productImagesMapWsDTO.setKey(k);
                productImagesMapWsDTO.setValue(getDataMapper().map(v, ImageWsDTO.class, fields));
                target.add(productImagesMapWsDTO);
            });
        });
        return target;
    }

    @Override
    public void customize(MapperFactory factory) {

    }

    public DataMapper getDataMapper() {
        return dataMapper;
    }

    public void setDataMapper(DataMapper dataMapper) {
        this.dataMapper = dataMapper;
    }
}
