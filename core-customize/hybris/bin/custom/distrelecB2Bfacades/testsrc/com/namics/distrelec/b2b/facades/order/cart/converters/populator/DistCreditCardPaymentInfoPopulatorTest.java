package com.namics.distrelec.b2b.facades.order.cart.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.namics.distrelec.b2b.core.service.order.DistCardPaymentService;
import com.namics.distrelec.b2b.core.service.user.DistUserService;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistCreditCardPaymentInfoPopulatorTest {

    @Mock
    private DistUserService distUserService;

    @Mock
    private DistCheckoutFacade distCheckoutFacade;

    @Mock
    private DistCardPaymentService distCardPaymentService;

    @InjectMocks
    private DistCreditCardPaymentInfoPopulator distCreditCardPaymentInfoPopulator;

    /**
     * Test A Valid Credit Card
     */
    @Test
    public void testPopulate_ValidCreditCard() {

        System.out.println("Running Test Case : testPopulate_ValidCreditCard");

        final PK pk = PK.parse("123");

        final CreditCardPaymentInfoModel creditCardPaymentInfoModel = mock(CreditCardPaymentInfoModel.class);
        final CCPaymentInfoData ccPaymentInfoData = new CCPaymentInfoData();

        // Set the credit card type
        final CreditCardType creditCardType = CreditCardType.VISA;

        // Mocks
        given(creditCardPaymentInfoModel.getPk()).willReturn(pk);
        given(creditCardPaymentInfoModel.getNumber()).willReturn("1111222233334444");
        given(creditCardPaymentInfoModel.getType()).willReturn(creditCardType);
        given(creditCardPaymentInfoModel.getCcOwner()).willReturn("Test User");
        given(creditCardPaymentInfoModel.getValidFromMonth()).willReturn("05");
        given(creditCardPaymentInfoModel.getValidFromYear()).willReturn("2017");
        given(creditCardPaymentInfoModel.getValidToMonth()).willReturn("06");
        given(creditCardPaymentInfoModel.getValidToYear()).willReturn("2024");

        distCreditCardPaymentInfoPopulator.populate(creditCardPaymentInfoModel, ccPaymentInfoData);
    }

    /**
     * Test a credit card which has current year and expiry year same.
     */
    @Test
    public void testPopulate_ValidFromValidToYearSame() {

        System.out.println("Running Test Case : testPopulate_ValidFromValidToYearSame");

        final PK pk = PK.parse("123");

        final CreditCardPaymentInfoModel creditCardPaymentInfoModel = mock(CreditCardPaymentInfoModel.class);
        final CCPaymentInfoData ccPaymentInfoData = new CCPaymentInfoData();

        // Set the credit card type
        final CreditCardType creditCardType = CreditCardType.VISA;

        // Mocks
        given(creditCardPaymentInfoModel.getPk()).willReturn(pk);
        given(creditCardPaymentInfoModel.getNumber()).willReturn("1111222233334444");
        given(creditCardPaymentInfoModel.getType()).willReturn(creditCardType);
        given(creditCardPaymentInfoModel.getCcOwner()).willReturn("Test User");
        given(creditCardPaymentInfoModel.getValidFromMonth()).willReturn("05");
        given(creditCardPaymentInfoModel.getValidFromYear()).willReturn("2019");
        given(creditCardPaymentInfoModel.getValidToMonth()).willReturn("06");
        given(creditCardPaymentInfoModel.getValidToYear()).willReturn("2018");

        distCreditCardPaymentInfoPopulator.populate(creditCardPaymentInfoModel, ccPaymentInfoData);
    }

    /**
     * Test a credit card which has current year and expiry year same but invalid month.
     */
    @Test
    public void testPopulate_ValidFromValidToYearSame_InvalidMonth() {

        System.out.println("Running Test Case : testPopulate_ValidFromValidToYearSame_InvalidMonth");

        final PK pk = PK.parse("123");

        final CreditCardPaymentInfoModel creditCardPaymentInfoModel = mock(CreditCardPaymentInfoModel.class);
        final CCPaymentInfoData ccPaymentInfoData = new CCPaymentInfoData();

        // Set the credit card type
        final CreditCardType creditCardType = CreditCardType.VISA;

        // Mocks
        given(creditCardPaymentInfoModel.getPk()).willReturn(pk);
        given(creditCardPaymentInfoModel.getNumber()).willReturn("1111222233334444");
        given(creditCardPaymentInfoModel.getType()).willReturn(creditCardType);
        given(creditCardPaymentInfoModel.getCcOwner()).willReturn("Test User");
        given(creditCardPaymentInfoModel.getValidFromMonth()).willReturn("05");
        given(creditCardPaymentInfoModel.getValidFromYear()).willReturn("2019");
        given(creditCardPaymentInfoModel.getValidToMonth()).willReturn("11");
        given(creditCardPaymentInfoModel.getValidToYear()).willReturn("2018");

        distCreditCardPaymentInfoPopulator.populate(creditCardPaymentInfoModel, ccPaymentInfoData);
    }

    /**
     * Test a credit card which has invalid year.
     */
    @Test
    public void testPopulate_InvalidYear() {

        System.out.println("Running Test Case : testPopulate_InvalidYear");

        final PK pk = PK.parse("123");

        final CreditCardPaymentInfoModel creditCardPaymentInfoModel = mock(CreditCardPaymentInfoModel.class);
        final CCPaymentInfoData ccPaymentInfoData = new CCPaymentInfoData();

        // Set the credit card type
        final CreditCardType creditCardType = CreditCardType.VISA;

        // Mocks
        given(creditCardPaymentInfoModel.getPk()).willReturn(pk);
        given(creditCardPaymentInfoModel.getNumber()).willReturn("1111222233334444");
        given(creditCardPaymentInfoModel.getType()).willReturn(creditCardType);
        given(creditCardPaymentInfoModel.getCcOwner()).willReturn("Test User");
        given(creditCardPaymentInfoModel.getValidFromMonth()).willReturn("05");
        given(creditCardPaymentInfoModel.getValidFromYear()).willReturn("2019");
        given(creditCardPaymentInfoModel.getValidToMonth()).willReturn("11");
        given(creditCardPaymentInfoModel.getValidToYear()).willReturn("2015");

        distCreditCardPaymentInfoPopulator.populate(creditCardPaymentInfoModel, ccPaymentInfoData);
    }
}
