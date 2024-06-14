package com.namics.distrelec.occ.core.mapping.converters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.namics.distrelec.occ.core.product.ws.dto.VolumePricesMapEntryWsDTO;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercewebservicescommons.dto.product.PriceWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

@WsDTOMapping
public class VolumePricesMapConverter extends BidirectionalConverter<List<VolumePricesMapEntryWsDTO>, Map<Long, Map<String, PriceData>>> {

    private DataMapper dataMapper;

    @Override
    public Map<Long, Map<String, PriceData>> convertTo(List<VolumePricesMapEntryWsDTO> source, Type<Map<Long, Map<String, PriceData>>> type,
                                                       MappingContext mappingContext) {
        Map<Long, Map<String, PriceData>> target = new HashMap<>();
        for (VolumePricesMapEntryWsDTO entry : source) {
            target.put(entry.getKey(), entry.getValue().entrySet().stream()
                                            .collect(Collectors.toMap(Map.Entry::getKey,
                                                                      e -> dataMapper.map(e.getValue(), PriceData.class))));
        }
        return target;
    }

    @Override
    public List<VolumePricesMapEntryWsDTO> convertFrom(Map<Long, Map<String, PriceData>> source, Type<List<VolumePricesMapEntryWsDTO>> type,
                                                       MappingContext mappingContext) {
        return source.entrySet().stream()
                     .map(e -> convertToWs(e.getKey(), e.getValue()))
                     .collect(Collectors.toList());
    }

    private VolumePricesMapEntryWsDTO convertToWs(Long key, Map<String, PriceData> value) {
        VolumePricesMapEntryWsDTO wsEntry = new VolumePricesMapEntryWsDTO();
        wsEntry.setKey(key);
        wsEntry.setValue(value.entrySet().stream()
                              .collect(Collectors.toMap(Map.Entry::getKey,
                                                        e -> dataMapper.map(e.getValue(), PriceWsDTO.class))));
        return wsEntry;
    }

    public DataMapper getDataMapper() {
        return dataMapper;
    }

    public void setDataMapper(DataMapper dataMapper) {
        this.dataMapper = dataMapper;
    }
}
