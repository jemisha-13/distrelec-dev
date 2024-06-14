package com.namics.distrelec.b2b.core.security.impl;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.security.SanitizationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class SanitizationServiceImpl implements SanitizationService {

    @Override
    public String removePeriods(String input) {
        return StringUtils.replace(input, DistConstants.Punctuation.DOT, StringUtils.EMPTY);
    }
}
