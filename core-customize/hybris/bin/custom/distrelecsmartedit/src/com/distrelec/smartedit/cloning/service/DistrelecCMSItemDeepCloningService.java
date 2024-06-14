package com.distrelec.smartedit.cloning.service;

import com.google.common.base.Preconditions;
import com.namics.distrelec.b2b.core.model.cms2.components.DistComponentGroupModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.cloning.service.CMSModelCloningContextFactory;
import de.hybris.platform.cms2.cloning.service.impl.CMSModelCloningContext;
import de.hybris.platform.cms2.cloning.service.impl.DefaultCMSItemDeepCloningService;
import de.hybris.platform.cms2.cloning.service.predicate.CMSItemCloneablePredicate;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.internal.model.ModelCloningContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DistrelecCMSItemDeepCloningService extends DefaultCMSItemDeepCloningService {

    @Autowired
    private CatalogVersionService catalogVersionService;

    @Autowired
    private CMSItemCloneablePredicate cmsItemCloneablePredicate;

    @Autowired
    private CMSModelCloningContextFactory cmsModelCloningContextFactory;

    @Override
    public ItemModel deepCloneComponent(ItemModel srcComponent, ModelCloningContext cloningContext) {
        ItemModel clonedComponent = super.deepCloneComponent(srcComponent, cloningContext);

        if (clonedComponent instanceof DistComponentGroupModel) {
            handleDistComponentGroup((DistComponentGroupModel) clonedComponent);
        }

        return clonedComponent;
    }

    private void handleDistComponentGroup(DistComponentGroupModel clonedComponent) {
        Instant now = Instant.now();
        List<SimpleCMSComponentModel> clonedChildComponents = clonedComponent.getComponents().stream().map(childComponent -> {
            CatalogVersionModel targetCatalogVersionModel = catalogVersionService.getSessionCatalogVersions().stream().findFirst().orElse(null);
            Preconditions.checkArgument(targetCatalogVersionModel != null, "CatalogVersion is required to perform this operation, null given");
            Preconditions.checkArgument(cmsItemCloneablePredicate.test(childComponent), "Component cannot be cloned. Its type belongs to the nonCloneableTypeList or to the typeBlacklistSet.");
            CMSModelCloningContext cloningContext = cmsModelCloningContextFactory.createCloningContextWithCatalogVersionPredicates(targetCatalogVersionModel);
            SimpleCMSComponentModel clonedChildComponent = (SimpleCMSComponentModel) deepCloneComponent(childComponent, cloningContext);
            clonedChildComponent.setName(childComponent.getName() + "_" + now.toString());
            clonedChildComponent.setSlots(Collections.emptyList());
            return clonedChildComponent;
        }).collect(Collectors.toList());
        clonedComponent.setComponents(clonedChildComponents);
    }
}
