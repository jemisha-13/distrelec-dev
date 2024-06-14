package com.namics.distrelec.b2b.facades.adobe.datalayer;

import com.namics.distrelec.b2b.facades.adobe.datalayer.data.*;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.namics.distrelec.b2b.facades.adobe.datalayer.impl.DefaultDistDigitalDatalayerFacade;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@UnitTest
public class DistDigitalDatalayerFacadeTest {

    private DistDigitalDatalayerFacade distDigitalDatalayerFacade;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        distDigitalDatalayerFacade = new DefaultDistDigitalDatalayerFacade();
    }

    /**
     * This method is used to set the payment error details on data-layer.
     */
    @Test
    public void testStorePaymentErrorData() {

        System.out.println("Running Test Case : testStorePaymentErrorData");

        // Set the method input parameters
        final DigitalDatalayer digitalDatalayer = new DigitalDatalayer();
        final Page page = new Page();
        final PageInfo pageInfo = new PageInfo();
        pageInfo.setPageName("checkoutPaymentPage");
        page.setPageInfo(pageInfo);
        digitalDatalayer.setPage(page);

        final String errorCode = "00112112";
        final String errorPage = "checkoutPaymentPage";

        distDigitalDatalayerFacade.storePaymentErrorData(digitalDatalayer, errorCode, errorPage);
    }

    /**
     * This method is used to set the payment error details on data-layer and check scenario if page info is null.
     */
    @Test
    public void testStorePaymentErrorData_PageInfoNull() {

        System.out.println("Running Test Case : testStorePaymentErrorData_PageInfoNull");

        // Set the method input parameters
        final DigitalDatalayer digitalDatalayer = new DigitalDatalayer();
        final Page page = new Page();
        digitalDatalayer.setPage(page);

        final String errorCode = "00112112";
        final String errorPage = "checkoutPaymentPage";

        distDigitalDatalayerFacade.storePaymentErrorData(digitalDatalayer, errorCode, errorPage);
    }

    /**
     * This method is used to set the checkout step on data-layer.
     */
    @Test
    public void testStoreCheckoutStep_Payment() {

        System.out.println("Running Test Case : testStoreCheckoutStep_Payment");

        // Set the method input parameters
        final DigitalDatalayer digitalDatalayer = new DigitalDatalayer();
        final Page page = new Page();
        final Checkout checkout = new Checkout();
        checkout.setCheckoutStep(3);
        page.setCheckout(checkout);
        digitalDatalayer.setPage(page);

        final String checkoutPage = "checkoutPaymentPage";

        distDigitalDatalayerFacade.storeCheckoutStep(digitalDatalayer, checkoutPage);
    }

    /**
     * This method is used to set the checkout step on data-layer.
     */
    @Test
    public void testStoreCheckoutStep_Delivery() {

        System.out.println("Running Test Case : testStoreCheckoutStep_Delivery");

        // Set the method input parameters
        final DigitalDatalayer digitalDatalayer = new DigitalDatalayer();
        final Page page = new Page();
        final Checkout checkout = new Checkout();
        checkout.setCheckoutStep(2);
        page.setCheckout(checkout);
        digitalDatalayer.setPage(page);

        final String checkoutPage = "checkoutDetailPage";

        distDigitalDatalayerFacade.storeCheckoutStep(digitalDatalayer, checkoutPage);
    }

    /**
     * This method is used to set the checkout step on data-layer.
     */
    @Test
    public void testStoreCheckoutStep_Billing() {

        System.out.println("Running Test Case : testStoreCheckoutStep_Billing");

        // Set the method input parameters
        final DigitalDatalayer digitalDatalayer = new DigitalDatalayer();
        final Page page = new Page();
        final Checkout checkout = new Checkout();
        checkout.setCheckoutStep(1);
        page.setCheckout(checkout);
        digitalDatalayer.setPage(page);

        final String checkoutPage = "checkoutAddressPage";

        distDigitalDatalayerFacade.storeCheckoutStep(digitalDatalayer, checkoutPage);
    }

    /**
     * This method is used to set the checkout step on data-layer.
     */
    @Test
    public void testStoreCheckoutStep_CheckoutNull() {

        System.out.println("Running Test Case : testStoreCheckoutStep_CheckoutNull");

        // Set the method input parameters
        final DigitalDatalayer digitalDatalayer = new DigitalDatalayer();
        final Page page = new Page();
        digitalDatalayer.setPage(page);

        final String checkoutPage = "checkoutAddressPage";

        distDigitalDatalayerFacade.storeCheckoutStep(digitalDatalayer, checkoutPage);
    }

    @Test
    public void AddProductFamilyNameToSubcategory_PageTypeNotProductFamilyPage_ProductFamilyNameNotSet() {
        PageCategory pageCategory = mock(PageCategory.class);
        when(pageCategory.getPageType()).thenReturn(DigitalDatalayerConstants.PageType.CATEGORYPLPPAGE);
        List<String> breadcrumbs = Arrays.asList("aaa","bbb","ccc");
        String productFamilyName = "product-family-name";

        distDigitalDatalayerFacade.addProductFamilyNameToSubcategory(pageCategory, breadcrumbs, productFamilyName);

        verify(pageCategory, never()).setSubCategoryL3(eq(productFamilyName));
    }

    @Test
    public void AddProductFamilyNameToSubcategory_PageTypeProductFamilyPage_ProductFamilyNameSet() {
        PageCategory pageCategory = mock(PageCategory.class);
        when(pageCategory.getPageType()).thenReturn(DigitalDatalayerConstants.PageType.PRODUCTFAMILYPAGE);
        List<String> breadcrumbs = Arrays.asList("aaa","bbb","ccc");
        String productFamilyName = "product-family-name";

        distDigitalDatalayerFacade.addProductFamilyNameToSubcategory(pageCategory, breadcrumbs, productFamilyName);

        verify(pageCategory).setSubCategoryL3(eq(productFamilyName));
    }
}
