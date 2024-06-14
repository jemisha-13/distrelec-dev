package com.namics.distrelec.b2b.facades.customer;

public interface DistUserDashboardFacade
{
    /**
     * @return the number of the open orders if the customer is logged in, zero otherwise.
     */
    Integer getOpenOrdersCount();
    /**
     * @return the number of the new invoice counts if the customer is logged in, zero otherwise.
     */
    Integer getNewInvoicesCount();
    /**
     * @return the number of approval requests if the customer is logged in, zero otherwise.
     */
    Integer getApprovalRequestsCount();

    /**
     * @return the number of the quotes having status "Offered" {@code "02"} if the customer is logged in, zero otherwise.
     */
    Integer getOfferedQuoteCount();

    /**
     * @return true if the customer logged in is a Ariba Customer, false otherwise.
     */
    boolean isAribaCustomer();

    /**
     * @return true if the customer logged in is a OCI Customer, false otherwise.
     */
    boolean isOciCustomer();

}
