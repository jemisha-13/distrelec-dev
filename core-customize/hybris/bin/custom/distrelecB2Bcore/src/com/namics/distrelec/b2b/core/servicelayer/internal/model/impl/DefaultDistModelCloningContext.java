package com.namics.distrelec.b2b.core.servicelayer.internal.model.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.jalo.type.AttributeDescriptor;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.RelationDescriptor;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.servicelayer.internal.model.impl.DefaultModelCloningContext;

/**
 * Skips relation descriptors marked by dontCopy.
 */
public class DefaultDistModelCloningContext extends DefaultModelCloningContext {

    @Override
    public boolean skipAttribute(Object original, String qualifier) {
        boolean skip = false;
        if (original instanceof ItemModel) {
            ComposedType ct = TypeManager.getInstance().getComposedType(((ItemModel) original).getItemtype());
            AttributeDescriptor ad = ct.getAttributeDescriptorIncludingPrivate(qualifier);
            if (ad instanceof RelationDescriptor) {
                RelationDescriptor rd = (RelationDescriptor) ad;
                skip = (rd.getRelationType().isOneToMany() && !ad.isPartOf() && !isOneEndAttribute(rd)) || isDontCopy(rd);
            }
        }

        if (skip) {
            System.err.println("Skipping " + original + "." + qualifier);
        }

        return skip;
    }

    private boolean isOneEndAttribute(RelationDescriptor rd) {
        return rd.isProperty() || rd.getPersistenceQualifier() != null;
    }

    private boolean isDontCopy(RelationDescriptor rd) {
        Object dontCopyValue = rd.getProperty(RelationDescriptor.DONT_COPY);
        return Boolean.TRUE.equals(dontCopyValue);
    }
}
