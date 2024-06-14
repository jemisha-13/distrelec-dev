/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.converters.response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.distrelec.webservice.if11.v3.*;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.inout.erp.PaymentOptionService;
import com.namics.distrelec.b2b.core.inout.erp.ShippingOptionService;
import com.namics.distrelec.b2b.core.inout.erp.exception.MoqConversionException;
import com.namics.distrelec.b2b.core.inout.erp.exception.MoqUnderflowException;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.core.model.cart.DeliveryCostModel;
import com.namics.distrelec.b2b.core.model.delivery.AbstractDistDeliveryModeModel;
import com.namics.distrelec.b2b.core.model.payment.AbstractDistPaymentModeModel;
import com.namics.distrelec.b2b.core.model.promotion.SapGeneratedVoucherModel;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.DeliveryModeService;
import de.hybris.platform.order.PaymentModeService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * {@code OrderCalculationResponsePopulator}
 *
 * @param <TARGET>
 * @author fbersani, Distrelec
 * @since Distrelec 2.0
 */
public class OrderCalculationResponsePopulator<TARGET extends AbstractOrderModel> implements Populator<OrderCalculationResponse, TARGET> {

    private static final Logger LOG = LogManager.getLogger(OrderCalculationResponsePopulator.class);

    private static final String HIDE_SHIPMENT_CONFIG = "shipment.hide";

    private ModelService modelService;

    private PaymentModeService paymentModeService;

    private DeliveryModeService deliveryModeService;

    private PaymentOptionService paymentOptionService;

    private ShippingOptionService shippingOptionService;

    private OrderCalculationResponseCommonMapping orderCalculationCommonMapping;

    private CMSSiteService cmsSiteService;

    private SessionService sessionService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private CommonI18NService commonI18NService;

    @Override
    public void populate(final OrderCalculationResponse source, final AbstractOrderModel target) throws ConversionException {
        try {

            // Newly generated vouchers
            addGeneratedVouchers(source, target);

            // map the order number
            if (StringUtils.isNotBlank(source.getOrderId())) {
                target.setErpOrderCode(source.getOrderId());
            }

            // // Map the order header

            // shipping and delivery cost
            target.setNetPaymentCost(source.getPaymentPrice() == null ? 0D : source.getPaymentPrice());
            target.setNetDeliveryCost(source.getShippingPrice() == null ? 0D : source.getShippingPrice());
            target.setDeliveryCost(target.getNetDeliveryCost());

            // map the totals
            target.setNetSubTotal(source.getSubtotal2());
            target.setTotalTax(source.getTax());
            target.setTotalPrice(source.getTotal());

            // map and save the order entries
            getOrderCalculationCommonMapping().fillOrderEntries(source.getOrderEntries(), target);

            // map and save the payment and delivery methods
            fillPaymentMethod(source, target);
            fillShippingMethod(source, target);

            // map the vouchers
            getOrderCalculationCommonMapping().fillVoucherInfo(source.getVouchers(), target);

            // map the discounts
            target.setTotalDiscounts(getOrderCalculationCommonMapping().getTotalDiscountsValue(source.getDiscounts()));

            target.setMovLimit(source.getMoValue());
            target.setMovCurrency(source.getMoCurrency());

            target.setMissingMov(source.getMissingMov());
            target.setCreditBlocked(source.getCreditBlock());

            if (null != source.getCurrency()) {
                populateCurrency(source, target);
            }
            getModelService().save(target);
            setCalculatedStatus(target);
        } catch (final CalculationException e) {
            LOG.error("{} Error during SAP order calculation. Order: {}", ErrorLogCode.ORDER_RELATED_ERROR.getCode(), target.getCode(), e);

            if (e instanceof MoqUnderflowException) {
                throw new MoqConversionException(e.getMessage(), e);
            }
            throw new ConversionException(e.getMessage(), e);
        }
    }

