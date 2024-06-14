/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order.strategies.impl.evopayment;

import java.io.StringReader;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.service.order.data.PaymentTransactionData;
import com.namics.distrelec.b2b.core.util.CountryCode;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;

/**
 * Credit Card Payment Method
 *
 * @author pbueschi, Namics AG
 */
public class CreditCardPaymentParamsStrategy extends EvoDistPaymentParamsStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(CreditCardPaymentParamsStrategy.class);

    private static final String FS_SKIP_MESSAGE_KEY = "distrelec.payment.fsSkip";

    private static final String FS_IGNORE_MESSAGE_KEY = "distrelec.payment.fsIgnore";

    private static final String FS_REJECT_MESSAGE_KEY = "distrelec.payment.fsReject";

    public static final String IS_3DS_VER2_ENABLED = "distrelec.payment.3DS.v2.enabled";

    private static final String CREDIT_CARD = "card";

    private static final String CREDIT_CARD_NUMBER = "number";

    private static final String CREDIT_CARD_BRAND = "brand";

    private static final String CREDIT_CARD_EXPIRY_DATE = "expiryDate";

    @Override
    public String getPaymentParams(final String merchantId, final CartModel cartModel) {

        final String refId = generaterefId(cartModel);
        final StringBuilder paymentParams = super.getDefaultPaymentParams(merchantId, cartModel, refId);

        // custom parameters
        boolean is3DSVer2Enabled = getConfigurationService().getConfiguration().getBoolean(IS_3DS_VER2_ENABLED, false);
        if (is3DSVer2Enabled) {
            paymentParams.append("&MsgVer=").append("2.0");
        }
        paymentParams.append("&OrderDesc=").append(getOrderDesc(cartModel, appendRefIdIntoOrderDesc(), refId));
        paymentParams.append("&Response=").append(ENCRYPTED_RESPONSE);
        paymentParams.append("&Capture=").append(getCaptureMethod(cartModel));

        final AddressModel paymentAddress = cartModel.getPaymentAddress();
        final B2BCustomerModel customer = ((B2BCustomerModel) cartModel.getUser());

        String paymentAddressBillToCustomer = isB2BAccount(customer) ? addB2BAddressInformation(paymentAddress, customer)
                                                                     : addB2CAddressInformation(paymentAddress, customer);

        paymentParams.append("&billToCustomer=").append(Base64Utils.encodeToString(paymentAddressBillToCustomer.getBytes()));
        paymentParams.append("&billingAddress=")
                     .append(Base64Utils.encodeToString(addAddressInformation(paymentAddress, StringUtils.EMPTY, "AddrStreetNr").getBytes()));

        final String zoneCountries = getNumericCountryCodesForCurrentSalesOrg();
        if (StringUtils.isNotBlank(zoneCountries)) {
            paymentParams.append("&IPZone=").append(zoneCountries);
            paymentParams.append("&Zone=").append(zoneCountries);
        }

        // delivery address
        final AddressModel deliveryAddress = cartModel.getDeliveryAddress();

        String deliveryAddressBillToCustomer = isB2BAccount(customer) ? addB2BAddressInformation(deliveryAddress, customer)
                                                                      : addB2CAddressInformation(deliveryAddress, customer);

        paymentParams.append("&shipToCustomer=").append(Base64Utils.encodeToString(deliveryAddressBillToCustomer.getBytes()));

        if (deliveryAddress != null) {
            paymentParams.append("&shippingAddress=")
                         .append(Base64Utils.encodeToString(addAddressInformation(deliveryAddress, StringUtils.EMPTY, "AddrStreetNr").getBytes()));
            if (deliveryAddress.getFax() != null) {
                paymentParams.append("&SDFax=").append(deliveryAddress.getFax());
            }
        }
        // additional fraud screening flags
        final String fsSkip = getConfigurationService().getConfiguration().getString(FS_SKIP_MESSAGE_KEY);
        if (StringUtils.isNotBlank(fsSkip)) {
            paymentParams.append("&fsSkip=").append(fsSkip);
        }
        final String fsIgnore = getConfigurationService().getConfiguration().getString(FS_IGNORE_MESSAGE_KEY);
        if (StringUtils.isNotBlank(fsIgnore)) {
            paymentParams.append("&fsIgnore=").append(fsIgnore);
        }
        final String fsReject = getConfigurationService().getConfiguration().getString(FS_REJECT_MESSAGE_KEY);
        if (StringUtils.isNotBlank(fsReject)) {
            paymentParams.append("&fsReject=").append(fsReject);
        }

        addPaymentTransaction(merchantId, cartModel, refId, paymentParams.toString());
        return paymentParams.toString();
    }

    private boolean isB2BAccount(B2BCustomerModel customer) {
        return customer.getCustomerType() == CustomerType.B2B || customer.getCustomerType() == CustomerType.B2B_KEY_ACCOUNT;
    }

    private String getOrderDesc(final CartModel cartModel, final boolean appendRefIdIntoOrderDesc, final String refId) {
        final StringBuilder orderDesc = new StringBuilder();
        boolean isSandboxMode = getConfigurationService().getConfiguration().getBoolean("distrelec.payment.sandbox.mode");
        if (isSandboxMode) {
            return ORDER_DESC_SANDBOX;
        }

        if (appendRefIdIntoOrderDesc) {
            orderDesc.append(refId).append(";");
        }
        orderDesc.append(getOrderDescForPaymentRequest(cartModel));
        correctOrderDescStringLength(orderDesc);
        return orderDesc.toString();
    }

    private String addB2BAddressInformation(AddressModel address, B2BCustomerModel customer) {
        JsonObjectBuilder additionalAddressBuilder = Json.createObjectBuilder();
        JsonObjectBuilder businessBuilder = Json.createObjectBuilder();

        businessBuilder.add("legalName",
                            address != null && StringUtils.isNotBlank(address.getCompany()) ? address.getCompany() : customer.getDefaultB2BUnit().getName());
        additionalAddressBuilder.add("business", businessBuilder.build());
        additionalAddressBuilder.add("email", customer.getEmail());
        return additionalAddressBuilder.build().toString();
    }

    private String addB2CAddressInformation(AddressModel address, B2BCustomerModel customer) {
        final AddressModel fallbackAddress = customer.getContactAddress();

        JsonObjectBuilder additionalAddressBuilder = Json.createObjectBuilder();
        JsonObjectBuilder consumerBuilder = Json.createObjectBuilder();

        consumerBuilder.add("firstName",
                            address != null && StringUtils.isNotBlank(address.getFirstname()) ?
                            address.getFirstname() : (fallbackAddress != null ? fallbackAddress.getFirstname() : ""));

        consumerBuilder.add("lastName",
                            address != null && StringUtils.isNotBlank(address.getLastname()) ?
                            address.getLastname() : (fallbackAddress != null ? fallbackAddress.getLastname() : ""));
        if (isTitleSet(address, customer)) {
            consumerBuilder.add("salutation", address.getTitle() != null ? address.getTitle().getEvoCode() : customer.getTitle().getEvoCode());
        }

        additionalAddressBuilder.add("consumer", consumerBuilder.build());
        additionalAddressBuilder.add("email", customer.getEmail());
        return additionalAddressBuilder.build().toString();
    }

    private boolean isTitleSet(AddressModel address, B2BCustomerModel customer) {
        return address != null && (address.getTitle() != null || customer.getTitle() != null);
    }

    @Override
    protected String addAddressInformation(AddressModel address, String prefix, String streetHouseNr) {
        JsonObjectBuilder additionalAddressBuilder = Json.createObjectBuilder();

        if (address != null) {
            if (StringUtils.isNotBlank(address.getTown())) {
                additionalAddressBuilder.add("city", address.getTown());
            }
            if (StringUtils.isNotBlank(address.getPostalcode())) {
                additionalAddressBuilder.add("postalCode", address.getPostalcode());
            }
            if (StringUtils.isNotBlank(address.getCountry().getIsocode())) {
                JsonObjectBuilder countryBuilder = Json.createObjectBuilder();
                countryBuilder.add("countryA3", String.valueOf(CountryCode.getByCode(address.getCountry().getIsocode()).getAlpha3()));
                additionalAddressBuilder.add("country", countryBuilder.build());
            }
            if (StringUtils.isNotBlank(address.getStreetname()) && StringUtils.isNotBlank(address.getStreetnumber())) {
                JsonObjectBuilder addressLineBuilder = Json.createObjectBuilder();
                addressLineBuilder.add("street", address.getStreetname());
                addressLineBuilder.add("streetNumber", address.getStreetnumber());
                additionalAddressBuilder.add("addressLine1", addressLineBuilder.build());
            }
        }
        return additionalAddressBuilder.build().toString();
    }

    @Override
    public void handlePaymentNotifyParams(final Map<String, String> paymentParams, final Map<String, String> encryptedPaymentParamsMap) {
        final String encryptedString = "Len=" + encryptedPaymentParamsMap.get("Len") + "\nData=" + encryptedPaymentParamsMap.get("Data");
        final PaymentTransactionData paymentTransactionData = createPaymentTransactionData(paymentParams, encryptedString);

        paymentTransactionData.setType(PaymentTransactionType.NOTIFY);

        final PaymentTransactionModel paymentTransaction = getLatestPaymentTransaction(paymentTransactionData);

        createPaymentTransactionEntry(paymentTransaction, paymentTransactionData);
    }

    @Override
    public void handlePaymentSuccessFailureParams(final Map<String, String> paymentParams, final String encryptedString) {
        final PaymentTransactionData paymentTransactionData = createPaymentTransactionData(paymentParams, encryptedString);

        paymentTransactionData
                              .setType(paymentParams.get("Status").equals("FAILED") ? PaymentTransactionType.FAILED_RESPONSE
                                                                                    : PaymentTransactionType.SUCCESS_RESPONSE);

        // set ccPaymentInfo on customer based on used credit card for this order
        if (PaymentTransactionType.SUCCESS_RESPONSE.equals(paymentTransactionData.getType())) {
            final AbstractOrderModel abstractOrderModel = getAbstractOrderForCode(paymentTransactionData.getReqId());
            if (abstractOrderModel == null) {
                LOG.error("{} {} Unable to find cart into database for cart number {}", ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS,
                          paymentTransactionData.getType());
            }

            boolean is3DSVer2Enabled = getConfigurationService().getConfiguration().getBoolean(IS_3DS_VER2_ENABLED, false);
            if (is3DSVer2Enabled) {
                String decodedCreditCardData = new String(Base64Utils.decodeFromString(paymentParams.get(CREDIT_CARD)));
                try (JsonReader jsonReader = Json.createReader(new StringReader(decodedCreditCardData))) {
                    JsonObject cardJson = jsonReader.readObject();
                    createPaymentInfo(abstractOrderModel, cardJson.getString(CREDIT_CARD_NUMBER), cardJson.getString(CREDIT_CARD_BRAND),
                                      cardJson.getString(CREDIT_CARD_EXPIRY_DATE));
                }
            } else {
                createPaymentInfo(abstractOrderModel, paymentParams.get("PCNr"), paymentParams.get("CCBrand"), paymentParams.get("CCExpiry"));
            }
        }

        final PaymentTransactionModel paymentTransaction = getLatestPaymentTransaction(paymentTransactionData);
        createPaymentTransactionEntry(paymentTransaction, paymentTransactionData);
    }

    @Override
    protected PaymentTransactionData createPaymentTransactionData(final Map<String, String> paymentParams, final String encryptedTransaction) {
        final PaymentTransactionData paymentTransactionData = super.createPaymentTransactionData(paymentParams, encryptedTransaction);
        paymentTransactionData.setAvResult(paymentParams.get("AVResult"));
        paymentTransactionData.setFsCode(paymentParams.get("FSCode"));
        paymentTransactionData.setFsStatus(paymentParams.get("FSStatus"));
        paymentTransactionData.setZone(paymentParams.get("Zone"));
        paymentTransactionData.setIpZone(paymentParams.get("IPZone"));
        paymentTransactionData.setIpState(paymentParams.get("IPState"));
        paymentTransactionData.setIpCity(paymentParams.get("IPCity"));
        paymentTransactionData.setIpLongitude(paymentParams.get("IPLongitude"));
        paymentTransactionData.setIpLatitude(paymentParams.get("IPLatitude"));
        return paymentTransactionData;
    }

    /* Creates CreditCard payment info based response from payment provider and save it for the cart and the current customer */
    protected void createPaymentInfo(final AbstractOrderModel abstractOrder, final String pcNr, final String ccBrand, final String ccExpiry) {
        if (abstractOrder != null && StringUtils.isNotBlank(pcNr) && StringUtils.isNotBlank(ccBrand) && StringUtils.isNotBlank(ccExpiry)) {
            CreditCardPaymentInfoModel ccPaymentInfo = null;
            final UserModel user = abstractOrder.getUser();
            final Collection<PaymentInfoModel> paymentInfos = user.getPaymentInfos();
            if (CollectionUtils.isNotEmpty(paymentInfos)) {
                for (final PaymentInfoModel paymentInfo : paymentInfos) {
                    if (((CreditCardPaymentInfoModel) paymentInfo).getNumber().equals(pcNr)) {
                        ccPaymentInfo = (CreditCardPaymentInfoModel) paymentInfo;
                        ccPaymentInfo = getModelService().get(ccPaymentInfo.getPk());
                        ccPaymentInfo.setSaved(true);
                        getModelService().save(ccPaymentInfo);
                    }
                }
            }

            // save for current customer if not already persisted
            if (ccPaymentInfo == null) {
                ccPaymentInfo = getModelService().create(CreditCardPaymentInfoModel.class);
                ccPaymentInfo.setCode(user.getUid() + ccBrand + UUID.randomUUID().toString().replaceAll("-", ""));
                ccPaymentInfo.setUser(user);
                ccPaymentInfo.setNumber(pcNr);
                ccPaymentInfo.setType(CreditCardType.valueOf(StringUtils.upperCase(ccBrand)));
                ccPaymentInfo.setCcOwner(user.getName());
                ccPaymentInfo.setValidToYear(StringUtils.left(ccExpiry, 4));
                ccPaymentInfo.setValidToMonth(StringUtils.right(ccExpiry, 2));
                ccPaymentInfo.setSaved(true);
                getModelService().save(ccPaymentInfo);
            }

            // set paymentInfo to current cart
            abstractOrder.setPaymentInfo(ccPaymentInfo);
            getModelService().save(abstractOrder);
        } else {
            final StringBuilder builder = new StringBuilder();
            builder.append("could not create PaymentInfo for creditcard payment:");
            builder.append(" [ abstractOrder isNotNull:" + abstractOrder + "]");
            builder.append(" [ isNotBlank(pcNr):" + StringUtils.isNotBlank(pcNr) + "]");
            builder.append(" [ isNotBlank(ccBrand):" + StringUtils.isNotBlank(ccBrand) + "]");
            builder.append(" [ isNotBlank(ccExpiry):" + StringUtils.isNotBlank(ccExpiry) + "]");
            LOG.error(builder.toString());
        }
    }
}
