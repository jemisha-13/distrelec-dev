/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.process.payment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.process.PaymentNotifyProcessModel;
import com.namics.distrelec.b2b.core.service.order.DistCommerceCheckoutService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.task.RetryLaterException;

/**
 * {@code PaymentNotifyPlaceOrder}
 * <p>
 * This decision action places the order after a waiting period of X minutes.
 * </p>
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.4
 */
public class PaymentNotifyPlaceOrder extends AbstractPaymentNotifyDecisionAction {

    private static final Logger LOG = LogManager.getLogger(AbstractPaymentNotifyDecisionAction.class);

    @Autowired
    private DistCommerceCheckoutService b2bCommerceCheckoutService;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private BaseSiteService baseSiteService;
    @Autowired
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.process.payment.AbstractPaymentNotifyDecisionAction#getActionName()
     */
    @Override
    public String getActionName() {
        return "PaymentNotifyPlaceOrder";
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.processengine.action.AbstractSimpleDecisionAction#executeAction(de.hybris.platform.processengine.model.
     * BusinessProcessModel)
     */
    @Override
    public Transition executeAction(final PaymentNotifyProcessModel process) throws RetryLaterException, Exception {
        try {
            final CartModel cartModel = getCommerceCartService().getCartForCodeAndUser(process.getCartCode(), process.getUser());
            // No cart found means that either the order was already placed or there is no cart with that code for the given user.
            if (cartModel != null) {
                // 1. setup the session first
                setupSession(cartModel);
                LOG.info("Going to place order on notify request for cart number " + process.getCartCode());
                // 2. Before placing the order
                beforePlaceOrder(cartModel,process);
                // 3. Placing the order
                final OrderModel orderModel = placeOrder(cartModel);
                // 4. After placing the order
                afterPlaceOrder(cartModel, orderModel);
                LOG.info("Finished with place order on notify request for cart number " + process.getCartCode());
            }
        } catch (final Exception exp) {
            LOG.error("An error occured while processing the action " + getActionName(), exp);
            process.setEndMessage("An error occured while processing the action" + getActionName());
            getModelService().save(process);
            return Transition.NOK;
        }
        return Transition.OK;
    }

    protected void setupSession(final CartModel cart) {
        setupSession();
        getSessionService().setAttribute("user", cart.getUser());
        // Set base site
        getBaseSiteService().setCurrentBaseSite(cart.getSite(), true);
        // Set site channel
        getSessionService().setAttribute("channel", cart.getStore().getChannel());
        // update "session.branch"
        getB2bUnitService().updateBranchInSession(getSessionService().getCurrentSession(), cart.getUser());
    }

    /**
     * Ensure we use a proper JaloSession having no rubbish (like httpSessionID) attached during last usage. <br/>
     * Important: Disable timeout to ensure session does not expire.
     * 
     * @return a proper JaloSession
     */
    private JaloSession setupSession() {
        if (!Registry.hasCurrentTenant()) {
            Registry.activateMasterTenant();
        }

        if (JaloSession.hasCurrentSession()) {
            JaloSession currentSession = JaloSession.getCurrentSession();
            LOG.info("Current session exists: sessionID [" + currentSession.getSessionID() + "], httpSessionId [" + currentSession.getHttpSessionId()
                    + "], user [" + currentSession.getUser() + "]. Deactivate current session and create a new one.");
            JaloSession.deactivate();
        }

        JaloSession newSession = JaloSession.getCurrentSession();
        newSession.setTimeout(-1);
        LOG.info("New session: sessionID [" + newSession.getSessionID() + "], httpSessionId [" + newSession.getHttpSessionId() + "], user ["
                + newSession.getUser() + "]. Deactivate current session and create a new one.");

        return newSession;
    }

    /**
     * Actions to make before placing the order for the given cart.
     * 
     * @param cartModel
     *            the target cart
     * @see de.hybris.platform.b2bacceleratorfacades.order.impl.DefaultB2BCheckoutFacade#beforePlaceOrder(de.hybris.platform.core.model.order.CartModel)
     */
    protected void beforePlaceOrder(final CartModel cartModel,final PaymentNotifyProcessModel process) {
        cartModel.setStatus((!cartModel.getB2bcomments().isEmpty()) ? OrderStatus.PENDING_QUOTE : OrderStatus.CREATED);
        cartModel.setPaymentMode(process.getPaymentMode());
    }

    /**
     * Placing the order for the given cart.
     * 
     * @param cartModel
     *            the source cart
     * @return the placed order
     * @throws InvalidCartException
     */
    protected OrderModel placeOrder(final CartModel cartModel) throws InvalidCartException {
        final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(cartModel, true, SalesApplication.WEB);
        return getB2bCommerceCheckoutService().placeOrder(parameter).getOrder();
    }

    /**
     * Creates the {@link CommerceCheckoutParameter} to be used for placing the order.
     * 
     * @param cart
     *            the source cart
     * @param enableHooks
     * @param salesApp
     * @return a new instance of {@link CommerceCheckoutParameter}
     */
    protected CommerceCheckoutParameter createCommerceCheckoutParameter(final CartModel cart, final boolean enableHooks, final SalesApplication salesApp) {
        final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
        parameter.setSalesApplication(salesApp);
        parameter.setEnableHooks(enableHooks);
        parameter.setCart(cart);
        return parameter;
    }

    /**
     * Actions to make after placing the order for both the source cart and the resulted order.
     * 
     * @param cartModel
     *            the source cart.
     * @param orderModel
     *            the placed order.
     */
    protected void afterPlaceOrder(final CartModel cartModel, final OrderModel orderModel) {
        if (orderModel != null) {
            if (cartModel != null) {
                getModelService().remove(cartModel);
            }
            getModelService().refresh(orderModel);
        }
    }

    // Getters & Setters

    public DistCommerceCheckoutService getB2bCommerceCheckoutService() {
        return b2bCommerceCheckoutService;
    }

    public void setB2bCommerceCheckoutService(final DistCommerceCheckoutService b2bCommerceCheckoutService) {
        this.b2bCommerceCheckoutService = b2bCommerceCheckoutService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService() {
        return b2bUnitService;
    }

    public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService) {
        this.b2bUnitService = b2bUnitService;
    }
}
