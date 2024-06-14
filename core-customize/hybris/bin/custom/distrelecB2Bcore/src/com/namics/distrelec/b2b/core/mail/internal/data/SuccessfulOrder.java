package com.namics.distrelec.b2b.core.mail.internal.data;

public class SuccessfulOrder {
    private String code;
    private String erpCode;    
    private String salesOrg;
    private String country;
    private String userId;    
    private String paymentType;
    private String transactionRefToken;    
    private String modifiedTime;
    private String totalPrice;
    private String currency;
    private String transactionType;
    private String transactionStatus;
    private int paymentCount;
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getErpCode() {
        return erpCode;
    }
    public void setErpCode(String erpCode) {
        this.erpCode = erpCode;
    }
    public String getSalesOrg() {
        return salesOrg;
    }
    public void setSalesOrg(String salesOrg) {
        this.salesOrg = salesOrg;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getPaymentType() {
        return paymentType;
    }
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
    public String getTransactionRefToken() {
        return transactionRefToken;
    }
    public void setTransactionRefToken(String transactionRefToken) {
        this.transactionRefToken = transactionRefToken;
    }
    public String getModifiedTime() {
        return modifiedTime;
    }
    public void setModifiedTime(String modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
    public String getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public String getTransactionType() {
        return transactionType;
    }
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    public String getTransactionStatus() {
        return transactionStatus;
    }
    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
    public int getPaymentCount() {
        return paymentCount;
    }
    public void setPaymentCount(int paymentCount) {
        this.paymentCount = paymentCount;
    }
        
}
