package com.namics.distrelec.b2b.core.mock.sap;

import java.util.List;

class If07CustomerPrices {

    private final String customerId;
    private final List<If07CustomerPrice> customerPrices;

    public If07CustomerPrices(String customerId, List<If07CustomerPrice> customerPrices) {
        this.customerId = customerId;
        this.customerPrices = customerPrices;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<If07CustomerPrice> getCustomerPrices() {
        return customerPrices;
    }
}
