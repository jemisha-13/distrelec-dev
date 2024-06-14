package com.namics.distrelec.b2b.core.inout.impex.translator;

import de.hybris.platform.impex.jalo.translators.AbstractValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import groovy.json.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

public class UnicodeValueTranslator extends AbstractValueTranslator {

    @Override
    public Object importValue(String s, Item item) throws JaloInvalidParameterException {
        if(StringUtils.isNotBlank(s)){
            return StringEscapeUtils.unescapeJava(s);
        }
        return null;
    }

    @Override
    public String exportValue(Object o) throws JaloInvalidParameterException {
        return null;
    }
}
