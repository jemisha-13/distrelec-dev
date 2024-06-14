/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.b2bapprovalprocess.actions;

import java.util.Date;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.b2b.process.approval.actions.SystemAuditAction;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;

/**
 * DistSystemAuditAction extends SystemAuditAction.
 * 
 * @author DAEHUSIR, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class DistSystemAuditAction extends SystemAuditAction {

    private boolean createSnapshot;

    @Override
    public OrderHistoryEntryModel createAuditHistory(final OrderModel order, final ItemModel historyEntryOwner, final String messageKey,
            final Object[] localizationArguments) {
        final String auditMessage = getL10NService().getLocalizedString(messageKey, localizationArguments);
        final OrderHistoryEntryModel historyEntry = (OrderHistoryEntryModel) getModelService().create(OrderHistoryEntryModel.class);
        historyEntry.setTimestamp(new Date());
        historyEntry.setOrder(order);
        historyEntry.setDescription(auditMessage);
        if (isCreateSnapshot()) {
            final OrderModel snapshot = getOrderHistoryService().createHistorySnapshot(order);
            historyEntry.setPreviousOrderVersion(snapshot);
            getOrderHistoryService().saveHistorySnapshot(snapshot);
        }
        historyEntry.setOwner(historyEntryOwner);
        getModelService().save(historyEntry);
        return historyEntry;
    }

    public boolean isCreateSnapshot() {
        return createSnapshot;
    }

    @Required
    public void setCreateSnapshot(final boolean createSnapshot) {
        this.createSnapshot = createSnapshot;
    }

}
