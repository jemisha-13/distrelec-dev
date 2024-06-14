package com.namics.distrelec.b2b.storefront.controllers.misc;

import com.namics.distrelec.b2b.core.service.classification.DistClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

import static com.namics.distrelec.b2b.core.service.classification.DistClassificationService.FACET_GROUP_NAME;
import static com.namics.distrelec.b2b.core.service.classification.DistClassificationService.FACET_VALUE_NAME;

@Controller
@RequestMapping("/**/classification")
public class ClassificationController {

    @Autowired
    private DistClassificationService distClassificationService;

    @RequestMapping("/translation")
    @ResponseBody
    public Map<String, String> getClassificationAttributeValueTranslation(@RequestParam(FACET_GROUP_NAME) String facetGroupName,
            @RequestParam(FACET_VALUE_NAME) String facetValueName) {
        return getDistClassificationService().getClassificationValueTranslation(facetGroupName, facetValueName);
    }

    public DistClassificationService getDistClassificationService() {
        return distClassificationService;
    }

    public void setDistClassificationService(DistClassificationService distClassificationService) {
        this.distClassificationService = distClassificationService;
    }
}
