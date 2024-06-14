package com.namics.distrelec.b2b.facades.order.invoice.converters;

import com.distrelec.webservice.sap.v1.CurrencyCode;
import com.distrelec.webservice.sap.v1.InvoiceSearchEntryOrderResponse;
import com.distrelec.webservice.sap.v1.InvoiceSearchEntryResponse;
import com.distrelec.webservice.sap.v1.InvoiceSearchResponse;
import com.distrelec.webservice.sap.v1.InvoiceStatus;
import com.namics.distrelec.b2b.facades.order.invoice.data.DistB2BInvoiceHistoryData;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistB2BInvoiceHistoryDataConverterUnitTest {

    DistB2BInvoiceHistoryDataConverter converter = new DistB2BInvoiceHistoryDataConverter();

    @Test
    public void testConvert() {
        // given
        InvoiceSearchResponse source = mock(InvoiceSearchResponse.class);
        List<DistB2BInvoiceHistoryData> prototype = new ArrayList<>();
        InvoiceSearchEntryResponse invoiceSearchEntryResponse = mock(InvoiceSearchEntryResponse.class);
        InvoiceSearchEntryOrderResponse orderEntry = mock(InvoiceSearchEntryOrderResponse.class);

        // when
        when(source.getInvoices()).thenReturn(List.of(invoiceSearchEntryResponse));
        when(invoiceSearchEntryResponse.getCurrencyCode()).thenReturn(CurrencyCode.CHF);
        when(invoiceSearchEntryResponse.getInvoiceDocId()).thenReturn("https://www.distrelec.com/document.pdf");
        when(orderEntry.getContactName()).thenReturn("Distrelec Customer");
        when(orderEntry.getOrderId()).thenReturn("ORD123");
        when(invoiceSearchEntryResponse.getOrders()).thenReturn(List.of(orderEntry));
        when(invoiceSearchEntryResponse.getInvoiceStatus()).thenReturn(InvoiceStatus.OPEN);

        List<DistB2BInvoiceHistoryData> result = converter.convert(source, prototype);

        // then
        assertThat(result.get(0).getCurrency(), equalTo("CHF"));
        assertThat(result.get(0).getInvoiceDocumentURL(), equalTo("/invoice-document-url/document.pdf"));
        assertThat(result.get(0).getRelatedOrders().get(0).getContactName(), equalTo("Distrelec Customer"));
        assertThat(result.get(0).getRelatedOrders().get(0).getOrderNumber(), equalTo("ORD123"));
    }
}
