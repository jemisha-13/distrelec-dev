package com.namics.distrelec.b2b.core.service.evaluator.impl;

import com.namics.distrelec.b2b.core.model.restrictions.DistComponentViewCountLimitRestrictionModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class DistComponentViewCountLimitRestrictionEvaluator implements CMSRestrictionEvaluator<DistComponentViewCountLimitRestrictionModel> {

    private static final String MAP_ATTRIBUTE_KEY = "restriction_limitComponentCountMap";

    @Override
    public boolean evaluate(DistComponentViewCountLimitRestrictionModel distComponentViewCountLimitRestrictionModel, RestrictionData restrictionData) {
        Map<AbstractCMSComponentModel, Integer> countMap = getCountViewMap();

        AbstractCMSComponentModel keyComponent = distComponentViewCountLimitRestrictionModel.getComponents().iterator().next();
        Integer currentCount = countMap.getOrDefault(keyComponent, 0);

        boolean result = currentCount < distComponentViewCountLimitRestrictionModel.getViewCountLimit();

        if (result) {
            currentCount++;
            countMap.put(keyComponent, currentCount);
        }

        return result;
    }

    private Map<AbstractCMSComponentModel, Integer> getCountViewMap() {
        Map<AbstractCMSComponentModel, Integer> countMap = (Map<AbstractCMSComponentModel, Integer>) getHttpSession().getAttribute(MAP_ATTRIBUTE_KEY);
        if (countMap == null) {
            countMap = new HashMap<>();
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession().setAttribute(MAP_ATTRIBUTE_KEY, countMap);
        }
        return countMap;
    }

    private HttpSession getHttpSession() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
    }
}