    protected void addGeneratedVouchers(final OrderCalculationResponse source, final AbstractOrderModel target) {
        if (source.getFreeVoucherPromotion() != null && source.getFreeVoucherPromotion().getCode() != null) {
            final SapGeneratedVoucherModel voucher = getModelService().create(SapGeneratedVoucherModel.class);
            voucher.setCode(source.getFreeVoucherPromotion().getCode());
            voucher.setValue(source.getFreeVoucherPromotion().getValue());
            voucher.setValidFrom(SoapConversionHelper.convertDate(source.getFreeVoucherPromotion().getValidFrom()));
            voucher.setValidUntil(SoapConversionHelper.convertDate(source.getFreeVoucherPromotion().getValidUntil()));
            target.setGeneratedVoucher(voucher);
        }
    }

    private void populateCurrency(final OrderCalculationResponse source, final AbstractOrderModel target) {
        try {
            target.setCurrency(getCommonI18NService().getCurrency(source.getCurrency()));
        } catch (Exception ex) {
            LOG.error("Currency with ISO code:{} not found during order calculation", source.getCurrency());
        }

    }

    /**
     * Fill the payment methods
     *
     * @param source
     * @param target
     */
    private void fillPaymentMethod(final OrderCalculationResponse source, final AbstractOrderModel target) {

        // fill valid payment methods for the customer
        final List<AbstractDistPaymentModeModel> validPaymentModes = new ArrayList<AbstractDistPaymentModeModel>();

        for (final PaymentMethodResponse paymentMethod : source.getPaymentMethods()) {
            if (BooleanUtils.isTrue(paymentMethod.isSelectable())) {
                final String erpPaymentCode = paymentMethod.getCode();
                final AbstractDistPaymentModeModel paymentMode = getPaymentModeForErpCode(erpPaymentCode);
                if (paymentMode != null) {
                    if (target.getPaymentMode() == null && paymentMethod.isSelected()) {
                        // set the selected value from SAP only if not yet present in the cart (DISTRELEC-3964)
                        target.setPaymentMode(paymentMode);
                    }
                    validPaymentModes.add(paymentMode);
                }

                // DISTRELEC-6854 Add the payment method selectable flag to the session
                getSessionService().setAttribute("paymentMode#" + paymentMethod.getCode(),
                                                 paymentMethod.isSelectable() != null ? paymentMethod.isSelectable() : Boolean.FALSE);
                // DISTRELEC-11216 Add the payment cost
                if (BooleanUtils.isTrue(paymentMethod.isSelectable())) {
                    getSessionService().setAttribute("paymentCost#" + paymentMethod.getCode(), paymentMethod.getPrice());
                }
            }
        }
        target.setValidPaymentModes(validPaymentModes);
    }

