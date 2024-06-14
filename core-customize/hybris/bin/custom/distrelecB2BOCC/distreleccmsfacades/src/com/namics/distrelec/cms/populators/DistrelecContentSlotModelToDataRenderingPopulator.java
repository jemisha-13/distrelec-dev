package com.namics.distrelec.cms.populators;

import com.namics.distrelec.b2b.core.model.cms2.components.DistLocalCatalogFilterComponentContainerModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.containers.AbstractCMSComponentContainerModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.registry.CMSComponentContainerRegistry;
import de.hybris.platform.cms2.strategies.CMSComponentContainerStrategy;
import de.hybris.platform.cmsfacades.data.AbstractCMSComponentData;
import de.hybris.platform.cmsfacades.data.PageContentSlotData;
import de.hybris.platform.cmsfacades.rendering.populators.ContentSlotModelToDataRenderingPopulator;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Override the OOTB populator to handle custom container.
 */
public class DistrelecContentSlotModelToDataRenderingPopulator extends ContentSlotModelToDataRenderingPopulator {
    private CMSComponentContainerRegistry cmsComponentContainerRegistry;


    protected void populateComponents(final ContentSlotModel slotModel, final PageContentSlotData targetData)
    {
        // Components
        List<AbstractCMSComponentData> componentDataList = new ArrayList<>();
        componentDataList.addAll(slotModel.getCmsComponents().stream().filter(getRenderingVisibilityService()::isVisible).flatMap(component -> {
            List<AbstractCMSComponentData> componentData = component instanceof AbstractCMSComponentContainerModel ? flattenComponentContainerHierarchy(component) :  Collections.singletonList(convertComponentModelToData(component));
            return componentData.stream();
        }).collect(Collectors.toList()));
        targetData.setComponents(componentDataList);

    }

    
    private List<AbstractCMSComponentData> flattenComponentContainerHierarchy(AbstractCMSComponentModel componentModel) {

        if(componentModel instanceof AbstractCMSComponentContainerModel) {
            AbstractCMSComponentContainerModel container = (AbstractCMSComponentContainerModel) componentModel;
            // Add the child components from the container
            final CMSComponentContainerStrategy strategy = getCmsComponentContainerRegistry().getStrategy(container);

            final List<AbstractCMSComponentModel> replacementComponents = strategy.getDisplayComponentsForContainer(container);
            if (CollectionUtils.isNotEmpty(replacementComponents)) {
                return replacementComponents.stream().filter(getRenderingVisibilityService()::isVisible).map(this::convertComponentModelToData).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    public CMSComponentContainerRegistry getCmsComponentContainerRegistry() {
        return cmsComponentContainerRegistry;
    }

    public void setCmsComponentContainerRegistry(CMSComponentContainerRegistry cmsComponentContainerRegistry) {
        this.cmsComponentContainerRegistry = cmsComponentContainerRegistry;
    }
}
