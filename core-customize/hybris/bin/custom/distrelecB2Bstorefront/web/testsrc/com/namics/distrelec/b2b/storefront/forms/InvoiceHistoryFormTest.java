package com.namics.distrelec.b2b.storefront.forms;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

@UnitTest
public class InvoiceHistoryFormTest {

    private static final String INVOICE_NUMBER = "7350298022";
    private static final String ORDER_NUMBER = "1000402161";
    private static final String CONTACT_ID = "00012485";
    private static final String ARTICLE_NUMBER = "18055840";
    private static final Date FROM_DATE = new Date();
    private static final Date TO_DATE = new Date();
    private static final Date FROM_DUE_DATE = new Date();
    private static final Date TO_DUE_DATE = new Date();
    private static final BigDecimal MINIMUM_TOTAL = BigDecimal.valueOf(0.0);
    private static final BigDecimal MAXIMUM_TOTAL = BigDecimal.valueOf(10000.0);

    private static final String SORT = "byDate";
    private static final String SORT_TYPE = "DESC";
    private static final String STATUS = "ALL";
    private static int PAGE = 0;
    private static int PAGE_SIZE = 10;

    private InvoiceHistoryForm invoiceHistoryForm;

    @Before
    public void setUp(){
        invoiceHistoryForm = new InvoiceHistoryForm();
    }

    @After
    public void tearDown(){
        invoiceHistoryForm = null;
    }

    @Test
    public void testGetterAndSetters(){
        invoiceHistoryForm.setArticleNumber(ARTICLE_NUMBER);
        invoiceHistoryForm.setToDate(TO_DATE);
        invoiceHistoryForm.setContactId(CONTACT_ID);
        invoiceHistoryForm.setFromDate(FROM_DATE);
        invoiceHistoryForm.setFromDueDate(FROM_DUE_DATE);
        invoiceHistoryForm.setToDate(TO_DATE);
        invoiceHistoryForm.setToDueDate(TO_DUE_DATE);
        invoiceHistoryForm.setInvoiceNumber(INVOICE_NUMBER);
        invoiceHistoryForm.setMaxTotal(MAXIMUM_TOTAL);
        invoiceHistoryForm.setMinTotal(MINIMUM_TOTAL);
        invoiceHistoryForm.setOrdernf(ORDER_NUMBER);
        invoiceHistoryForm.setOrderNumber(ORDER_NUMBER);
        invoiceHistoryForm.setPage(PAGE);
        invoiceHistoryForm.setPageSize(PAGE_SIZE);
        invoiceHistoryForm.setSort(SORT);
        invoiceHistoryForm.setSortType(SORT_TYPE);
        invoiceHistoryForm.setStatus(STATUS);

        Assert.assertEquals(ARTICLE_NUMBER, invoiceHistoryForm.getArticleNumber());
        Assert.assertEquals(TO_DATE, invoiceHistoryForm.getToDate());
        Assert.assertEquals(CONTACT_ID, invoiceHistoryForm.getContactId());
        Assert.assertEquals(FROM_DATE, invoiceHistoryForm.getFromDate());
        Assert.assertEquals(FROM_DUE_DATE, invoiceHistoryForm.getFromDueDate());
        Assert.assertEquals(TO_DUE_DATE, invoiceHistoryForm.getToDueDate());
        Assert.assertEquals(INVOICE_NUMBER, invoiceHistoryForm.getInvoiceNumber());
        Assert.assertEquals(MAXIMUM_TOTAL, invoiceHistoryForm.getMaxTotal());
        Assert.assertEquals(MINIMUM_TOTAL, invoiceHistoryForm.getMinTotal());
        Assert.assertEquals(ORDER_NUMBER, invoiceHistoryForm.getOrdernf());
        Assert.assertEquals(ORDER_NUMBER, invoiceHistoryForm.getOrderNumber());
        Assert.assertEquals(PAGE, invoiceHistoryForm.getPage());
        Assert.assertEquals(PAGE_SIZE, invoiceHistoryForm.getPageSize());
        Assert.assertEquals(SORT, invoiceHistoryForm.getSort());
        Assert.assertEquals(SORT_TYPE, invoiceHistoryForm.getSortType());
        Assert.assertEquals(STATUS, invoiceHistoryForm.getStatus());
    }

}
