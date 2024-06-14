package com.namics.distrelec.b2b.core.service.order.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.namics.distrelec.b2b.core.security.IpAddressService;
import com.namics.distrelec.b2b.core.service.user.DistUserService;
import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.enums.DistChannel;
import com.namics.distrelec.b2b.core.service.order.DistOrderService;
import com.namics.distrelec.b2b.core.service.order.strategies.DistCommercePlaceOrderStrategy;

import de.hybris.platform.b2b.strategies.BusinessProcessStrategy;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.commerceservices.order.impl.DefaultCommercePlaceOrderStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.session.SessionService;

public class DefaultDistCommercePlaceOrderStrategy extends DefaultCommercePlaceOrderStrategy
                                                   implements DistCommercePlaceOrderStrategy {

    private static final Logger LOG = LogManager.getLogger(DefaultDistCommercePlaceOrderStrategy.class);

    private List<BusinessProcessStrategy> businessProcessStrategies;

    private SessionService sessionService;

    @Autowired
    private IpAddressService ipAddressService;

    /*
     * (non-Javadoc)
     * @see de.hybris.platform.commerceservices.order.impl.
     * DefaultCommercePlaceOrderStrategy#placeOrder(de.hybris.platform.
     * commerceservices. service.data.CommerceCheckoutParameter)
     */
    @Override
    public CommerceOrderResult placeOrder(final CommerceCheckoutParameter parameter) throws InvalidCartException {
        validateParameterNotNull(parameter, "Cart model cannot be null");
        validateParameterNotNull(parameter.getCart(), "Cart model cannot be null");
        final CartModel cartModel = parameter.getCart();
        final SalesApplication salesApplication = parameter.getSalesApplication();

        if (!Boolean.TRUE.equals(cartModel.getCalculated())) {
            throw new IllegalArgumentException("Cart model must be calculated");
        }
        final CustomerModel customer = (CustomerModel) cartModel.getUser();
        validateParameterNotNull(customer, "Customer model cannot be null");

        final CommerceOrderResult result = new CommerceOrderResult();

        normalizeEntriesNumbers(cartModel);

        // remove the existing order model in case of existing open order
        // checkout
        if (cartModel.isOpenOrder() && StringUtils.isNotEmpty(cartModel.getErpOpenOrderCode())) {
            final OrderModel existingOrder = getOrderService().getOrderForErpCode(cartModel.getErpOpenOrderCode());
            if (existingOrder != null) {
                LOG.debug("Removing the order with code: {}, ERP Code: {}", existingOrder.getCode(),
                          existingOrder.getErpOrderCode());
                LOG.debug("The new order will be generated based on cart: {}", cartModel.getCode());
                getModelService().remove(existingOrder);
            }
        }

        final OrderModel orderModel = getOrderService().createOrderFromCart(cartModel);
        if (orderModel != null) {
            // Store the current site and store on the order
            orderModel.setSite(getBaseSiteService().getCurrentBaseSite());
            orderModel.setStore(getBaseStoreService().getCurrentBaseStore());
            orderModel.setDate(new Date());
            orderModel.setClientID(cartModel.getClientID());
            orderModel.setIpAddress(ipAddressService.getClientIpAddress());
            if (salesApplication != null) {
                orderModel.setSalesApplication(salesApplication);
            }
            Boolean isStorefrontRequest = getSessionService().getAttribute(DistConstants.Session.IS_STOREFRONT_REQUEST);
            orderModel.setDistChannel(
                                      Boolean.TRUE.equals(isStorefrontRequest) ? DistChannel.STOREFRONT : DistChannel.HEADLESS);
            getModelService().saveAll(customer, orderModel);

            // clear the promotionResults that where cloned from cart
            // PromotionService.transferPromotionsToOrder will copy them over
            // bellow.
            orderModel.setAllPromotionResults(Collections.emptySet());
            orderModel.setCompleteDelivery(cartModel.getCompleteDelivery());

            if (cartModel.getPaymentInfo() != null && cartModel.getPaymentInfo().getBillingAddress() != null) {
                orderModel.setPaymentAddress(cartModel.getPaymentInfo().getBillingAddress());
                orderModel.getPaymentInfo().setBillingAddress(cartModel.getPaymentInfo().getBillingAddress());
                getModelService().save(orderModel.getPaymentInfo());
            }
            getModelService().save(orderModel);

            // Transfer promotions to the order
            getPromotionsService().transferPromotionsToOrder(cartModel, orderModel, false);

            getModelService().refresh(orderModel);
            getModelService().refresh(customer);

            // Start the business process for the order approval. If no approval
            // is need the process will go directly to the fulfillment
            startApprovalProcess(orderModel, cartModel);
            getOrderService().submitOrder(orderModel);
        }

        result.setOrder(orderModel);

        return result;
    }

    private void startApprovalProcess(final OrderModel orderFromCart, final CartModel cart) {
        LOG.debug("Post processing a b2b order {} created from cart {}.", orderFromCart, cart);

        createB2BBusinessProcess(orderFromCart);
    }

    protected void normalizeEntriesNumbers(final CartModel cartModel) {
        // remove the confirmed order entries in case of open orders
        if (cartModel.isOpenOrder()) {
            cartModel.setEntries(cartModel.getNewOrderEntries());
            getModelService().save(cartModel);
        }
        final List<AbstractOrderEntryModel> allEntries = new ArrayList<>(cartModel.getEntries());
        Collections.sort(allEntries,
                         (orderEntry1, orderEntry2) -> orderEntry1.getEntryNumber().compareTo(orderEntry2.getEntryNumber()));

        for (int i = 0; i < allEntries.size(); i++) {
            final AbstractOrderEntryModel orderEntryModel = allEntries.get(i);
            orderEntryModel.setEntryNumber(Integer.valueOf(i));
            getModelService().save(orderEntryModel);
        }
    }

    private void createB2BBusinessProcess(final OrderModel order) {
        final OrderStatus status = order.getStatus();
        Assert.notNull(status, "Order status should have been set for order " + order.getCode());

        final BusinessProcessStrategy businessProcessStrategy = getBusinessProcessStrategy(status.getCode());
        Assert.notNull(businessProcessStrategy,
                       String.format("The strategy for creating a business process with name %s should have been created!",
                                     status.getCode()));
        businessProcessStrategy.createB2BBusinessProcess(order);
    }

    public BusinessProcessStrategy getBusinessProcessStrategy(final String code) {
        return (BusinessProcessStrategy) CollectionUtils.find(this.businessProcessStrategies,
                                                              new BeanPropertyValueEqualsPredicate("processName", code));
    }

    @Override
    protected DistOrderService getOrderService() {
        return (DistOrderService) super.getOrderService();
    }

    public List<BusinessProcessStrategy> getBusinessProcessStrategies() {
        return businessProcessStrategies;
    }

    @Required
    public void setBusinessProcessStrategies(final List<BusinessProcessStrategy> businessProcessStrategies) {
        this.businessProcessStrategies = businessProcessStrategies;
    }

    /**
     * @return the sessionService
     */
    public SessionService getSessionService() {
        return sessionService;
    }

    /**
     * @param sessionService
     *            the sessionService to set
     */
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

}
