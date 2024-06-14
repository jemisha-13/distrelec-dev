package com.namics.distrelec.occ.core.mapping.resolvers.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;

import com.namics.distrelec.occ.core.mapping.resolvers.DistHeadlessResolver;

import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

public class DistHeadlessURLResolver implements InitializingBean, DistHeadlessResolver<String, String> {

    /**
     * Regex format which needs to be filled with active languages.
     * Expected result: ^\/?[cs|da|de|en|et|fi|fr|hu|it|lt|lv|nl|no|pl|ro|sk|sv]{2}(\/.*$)$
     */
    private static final String PATH_WITH_LANGUAGE_REGEX_FORMAT = "^\\/?[%s]{2}(\\/.*$)$";

    private CommonI18NService commonI18NService;

    private Pattern pattern;

    @Override
    public void afterPropertiesSet() throws Exception {
        String activeLanguages = commonI18NService.getAllLanguages()
                                                  .stream()
                                                  .filter(language -> isTrue(language.getActive()))
                                                  .map(language -> language.getIsocode())
                                                  .filter(languageCode -> languageCode.length() == 2)
                                                  .collect(Collectors.joining("|"));
        String pathWithLanguageRegex = String.format(PATH_WITH_LANGUAGE_REGEX_FORMAT, activeLanguages);
        pattern = Pattern.compile(pathWithLanguageRegex, CASE_INSENSITIVE);
    }

    @Override
    public String resolve(String urlPath) {
        if (isBlank(urlPath)) {
            return urlPath;
        }
        Matcher matcher = pattern.matcher(urlPath);
        return matcher.matches() ? matcher.group(1) : urlPath;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }
}
