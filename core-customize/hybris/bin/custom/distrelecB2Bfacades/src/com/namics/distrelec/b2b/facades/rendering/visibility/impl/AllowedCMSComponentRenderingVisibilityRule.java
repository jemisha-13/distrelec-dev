package com.namics.distrelec.b2b.facades.rendering.visibility.impl;

import com.namics.distrelec.b2b.core.model.cms2.components.DistRestrictionComponentGroupModel;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cmsfacades.rendering.visibility.impl.CMSComponentRenderingVisibilityRule;

/**
 * Allows the restriction component group to enable lazy restriction evaluation at frontend layer.
 */
public class AllowedCMSComponentRenderingVisibilityRule extends CMSComponentRenderingVisibilityRule {

    @Override
    public boolean isVisible(AbstractCMSComponentModel component) {
        boolean isRestrictedComponentGroup = component instanceof DistRestrictionComponentGroupModel;
        if (isRestrictedComponentGroup) {
            // restriction component group is always visible because frontend layer needs to check its visibility in client browser
            boolean isVisible = Boolean.TRUE.equals(component.getVisible());
            return isVisible;
        }

        return super.isVisible(component);
    }
}
