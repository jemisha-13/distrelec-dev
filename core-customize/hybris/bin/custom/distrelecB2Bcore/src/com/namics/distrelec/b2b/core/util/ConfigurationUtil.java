package com.namics.distrelec.b2b.core.util;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Punctuation.COMMA;

public class ConfigurationUtil {

    @Autowired
    private ConfigurationService configurationService;

    public String getText(final String key){
        return configurationService.getConfiguration().getString(key);
    }

    public int getNumber(final String key){
        return configurationService.getConfiguration().getInt(key);
    }

    public int getNumber(final String key, final int defaultValue){
        return configurationService.getConfiguration().getInt(key, defaultValue);
    }

    public List<String> getListOfText(final String key){
        final String values = configurationService.getConfiguration().getString(key);
        return StringUtils.isEmpty(values) ? new ArrayList<>() : Stream.of(values.split(COMMA)).collect(Collectors.toList());
    }
}
