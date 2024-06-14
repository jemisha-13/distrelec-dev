/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.jalo.session;

import de.hybris.platform.commerceservices.jalo.CommerceJaloSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.order.Cart;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * {@code DistJaloSession}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.2
 */
public class DistJaloSession extends CommerceJaloSession {

    private static final Logger LOG = LoggerFactory.getLogger(DistJaloSession.class);
    public static final String DIST_BACKUP_CART = "DIST_BACKUP_CART";
    /**
     * A boolean flag indicating whether we should remove the cart or not.
     */
    private volatile boolean removeCart = true;
    private volatile transient CartFactory cartFactory;
    private volatile transient ModelService modelService;

    /**
     * Removes the specified attribute from the attribute map of the current session context.
     * 
     * @param name
     *            of the attribute, which should be removed
     * @param force
     *            if {@code true}, then it return the {@code removeAttribute(name)}, otherwise
     *            {@code getSessionContext().removeAttribute(name)} if the name is not equal to "cart"
     * @return the value of the removed attribute
     * @see #removeAttribute(String)
     * @see de.hybris.platform.jalo.SessionContext#removeAttribute(String)
     */
    public Object removeAttribute(final String name, final boolean force) {
        if (force) {
            return removeAttribute(name);
        } else {
            if (CART.equalsIgnoreCase(name)) {
                final Cart old = getAttachedCart();
                setAttachedCart(null);
                return old;
            } else {
                return getSessionContext().removeAttribute(name);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.jalo.JaloSession#removeCart()
     */
    @Override
    public void removeCart() {
        if (removeCart) {
            // Remove the cart from the session and from the database.
            super.removeCart();
        } else {
            // Remove the cart only from the session
            setAttachedCart(null);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.jalo.JaloSession#createCart()
     */
    @Override
    protected Cart createCart() {
        return getModelService().getSource(getCartFactory().createCart());
    }

    /**
     * This is a customization of the {@link #getCart()}. This method checks if there is a session cart already. If yes, then returns it
     * otherwise returns {@code null}.
     * 
     * @return the session cart if there is one, {@code null} otherwise.
     * @see #getCart()
     */
    public Cart getCartIfPresent() {
        return hasCart() ? getCart() : null;
    }

    /**
     * Sets the boolean flag {@code removeCart} to {@code true} if the current user is anonymous customer, otherwise {@code false}
     */
    private void beforeCleanup() {
        final User user = getUser();
        if (user != null) {
            removeCart = user.isAnonymousCustomer();
        }
    }

    /**
     * delete any backup cart from the database.
     */
    public void removeBackupCart() {
        if (getAttribute(DIST_BACKUP_CART) instanceof CartModel) {
            try {
                // Required to extract and cast the object from the session first to avoid
                // java.lang.ClassCastException: de.hybris.platform.core.model.order.CartModel cannot be cast to
                // de.hybris.platform.core.PK
                final CartModel old_cart = (CartModel) getAttribute(DIST_BACKUP_CART);
                getModelService().remove(old_cart);
                setAttribute(DIST_BACKUP_CART, null);
            } catch (final Exception exp) {
                LOG.error("Error occur while removing the emptied cart!", exp);
            }
        }
    }

    /**
     * Reset the boolean flag {@code removeCart} to {@code true}
     */
    private void afterCleanup() {
        removeCart = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.jalo.JaloSession#cleanup()
     */
    @Override
    protected void cleanup() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Cleaning up the session {}", getSessionID());
        }
        // Before cleanup
        beforeCleanup();
        // Remove backup cart
        removeBackupCart();
        // Call original cleanup method
        super.cleanup();
        // After cleanup
        afterCleanup();
    }

    public CartFactory getCartFactory() {
        return cartFactory;
    }

    public void setCartFactory(final CartFactory cartFactory) {
        this.cartFactory = cartFactory;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }
}
