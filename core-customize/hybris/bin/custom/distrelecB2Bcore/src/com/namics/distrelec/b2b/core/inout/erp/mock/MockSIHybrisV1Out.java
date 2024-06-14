/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.mock;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.logging.log4j.spi.StandardLevel;
import org.springframework.beans.factory.annotation.Autowired;

import com.distrelec.webservice.sap.v1.AvailabilityRequest;
import com.distrelec.webservice.sap.v1.AvailabilityResponse;
import com.distrelec.webservice.sap.v1.CheckBusinessCustomerRequest;
import com.distrelec.webservice.sap.v1.CheckBusinessCustomerResponse;
import com.distrelec.webservice.sap.v1.CheckPrivateCustomerRequest;
import com.distrelec.webservice.sap.v1.CheckPrivateCustomerResponse;
import com.distrelec.webservice.sap.v1.CheckUserAuthenticationRequest;
import com.distrelec.webservice.sap.v1.CheckUserAuthenticationResponse;
import com.distrelec.webservice.sap.v1.CodeList;
import com.distrelec.webservice.sap.v1.CodeListUnit;
import com.distrelec.webservice.sap.v1.ContactStatus;
import com.distrelec.webservice.sap.v1.CreateAddressRequest;
import com.distrelec.webservice.sap.v1.CreateAddressResponse;
import com.distrelec.webservice.sap.v1.CreateContactRequest;
import com.distrelec.webservice.sap.v1.CreateContactResponse;
import com.distrelec.webservice.sap.v1.CreateCustomerRequest;
import com.distrelec.webservice.sap.v1.CreateCustomerResponse;
import com.distrelec.webservice.sap.v1.CreateQuotationRequest;
import com.distrelec.webservice.sap.v1.CreateQuotationResponse;
import com.distrelec.webservice.sap.v1.CreateRmaRequest;
import com.distrelec.webservice.sap.v1.CreateRmaResponse;
import com.distrelec.webservice.sap.v1.CurrencyCode;
import com.distrelec.webservice.sap.v1.CustomerPriceArticlesResponse;
import com.distrelec.webservice.sap.v1.CustomerPriceRequest;
import com.distrelec.webservice.sap.v1.CustomerPriceResponse;
import com.distrelec.webservice.sap.v1.CustomerStatus;
import com.distrelec.webservice.sap.v1.DeleteAddressRequest;
import com.distrelec.webservice.sap.v1.DeleteAddressResponse;
import com.distrelec.webservice.sap.v1.DeleteContactRequest;
import com.distrelec.webservice.sap.v1.DeleteContactResponse;
import com.distrelec.webservice.sap.v1.ErpArticleAvailability;
import com.distrelec.webservice.sap.v1.FindContactRequest;
import com.distrelec.webservice.sap.v1.FindContactResponse;
import com.distrelec.webservice.sap.v1.InvoiceSearchRequest;
import com.distrelec.webservice.sap.v1.InvoiceSearchResponse;
import com.distrelec.webservice.sap.v1.LocalizedString;
import com.distrelec.webservice.sap.v1.OpenOrderCalculationRequest;
import com.distrelec.webservice.sap.v1.OpenOrderCalculationResponse;
import com.distrelec.webservice.sap.v1.OrderCalculationRequest;
import com.distrelec.webservice.sap.v1.OrderCalculationResponse;
import com.distrelec.webservice.sap.v1.OrderEntryRequest;
import com.distrelec.webservice.sap.v1.OrderEntryResponse;
import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.PaymentMethod;
import com.distrelec.webservice.sap.v1.PaymentMethodResponse;
import com.distrelec.webservice.sap.v1.ReadCustomerRequest;
import com.distrelec.webservice.sap.v1.ReadCustomerResponse;
import com.distrelec.webservice.sap.v1.ReadDocumentRequest;
import com.distrelec.webservice.sap.v1.ReadDocumentResponse;
import com.distrelec.webservice.sap.v1.ReadModifiedCodelistRequest;
import com.distrelec.webservice.sap.v1.ReadModifiedCodelistResponse;
import com.distrelec.webservice.sap.v1.ReadPaymentMethodsRequest;
import com.distrelec.webservice.sap.v1.ReadPaymentMethodsResponse;
import com.distrelec.webservice.sap.v1.ReadQuotationsRequest;
import com.distrelec.webservice.sap.v1.ReadQuotationsResponse;
import com.distrelec.webservice.sap.v1.ReadShippingMethodsRequest;
import com.distrelec.webservice.sap.v1.ReadShippingMethodsResponse;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.distrelec.webservice.sap.v1.SearchDunsRequest;
import com.distrelec.webservice.sap.v1.SearchDunsResponse;
import com.distrelec.webservice.sap.v1.SearchQuotationsRequest;
import com.distrelec.webservice.sap.v1.SearchQuotationsResponse;
import com.distrelec.webservice.sap.v1.SearchRmasRequest;
import com.distrelec.webservice.sap.v1.SearchRmasResponse;
import com.distrelec.webservice.sap.v1.ShippingMethodCode;
import com.distrelec.webservice.sap.v1.ShippingMethodResponse;
import com.distrelec.webservice.sap.v1.ShippingMethods;
import com.distrelec.webservice.sap.v1.StockLevels;
import com.distrelec.webservice.sap.v1.UpdateAddressRequest;
import com.distrelec.webservice.sap.v1.UpdateAddressResponse;
import com.distrelec.webservice.sap.v1.UpdateContactRequest;
import com.distrelec.webservice.sap.v1.UpdateContactResponse;
import com.distrelec.webservice.sap.v1.UpdateCustomerRequest;
import com.distrelec.webservice.sap.v1.UpdateCustomerResponse;
import com.distrelec.webservice.sap.v1.UpdateDefaultShippingMethodRequest;
import com.distrelec.webservice.sap.v1.UpdateDefaultShippingMethodResponse;
import com.distrelec.webservice.sap.v1.UpdatePaymentMethodRequest;
import com.distrelec.webservice.sap.v1.UpdatePaymentMethodResponse;
import com.namics.distrelec.b2b.core.annotations.LogInOut;

