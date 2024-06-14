package com.namics.distrelec.b2b.facades.order.invoice.converters.if12;

import com.distrelec.webservice.if12.v1.ObjectFactory;
import com.distrelec.webservice.if12.v1.SearchRequest;
import com.distrelec.webservice.if12.v1.SortCriteriaType;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOnlineInvoiceHistoryPageableData;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class DistInvoiceSearchRequestIF12Converter implements Converter<DistOnlineInvoiceHistoryPageableData, SearchRequest> {

    private static final Logger LOGGER = LogManager.getLogger(DistInvoiceSearchRequestIF12Converter.class);
    private static final String CUSTOMER_ID_NOT_SUPPLIED_MESSAGE = "F01 Customer ID not supplied.";
    private static final String RESULT_SIZE_IS_NOT_SUPPLIED_MESSAGE = "Result size is not supplied";
    private static final String RESULT_OFFSET_IS_NOT_SUPPLIED_MESSAGE = "Result offset is not supplied";
    private static final int ZERO = 0;
    private static final String BY_DATE = "byDate";
    private static final String BY_DUE_DATE = "byDueDate";
    private static final String INVOICE_DATE = "INVOICE_DATE";
    private static final String BY_STATUS = "byStatus";
    private static final String INVOICE_STATUS = "INVOICE_STATUS";
    private static final String BY_TOTAL_PRICE = "byTotalPrice";
    private static final String INVOICE_TOTAL = "INVOICE_TOTAL";
    private static final String INVOICE_DUE_DATE = "INVOICE_DUE_DATE";


    private static final Map<String, String> SORT_CRITERIA_MAP = new HashMap<>();

    /**
     * Populates the CriteriaMap after the Constructor is called.
     */
    static {
        LOGGER.info("Populating SORT_CRITERIA_MAP");
        SORT_CRITERIA_MAP.put(BY_DATE, INVOICE_DATE);
        SORT_CRITERIA_MAP.put(BY_DUE_DATE, INVOICE_DUE_DATE);
        SORT_CRITERIA_MAP.put(BY_STATUS, INVOICE_STATUS);
        SORT_CRITERIA_MAP.put(BY_TOTAL_PRICE, INVOICE_TOTAL);
    }

    private UserService userService;
    private DistSalesOrgService distSalesOrgService;
    @Autowired
    private ObjectFactory sapObjectFactory;

    /**
     * Converts the PageableData from the InvoiceHistory Search into a SearchRequest for SAP.
     *
     * @param source The Pageable items.
     * @return The search request for the IF12 Specification
     * @throws ConversionException if the Conversion throws an exception.
     */
    @Override
    public final SearchRequest convert(final DistOnlineInvoiceHistoryPageableData source) throws ConversionException {
        return convert(source, sapObjectFactory.createSearchRequest());
    }

    /**
     * Converts the PageableData from the InvoiceHistory Search into a SearchRequest for SAP.
     *
     * @param source The Pageable items.
     * @return The search request for the IF12 Specification
     * @throws ConversionException if the Conversion throws an exception.
     */
    @Override
    public final SearchRequest convert(final DistOnlineInvoiceHistoryPageableData source, final SearchRequest prototype) throws ConversionException {
        if(null == source){
            LOGGER.warn("Pageable Data object is null, returning Search Request.");
            return prototype;
        }

        LOGGER.debug("Converting: {} to search request", source.getCustomerID());
        checkParameterValidity(source);
        final B2BCustomerModel currentUser = (B2BCustomerModel) getUserService().getCurrentUser();
        prototype.setSalesOrganization(getDistSalesOrgService().getCurrentSalesOrg().getCode());
        prototype.setCustomerID(currentUser.getDefaultB2BUnit().getErpCustomerID());
        prototype.setInvoiceStatus(source.getInvoiceStatusType());

        final Date fromInvoiceDate = source.getInvoiceDateFrom();
        final Date toInvoiceDate = source.getInvoiceDateTo();
        if(null != fromInvoiceDate && null != toInvoiceDate){
            prototype.setInvoiceDateFilter(createInvoiceDateFilter(fromInvoiceDate, toInvoiceDate));
        }

        final Date fromDueDate = source.getDueDateFrom();
        final Date toDueDate = source.getDueDateTo();

        if(null != fromDueDate && null != toDueDate){
            prototype.setDueDateFilter(createDueDateFilter(fromDueDate, toDueDate));
        }

        prototype.setInvoiceTotalFilter(createInvoiceTotalFilter(source.getTotalAmountFrom(), source.getTotalAmountTo()));
        prototype.setSalesOrderFilter(createSalesOrderFilter(removeNullAndBlanks(source.getSalesOrderNumbers())));

        final List<String> filteredContactPersonIDs = removeNullAndBlanks(source.getContactPersonIDs());
        if(filteredContactPersonIDs.size() > ZERO) {
            LOGGER.debug("Found {} contact person ids for : {}", filteredContactPersonIDs.size(), source.getCustomerID());
            prototype.setContactPersonFilter(createContactPersonFilter(currentUser, filteredContactPersonIDs));
        }

        final List<String> filteredInvoiceNumbers = removeNullAndBlanks(source.getInvoiceNumbers());
        if(filteredInvoiceNumbers.size() > ZERO) {
            LOGGER.debug("Found {} invoice numbers for : {}", filteredInvoiceNumbers.size(), source.getCustomerID());
            prototype.setInvoiceNumberFilter(createInvoiceNumberFilter(source.getInvoiceNumbers()));
        }

        final List<String> filteredSalesOrderReferenceNumbers = removeNullAndBlanks(source.getSalesOrderReferenceNumbers());
        if(filteredSalesOrderReferenceNumbers.size() > ZERO) {
            LOGGER.debug("Found {} sales order reference numbers for : {}", filteredSalesOrderReferenceNumbers.size(), source.getCustomerID());
            prototype.setSalesOrderReferenceFilter(createSalesOrderReferenceNumberFilter(source.getSalesOrderReferenceNumbers()));
        }

        final List<String> filteredArticleNumbers = removeNullAndBlanks(source.getInvoicesContainingArticle());
        if(filteredArticleNumbers.size() > ZERO){
            LOGGER.debug("Found {} article numbers : {}", filteredArticleNumbers.size(), source.getCustomerID());
            prototype.setArticleFilter(createArticleFilter(source.getInvoicesContainingArticle()));
        }

        prototype.setPagingOptions(createPagingOptions(source));

        return prototype;
    }

    private List<String> removeNullAndBlanks(final List<String> unfilteredList) {
        return unfilteredList.stream().filter(value -> value != null && value.length() > ZERO).collect(Collectors.toList());
    }

    private void checkParameterValidity(final DistOnlineInvoiceHistoryPageableData source) {
        Assert.notNull(source.getCustomerID(), CUSTOMER_ID_NOT_SUPPLIED_MESSAGE);
        Assert.hasLength(source.getCustomerID(), CUSTOMER_ID_NOT_SUPPLIED_MESSAGE);
        Assert.notNull(source.getResultSize(), RESULT_SIZE_IS_NOT_SUPPLIED_MESSAGE);
        Assert.notNull(source.getResultOffset(), RESULT_OFFSET_IS_NOT_SUPPLIED_MESSAGE);

    }

    private XMLGregorianCalendar dateToXmlGregorianCalendar(final Date date) {
        if(null == date){
            return null;
        }

        try {
            final GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(date);

            final XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            xmlCalendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
            return xmlCalendar;
        } catch (final DatatypeConfigurationException e) {
            LOGGER.error("DatatypeFactory cannot be instantiated: ", e);
            return null;
        }
    }

    private SearchRequest.ContactPersonFilter createContactPersonFilter(final B2BCustomerModel currentUser, List<String> contactPersonIds) {
        SearchRequest.ContactPersonFilter contactPersonFilter = null;
        contactPersonIds = removeNullAndBlanks(contactPersonIds);
        if(contactPersonIds.size() == ZERO){
            return contactPersonFilter;
        }

        contactPersonFilter = new SearchRequest.ContactPersonFilter();
        for (final String contactPersonID : contactPersonIds) {
            contactPersonFilter.getContactPerson().add(contactPersonID);
        }
        return contactPersonFilter;
    }

    private SearchRequest.InvoiceDateFilter createInvoiceDateFilter(final Date from, final Date to) {
        final SearchRequest.InvoiceDateFilter invoiceDateFilter = new SearchRequest.InvoiceDateFilter();
        final SearchRequest.InvoiceDateFilter.InvoiceDate invoiceDate = new SearchRequest.InvoiceDateFilter.InvoiceDate();

        invoiceDate.setFrom(dateToXmlGregorianCalendar(from));
        invoiceDate.setTo(dateToXmlGregorianCalendar(to));
        invoiceDateFilter.getInvoiceDate().add(invoiceDate);
        return invoiceDateFilter;

    }

    private SearchRequest.InvoiceTotalFilter createInvoiceTotalFilter(final BigDecimal totalAmountFrom, final BigDecimal totalAmountTo) {
        if (totalAmountFrom != null || totalAmountTo != null) {
            final SearchRequest.InvoiceTotalFilter invoiceTotalFilter = new SearchRequest.InvoiceTotalFilter();
            final SearchRequest.InvoiceTotalFilter.InvoiceTotal invoiceTotal = new SearchRequest.InvoiceTotalFilter.InvoiceTotal();
            if (totalAmountFrom != null) {
                invoiceTotal.setFrom(totalAmountFrom);
            } else {
                invoiceTotal.setFrom(BigDecimal.ZERO);
            }

            if (totalAmountTo != null) {
                invoiceTotal.setTo(totalAmountTo);
            } else {
                invoiceTotal.setTo(BigDecimal.valueOf(Integer.MAX_VALUE));
            }

            invoiceTotalFilter.getInvoiceTotal().add(invoiceTotal);
            return invoiceTotalFilter;
        }
        return null;
    }

    private SearchRequest.ArticleFilter createArticleFilter(final List<String> articleNumbers) {
        final SearchRequest.ArticleFilter articleFilter = new SearchRequest.ArticleFilter();

        for (final String article : articleNumbers) {
            articleFilter.getArticle().add(article);
        }
        return articleFilter;
    }

    private SearchRequest.SalesOrderFilter createSalesOrderFilter(final List<String> salesOrderNumbers) {
        if(salesOrderNumbers.size() == 0){
            return null;
        }

        final SearchRequest.SalesOrderFilter salesOrderFilter = new SearchRequest.SalesOrderFilter();

        for (final String orderNumber : salesOrderNumbers) {
            salesOrderFilter.getSalesOrder().add(orderNumber);
        }
        return salesOrderFilter;
    }

    private SearchRequest.SalesOrderReferenceFilter createSalesOrderReferenceNumberFilter(final List<String> salesOrderReferenceNumbers) {
        final SearchRequest.SalesOrderReferenceFilter salesOrderReferenceFilter = new SearchRequest.SalesOrderReferenceFilter();
        for (final String referenceNumber : salesOrderReferenceNumbers) {
            salesOrderReferenceFilter.getSalesOrderReference().add(referenceNumber);
        }
        return salesOrderReferenceFilter;
    }

    private SearchRequest.PagingOptions createPagingOptions(final DistOnlineInvoiceHistoryPageableData source) {
        final SearchRequest.PagingOptions pagingOptions = new SearchRequest.PagingOptions();
        pagingOptions.setResultSize(BigInteger.valueOf(source.getPageSize()));
        pagingOptions.setResultOffset(BigInteger.valueOf(source.getResultOffset()));

        if(null != source.getSort()) {
            pagingOptions.setSortCriteria(SortCriteriaType.valueOf(SORT_CRITERIA_MAP.get(source.getSort())));
        }
        pagingOptions.setSortAscending(source.isSortAscending());
        return pagingOptions;
    }

    private SearchRequest.DueDateFilter createDueDateFilter(final Date dueDateFrom, final Date dueDateTo) {
        final SearchRequest.DueDateFilter dueDateFilter = new SearchRequest.DueDateFilter();
        final SearchRequest.DueDateFilter.DueDate dueDate = new SearchRequest.DueDateFilter.DueDate();
        dueDate.setFrom(dateToXmlGregorianCalendar(dueDateFrom));
        dueDate.setTo(dateToXmlGregorianCalendar(dueDateTo));
        dueDateFilter.getDueDate().add(dueDate);
        return dueDateFilter;
    }

    private SearchRequest.InvoiceNumberFilter createInvoiceNumberFilter(List<String> invoiceNumbers) {
        SearchRequest.InvoiceNumberFilter invoiceNumberFilter = null;
        invoiceNumbers = removeNullAndBlanks(invoiceNumbers);

        if (invoiceNumbers.size() == ZERO) {
            return invoiceNumberFilter;
        }
        invoiceNumberFilter = new SearchRequest.InvoiceNumberFilter();

        for (final String invoiceNumber : invoiceNumbers) {
            invoiceNumberFilter.getInvoiceNumber().add(invoiceNumber);
        }

        return invoiceNumberFilter;
    }

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    @Required
    public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

    public UserService getUserService() {
        return userService;
    }

    @Required
    public void setUserService(final UserService userService) {
        this.userService = userService;
    }
}
