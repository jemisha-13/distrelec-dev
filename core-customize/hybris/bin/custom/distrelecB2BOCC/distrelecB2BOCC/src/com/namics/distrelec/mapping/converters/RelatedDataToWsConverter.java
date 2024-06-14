package com.namics.distrelec.mapping.converters;

import com.namics.distrelec.b2b.core.message.queue.model.CRelatedData;
import com.namics.distrelec.b2b.core.message.queue.model.RelatedDataType;
import com.namics.distrelec.b2b.facades.message.queue.data.RelatedData;
import com.namics.distrelec.occ.core.categories.ws.dto.RelatedWsDTO;
import com.namics.distrelec.occ.core.topcategories.ws.dto.CIValueWsDTO;
import com.namics.distrelec.occ.core.topcategories.ws.dto.CRelatedWsDTO;
import de.hybris.platform.cmsoccaddon.mapping.converters.DataToWsConverter;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import ma.glasnost.orika.MapperFactory;
import org.apache.commons.collections4.MapUtils;

public class RelatedDataToWsConverter implements DataToWsConverter<RelatedData, List<RelatedWsDTO>> {
    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

    @Override
    public Class getDataClass() {
        return RelatedData.class;
    }

    @Override
    public Class getWsClass() {
        return RelatedWsDTO.class;
    }

    @Override
    public Predicate<Object> canConvert() {
        return null;
    }

    @Override
    public List<RelatedWsDTO> convert(RelatedData source, String fields) {
        List<RelatedWsDTO> list = new ArrayList<>();
        if (MapUtils.isNotEmpty(source.getRelatedDataMap())) {
            for (Map.Entry<RelatedDataType, List<CRelatedData>> entryRelatedData : source.getRelatedDataMap().entrySet()) {
                RelatedWsDTO relatedWsDTO = new RelatedWsDTO();
                relatedWsDTO.setType(entryRelatedData.getKey().getCode());
                List<CRelatedWsDTO> cRelatedWsDTOS = entryRelatedData.getValue().stream().map(c -> convertToCRelatedWsDTO(c, fields))
                                                                     .collect(Collectors.toList());
                relatedWsDTO.setCRelatedData(cRelatedWsDTOS);
                list.add(relatedWsDTO);
            }
        }
        return list;
    }

    private CRelatedWsDTO convertToCRelatedWsDTO(CRelatedData cRelatedData, String fields) {
        CRelatedWsDTO target = new CRelatedWsDTO();
        if (cRelatedData.getType() != null) {
            target.setType(cRelatedData.getType().getCode());
        }
        target.setValues(dataMapper.mapAsList(cRelatedData.getValues(), CIValueWsDTO.class, fields));
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