import de.hybris.platform.servicelayer.config.ConfigurationService;

public class MockSIHybrisV1Out implements SIHybrisV1Out {

    private static final String MOCK_IF08_FIND_CONTACT_CONTACT_ID = "mock.if08.findContact.contactId";

    private static final Logger LOG = Logger.getLogger(MockSIHybrisV1Out.class);
    
    private ShippingMethodCode defaultShippingMethod = ShippingMethodCode.X_1;

    private static final String[] PAYMENT_METHOD_CODES = { "WB01", "WB02", "WB03", "WB04", "WB05", "WB06", "WB07" };

    private static final String[] WAREHOUSES = {
            "7371", "EF71", "ES71", "7372", "EF72", "ES72", "7374", "7641", "7661", "7373", "7791", "7811", "EF51", "ES51", "EF31", "ES31", "7375"
    };

    private String defaultPaymentMethod = PAYMENT_METHOD_CODES[0];

    @Autowired
    private ConfigurationService configurationService;

    @Override
    @LogInOut(StandardLevel.INFO)
    public OrderCalculationResponse if11OrderCalculation(final OrderCalculationRequest orderCalculationRequest) throws P1FaultMessage {
        LOG.info("mock if11OrderCalculation");
        final OrderCalculationResponse response = new OrderCalculationResponse();
        double netSum = 0;
        for (final OrderEntryRequest orderEntryRequest : orderCalculationRequest.getOrderEntries()) {
            final OrderEntryResponse orderEntryResponse = new OrderEntryResponse();
            orderEntryResponse.setMaterialNumber(orderEntryRequest.getMaterialNumber());
            final long quantity = orderEntryRequest.getQuantity();
            orderEntryResponse.setOrderQuantity(quantity);
            final boolean isFreeGiftPromotion = orderEntryRequest.isFreeGiftPromotion();
            orderEntryResponse.setFreeGiftPromotion(isFreeGiftPromotion);
            final double price = 0.42;
            orderEntryResponse.setListPrice(price);
            final double totalPrice = price * quantity;
            orderEntryResponse.setListPriceTotal(totalPrice);
            if (isFreeGiftPromotion) {
                orderEntryResponse.setActualPrice(0);
                orderEntryResponse.setActualPriceTotal(0);
            } else {
                orderEntryResponse.setActualPrice(price);
                orderEntryResponse.setActualPriceTotal(totalPrice);
                netSum += totalPrice;
            }
            // TODO: Availability
            response.getOrderEntries().add(orderEntryResponse);
        }

        Arrays.stream(PAYMENT_METHOD_CODES).map(pmc -> {
            final PaymentMethodResponse paymentMethod = new PaymentMethodResponse();
            paymentMethod.setCode(pmc);
            paymentMethod.setPrice(7.50);
            paymentMethod.setSelectable(Boolean.TRUE);
            paymentMethod.setSelected(pmc.equals(defaultPaymentMethod));
            return paymentMethod;
        }).forEach(response.getPaymentMethods()::add);

        EnumSet.allOf(ShippingMethodCode.class).stream().map(smc -> {
            final ShippingMethodResponse shippingMethod = new ShippingMethodResponse();
            shippingMethod.setCode(smc);
            shippingMethod.setPrice(7.50);
            shippingMethod.setSelectable(Boolean.TRUE);
            shippingMethod.setSelected(smc.equals(defaultShippingMethod));
            return shippingMethod;
        }).forEach(response.getShippingMethods()::add);

        response.setSubtotal1(netSum);
        final double shippingPrice = 4.95;
        response.setShippingPrice(Double.valueOf(shippingPrice));
        netSum += shippingPrice;
        response.setSubtotal2(netSum);
        final double tax = netSum * 0.2;
        response.setTax(tax);
        netSum += tax;
        response.setTotal(netSum);
        return response;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public ReadShippingMethodsResponse if09ReadShippingMethods(final ReadShippingMethodsRequest readShippingMethodsRequest) throws P1FaultMessage {
        final String customerId = readShippingMethodsRequest.getCustomerId();
        final ReadShippingMethodsResponse response = new ReadShippingMethodsResponse();
        response.setCustomerId(customerId);
        for (final ShippingMethodCode code : ShippingMethodCode.values()) {
            final ShippingMethods shippingMethod = new ShippingMethods();
            shippingMethod.setShippingMethodCode(code);
            shippingMethod.setDefault(code.equals(defaultShippingMethod));
            response.getShippingMethods().add(shippingMethod);
        }
        return response;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public UpdateDefaultShippingMethodResponse if09UpdateDefaultShippingMethod(final UpdateDefaultShippingMethodRequest updateDefaultShippingMethodRequest)
            throws P1FaultMessage {
        defaultShippingMethod = updateDefaultShippingMethodRequest.getShippingMethodCode();
        final UpdateDefaultShippingMethodResponse response = new UpdateDefaultShippingMethodResponse();
        response.setSuccessful(true);
        return response;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public ReadPaymentMethodsResponse if10ReadPaymentMethods(final ReadPaymentMethodsRequest readPaymentMethodsRequest) throws P1FaultMessage {
        final String customerId = readPaymentMethodsRequest.getCustomerId();
        final ReadPaymentMethodsResponse response = new ReadPaymentMethodsResponse();
        response.setCustomerId(customerId);
        for (final String code : PAYMENT_METHOD_CODES) {
            final PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.setPaymentMethodCode(code);
            paymentMethod.setDefault(code.equals(defaultPaymentMethod));
            response.getPaymentMethods().add(paymentMethod);
        }
        return response;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public UpdatePaymentMethodResponse if10UpdatePaymentMethod(final UpdatePaymentMethodRequest updatePaymentMethodRequest) throws P1FaultMessage {
        defaultPaymentMethod = updatePaymentMethodRequest.getPaymentMethodCode();
        final UpdatePaymentMethodResponse response = new UpdatePaymentMethodResponse();
        response.setSuccessful(true);
        return response;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public ReadModifiedCodelistResponse if50ReadModifiedCodelist(final ReadModifiedCodelistRequest readModifiedCodelistRequest) throws P1FaultMessage {
        final ReadModifiedCodelistResponse response = new ReadModifiedCodelistResponse();
        mockCountries(response.getCountry());
        mockDepartments(response.getDepartment());
        mockFunctions(response.getFunction());
        mockPaymentMethods(response.getPaymentMethod());
        mockReplacementReasons(response.getReplacementReason());
        mockRohs(response.getRohs());
        mockSalesOrgs(response.getSalesOrg());
        mockSalesStatus(response.getSalesStatus());
        mockSalesUnits(response.getSalesUnit());
        mockShippingMethods(response.getShippingMethod());
        mockTransportGroups(response.getTransportGroup());
        return response;
    }

    private void mockFunctions(final List<CodeList> functions) {
        CodeList function = new CodeList();
        function.setCode("23");
        function.setLastModifiedErp(BigInteger.valueOf(20140123190000l));
        List<LocalizedString> descriptions = function.getDescription();
        LocalizedString locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue("IT Helfer");
        descriptions.add(locStrDe);
        LocalizedString locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue("IT Supporter");
        descriptions.add(locStrEn);
        functions.add(function);

        function = new CodeList();
        function.setCode("42");
        function.setLastModifiedErp(BigInteger.valueOf(20140123190000l));
        descriptions = function.getDescription();
        locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue("Raumpfleger");
        descriptions.add(locStrDe);
        locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue("Cleaner");
        descriptions.add(locStrEn);
        functions.add(function);
    }

    private void mockDepartments(final List<CodeList> departments) {
        CodeList department = new CodeList();
        department.setCode("0023");
        department.setLastModifiedErp(BigInteger.valueOf(20140123190000l));
        List<LocalizedString> descriptions = department.getDescription();
        LocalizedString locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue("IT Haufen");
        descriptions.add(locStrDe);
        LocalizedString locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue("IT Crowd");
        descriptions.add(locStrEn);
        departments.add(department);

        department = new CodeList();
        department.setCode("0042");
        department.setLastModifiedErp(BigInteger.valueOf(20140123190000l));
        descriptions = department.getDescription();
        locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue("Putzabteilung");
        descriptions.add(locStrDe);
        locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue("Cleaning Department");
        descriptions.add(locStrEn);
        departments.add(department);
    }

    private void mockSalesUnits(final List<CodeListUnit> salesUnits) {
        CodeListUnit salesUnit = new CodeListUnit();
        salesUnit.setCode("423");
        salesUnit.setLastModifiedErp(BigInteger.valueOf(20140123190000l));
        salesUnit.setAmount(BigInteger.valueOf(65));
        salesUnit.setUnit("PCCC");
        List<LocalizedString> descriptions = salesUnit.getDescription();
        LocalizedString locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue("Packung mit 65 Stück");
        descriptions.add(locStrDe);
        LocalizedString locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue("Package with 65 pieces");
        descriptions.add(locStrEn);
        salesUnits.add(salesUnit);

        salesUnit = new CodeListUnit();
        salesUnit.setCode("237");
        salesUnit.setLastModifiedErp(BigInteger.valueOf(20140123190000l));
        salesUnit.setAmount(BigInteger.valueOf(19));
        salesUnit.setUnit("PC");
        descriptions = salesUnit.getDescription();
        locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue("Packung mit 19 Stück");
        descriptions.add(locStrDe);
        locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue("Package with 19 pieces");
        descriptions.add(locStrEn);
        salesUnits.add(salesUnit);
    }

    private void mockTransportGroups(final List<CodeList> transportGroups) {
        CodeList transportGroup = new CodeList();
        transportGroup.setCode("0023");
        transportGroup.setLastModifiedErp(BigInteger.valueOf(20140123190000l));
        List<LocalizedString> descriptions = transportGroup.getDescription();
        LocalizedString locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue("Kleine Güter");
        descriptions.add(locStrDe);
        LocalizedString locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue("Small goods");
        descriptions.add(locStrEn);
        transportGroups.add(transportGroup);

        transportGroup = new CodeList();
        transportGroup.setCode("0042");
        transportGroup.setLastModifiedErp(BigInteger.valueOf(20140123190000l));
        descriptions = transportGroup.getDescription();
        locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue("Grosse Güter");
        descriptions.add(locStrDe);
        locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue("Big goods");
        descriptions.add(locStrEn);
        transportGroups.add(transportGroup);
    }

    private void mockShippingMethods(final List<CodeList> shippingMethods) {
        CodeList shippingMethod = new CodeList();
        shippingMethod.setCode("Y1");
        shippingMethod.setLastModifiedErp(BigInteger.valueOf(20140123190000l));
        List<LocalizedString> descriptions = shippingMethod.getDescription();
        LocalizedString locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue("Brieftaube");
        descriptions.add(locStrDe);
        LocalizedString locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue("Carrier pigeon");
        descriptions.add(locStrEn);
        shippingMethods.add(shippingMethod);

        shippingMethod = new CodeList();
        shippingMethod.setCode("Y2");
        shippingMethod.setLastModifiedErp(BigInteger.valueOf(20140123190000l));
        descriptions = shippingMethod.getDescription();
        locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue("Schneckenpost");
        descriptions.add(locStrDe);
        locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue("Snail mail");
        descriptions.add(locStrEn);
        shippingMethods.add(shippingMethod);
    }

    private void mockSalesStatus(final List<CodeList> salesStatus) {
        CodeList salesStat = new CodeList();
        salesStat.setCode("70");
        salesStat.setLastModifiedErp(BigInteger.valueOf(20140123190000l));
        List<LocalizedString> descriptions = salesStat.getDescription();
        LocalizedString locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue("Lebensmitte");
        descriptions.add(locStrDe);
        LocalizedString locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue("Middle of life");
        descriptions.add(locStrEn);
        salesStatus.add(salesStat);

        salesStat = new CodeList();
        salesStat.setCode("71");
        salesStat.setLastModifiedErp(BigInteger.valueOf(20140123190000l));
        descriptions = salesStat.getDescription();
        locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue("Lebensende");
        descriptions.add(locStrDe);
        locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue("End of life");
        descriptions.add(locStrEn);
        salesStatus.add(salesStat);
    }

    private void mockRohs(final List<CodeList> rohs) {
        CodeList roh = new CodeList();
        roh.setCode("16");
        roh.setLastModifiedErp(BigInteger.valueOf(20140123190000l));
        List<LocalizedString> descriptions = roh.getDescription();
        LocalizedString locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue("Ja, Level 3");
        descriptions.add(locStrDe);
        LocalizedString locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue("yes, level 3");
        descriptions.add(locStrEn);
        rohs.add(roh);

        roh = new CodeList();
        roh.setCode("17");
        roh.setLastModifiedErp(BigInteger.valueOf(20140123190000l));
        descriptions = roh.getDescription();
        locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue("Ja, Level 4");
        descriptions.add(locStrDe);
        locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue("yes, level 4");
        descriptions.add(locStrEn);
        rohs.add(roh);
    }

    private void mockReplacementReasons(final List<CodeList> replacementReasons) {
        CodeList replacementReason = new CodeList();
        replacementReason.setCode("K");
        replacementReason.setLastModifiedErp(BigInteger.valueOf(20140123190000l));
        List<LocalizedString> descriptions = replacementReason.getDescription();
        LocalizedString locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue("zu klein");
        descriptions.add(locStrDe);
        LocalizedString locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue("too small");
        descriptions.add(locStrEn);
        replacementReasons.add(replacementReason);

        replacementReason = new CodeList();
        replacementReason.setCode("L");
        replacementReason.setLastModifiedErp(BigInteger.valueOf(20140123190000l));
        descriptions = replacementReason.getDescription();
        locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue("zu gross");
        descriptions.add(locStrDe);
        locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue("too big");
        descriptions.add(locStrEn);
        replacementReasons.add(replacementReason);
    }

    private void mockCountries(final List<CodeList> countries) {
        final CodeList country = new CodeList();
        country.setCode("XX");
        country.setLastModifiedErp(BigInteger.valueOf(20140123183000l));
        final List<LocalizedString> descriptions = country.getDescription();
        final LocalizedString locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue("Mittelerde");
        descriptions.add(locStrDe);
        final LocalizedString locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue("Middle Earth");
        descriptions.add(locStrEn);
        countries.add(country);
    }

    private void mockPaymentMethods(final List<CodeList> paymentMethodCodeList) {
        CodeList paymentMethod = new CodeList();
        paymentMethod.setCode("C001");
        paymentMethod.setLastModifiedErp(BigInteger.valueOf(20140123183000l));
        List<LocalizedString> descriptions = paymentMethod.getDescription();
        LocalizedString locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue("Münzen");
        descriptions.add(locStrDe);
        LocalizedString locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue("Coins");
        descriptions.add(locStrEn);
        paymentMethodCodeList.add(paymentMethod);

        paymentMethod = new CodeList();
        paymentMethod.setCode("C002");
        paymentMethod.setLastModifiedErp(BigInteger.valueOf(20140123183000l));
        descriptions = paymentMethod.getDescription();
        locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue("Banknoten");
        descriptions.add(locStrDe);
        locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue("Bank notes");
        descriptions.add(locStrEn);
        paymentMethodCodeList.add(paymentMethod);
    }

    private void mockSalesOrgs(final List<CodeList> salesOrgCodeList) {
        CodeList salesOrg = new CodeList();
        salesOrg.setCode("9410");
        salesOrg.setLastModifiedErp(BigInteger.valueOf(20140123161500l));
        List<LocalizedString> descriptions = salesOrg.getDescription();
        LocalizedString locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue("Distrelec Schweiz");
        descriptions.add(locStrDe);
        LocalizedString locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue("Distrelec Switzerland");
        descriptions.add(locStrEn);
        salesOrgCodeList.add(salesOrg);

        salesOrg = new CodeList();
        salesOrg.setCode("9520");
        salesOrg.setLastModifiedErp(BigInteger.valueOf(20140123161500l));
        descriptions = salesOrg.getDescription();
        locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue("Datwyler Schweiz");
        descriptions.add(locStrDe);
        locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue("Datwyler Switzerland");
        descriptions.add(locStrEn);
        salesOrgCodeList.add(salesOrg);
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public SearchQuotationsResponse if18SearchQuotations(final SearchQuotationsRequest searchQuotationsRequest) throws P1FaultMessage {
        final SearchQuotationsResponse searchQuotationsResponse = new SearchQuotationsResponse();
        searchQuotationsResponse.setCustomerId(searchQuotationsRequest.getCustomerId());
        searchQuotationsResponse.setResultTotalSize(BigInteger.ZERO);
        return searchQuotationsResponse;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public ReadCustomerResponse if08ReadCustomer(final ReadCustomerRequest readCustomerRequest) throws P1FaultMessage {
        final ReadCustomerResponse readCustomerResponse = new ReadCustomerResponse();
        readCustomerResponse.setSalesOrganization(readCustomerRequest.getSalesOrganization());
        readCustomerResponse.setCustomerId(readCustomerRequest.getCustomerId());
        final String customerType = getConfigurationService().getConfiguration().getBoolean("mock.if08.readCustomer.customerType.b2b", true) ? "COMPANY"
                : "PRIVATE";
        readCustomerResponse.setCustomerType(customerType);
        readCustomerResponse.setActive(true);
        readCustomerResponse.setKeyAccountBusinessCustomer(false);
        readCustomerResponse.setCurrency(CurrencyCode.CHF);
        readCustomerResponse.setOnlinePriceCalculation(true);
        // final ContactWithId contact = new ContactWithId(); TODO
        // readCustomerResponse.getContacts().add(contact);
        return readCustomerResponse;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public CreateCustomerResponse if08CreateCustomer(final CreateCustomerRequest createCustomerRequest) throws P1FaultMessage {
        final CreateCustomerResponse createCustomerResponse = new CreateCustomerResponse();
        createCustomerResponse.setContactId(hash("contactId", createCustomerRequest.getEmail()));
        final String customerId = StringUtils.isNotBlank(createCustomerRequest.getOrganizationNumber()) ? createCustomerRequest.getOrganizationNumber()
                : hash("customerId", createCustomerRequest.getEmail());
        createCustomerResponse.setCustomerId(customerId);
        return createCustomerResponse;
    }

    protected String hash(final String... strings) {
        return String.valueOf(Stream.of(strings).collect(Collectors.joining()).hashCode());
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public UpdateContactResponse if08UpdateContact(final UpdateContactRequest updateContactRequest) throws P1FaultMessage {
        final UpdateContactResponse updateContactResponse = new UpdateContactResponse();
        updateContactResponse.setSuccessful(true);
        return updateContactResponse;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public SearchRmasResponse if17SearchRmas(final SearchRmasRequest searchRmasRequest) throws P1FaultMessage {
        throw new UnsupportedOperationException();
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public CustomerPriceResponse if07CustomerPrice(final CustomerPriceRequest customerPriceRequest) throws P1FaultMessage {
        final CustomerPriceResponse customerPriceResponse = new CustomerPriceResponse();
        customerPriceRequest.getArticles().stream().map(requestArticle -> {
            final CustomerPriceArticlesResponse customerPriceArticlesResponse = new CustomerPriceArticlesResponse();
            customerPriceArticlesResponse.setArticleNumber(requestArticle.getArticleNumber());
            customerPriceArticlesResponse.setCurrencyCode(customerPriceRequest.getCurrencyCode());
            customerPriceArticlesResponse.setQuantity(requestArticle.getQuantity());
            customerPriceArticlesResponse.setPriceWithoutVat(123.45);
            customerPriceArticlesResponse.setVatPercentage("20");
            customerPriceArticlesResponse.setVat(new BigDecimal(24.69));
            customerPriceArticlesResponse.setPriceWithVat(148.14);
            return customerPriceArticlesResponse;
        }).forEach(customerPriceResponse.getArticles()::add);
        return customerPriceResponse;

    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public ReadDocumentResponse if51ReadDocument(final ReadDocumentRequest readDocumentRequest) throws P1FaultMessage {
        throw new UnsupportedOperationException();
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public CreateRmaResponse if17CreateRma(final CreateRmaRequest createRmaRequest) throws P1FaultMessage {
        throw new UnsupportedOperationException();
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public DeleteAddressResponse if08DeleteAddress(final DeleteAddressRequest deleteAddressRequest) throws P1FaultMessage {
        final DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse();
        deleteAddressResponse.setSuccessful(true);
        return deleteAddressResponse;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public InvoiceSearchResponse if16SearchInvoices(final InvoiceSearchRequest invoiceSearchRequest) throws P1FaultMessage {
        throw new UnsupportedOperationException();
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public AvailabilityResponse if06Availability(final AvailabilityRequest availabilityRequest) throws P1FaultMessage {
        final AvailabilityResponse availabilityResponse = new AvailabilityResponse();
        availabilityRequest.getArticleNumbers().stream().map(articleNumber -> {
            final ErpArticleAvailability erpArticleAvailability = new ErpArticleAvailability();
            erpArticleAvailability.setArticleNumber(articleNumber);
            erpArticleAvailability.setArticleFound(true);
            Arrays.stream(WAREHOUSES).map(w -> {
                final StockLevels stockLevels = new StockLevels();
                stockLevels.setArticleFoundInWarehouse(true);
                stockLevels.setAvailable(BigInteger.TEN);
                stockLevels.setWarehouseId(w);
                return stockLevels;
            }).forEach(erpArticleAvailability.getStockLevels()::add);
            return erpArticleAvailability;
        }).forEach(availabilityResponse.getErpArticleAvailability()::add);
        LOG.info("if06Availability");
        return availabilityResponse;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public CreateAddressResponse if08CreateAddress(final CreateAddressRequest createAddressRequest) throws P1FaultMessage {
        final CreateAddressResponse createAddressResponse = new CreateAddressResponse();
        createAddressResponse.setAddressId(String.valueOf(new Random().nextInt()));
        return createAddressResponse;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public CreateContactResponse if08CreateContact(final CreateContactRequest createContactRequest) throws P1FaultMessage {
        final CreateContactResponse createContactResponse = new CreateContactResponse();
        createContactResponse.setContactId(createContactRequest.getCustomerId());
        return createContactResponse;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public CreateQuotationResponse if18CreateQuotation(final CreateQuotationRequest createQuotationRequest) throws P1FaultMessage {
        throw new UnsupportedOperationException();
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public DeleteContactResponse if08DeleteContact(final DeleteContactRequest deleteContactRequest) throws P1FaultMessage {
        throw new UnsupportedOperationException();
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public CheckBusinessCustomerResponse if08CheckBusinessCustomer(final CheckBusinessCustomerRequest checkBusinessCustomerRequest) throws P1FaultMessage {
        throw new UnsupportedOperationException();
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public CheckPrivateCustomerResponse if08CheckPrivateCustomer(final CheckPrivateCustomerRequest checkPrivateCustomerRequest) throws P1FaultMessage {
        throw new UnsupportedOperationException();
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public UpdateAddressResponse if08UpdateAddress(final UpdateAddressRequest updateAddressRequest) throws P1FaultMessage {
        final UpdateAddressResponse updateAddressResponse = new UpdateAddressResponse();
        updateAddressResponse.setSuccessful(true);
        return updateAddressResponse;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public CheckUserAuthenticationResponse if08CheckUserAuthentication(final CheckUserAuthenticationRequest checkUserAuthenticationRequest)
            throws P1FaultMessage {
        throw new UnsupportedOperationException();
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public OpenOrderCalculationResponse if11OpenOrderCalculation(final OpenOrderCalculationRequest openOrderCalculationRequest) throws P1FaultMessage {
        final OpenOrderCalculationResponse response = new OpenOrderCalculationResponse();
        response.setOrderId(openOrderCalculationRequest.getOrderId());

        // set new order entries
        final OrderEntryResponse newEntryResponse = new OrderEntryResponse();
        newEntryResponse.setMaterialNumber("4022167");
        newEntryResponse.setOrderQuantity(3);
        newEntryResponse.setListPrice(5);
        newEntryResponse.setListPriceTotal(15);
        newEntryResponse.setActualPrice(7);
        newEntryResponse.setActualPriceTotal(21);
        response.getNewOrderEntries().add(newEntryResponse);

        // set confirmed entries
        final OrderEntryResponse confirmedOrderEntry = new OrderEntryResponse();
        confirmedOrderEntry.setMaterialNumber("4022167");
        confirmedOrderEntry.setOrderQuantity(7);
        confirmedOrderEntry.setListPrice(4);
        confirmedOrderEntry.setListPriceTotal(28);
        confirmedOrderEntry.setActualPrice(6);
        confirmedOrderEntry.setActualPriceTotal(42);
        response.getConfirmedOrderEntries().add(confirmedOrderEntry);

        response.setSubtotal1(50.5);
        response.setPaymentPrice(Double.valueOf(2.5));
        response.setShippingPrice(Double.valueOf(5));
        // set vouchers
        // add discounts
        response.setSubtotal2(30);
        response.setTax(15);
        response.setTotal(45);
        // set freeVoucherPromotion

        return response;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public FindContactResponse if08FindContact(final FindContactRequest findContactRequest) throws P1FaultMessage {
        final String contactId = getConfigurationService().getConfiguration().getString(MOCK_IF08_FIND_CONTACT_CONTACT_ID);
        final FindContactResponse findContactResponse = new FindContactResponse();
        findContactResponse.setCustomerStatus(CustomerStatus.CUSTOMER_FOUND);
        findContactResponse.setCustomerId(findContactRequest.getCustomerId());
        findContactResponse.setContactStatus(StringUtils.isBlank(contactId) ? ContactStatus.CONTACT_NOT_FOUND : ContactStatus.CONTACT_FOUND);
        findContactResponse.setContactId(contactId);
        return findContactResponse;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public ReadQuotationsResponse if18ReadQuotations(final ReadQuotationsRequest readQuotationsRequest) throws P1FaultMessage {
        // YTODO Auto-generated method stub
        return null;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public com.distrelec.webservice.sap.v1.OrderSearchResponse if15SearchOrders(final com.distrelec.webservice.sap.v1.OrderSearchRequest orderSearchRequest)
            throws P1FaultMessage {
        // YTODO Auto-generated method stub
        return null;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public com.distrelec.webservice.sap.v1.ReadAllOpenOrdersResponse if15ReadAllOpenOrders(
            final com.distrelec.webservice.sap.v1.ReadAllOpenOrdersRequest readAllOpenOrdersRequest) throws P1FaultMessage {
        // YTODO Auto-generated method stub
        return null;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public com.distrelec.webservice.sap.v1.ReadOrderResponse if15ReadOrder(final com.distrelec.webservice.sap.v1.ReadOrderRequest readOrderRequest)
            throws P1FaultMessage {
        // YTODO Auto-generated method stub
        return null;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

   
	@Override
    @LogInOut(StandardLevel.INFO)	
	public UpdateCustomerResponse if08UpdateCustomer(UpdateCustomerRequest updateCustomerRequest)
			throws P1FaultMessage {
        final UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse();
        updateCustomerResponse.setSuccessful(true);
        return updateCustomerResponse;
	}

    @Override
    @LogInOut(StandardLevel.INFO)
    public SearchDunsResponse if08SearchDuns(SearchDunsRequest searchDunsRequest) throws P1FaultMessage {
        final SearchDunsResponse searchDunsResponse = new SearchDunsResponse();
        searchDunsResponse.setDunsExists(Boolean.TRUE);
        return searchDunsResponse;
    }

}
