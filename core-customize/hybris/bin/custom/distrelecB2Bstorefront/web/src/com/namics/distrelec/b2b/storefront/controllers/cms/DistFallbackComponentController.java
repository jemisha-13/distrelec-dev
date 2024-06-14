package com.namics.distrelec.b2b.storefront.controllers.cms;

import com.namics.distrelec.b2b.core.model.cms2.components.DistFallbackComponentModel;
import com.namics.distrelec.b2b.core.model.restrictions.DistManufacturerRestrictionModel;
import com.namics.distrelec.b2b.core.service.data.impl.DefaultDistRestrictionData;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSCategoryRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSProductRestrictionModel;
import de.hybris.platform.cms2.servicelayer.services.CMSRestrictionService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller("DistFallbackComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistFallbackComponent)
public class DistFallbackComponentController extends AbstractDistCMSComponentController<DistFallbackComponentModel> {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private CMSRestrictionService cmsRestrictionService;

    @Autowired
    private ModelService modelService;

    @Override
    protected void fillModel(HttpServletRequest request, Model model, DistFallbackComponentModel component) {
        model.addAttribute("cmsComponents", getCurrentCMSComponents(component));
    }

    public List<SimpleCMSComponentModel> getCurrentCMSComponents(DistFallbackComponentModel component) {
        DefaultDistRestrictionData restrictionData = new DefaultDistRestrictionData();

        ProductModel product = sessionService.getAttribute("currentProduct");
        if (product != null) {
            restrictionData.setProduct(product);
            restrictionData.setManufacturer(product.getManufacturer());
            restrictionData.setCategory(product.getPimCategory());
        }

        Stream<SimpleCMSComponentModel> restrictionEvaluationStream = component.getPrimaryCMSComponents().stream()
                .sorted((a, b) -> {
                    SimpleCMSComponentModel componentA = getModelService().get(a);
                    SimpleCMSComponentModel componentB = getModelService().get(b);

                    AbstractRestrictionModel restrictionA = getHighestOrderRestriction(componentA.getRestrictions());
                    AbstractRestrictionModel restrictionB = getHighestOrderRestriction(componentB.getRestrictions());

                    return compareRestrictionPriorities(restrictionA, restrictionB);
                })
                .filter(cmsComponentModel -> getCmsRestrictionService().evaluateCMSComponent(cmsComponentModel, restrictionData));

        Optional<SimpleCMSComponentModel> onlyFirstComponent = restrictionEvaluationStream.findFirst();

        if (onlyFirstComponent.isPresent()) {
            return Arrays.asList(onlyFirstComponent.get());
        } else {
            return component.getFallbackCMSComponents().stream()
                    .filter(cmsComponentModel -> getCmsRestrictionService().evaluateCMSComponent(cmsComponentModel, restrictionData))
                    .collect(Collectors.toList());
        }
    }

    private CMSRestrictionService getCmsRestrictionService() {
        return cmsRestrictionService;
    }

    private ModelService getModelService() {
        return modelService;
    }

    private AbstractRestrictionModel getHighestOrderRestriction(List<AbstractRestrictionModel> abstractRestrictionModels) {
        AbstractRestrictionModel highestPriorityRestriction = null;

        for (AbstractRestrictionModel restriction : abstractRestrictionModels) {
            if (restriction instanceof CMSProductRestrictionModel) {
                highestPriorityRestriction = restriction;
            } else if (restriction instanceof DistManufacturerRestrictionModel && !(highestPriorityRestriction instanceof CMSProductRestrictionModel)) {
                highestPriorityRestriction = restriction;
            } else if (restriction instanceof CMSCategoryRestrictionModel) {
                if (highestPriorityRestriction == null) {
                    highestPriorityRestriction = restriction;
                } else if (highestPriorityRestriction instanceof CMSCategoryRestrictionModel) {
                    CMSCategoryRestrictionModel currentHighestCategoryRestriction = (CMSCategoryRestrictionModel) highestPriorityRestriction;
                    CMSCategoryRestrictionModel thisHighestCategoryRestriction = (CMSCategoryRestrictionModel) restriction;

                    if (!thisHighestCategoryRestriction.isRecursive() && currentHighestCategoryRestriction.isRecursive()) {
                        highestPriorityRestriction = thisHighestCategoryRestriction;
                    }
                } else if (highestPriorityRestriction == null) {
                    highestPriorityRestriction = restriction;
                }
            }
        }
        return highestPriorityRestriction;
    }

    private int compareRestrictionPriorities(AbstractRestrictionModel restrictionA, AbstractRestrictionModel restrictionB) {
        if (restrictionA != null && restrictionB == null) {
            return -1;
        }

        if (restrictionA == null && restrictionB != null) {
            return 1;
        }

        if (restrictionA instanceof CMSProductRestrictionModel) {
            return -1;
        }

        if (restrictionA instanceof DistManufacturerRestrictionModel && restrictionB instanceof CMSProductRestrictionModel) {
            return 1;
        }

        if (restrictionA instanceof DistManufacturerRestrictionModel && restrictionB instanceof CMSCategoryRestrictionModel) {
            return -1;
        }

        if (restrictionA instanceof CMSCategoryRestrictionModel) {
            if (restrictionB instanceof CMSProductRestrictionModel || restrictionB instanceof DistManufacturerRestrictionModel) {
                return 1;
            }

            if (restrictionB instanceof CMSCategoryRestrictionModel) {
                CMSCategoryRestrictionModel categoryRestrictionA = (CMSCategoryRestrictionModel) restrictionA;
                CMSCategoryRestrictionModel categoryRestrictionB = (CMSCategoryRestrictionModel) restrictionB;

                if (!categoryRestrictionA.isRecursive() && categoryRestrictionB.isRecursive()) {
                    return -1;
                }
                if (categoryRestrictionA.isRecursive() && !categoryRestrictionB.isRecursive()) {
                    return 1;
                }
            }
        }

        return 0;
    }

}
