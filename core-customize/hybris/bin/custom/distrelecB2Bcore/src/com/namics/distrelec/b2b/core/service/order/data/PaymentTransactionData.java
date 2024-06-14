/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order.data;

import java.math.BigDecimal;

import de.hybris.platform.payment.enums.PaymentTransactionType;

/**
 * Data object for payment transactions
 * 
 */
public class PaymentTransactionData {

    private String merchantId;
    private String transId;
    private String refNr;
    private BigDecimal amount;
    private String currency;
    private String crn;
    private String userData;
    private String reqId;
    private String paymentMethod;

    private String payId;
    private String xId;
    private String status;
    private String description;
    private String errorCode;

    private String accIban;
    private String accBank;
    private String accNr;
    private String blzv;

    private String zone;
    private String ipZone;
    private String ipState;
    private String ipCity;
    private String ipLongitude;
    private String ipLatitude;

    private String avResult;
    private String fsCode;
    private String fsStatus;

    private String paymentProvider;
    private PaymentTransactionType type;
    private String encryptedTransaction;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(final String merchantId) {
        this.merchantId = merchantId;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(final String transId) {
        this.transId = transId;
    }

    public String getRefNr() {
        return refNr;
    }

    public void setRefNr(final String refNr) {
        this.refNr = refNr;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(final String payId) {
        this.payId = payId;
    }

    public String getxId() {
        return xId;
    }

    public void setxId(final String xId) {
        this.xId = xId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
    }

    public String getAvResult() {
        return avResult;
    }

    public void setAvResult(final String avResult) {
        this.avResult = avResult;
    }

    public String getFsCode() {
        return fsCode;
    }

    public void setFsCode(final String fsCode) {
        this.fsCode = fsCode;
    }

    public String getFsStatus() {
        return fsStatus;
    }

    public void setFsStatus(final String fsStatus) {
        this.fsStatus = fsStatus;
    }

    public String getPaymentProvider() {
        return paymentProvider;
    }

    public void setPaymentProvider(final String paymentProvider) {
        this.paymentProvider = paymentProvider;
    }

    public PaymentTransactionType getType() {
        return type;
    }

    public void setType(final PaymentTransactionType type) {
        this.type = type;
    }

    public String getCrn() {
        return crn;
    }

    public void setCrn(final String crn) {
        this.crn = crn;
    }

    public String getAccIban() {
        return accIban;
    }

    public void setAccIban(final String accIban) {
        this.accIban = accIban;
    }

    public String getAccBank() {
        return accBank;
    }

    public void setAccBank(final String accBank) {
        this.accBank = accBank;
    }

    public String getAccNr() {
        return accNr;
    }

    public void setAccNr(final String accNr) {
        this.accNr = accNr;
    }

    public String getBlzv() {
        return blzv;
    }

    public void setBlzv(final String blzv) {
        this.blzv = blzv;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(final String userData) {
        this.userData = userData;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(final String reqId) {
        this.reqId = reqId;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(final String zone) {
        this.zone = zone;
    }

    public String getIpZone() {
        return ipZone;
    }

    public void setIpZone(final String ipZone) {
        this.ipZone = ipZone;
    }

    public String getIpState() {
        return ipState;
    }

    public void setIpState(final String ipState) {
        this.ipState = ipState;
    }

    public String getIpCity() {
        return ipCity;
    }

    public void setIpCity(final String ipCity) {
        this.ipCity = ipCity;
    }

    public String getIpLongitude() {
        return ipLongitude;
    }

    public void setIpLongitude(final String ipLongitude) {
        this.ipLongitude = ipLongitude;
    }

    public String getIpLatitude() {
        return ipLatitude;
    }

    public void setIpLatitude(final String ipLatitude) {
        this.ipLatitude = ipLatitude;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(final String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getEncryptedTransaction() {
        return encryptedTransaction;
    }

    public void setEncryptedTransaction(final String encryptedTransaction) {
        this.encryptedTransaction = encryptedTransaction;
    }

}
