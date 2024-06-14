package com.distrelec.smartedit.converter;

import de.hybris.platform.cms2.common.functions.Converter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StringToStringMapToMapEntryDataContentConverter implements Converter<Map<String, String>, List<MapEntry>> {

    @Override
    public List<MapEntry> convert(Map<String, String> stringStringMap) {
        return stringStringMap.entrySet().stream()
                .map(entry -> {
                    MapEntry<String, String> mapEntry = new MapEntry<>();
                    mapEntry.setKey(entry.getKey());
                    mapEntry.setValue(entry.getValue());
                    return mapEntry;
                }).collect(Collectors.toList());
    }

}
