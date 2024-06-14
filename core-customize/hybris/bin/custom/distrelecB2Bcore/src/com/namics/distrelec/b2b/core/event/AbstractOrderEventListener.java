/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.orderhistory.OrderHistoryService;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;
import java.util.Locale;

public abstract class AbstractOrderEventListener<T extends AbstractEvent> extends AbstractEventListener<T> {
    private boolean createSnapshot;

    public ModelService getModelServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getModelServiceViaLookup().");
    }

    public OrderHistoryService getOrderHistoryServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getOrderHistoryServiceViaLookup().");
    }

    public I18NService getI18NServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getI18NServiceViaLookup().");
    }

    protected boolean isCreateSnapshot() {
        return createSnapshot;
    }

    public void setCreateSnapshot(final boolean createSnapshot) {
        this.createSnapshot = createSnapshot;
    }

    protected void setSessionLocaleForOrder(final OrderModel order) {
        if (order.getLocale() != null) {
            getI18NServiceViaLookup().setCurrentLocale(new Locale(order.getLocale()));
        }
    }

    protected void createOrderHistoryEntry(final PrincipalModel owner, final OrderModel order, final OrderStatus status, final String description) {
        final OrderHistoryEntryModel historyEntry = getModelServiceViaLookup().create(OrderHistoryEntryModel.class);
        historyEntry.setTimestamp(new Date());
        historyEntry.setOrder(order);
        historyEntry.setDescription(description);
        historyEntry.setOwner(owner);
        if (this.isCreateSnapshot()) {
            final OrderModel snapshot = getOrderHistoryServiceViaLookup().createHistorySnapshot(order);
            snapshot.setStatus(status);
            historyEntry.setPreviousOrderVersion(snapshot);
            getOrderHistoryServiceViaLookup().saveHistorySnapshot(snapshot);
        }
        getModelServiceViaLookup().save(historyEntry);
    }
}
