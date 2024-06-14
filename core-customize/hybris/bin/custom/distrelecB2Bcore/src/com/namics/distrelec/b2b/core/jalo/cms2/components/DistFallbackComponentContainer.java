package com.namics.distrelec.b2b.core.jalo.cms2.components;

import com.namics.distrelec.b2b.core.model.restrictions.DistManufacturerRestrictionModel;
import com.namics.distrelec.b2b.core.service.data.impl.DefaultDistRestrictionData;
import de.hybris.platform.cms2.jalo.contents.components.SimpleCMSComponent;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSCategoryRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSProductRestrictionModel;
import de.hybris.platform.cms2.servicelayer.services.CMSRestrictionService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DistFallbackComponentContainer extends GeneratedDistFallbackComponentContainer {
    @SuppressWarnings("unused")
    private final static Logger LOG = Logger.getLogger(DistFallbackComponentContainer.class.getName());

    @Override
    protected Item createItem(final SessionContext ctx, final ComposedType type, final ItemAttributeMap allAttributes) throws JaloBusinessException {
        // business code placed here will be executed before the item is created
        // then create the item
        final Item item = super.createItem(ctx, type, allAttributes);
        // business code placed here will be executed after the item was created
        // and return the item
        return item;
    }

    @Override
    public List<SimpleCMSComponent> getCurrentCMSComponents(SessionContext sessionContext) {
        DefaultDistRestrictionData restrictionData = new DefaultDistRestrictionData();

        Product product = sessionContext.getAttribute("currentProduct");
        if(product!=null) {
            ProductModel currentProduct = getModelService().get(product);

            restrictionData.setProduct(currentProduct);
            restrictionData.setManufacturer(currentProduct.getManufacturer());
            restrictionData.setCategory(currentProduct.getPimCategory());
        }

        Stream<SimpleCMSComponent> restrictionEvaluationStream = getPrimaryCMSComponents().stream()
                .sorted((a, b) -> {
                    SimpleCMSComponentModel componentA = getModelService().get(a);
                    SimpleCMSComponentModel componentB = getModelService().get(b);

                    AbstractRestrictionModel restrictionA = getHighestOrderRestriction(componentA.getRestrictions());
                    AbstractRestrictionModel restrictionB = getHighestOrderRestriction(componentB.getRestrictions());

                    return compareRestrictionPriorities(restrictionA, restrictionB);
                })
                .map(getModelService()::get)
                .map(obj -> (SimpleCMSComponentModel) obj)
                .filter(cmsComponentModel -> getCmsRestrictionService().evaluateCMSComponent(cmsComponentModel, restrictionData))
                .map(getModelService()::getSource);

        Optional<SimpleCMSComponent> onlyFirstComponent = restrictionEvaluationStream.findFirst();

        if (onlyFirstComponent.isPresent()) {
            return Arrays.asList(onlyFirstComponent.get());
        } else {
            return getFallbackCMSComponents().stream()
                    .map(getModelService()::get)
                    .map(obj -> (SimpleCMSComponentModel) obj)
                    .filter(cmsComponentModel -> getCmsRestrictionService().evaluateCMSComponent(cmsComponentModel, restrictionData))
                    .map(getModelService()::getSource)
                    .map(component -> (SimpleCMSComponent)component)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<SimpleCMSComponent> getSimpleCMSComponents(SessionContext ctx) {
        return Stream.concat(getPrimaryCMSComponents().stream(), getFallbackCMSComponents().stream()).collect(Collectors.toList());
    }

    private CMSRestrictionService getCmsRestrictionService() {
        return (CMSRestrictionService) Registry.getApplicationContext().getBean("defaultCMSRestrictionService");
    }

    private ModelService getModelService() {
        return Registry.getApplicationContext().getBean(ModelService.class);
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