    /**
     * Fill the shipping methods
     *
     * @param orderCalculationResponse
     * @param abstractOrder
     */
    private void fillShippingMethod(final OrderCalculationResponse orderCalculationResponse, final AbstractOrderModel abstractOrder) {
        // fill valid shipping methods for the customer
        final List<AbstractDistDeliveryModeModel> validDeliveryModes = new ArrayList<AbstractDistDeliveryModeModel>();

        boolean selectedValid = false;
        Set<DeliveryCostModel> deliveryCosts = new HashSet<>();
        for (final ShippingMethodResponse shippingMethod : orderCalculationResponse.getShippingMethods()) {
            if (BooleanUtils.isTrue(shippingMethod.isSelectable())) {
                final String erpShippingCode = shippingMethod.getCode() == null ? null : shippingMethod.getCode().value();

                final AbstractDistDeliveryModeModel deliveryMode = getDeliveryModeForErpCode(erpShippingCode);
                if (deliveryMode != null) {
                    if (shippingMethod.isSelected()) {
                        abstractOrder.setDeliveryMode(deliveryMode);
                        selectedValid = true;

                        if (DistConstants.Shipping.METHOD_PICKUP.equals(deliveryMode.getCode()) && abstractOrder.getPickupLocation() == null) {
                            final CMSSiteModel cmsSite = abstractOrder.getSite() != null ? (CMSSiteModel) abstractOrder.getSite()
                                                                                         : getCmsSiteService().getCurrentSite();

                            final Set<WarehouseModel> pickUpLocations = cmsSite.getCheckoutPickupWarehouses();
                            if (CollectionUtils.isNotEmpty(pickUpLocations)) {
                                abstractOrder.setPickupLocation(pickUpLocations.stream().findFirst().get());
                            } else {
                                LOG.error("{} Pick up is the selected delivery mode, but there is no pickup location configured for this site!",
                                          ErrorLogCode.DELIVERY_MODE_ERROR.getCode());
                            }
                        }
                    }

                    // DISTRELEC-6854 Add the shipping method selectable flag to the session
                    getSessionService().setAttribute("deliveryMode#" + shippingMethod.getCode().value(),
                                                     shippingMethod.isSelectable() != null ? shippingMethod.isSelectable() : Boolean.FALSE);

                    // DISTRELEC-7382 removing the if that is preventing pickup to be a vaild shipping method
                    // TODO: Find a good reason to have this if statement and avoid the pickup to be a valid shipping method
                    // DISTRELEC-7023 Add all delivery modes, except pick up. Pick up is not a selectable delivery mode on the choose
                    // delivery mode page.
                    // if (!DistConstants.Shipping.METHOD_PICKUP.equals(deliveryMode.getCode())) {
                    validDeliveryModes.add(deliveryMode);
                    // }

                    // DISTRELEC-11216 Add the shipping cost
                    if (BooleanUtils.isTrue(shippingMethod.isSelectable())) {
                        DeliveryCostModel deliveryCost = new DeliveryCostModel();
                        deliveryCost.setDeliveryModeCode(deliveryMode.getCode());
                        deliveryCost.setCost(shippingMethod.getPrice());
                        deliveryCosts.add(deliveryCost);
                    }
                }
            }
        }

        modelService.saveAll(deliveryCosts);
        abstractOrder.setDeliveryCosts(deliveryCosts);

        if (!selectedValid) {
            abstractOrder.setDeliveryMode(null);
        }

        // DISTRELEC-7024 Filter additinally the delivery modes in case the customer has choosed a specific delivery date
        if (abstractOrder.getRequestedDeliveryDate() == null) {
            abstractOrder.setOriginalErpDeliveryDate(getMinimumDeliveryDate(orderCalculationResponse));
        }

        // DISTRELEC-19198
        List<AbstractDistDeliveryModeModel> filteredDeliveryModes = validDeliveryModes.stream()
                                                                                      .filter(deliveryMode -> hideShippingOptions(deliveryMode, abstractOrder))
                                                                                      .collect(Collectors.toList());

        // If hiding siping removes all of them then use unfiltered list
        if (!filteredDeliveryModes.isEmpty() && !containsOnlyPickupOption(filteredDeliveryModes)) {
            abstractOrder.setValidDeliveryModes(filteredDeliveryModes);
        } else {
            abstractOrder.setValidDeliveryModes(validDeliveryModes);
        }
    }

    private boolean containsOnlyPickupOption(List<AbstractDistDeliveryModeModel> shippingOptions) {
        return shippingOptions.size() == 1 && shippingOptions.get(0).getCode().equals(DistConstants.Shipping.METHOD_PICKUP);
    }

    private boolean hideShippingOptions(AbstractDistDeliveryModeModel deliveryMode, AbstractOrderModel cart) {
        String configKey = HIDE_SHIPMENT_CONFIG + "." + cmsSiteService.getCurrentSite().getUid();
        String[] hideIfPossible = configurationService.getConfiguration().getString(configKey, "").split(",");
        if (hideIfPossible != null && hideIfPossible.length > 0 && !isDangerousGoodsPresent(cart)) {
            return !Arrays.asList(hideIfPossible)
                          .contains(deliveryMode.getCode());
        }
        return Boolean.FALSE;
    }

