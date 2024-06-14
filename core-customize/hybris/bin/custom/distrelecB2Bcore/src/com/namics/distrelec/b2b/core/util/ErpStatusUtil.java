package com.namics.distrelec.b2b.core.util;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component("erpStatusUtil")
public class ErpStatusUtil {

    @Autowired
    private ConfigurationService configurationService;

    public static final String COMMA = DistConstants.Punctuation.COMMA;

    public List<String> getErpSalesStatusFromConfiguration(final String key){
        final String textualErpStatusCodes = configurationService.getConfiguration().getString(key);
        return StringUtils.isEmpty(textualErpStatusCodes) ? new ArrayList<>() : Stream.of(textualErpStatusCodes.split(COMMA)).collect(Collectors.toList());
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}

