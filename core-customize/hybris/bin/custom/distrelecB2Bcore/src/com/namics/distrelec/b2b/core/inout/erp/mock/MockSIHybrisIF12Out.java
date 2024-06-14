/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.mock;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;

import com.distrelec.webservice.if12.v1.GetPaymentTermsRequest;
import com.distrelec.webservice.if12.v1.GetPaymentTermsResponse;
import com.distrelec.webservice.if12.v1.InvoiceType;
import com.distrelec.webservice.if12.v1.P3FaultMessage;
import com.distrelec.webservice.if12.v1.PayRequest;
import com.distrelec.webservice.if12.v1.PayResponse;
import com.distrelec.webservice.if12.v1.SIHybrisIF12Out;
import com.distrelec.webservice.if12.v1.SearchRequest;
import com.distrelec.webservice.if12.v1.SearchResponse;
import com.distrelec.webservice.if12.v1.SearchResponse.Invoices.Invoice;
import com.distrelec.webservice.if12.v1.SendEmailRequest;
import com.distrelec.webservice.if12.v1.SendEmailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockSIHybrisIF12Out implements SIHybrisIF12Out {

    private static final Logger LOG = LoggerFactory.getLogger(MockSIHybrisIF12Out.class);

    private static final int AMOUNT_OF_INVOICES_TO_GENERATE_FOR_SEARCH = 10;

    @Override
    public SendEmailResponse sendEmail(final SendEmailRequest sendEmailRequest) throws P3FaultMessage {
        throw new UnsupportedOperationException();
    }

    @Override
    public SearchResponse search(final SearchRequest searchRequest) throws P3FaultMessage {
        final Random randomGenerator = new Random(searchRequest.getCustomerID().hashCode());
        final SearchResponse response = new SearchResponse();
        response.setInvoices(new SearchResponse.Invoices());
        response.setCustomerID(searchRequest.getCustomerID());

        final List<Invoice> invoiceList = response.getInvoices().getInvoice();
        Objects.requireNonNull(invoiceList);
        IntStream.rangeClosed(1, AMOUNT_OF_INVOICES_TO_GENERATE_FOR_SEARCH)
                .forEach(n -> invoiceList.add(createMockInvoice(searchRequest, randomGenerator)));

        response.setResultTotalSize(BigInteger.valueOf(AMOUNT_OF_INVOICES_TO_GENERATE_FOR_SEARCH));

        return response;
    }

    private SearchResponse.Invoices.Invoice createMockInvoice(final SearchRequest searchRequest, final Random randomGenerator) {
        final SearchResponse.Invoices.Invoice invoice = new SearchResponse.Invoices.Invoice();
        invoice.setContactPersonID(getContactPerson(searchRequest));
        invoice.setInvoiceDueDate(createXMLGregorianDate(200));
        invoice.setInvoiceDate(createXMLGregorianDate(-200));

        final BigDecimal openAmount = BigDecimal.valueOf(randomGenerator.nextInt(90));
        invoice.setInvoiceOpenAmount(openAmount);
        final BigDecimal taxes = BigDecimal.valueOf(randomGenerator.nextInt(10));
        invoice.setInvoiceTaxes(taxes);
        invoice.setInvoiceTotalAmountIncludingTaxes(openAmount.add(taxes));

        invoice.setInvoiceCreationDate(createXMLGregorianDate(-300));
        invoice.setInvoiceCurrency("CHF");
        invoice.setInvoiceNumber(String.valueOf(randomGenerator.nextInt(Integer.MAX_VALUE)));
        invoice.setInvoiceType(InvoiceType.D);
        invoice.setInvoiceStatus(
                StringUtils.isNotBlank(searchRequest.getInvoiceStatus()) ? searchRequest.getInvoiceStatus() : "01");
        return invoice;
    }

    private String getContactPerson(final SearchRequest searchRequest) {
        try {
            return searchRequest.getContactPersonFilter().getContactPerson().get(0);
        } catch (final Exception e) {
            return searchRequest.getCustomerID();
        }
    }

    @Override
    public PayResponse pay(final PayRequest payRequest) throws P3FaultMessage {
        throw new UnsupportedOperationException();
    }

    @Override
    public GetPaymentTermsResponse getPaymentTerms(final GetPaymentTermsRequest getPaymentTermsRequest) throws P3FaultMessage {
        throw new UnsupportedOperationException();
    }

    private XMLGregorianCalendar createXMLGregorianDate(final int durationInDays) {
        final GregorianCalendar c = new GregorianCalendar();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_MONTH, durationInDays);
        XMLGregorianCalendar calendar = null;
        try {
            calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (final DatatypeConfigurationException e) {
            LOG.warn("Exception occurred during date creation", e);
        }
        return calendar;
    }

}
