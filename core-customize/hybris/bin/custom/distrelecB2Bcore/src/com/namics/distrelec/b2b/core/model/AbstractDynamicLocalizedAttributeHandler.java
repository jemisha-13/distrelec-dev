/**
 * 
 */
package com.namics.distrelec.b2b.core.model;

import java.util.Locale;

import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicLocalizedAttributeHandler;

/**
 * Convenience base class for implementing {@link DynamicLocalizedAttributeHandler} classes.
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 1.0
 * 
 * @param <VALUE>
 *            value
 * @param <MODEL>
 *            model extends AbstractItemModel
 */
public abstract class AbstractDynamicLocalizedAttributeHandler<VALUE, MODEL extends AbstractItemModel> implements
        DynamicLocalizedAttributeHandler<VALUE, MODEL> {

    @Override
    public VALUE get(final MODEL model) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VALUE get(MODEL model, Locale loc) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(final MODEL model, final VALUE value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(final MODEL model, final VALUE value, Locale loc) {
        throw new UnsupportedOperationException();
    }

}