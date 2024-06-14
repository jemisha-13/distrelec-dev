package com.namics.distrelec.b2b.storefront.controllers.progressbar.interceptor;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.storefront.controllers.progressbar.data.ProgressBarData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProgressBarInterceptor extends HandlerInterceptorAdapter {

    private String stepsKey;
    private boolean urlHasToBeEqual;

    protected static final String PROGRESS_BAR_STEPS = "processSteps";

    @Autowired
    protected ConfigurationService configurationService;

    @Autowired
    @Qualifier("progressBarConverter")
    protected Converter<String, ProgressBarData> progressBarConverter;

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final ModelAndView modelAndView) {
        if (modelAndView != null) {
            final String steps = configurationService.getConfiguration().getString(stepsKey, StringUtils.EMPTY);
            final List<ProgressBarData> progressBar = Stream.of(StringUtils.split(steps, DistConstants.Punctuation.DASH)).map(progressStep -> {
                final ProgressBarData progressBarData = progressBarConverter.convert(stepsKey + DistConstants.Punctuation.DOT + progressStep);
                progressBarData.setActive(checkIsLinkActive(request, progressBarData));

                return progressBarData;
            }).collect(Collectors.toList());

            modelAndView.addObject(PROGRESS_BAR_STEPS, progressBar);
        }
    }

    private boolean checkIsLinkActive(HttpServletRequest request, ProgressBarData progressBarData) {
        return urlHasToBeEqual && StringUtils.equalsIgnoreCase(request.getRequestURI(), progressBarData.getPageUrl())
                || StringUtils.startsWith(request.getRequestURI(), progressBarData.getPageUrl())
                || CollectionUtils.isNotEmpty(progressBarData.getPageUrls())
                && CollectionUtils.containsAny(progressBarData.getPageUrls(), request.getRequestURI());
    }

    protected String getStepsKey() {
        return stepsKey;
    }

    public void setStepsKey(final String stepsKey) {
        this.stepsKey = stepsKey;
    }

    public void setUrlHasToBeEqual(final boolean urlHasToBeEqual) {
        this.urlHasToBeEqual = urlHasToBeEqual;
    }
}
