package com.namics.distrelec.b2b.backoffice.widgets.editorarea.renderer.impl;

import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.widgets.editorarea.renderer.impl.DefaultEditorAreaSectionRenderer;

public class DefaultDistEditorAreaSectionRenderer extends DefaultEditorAreaSectionRenderer {

    @Override
    protected boolean canChangeProperty(DataAttribute attribute, Object instance) {
        if (attribute != null) {
            boolean attributeWritable = attribute.isWritable();
            return attributeWritable && this.getEditAvailabilityProviderFactory().getProvider(attribute, instance).isAllowedToEdit(attribute, instance) && this.getPermissionFacade().canChangeInstanceProperty(instance, attribute.getQualifier());
        } else {
            return false;
        }
    }
}
