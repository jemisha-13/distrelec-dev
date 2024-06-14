/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.order.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceUpdateCartEntryStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * {@code DefaultDistCommerceUpdateCartEntryStrategy}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.7
 */
public class DefaultDistCommerceUpdateCartEntryStrategy extends DefaultCommerceUpdateCartEntryStrategy {

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.hybris.platform.commerceservices.order.impl.DefaultCommerceUpdateCartEntryStrategy#updateQuantityForCartEntry(de.hybris.platform.
     * commerceservices.service.data.CommerceCartParameter)
     */
    @Override
    public CommerceCartModification updateQuantityForCartEntry(final CommerceCartParameter parameters) throws CommerceCartModificationException {
        beforeUpdateCartEntry(parameters);
        final CartModel cartModel = parameters.getCart();
        final long newQuantity = parameters.getQuantity();
        final long entryNumber = parameters.getEntryNumber();

        validateParameterNotNull(cartModel, "Cart model cannot be null");
        CommerceCartModification modification;

        final AbstractOrderEntryModel entryToUpdate = getEntryForNumber(cartModel, (int) entryNumber);
        validateEntryBeforeModification(newQuantity, entryToUpdate);
        final Integer maxOrderQuantity = entryToUpdate.getProduct().getMaxOrderQuantity();
        // Work out how many we want to add (could be negative if we are
        // removing items)
        final long quantityToAdd = newQuantity - entryToUpdate.getQuantity().longValue();

        if (entryToUpdate.getDeliveryPointOfService() != null) {
            // So now work out what the maximum allowed to be added is (note that
            // this may be negative!)
            final long actualAllowedQuantityChange = getAllowedCartAdjustmentForProduct(cartModel, entryToUpdate.getProduct(), quantityToAdd,
                    entryToUpdate.getDeliveryPointOfService());
            // Now do the actual cartModification
            modification = modifyEntry(cartModel, entryToUpdate, actualAllowedQuantityChange, newQuantity, maxOrderQuantity, parameters.isRecalculate());
            return modification;
        } else {
            final long actualAllowedQuantityChange = getAllowedCartAdjustmentForProduct(cartModel, entryToUpdate.getProduct(), quantityToAdd, null);
            modification = modifyEntry(cartModel, entryToUpdate, actualAllowedQuantityChange, newQuantity, maxOrderQuantity, parameters.isRecalculate());
            afterUpdateCartEntry(parameters, modification);
            return modification;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.hybris.platform.commerceservices.order.impl.DefaultCommerceUpdateCartEntryStrategy#modifyEntry(de.hybris.platform.core.model.order
     * .CartModel, de.hybris.platform.core.model.order.AbstractOrderEntryModel, long, long, java.lang.Integer)
     */
    @Override
    protected CommerceCartModification modifyEntry(final CartModel cartModel, final AbstractOrderEntryModel entryToUpdate,
            final long actualAllowedQuantityChange, final long newQuantity, final Integer maxOrderQuantity) {
        return modifyEntry(cartModel, entryToUpdate, actualAllowedQuantityChange, newQuantity, maxOrderQuantity, true);
    }

    /**
     * Update the target entry.
     * 
     * @param cartModel
     * @param entryToUpdate
     * @param actualAllowedQuantityChange
     * @param newQuantity
     * @param maxOrderQuantity
     * @param recalculate
     *            a flag indication whether we must recalculate the cart or not.
     * @return the #CommerceCartModification
     */
    protected CommerceCartModification modifyEntry(final CartModel cartModel, final AbstractOrderEntryModel entryToUpdate,
            final long actualAllowedQuantityChange, final long newQuantity, final Integer maxOrderQuantity, final boolean recalculate) {

        // Now work out how many that leaves us with on this entry
        final long entryNewQuantity = entryToUpdate.getQuantity().longValue() + actualAllowedQuantityChange;

        final ModelService modelService = getModelService();

        if (entryNewQuantity <= 0) {
            final CartEntryModel entry = new CartEntryModel() {
                @Override
                public Double getBasePrice() {
                    return null;
                }

                @Override
                public Double getTotalPrice() {
                    return null;
                }
            };
            entry.setProduct(entryToUpdate.getProduct());

            // The allowed new entry quantity is zero or negative
            // just remove the entry
            modelService.remove(entryToUpdate);
            modelService.refresh(cartModel);
            normalizeEntryNumbers(cartModel);
            if (recalculate) {
                getCommerceCartCalculationStrategy().calculateCart(cartModel);
            } else {
                cartModel.setCalculated(Boolean.FALSE);
                modelService.save(cartModel);
            }

            // Return an empty modification
            final CommerceCartModification modification = new CommerceCartModification();
            modification.setEntry(entry);
            modification.setQuantity(0);
            // We removed all the quantity from this row
            modification.setQuantityAdded(-entryToUpdate.getQuantity().longValue());

            if (newQuantity == 0) {
                modification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
            } else {
                modification.setStatusCode(CommerceCartModificationStatus.LOW_STOCK);
            }

            return modification;
        } else {
            // Adjust the entry quantity to the new value
            entryToUpdate.setQuantity(Long.valueOf(entryNewQuantity));
            modelService.save(entryToUpdate);
            modelService.refresh(cartModel);
            if (recalculate) {
                getCommerceCartCalculationStrategy().calculateCart(cartModel);
            } else {
                cartModel.setCalculated(Boolean.FALSE);
                modelService.save(cartModel);
            }
            modelService.refresh(entryToUpdate);

            // Return the modification data
            final CommerceCartModification modification = new CommerceCartModification();
            modification.setQuantityAdded(actualAllowedQuantityChange);
            modification.setEntry(entryToUpdate);
            modification.setQuantity(entryNewQuantity);

            if (isMaxOrderQuantitySet(maxOrderQuantity) && entryNewQuantity == maxOrderQuantity.longValue()) {
                modification.setStatusCode(CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED);
            } else if (newQuantity == entryNewQuantity) {
                modification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
            } else {
                modification.setStatusCode(CommerceCartModificationStatus.LOW_STOCK);
            }

            return modification;
        }
    }
}