    private Date getMinimumDeliveryDate(final OrderCalculationResponse orderCalculationResponse) {
        Date result = DateUtils.addDays(new Date(), 60);

        // Loop over all order entries
        for (final OrderEntryResponse entry : orderCalculationResponse.getOrderEntries()) {
            final List<OrderEntryAvailabilityInfoResponse> availabilityInfos = entry.getAvailabilityInfo();
            // loop all availability infos (from ERP)
            for (final OrderEntryAvailabilityInfoResponse availabilityInfo : availabilityInfos) {
                // identify always the smaller one (see:
                // http://jira.distrelec.com/browse/DISTRELEC-7024?focusedCommentId=41602&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-41602)
                if (availabilityInfo.getAvailableQuantity() > 0
                        && SoapConversionHelper.convertDate(availabilityInfo.getEstimatedDeliveryDate()).before(result)) {
                    result = SoapConversionHelper.convertDate(availabilityInfo.getEstimatedDeliveryDate());
                }
            }
        }

        return result;
    }

    private AbstractDistPaymentModeModel getPaymentModeForErpCode(final String erpCode) {
        try {
            return StringUtils.isNotBlank(erpCode) ? getPaymentOptionService().getAbstractDistPaymentModeForErpPaymentModeCode(erpCode) : null;
        } catch (final ModelNotFoundException e) {
            LOG.warn("Can not find hybris payment mode for SAP payment code " + erpCode);
        }
        return null;
    }

    private AbstractDistDeliveryModeModel getDeliveryModeForErpCode(final String erpCode) {
        try {
            return StringUtils.isNotBlank(erpCode) ? getShippingOptionService().getAbstractDistDeliveryModeForDistShippingMethodCode(erpCode) : null;
        } catch (final ModelNotFoundException mnfex) {
            LOG.warn("Can not find hybris delivery mode for SAP delivery code " + erpCode);
        }
        return null;
    }

    private void setCalculatedStatus(final AbstractOrderModel order) {
        order.setCalculated(Boolean.TRUE);
        getModelService().save(order);
        getModelService().refresh(order);
        final List<AbstractOrderEntryModel> entries = order.getEntries();
        if (entries != null && !entries.isEmpty()) {
            entries.forEach(entry -> entry.setCalculated(Boolean.TRUE));
            getModelService().saveAll(entries);
        }
    }

    private Boolean isDangerousGoodsPresent(final AbstractOrderModel order) {
        final List<AbstractOrderEntryModel> entries = order.getEntries();
        for (AbstractOrderEntryModel orderEntry : entries) {
            final ProductModel productModel = orderEntry.getProduct();
            if (productModel.getTransportGroup() != null && productModel.getTransportGroup().isDangerous()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public PaymentModeService getPaymentModeService() {
        return paymentModeService;
    }

    @Required
    public void setPaymentModeService(final PaymentModeService paymentModeService) {
        this.paymentModeService = paymentModeService;
    }

    public DeliveryModeService getDeliveryModeService() {
        return deliveryModeService;
    }

    @Required
    public void setDeliveryModeService(final DeliveryModeService deliveryModeService) {
        this.deliveryModeService = deliveryModeService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public PaymentOptionService getPaymentOptionService() {
        return paymentOptionService;
    }

    @Required
    public void setPaymentOptionService(final PaymentOptionService paymentOptionService) {
        this.paymentOptionService = paymentOptionService;
    }

    public ShippingOptionService getShippingOptionService() {
        return shippingOptionService;
    }

    @Required
    public void setShippingOptionService(final ShippingOptionService shippingOptionService) {
        this.shippingOptionService = shippingOptionService;
    }

    public OrderCalculationResponseCommonMapping getOrderCalculationCommonMapping() {
        return orderCalculationCommonMapping;
    }

    @Required
    public void setOrderCalculationCommonMapping(final OrderCalculationResponseCommonMapping orderCalculationCommonMapping) {
        this.orderCalculationCommonMapping = orderCalculationCommonMapping;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    @Required
    public void setCmsSiteService(CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    @Required
    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

}
