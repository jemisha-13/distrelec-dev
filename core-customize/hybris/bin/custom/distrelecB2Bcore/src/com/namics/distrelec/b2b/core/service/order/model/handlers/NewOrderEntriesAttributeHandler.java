/*
 * Copyright 2000-2014 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order.model.handlers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.BooleanUtils;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

/**
 * {@code NewOrderEntriesAttributeHandler}
 * 
 *
 * @author Francesco Bersani, Distrelec Group AG
 * @since Distrelec x.x
 */
public class NewOrderEntriesAttributeHandler extends AbstractDynamicAttributeHandler<List<AbstractOrderEntryModel>, AbstractOrderModel> {

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler#get(de.hybris.platform.servicelayer.model.
     * AbstractItemModel)
     */
    @Override
    public List<AbstractOrderEntryModel> get(final AbstractOrderModel model) {
        if (model.getEntries() == null || model.getEntries().isEmpty()) {
            Collections.<AbstractOrderEntryModel> emptyList();
        }

        return model.getEntries().stream().filter(entry -> !BooleanUtils.isTrue(entry.getConfirmed())).collect(Collectors.toList());
    }
}