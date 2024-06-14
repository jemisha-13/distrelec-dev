package com.distrelec.smartedit.converter;

import de.hybris.platform.cms2.common.functions.Converter;

public class StringDataToAttributeConverter implements Converter<Object,Object> {

    @Override
    public Object convert(Object source) {
        String text = (String)source;

        if(text == null || text.equals("")){
            return null;
        }

        return text;
    }

}
