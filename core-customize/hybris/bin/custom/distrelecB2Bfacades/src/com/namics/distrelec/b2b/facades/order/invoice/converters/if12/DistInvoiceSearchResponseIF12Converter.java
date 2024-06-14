package com.namics.distrelec.b2b.facades.order.invoice.converters.if12;

import com.distrelec.webservice.if12.v1.InvoiceSearchOrderResponse;
import com.distrelec.webservice.if12.v1.SearchResponse;
import com.namics.distrelec.b2b.facades.order.invoice.data.DistB2BInvoiceHistoryOrderData;
import com.namics.distrelec.b2b.facades.order.invoice.data.DistOnlineInvoiceSearchData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang.StringUtils;
import org.fest.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DistInvoiceSearchResponseIF12Converter implements Converter<SearchResponse, List<DistOnlineInvoiceSearchData>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DistInvoiceSearchResponseIF12Converter.class);
    private static final String URL_PREFIX = "/invoice-document-url";
    public static final String BLANK_STRING = "";
    public static final String ZERO_ONE = "01";
    public static final String ZERO_TWO = "02";
    public static final String OPEN = "OPEN";
    public static final String PAID = "PAID";
    public static final String ALL = "ALL";

    /**
     * Converts SearchResponse into a list of objects which are used to render information on the Invoice Manager.
     * @param searchResponse The {@link SearchResponse} from SAP
     * @return A list of processed Invoices as {@link DistOnlineInvoiceSearchData} ready to be used in the Invoice Manager.
     * @throws ConversionException when an error occurs in the conversion process.
     */
    @Override
    public List<DistOnlineInvoiceSearchData> convert(SearchResponse searchResponse) throws ConversionException {
        return convert(searchResponse, new ArrayList<>());
    }

    /**
     * Converts SearchResponse into a list of objects which are used to render information on the Invoice Manager.
     * @param searchResponse The {@link SearchResponse} from SAP
     * @return A list of processed Invoices as {@link DistOnlineInvoiceSearchData} ready to be used in the Invoice Manager.
     * @throws ConversionException when an error occurs in the conversion process.
     */
    @Override
    public List<DistOnlineInvoiceSearchData> convert(SearchResponse searchResponse, List<DistOnlineInvoiceSearchData> prototype) throws ConversionException {
        if(null == prototype || null == searchResponse.getInvoices() || Collections.isEmpty(searchResponse.getInvoices().getInvoice())) {
            LOGGER.warn("SearchResponse returned from IF12 Soap Server is null");
            return prototype;
        }

        LOGGER.info("Processing Search Response for :"+searchResponse.getCustomerID());

        for(SearchResponse.Invoices.Invoice invoice : searchResponse.getInvoices().getInvoice()){
            DistOnlineInvoiceSearchData data = new DistOnlineInvoiceSearchData();
            data.setCustomerId(invoice.getContactPersonID());

            setInvoiceData(data, invoice);
            setFinancialData(data, invoice);
            data.setInvoices(searchResponse.getInvoices());
            data.setResultTotalSize(searchResponse.getResultTotalSize());

            List<DistB2BInvoiceHistoryOrderData> orders = processOrders(invoice);
            data.setRelatedOrders(orders);
            prototype.add(data);

        }
        return prototype;
    }

    /***
     * Processes related orders and adds them to a list.
     * @param invoice The invoice section of the Search Response from SAP.
     * @return A list of orders related to an invoice.
     */
    private List<DistB2BInvoiceHistoryOrderData> processOrders(SearchResponse.Invoices.Invoice invoice) {
        final List<DistB2BInvoiceHistoryOrderData> orders = new ArrayList<DistB2BInvoiceHistoryOrderData>();

        for(InvoiceSearchOrderResponse order: invoice.getOrder()){
            DistB2BInvoiceHistoryOrderData if12OrderData = new DistB2BInvoiceHistoryOrderData();
            if12OrderData.setContactName(order.getContactName());
            if12OrderData.setOrderNumber(order.getOrderId());
            orders.add(if12OrderData);
        }

        return orders;
    }

    /**
     * Sets the financial data for invoices
     * @param data The Data passed onto the INvoice History on the site.
     * @param invoice The Invoice part of the Search Response from SAP.
     */
    private void setFinancialData(DistOnlineInvoiceSearchData data , SearchResponse.Invoices.Invoice invoice) {
        BigDecimal totalWithTaxes = invoice.getInvoiceTotalAmountIncludingTaxes();
        BigDecimal taxAmount = invoice.getInvoiceTaxes();
        BigDecimal subtotal = totalWithTaxes.subtract(taxAmount);

        data.setCurrency(invoice.getInvoiceCurrency());
        data.setInvoiceTotal(totalWithTaxes.doubleValue());
        data.setInvoiceTaxes(taxAmount.doubleValue());
        data.setInvoiceTotalWithoutTaxes(subtotal.doubleValue());
    }

    /**
     * Set invoice data to the object to be used on the site.
     * @param data The data which will be used on the site.
     * @param invoice The Invoice part of the Search Response from SAP.
     */
    private void setInvoiceData(DistOnlineInvoiceSearchData data , SearchResponse.Invoices.Invoice invoice){
        data.setInvoiceDate(convertToDate(invoice.getInvoiceDate()));
        data.setInvoiceDocumentURL(invoice.getInvoiceDocURL());
        data.setInvoiceDueDate(convertToDate(invoice.getInvoiceDueDate()));
        data.setInvoiceNumber(invoice.getInvoiceNumber());
        data.setInvoiceStatus(mapStatus(invoice.getInvoiceStatus()));
        data.setInvoiceDocumentURL(getDocumentURL(invoice.getInvoiceDocURL()));
        data.setInvoiceType(invoice.getInvoiceType().value());
    }

    /**
     * Converts an {@link XMLGregorianCalendar} format to a {@ Date}
     * @param calendar The calendar from the SAP Response.
     * @return The converted calendar, as a date.
     */
    private Date convertToDate(XMLGregorianCalendar calendar){
        return calendar.toGregorianCalendar().getTime();
    }

    private String mapStatus(String status){
        String convertedStatus = BLANK_STRING;

        switch(status.toLowerCase()){
            case ZERO_ONE:
                convertedStatus = OPEN;
                break;
            case ZERO_TWO:
                convertedStatus = PAID;
                break;
            default:
                convertedStatus = ALL;
                break;

        }
        return convertedStatus;
    }

    /**
     *  Constructs a link for invoice downloads.
     * @param originalURL The URL from SAP.
     * @return the link for invoicing or null if it doesn't exist.
     */
    private String getDocumentURL(final String originalURL) {
        if (StringUtils.isNotEmpty(originalURL)) {
            try {
                return URL_PREFIX + (new URL(originalURL)).getFile();
            } catch (final MalformedURLException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        return null;
    }
}
