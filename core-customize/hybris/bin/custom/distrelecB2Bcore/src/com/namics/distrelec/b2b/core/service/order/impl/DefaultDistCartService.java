/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.order.impl;

import static java.util.Objects.nonNull;

import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.enums.DistCreditBlockEnum;
import com.namics.distrelec.b2b.core.service.order.DistCartService;
import com.namics.distrelec.b2b.core.service.session.DistSession;

import de.hybris.platform.b2b.services.impl.DefaultB2BCartService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * {@code DefaultDistCartService}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.7
 */
public class DefaultDistCartService extends DefaultB2BCartService implements DistCartService {

    private static final Logger LOG = LogManager.getLogger(DefaultDistCartService.class);

    @Override
    public void removeSessionCart(final boolean force) {
        if (hasSessionCart()) {
            ((DistSession) getSessionService().getCurrentSession()).removeAttribute(SESSION_CART_PARAMETER_NAME, force);
        }
    }

    @Override
    public AbstractOrderEntryModel replace(final ProductModel source, final ProductModel replacement, final int entryNumber, final long quantity) {
        if (hasSessionCart() && CollectionUtils.isNotEmpty(getSessionCart().getEntries())) {
            AbstractOrderEntryModel targetEntry = getSessionCart().getEntries()
                                                                  .stream()
                                                                  .filter(Objects::nonNull)
                                                                  .filter(entry -> areProductCodeAndEntryNumberMatching(source, entryNumber, entry))
                                                                  .findFirst().orElse(null);

            if (targetEntry != null) {
                targetEntry.setProduct(replacement);
                if (quantity > 0) {
                    targetEntry.setQuantity(quantity);
                }
                getModelService().save(targetEntry);
                return targetEntry;
            }
        }
        return null;
    }

    private boolean areProductCodeAndEntryNumberMatching(ProductModel product, int entryNumber, AbstractOrderEntryModel entry) {
        return entry.getEntryNumber() == entryNumber && StringUtils.equals(entry.getProduct().getCode(), product.getCode());
    }

    @Override
    public void removeBackupCart() {
        ((DistSession) getSessionService().getCurrentSession()).removeBackupCart();
    }

    @Override
    public boolean isCreditBlocked(CartModel cart) {
        if (StringUtils.isNotEmpty(cart.getCreditBlocked())) {
            return DistCreditBlockEnum.B.getValue().equalsIgnoreCase(cart.getCreditBlocked())
                    || DistCreditBlockEnum.C.getValue().equalsIgnoreCase(cart.getCreditBlocked());
        }
        return Boolean.FALSE;
    }

    @Override
    public boolean isWaldom(CartModel cart) {
        return cart.getEntries().stream()
                   .anyMatch(entry -> StringUtils.equals(DistConstants.Product.WALDOM, entry.getMview()));
    }

    @Override
    public boolean isRs(CartModel cart) {
        return cart.getEntries().stream()
                   .anyMatch(entry -> StringUtils.equals(DistConstants.Product.RS, entry.getMview()));
    }

    @Override
    public boolean doesQuoteAlreadyExistInCart(String quotationId) {
        CartModel cart = getSessionCart();
        if (nonNull(cart) && CollectionUtils.isNotEmpty(cart.getEntries())) {
            return cart.getEntries().stream()
                       .anyMatch(entry -> BooleanUtils.isTrue(entry.getQuotation())
                               && StringUtils.equals(entry.getQuotationId(), quotationId));
        }
        return false;
    }

    @Override
    public void setSessionCart(final CartModel cart) {
        if (BooleanUtils.isTrue(cart.isGhostOrder())) {
            LOG.error(new ParameterizedMessage("trying to set ghost cart {} as session cart", cart.getCode()), new Throwable());
        }
        super.setSessionCart(cart);
    }

    @Override
    public CartModel getSessionCart() {
        final CartModel sessionCart = super.getSessionCart();
        if (BooleanUtils.isTrue(sessionCart.isGhostOrder())) {
            LOG.error(new ParameterizedMessage("session cart {} is ghost", sessionCart.getCode()), new Throwable());
        }
        return sessionCart;
    }

}
