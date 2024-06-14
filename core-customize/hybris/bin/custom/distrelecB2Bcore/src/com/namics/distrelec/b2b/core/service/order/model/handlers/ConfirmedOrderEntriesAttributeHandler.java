/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order.model.handlers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

public class ConfirmedOrderEntriesAttributeHandler extends AbstractDynamicAttributeHandler<List<AbstractOrderEntryModel>, AbstractOrderModel> {

    @Override
    public List<AbstractOrderEntryModel> get(final AbstractOrderModel model) {

        final List<AbstractOrderEntryModel> confirmedEntries = new ArrayList<AbstractOrderEntryModel>();
        confirmedEntries.addAll(model.getEntries());

        // filter the whole entries to get only the "CONFIRMED" order entries
        CollectionUtils.filter(confirmedEntries, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                final AbstractOrderEntryModel entry = (AbstractOrderEntryModel) o;
                return Boolean.TRUE.equals(entry.getConfirmed());
            }
        });

        return confirmedEntries;
    }

}
