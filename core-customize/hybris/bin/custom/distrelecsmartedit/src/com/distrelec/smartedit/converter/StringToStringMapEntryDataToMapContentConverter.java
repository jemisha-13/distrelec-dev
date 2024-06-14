package com.distrelec.smartedit.converter;

import de.hybris.platform.cms2.common.functions.Converter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StringToStringMapEntryDataToMapContentConverter implements Converter<List<LinkedHashMap<String, String>>, Map<String,String>> {

    @Override
    public Map<String, String> convert(List<LinkedHashMap<String, String>> mappedObjects) {
        Map<String, String> map = new LinkedHashMap<>();
        for(LinkedHashMap<String,String> mappedObject : mappedObjects){
            map.put(mappedObject.get("key"), mappedObject.get("value"));
        }
        return map;
    }

}
