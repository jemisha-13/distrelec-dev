/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order.strategies.impl;

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.model.payment.HopSupportingPaymentTransactionEntryModel;
import com.namics.distrelec.b2b.core.service.order.data.PaymentTransactionData;
import com.namics.distrelec.b2b.core.service.order.strategies.DistPaymentParamsStrategy;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.util.DistUtils;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.namics.distrelec.b2b.core.service.order.util.FindOrderForCodeUtil.getAbstractOrderByCode;

public abstract class AbstractDistPaymentParamStrategy implements DistPaymentParamsStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDistPaymentParamStrategy.class);
    private static final int MAX_REF_LENGTH = 20;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private CartService cartService;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    protected abstract String getPaymentProvider();

    protected abstract PaymentTransactionData createPaymentTransactionData(final Map<String, String> paymentParams, final String encryptedTransaction);

    protected void addPaymentTransaction(final String merchantId, final CartModel cartModel, final String refId, String paymentParams) {
        final PaymentTransactionData paymentTransactionData = createPaymentTransactionData(merchantId, cartModel, refId, paymentParams);
        final PaymentTransactionModel paymentTransaction = createPaymentTransaction(paymentTransactionData);
        createPaymentTransactionEntry(paymentTransaction, paymentTransactionData);
    }

    /* Creates paymentTransactionData for first request to payment provider */
    protected PaymentTransactionData createPaymentTransactionData(final String merchantId, final CartModel cartModel, final String refId,
                                                                  String paymentParams) {
        final PaymentTransactionData paymentTransactionData = new PaymentTransactionData();
        paymentTransactionData.setMerchantId(merchantId);
        paymentTransactionData.setTransId(cartModel.getUser().getPk().toString());
        paymentTransactionData.setRefNr(refId);
        paymentTransactionData.setReqId(getOrderCodeToTransmit(cartModel));
        // paymentTransactionData.setReqId(cartModel.getErpOrderCode());
        paymentTransactionData
                .setAmount(new BigDecimal(DistUtils.getSmallestUnitOfCurrency(cartModel.getCurrency(), cartModel.getTotalPrice())).stripTrailingZeros());
        paymentTransactionData.setCurrency(cartModel.getCurrency().getIsocode());
        paymentTransactionData.setPaymentMethod(cartModel.getPaymentMode().getCode());
        paymentTransactionData.setPaymentProvider(getPaymentProvider());
        paymentTransactionData.setType(PaymentTransactionType.PAYMENT_REQUEST);
        paymentTransactionData.setEncryptedTransaction(paymentParams);

        return paymentTransactionData;
    }

    /**
     * @return current card currency
     */
    @Override
    public String getCurrentCartCurrencyCode() {
        return getCartService().getSessionCart().getCurrency().getIsocode().toUpperCase();
    }

    /* Creates paymentTransactionModel for current cart/order */
    protected PaymentTransactionModel createPaymentTransaction(final PaymentTransactionData paymentTransactionData) {
        final AbstractOrderModel abstractOrderModel = getAbstractOrderForCode(paymentTransactionData.getReqId());
        if (abstractOrderModel == null) {
            LOG.error("{} {} No AbstractOrder found for userId {} and code {}!", ErrorLogCode.PAYMENT_ERROR, ErrorSource.HYBRIS,
                    paymentTransactionData.getTransId(), paymentTransactionData.getRefNr());
            throw new IllegalArgumentException("Create paymentTransaction not possible because of mising cartModel.");
        } else {
            final PaymentTransactionModel paymentTransaction = modelService.create(PaymentTransactionModel.class);

            paymentTransaction.setCode(getGeneratedPaymentTransactionCode(abstractOrderModel));
            paymentTransaction.setInfo(abstractOrderModel.getPaymentInfo());
            paymentTransaction.setCurrency(abstractOrderModel.getCurrency());
            paymentTransaction.setOrder(abstractOrderModel);
            paymentTransaction.setPaymentProvider(paymentTransactionData.getPaymentProvider());
            paymentTransaction.setPlannedAmount(DistUtils.getBiggestUnitOfCurrency(abstractOrderModel.getCurrency(), paymentTransactionData.getAmount()));
            paymentTransaction.setPaymentMethod(paymentTransactionData.getPaymentMethod());
            paymentTransaction.setReqId(paymentTransactionData.getReqId());
            paymentTransaction.setRequestToken(paymentTransactionData.getRefNr());

            getModelService().save(paymentTransaction);
            return paymentTransaction;
        }
    }

    /* Creates paymentTransactionEntryModel for current paymentTransactionModel */
    protected void createPaymentTransactionEntry(final PaymentTransactionModel paymentTransaction, final PaymentTransactionData paymentTransactionData) {
        final HopSupportingPaymentTransactionEntryModel paymentTransactionEntry = getModelService().create(HopSupportingPaymentTransactionEntryModel.class);

        paymentTransactionEntry.setCode(getGeneratedPaymentTransactionEntryCode(paymentTransaction));
        paymentTransactionEntry.setMerchantId(paymentTransactionData.getMerchantId());
        paymentTransactionEntry.setPayId(paymentTransactionData.getPayId());
        paymentTransactionEntry.setXId(paymentTransactionData.getxId());
        paymentTransactionEntry.setTransId(paymentTransactionData.getTransId());
        paymentTransactionEntry.setTransactionStatus(paymentTransactionData.getStatus());
        paymentTransactionEntry.setDescription(paymentTransactionData.getDescription());
        paymentTransactionEntry.setErrorCode(paymentTransactionData.getErrorCode());

        if (paymentTransactionData.getCurrency() == null) {
            paymentTransactionEntry.setCurrency(getCommonI18NService().getCurrentCurrency());
        } else {
            paymentTransactionEntry.setCurrency(getCommonI18NService().getCurrency(paymentTransactionData.getCurrency()));
        }
        paymentTransactionEntry.setAmount(DistUtils.getBiggestUnitOfCurrency(paymentTransactionEntry.getCurrency(), paymentTransactionData.getAmount()));
        paymentTransactionEntry.setType(paymentTransactionData.getType());
        paymentTransactionEntry.setTime(new Date());

        paymentTransactionEntry.setAvResult(paymentTransactionData.getAvResult());
        paymentTransactionEntry.setFsCode(paymentTransactionData.getFsCode());
        paymentTransactionEntry.setFsStatus(paymentTransactionData.getFsStatus());

        paymentTransactionEntry.setCrn(paymentTransactionData.getCrn());
        paymentTransactionEntry.setUserData(paymentTransactionData.getUserData());

        paymentTransactionEntry.setAccBank(paymentTransactionData.getAccBank());
        paymentTransactionEntry.setAccIban(paymentTransactionData.getAccIban());
        paymentTransactionEntry.setAccNr(paymentTransactionData.getAccNr());
        paymentTransactionEntry.setBlzv(paymentTransactionData.getBlzv());

        paymentTransactionEntry.setZone(paymentTransactionData.getZone());
        paymentTransactionEntry.setIpZone(paymentTransactionData.getIpZone());
        paymentTransactionEntry.setIpState(paymentTransactionData.getIpState());
        paymentTransactionEntry.setIpCity(paymentTransactionData.getIpCity());
        paymentTransactionEntry.setIpLongitude(paymentTransactionData.getIpLongitude());
        paymentTransactionEntry.setIpLatitude(paymentTransactionData.getIpLatitude());

        paymentTransactionEntry.setEncryptedTransaction(paymentTransactionData.getEncryptedTransaction());

        paymentTransactionEntry.setPaymentTransaction(paymentTransaction);

        getModelService().save(paymentTransactionEntry);
        getModelService().refresh(paymentTransaction);
    }

    /* Gets cart by orderCode / order by code -> if code is empty try to get session cart */
    protected AbstractOrderModel getAbstractOrderForCode(final String code) {
        if (StringUtils.isEmpty(code)) {
            return cartService.getSessionCart();
        }
        return getAbstractOrderByCode(code, getFlexibleSearchService());
    }

    /* Generates unique code for paymentTransactionModel */
    protected String getGeneratedPaymentTransactionCode(final AbstractOrderModel abstractOrderModel) {

        final String orderCodeToTransmit = getOrderCodeToTransmit(abstractOrderModel);

        return abstractOrderModel.getUser().getUid() + "." + orderCodeToTransmit + "." + UUID.randomUUID().toString().replaceAll("-", "");

    }

    private String getOrderCodeToTransmit(final AbstractOrderModel abstractOrderModel) {
        return StringUtils.isNotEmpty(abstractOrderModel.getErpOrderCode()) ? abstractOrderModel.getErpOrderCode() : abstractOrderModel.getCode();
    }

    /* Gets latest paymentTransactionModel from current cart/order */
    protected PaymentTransactionModel getLatestPaymentTransaction(final PaymentTransactionData paymentTransactionData) {
        final AbstractOrderModel abstractOrderModel = getAbstractOrderForCode(paymentTransactionData.getReqId());
        return getLatestPaymentTransaction(abstractOrderModel);
    }

    public PaymentTransactionModel getLatestPaymentTransaction(final AbstractOrderModel abstractOrderModel) {
        PaymentTransactionModel latestPaymentTransaction = null;
        if (abstractOrderModel != null) {
            final List<PaymentTransactionModel> paymentTransactions = abstractOrderModel.getPaymentTransactions();
            for (final PaymentTransactionModel paymentTransaction : paymentTransactions) {
                if (latestPaymentTransaction == null) {
                    latestPaymentTransaction = paymentTransaction;
                }

                if (paymentTransaction.getCreationtime().after(latestPaymentTransaction.getCreationtime())) {
                    latestPaymentTransaction = paymentTransaction;
                }
            }
        }

        if (latestPaymentTransaction == null) {
            throw new IllegalStateException("Transaction have to exist already for notify and success/failure responses [order/cart: "
                    + (abstractOrderModel != null ? abstractOrderModel.getCode() : "no order/cart found") + "]");
        }

        return latestPaymentTransaction;
    }

    /* Generates unique code for paymentTransactionEntryModel */
    protected String getGeneratedPaymentTransactionEntryCode(final PaymentTransactionModel paymentTransaction) {
        final int entryCounter = CollectionUtils.isNotEmpty(paymentTransaction.getEntries()) ? paymentTransaction.getEntries().size() + 1 : 1;
        return paymentTransaction.getCode() + "-" + entryCounter;
    }

    /**
     * generate ref id for payment
     *
     * @param cartModel
     * @return refId
     */
    protected String generaterefId(final CartModel cartModel) {

        String orderCode = StringUtils.isNotEmpty(cartModel.getErpOrderCode()) ? cartModel.getErpOrderCode() : cartModel.getCode();

        final StringBuffer refId = new StringBuffer();

        final SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
        final String now = format.format(Calendar.getInstance().getTime());

        refId.append(orderCode);
        refId.append(now);

        // DSUP-584 : max length of RefId is 20
        return (refId.length() <= MAX_REF_LENGTH) ? refId.toString() : refId.substring(0, MAX_REF_LENGTH);

    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public CartService getCartService() {
        return cartService;
    }

    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

}
