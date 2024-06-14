package com.namics.distrelec.b2b.facades.order.invoice.converters;

import com.distrelec.webservice.sap.v1.InvoiceSearchEntryOrderResponse;
import com.distrelec.webservice.sap.v1.InvoiceSearchEntryResponse;
import com.distrelec.webservice.sap.v1.InvoiceSearchResponse;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.facades.order.invoice.data.DistB2BInvoiceHistoryData;
import com.namics.distrelec.b2b.facades.order.invoice.data.DistB2BInvoiceHistoryOrderData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DistB2BInvoiceHistoryDataConverter implements Converter<InvoiceSearchResponse, List<DistB2BInvoiceHistoryData>> {

    private static final Logger LOG = LogManager.getLogger(DistB2BInvoiceHistoryDataConverter.class);
    private static final String URL_PREFIX = "/invoice-document-url";

    @Override
    public List<DistB2BInvoiceHistoryData> convert(final InvoiceSearchResponse source) {
        return convert(source, new ArrayList<DistB2BInvoiceHistoryData>());
    }

    @Override
    public List<DistB2BInvoiceHistoryData> convert(final InvoiceSearchResponse source, final List<DistB2BInvoiceHistoryData> prototype) {

        if (source == null || CollectionUtils.isEmpty(source.getInvoices())) {
            // return empty list
            return new ArrayList<DistB2BInvoiceHistoryData>();
        }

        for (final InvoiceSearchEntryResponse invoiceEntry : source.getInvoices()) {
            final DistB2BInvoiceHistoryData invoiceData = new DistB2BInvoiceHistoryData();
            invoiceData.setCurrency(invoiceEntry.getCurrencyCode().value());
            invoiceData.setInvoiceDate(SoapConversionHelper.convertDate(invoiceEntry.getInvoiceDate()));
            // Replace the internal host name by the web-site URL
            invoiceData.setInvoiceDocumentURL(getDocumentURL(invoiceEntry.getInvoiceDocId(), URL_PREFIX));
            invoiceData.setInvoiceDueDate(SoapConversionHelper.convertDate(invoiceEntry.getInvoiceDueDate()));
            invoiceData.setInvoiceNumber(invoiceEntry.getInvoiceId());
            invoiceData.setInvoiceStatus(invoiceEntry.getInvoiceStatus().name());
            invoiceData.setInvoiceTotal(Double.valueOf(invoiceEntry.getInvoiceTotal()));

            final List<DistB2BInvoiceHistoryOrderData> orders = new ArrayList<DistB2BInvoiceHistoryOrderData>();
            if (invoiceEntry.getOrders() != null) {
                for (final InvoiceSearchEntryOrderResponse orderEntry : invoiceEntry.getOrders()) {
                    final DistB2BInvoiceHistoryOrderData order = new DistB2BInvoiceHistoryOrderData();
                    order.setContactName(orderEntry.getContactName());
                    order.setOrderNumber(orderEntry.getOrderId());
                    orders.add(order);
                }
            }
            invoiceData.setRelatedOrders(orders);
            prototype.add(invoiceData);
        }

        return prototype;
    }

    private String getDocumentURL(final String originalURL, final String prefix) {
        if (StringUtils.isNotEmpty(originalURL)) {
            try {
                return prefix + (new URL(originalURL)).getFile();
            } catch (final MalformedURLException e) {
                LOG.warn(e.getMessage(), e);
            }
        }

        return null;
    }
}
