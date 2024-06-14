package com.namics.distrelec.b2b.core.mock.sap;

import com.distrelec.webservice.sap.v1.*;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;

public class SIHybrisV1OutMockBuilder {

    // builders
    private static final If07CustomerPriceSapCallBuilder if07CustomerPriceSapCallBuilder = new If07CustomerPriceSapCallBuilder();

    private static final If09ReadShippingMethodsCallBuilder if09ReadShippingMethodsSapCallBuilder = new If09ReadShippingMethodsCallBuilder();

    private static final If09UpdateDefaultShippingMethodCallBuilder if09UpdateDefaultShippingMethodSapCallBuilder =
            new If09UpdateDefaultShippingMethodCallBuilder();

    private static final If10ReadPaymentMethodsSapCallBuilder if10ReadPaymentMethodsSapCallBuilder = new If10ReadPaymentMethodsSapCallBuilder();

    private static final If11OrderCalculationSapCallBuilder if11OrderCalculationSapCallBuilder = new If11OrderCalculationSapCallBuilder();

    private static final If08FindContactSapCallBuilder if08FindContactSapCallBuilder = new If08FindContactSapCallBuilder();

    private static final If08UpdateContactSapCallBuilder if08UpdateContactSapCallBuilder = new If08UpdateContactSapCallBuilder();

    private static final If08ReadCustomerSapCallBuilder if08ReadCustomerSapCallBuilder = new If08ReadCustomerSapCallBuilder();

    // calls
    private final List<If07CustomerPrices> customerPrices = new ArrayList<>();

    private final List<If09ReadShippingMethods> readShippingMethods = new ArrayList<>();

    private final List<If09UpdateDefaultShippingMethod> updateDefaultShippingMethods = new ArrayList<>();

    private final List<If10ReadPaymentMethods> readPaymentMethods = new ArrayList<>();

    private final List<If11OrderCalculation> orderCalculations = new ArrayList<>();

    private final List<If08FindContact> findContacts = new ArrayList<>();

    private final List<If08UpdateContact> updateContacts = new ArrayList<>();

    private final List<If08ReadCustomer> readCustomers = new ArrayList<>();

    public static If07CustomerPrice customerPrice(ProductModel product, long quantity, double price) {
        return new If07CustomerPrice(product.getCode(), quantity, price);
    }

    public static If09ReadShippingMethod readShippingMethod(ShippingMethodCode shippingMethodCode, boolean isDefault) {
        return new If09ReadShippingMethod(shippingMethodCode, isDefault);
    }

    public static If10ReadPaymentMethod readPaymentMethod(String paymentMethodCode, boolean isDefault) {
        return new If10ReadPaymentMethod(paymentMethodCode, isDefault);
    }

    public static If11OrderCalculationEntry orderCalculationEntry(String productCode) {
        return new If11OrderCalculationEntry(productCode);
    }

    public SIHybrisV1OutMockBuilder expectIf07CustomerPrice(String customerId, If07CustomerPrice... priceEntries) {
        If07CustomerPrices request = new If07CustomerPrices(customerId, Arrays.asList(priceEntries));
        customerPrices.add(request);
        return this;
    }

    public SIHybrisV1OutMockBuilder expectIf09ReadShippingMethods(String customerId, If09ReadShippingMethod... shippingMethods) {
        If09ReadShippingMethods request = new If09ReadShippingMethods(customerId, Arrays.asList(shippingMethods));
        readShippingMethods.add(request);
        return this;
    }

    public SIHybrisV1OutMockBuilder expectIf09UpdateDefaultShippingMethod(String customerId, ShippingMethodCode shippingMethodCode, boolean successful) {
        If09UpdateDefaultShippingMethod request = new If09UpdateDefaultShippingMethod(customerId, shippingMethodCode, successful);
        updateDefaultShippingMethods.add(request);
        return this;
    }

    public SIHybrisV1OutMockBuilder expectIf10ReadPaymentMethods(String customerId, If10ReadPaymentMethod... paymentMethods) {
        If10ReadPaymentMethods request = new If10ReadPaymentMethods(customerId, Arrays.asList(paymentMethods));
        readPaymentMethods.add(request);
        return this;
    }

    public SIHybrisV1OutMockBuilder expectIf11OrderCalculation(String customerId, If11OrderCalculationEntry... entries) {
        If11OrderCalculation request = new If11OrderCalculation(customerId, Arrays.asList(entries));
        orderCalculations.add(request);
        return this;
    }

    public SIHybrisV1OutMockBuilder expectIf08FindContact(String customerId, CustomerStatus customerStatus, ContactStatus contactStatus, String contactId) {
        If08FindContact request = new If08FindContact(customerId, customerStatus, contactStatus, contactId);
        findContacts.add(request);
        return this;
    }

    public SIHybrisV1OutMockBuilder expectIf08UpdateContact(String customerId, boolean successful) {
        If08UpdateContact request = new If08UpdateContact(customerId, successful);
        updateContacts.add(request);
        return this;
    }

    public SIHybrisV1OutMockBuilder expectIf08ReadCustomer(String customerId, String salesOrganization,
                                                           String customerType, CurrencyCode currency,
                                                           String priceList, boolean active) {
        If08ReadCustomer request = new If08ReadCustomer(customerId, salesOrganization, customerType, currency, priceList, active);
        readCustomers.add(request);
        return this;
    }

    public SIHybrisV1Out build() {
        SIHybrisV1Out mock = mock(SIHybrisV1Out.class);

        // mock methods
        if07CustomerPriceSapCallBuilder.build(mock, customerPrices);
        if09ReadShippingMethodsSapCallBuilder.build(mock, readShippingMethods);
        if09UpdateDefaultShippingMethodSapCallBuilder.build(mock, updateDefaultShippingMethods);
        if10ReadPaymentMethodsSapCallBuilder.build(mock, readPaymentMethods);
        if11OrderCalculationSapCallBuilder.build(mock, orderCalculations);
        if08FindContactSapCallBuilder.build(mock, findContacts);
        if08UpdateContactSapCallBuilder.build(mock, updateContacts);
        if08ReadCustomerSapCallBuilder.build(mock, readCustomers);

        return mock;
    }
}
