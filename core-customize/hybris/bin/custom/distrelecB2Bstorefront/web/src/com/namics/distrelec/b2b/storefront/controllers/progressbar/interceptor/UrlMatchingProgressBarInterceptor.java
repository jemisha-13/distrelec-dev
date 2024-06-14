package com.namics.distrelec.b2b.storefront.controllers.progressbar.interceptor;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.storefront.controllers.progressbar.data.ProgressBarData;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlMatchingProgressBarInterceptor extends ProgressBarInterceptor {


    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final ModelAndView modelAndView) {
        if (modelAndView != null) {
            final List<ProgressBarData> progressBar = new ArrayList<>();
            final String steps = configurationService.getConfiguration().getString(getStepsKey());
            final String[] progressSteps = StringUtils.split(steps, DistConstants.Punctuation.DASH);
            for (final String progressStep : progressSteps) {
                String progressBarStepData = getProgressBarStep(progressStep);
                final ProgressBarData progressBarData = progressBarConverter.convert(progressBarStepData);
                populateProgressBar(request, progressBar, progressBarData);
            }
            modelAndView.addObject(PROGRESS_BAR_STEPS, progressBar);
        }
    }

    private void populateProgressBar(HttpServletRequest request, List<ProgressBarData> progressBar, ProgressBarData progressBarData) {
        if (progressBarData != null && StringUtils.isNotBlank(request.getRequestURI()) && StringUtils.isNotBlank(progressBarData.getPageUrl())) {
            final Matcher matcher = Pattern.compile(progressBarData.getPageUrl()).matcher(request.getRequestURI());

            if (matcher.matches()) {
                progressBarData.setActive(true);
                progressBarData.setUrl(request.getRequestURI());
            } else {
                matcher.reset();
                final StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    matcher.appendReplacement(sb, matcher.group());
                }
                progressBarData.setUrl(sb.toString());
            }
        }
        progressBar.add(progressBarData);
    }

    private String getProgressBarStep(String progressStep) {
        return getStepsKey() + DistConstants.Punctuation.DOT + progressStep;
    }
}
