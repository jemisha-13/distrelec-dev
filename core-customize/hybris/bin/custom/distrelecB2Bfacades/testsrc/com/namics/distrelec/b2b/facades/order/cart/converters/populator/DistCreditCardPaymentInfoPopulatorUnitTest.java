package com.namics.distrelec.b2b.facades.order.cart.converters.populator;

import com.namics.distrelec.b2b.core.service.order.DistCardPaymentService;
import com.namics.distrelec.b2b.core.service.user.DistUserService;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistCreditCardPaymentInfoPopulatorUnitTest {

    private CreditCardPaymentInfoModel creditCardPaymentInfoModel;
    private CCPaymentInfoData ccPaymentInfoData;
    private PK pk;
    private CreditCardType creditCardType;

    @Mock
    private DistUserService distUserService;

    @Mock
    private DistCheckoutFacade distCheckoutFacade;

    @Mock
    private DistCardPaymentService distCardPaymentService;

    @Mock
    private DistCheckoutFacade checkoutFacade;

    @Mock
    private DistUserService userService;

    @InjectMocks
    private DistCreditCardPaymentInfoPopulator distCreditCardPaymentInfoPopulator;

    @Before
    public void setUp() {
        pk = PK.parse("123");
        creditCardPaymentInfoModel = mock(CreditCardPaymentInfoModel.class);
        ccPaymentInfoData = new CCPaymentInfoData();
        creditCardType = CreditCardType.VISA;

        when(creditCardPaymentInfoModel.getPk()).thenReturn(pk);
        when(creditCardPaymentInfoModel.getType()).thenReturn(creditCardType);
    }

    @Test
    public void testPopulateValidCreditCard() {
        // when
        when(creditCardPaymentInfoModel.getNumber()).thenReturn("1111222233334444");
        when(creditCardPaymentInfoModel.getCcOwner()).thenReturn("Test User");
        when(creditCardPaymentInfoModel.getValidFromMonth()).thenReturn("05");
        when(creditCardPaymentInfoModel.getValidFromYear()).thenReturn("2017");
        when(creditCardPaymentInfoModel.getValidToMonth()).thenReturn("06");
        when(creditCardPaymentInfoModel.getValidToYear()).thenReturn("2024");

        distCreditCardPaymentInfoPopulator.populate(creditCardPaymentInfoModel, ccPaymentInfoData);
    }

    @Test
    public void testPopulateCreditCardHasCurrentYearAndExpiryYearSame() {
        // when
        when(creditCardPaymentInfoModel.getNumber()).thenReturn("1111222233334444");
        when(creditCardPaymentInfoModel.getCcOwner()).thenReturn("Test User");
        when(creditCardPaymentInfoModel.getValidFromMonth()).thenReturn("05");
        when(creditCardPaymentInfoModel.getValidFromYear()).thenReturn("2019");
        when(creditCardPaymentInfoModel.getValidToMonth()).thenReturn("06");
        when(creditCardPaymentInfoModel.getValidToYear()).thenReturn("2018");

        distCreditCardPaymentInfoPopulator.populate(creditCardPaymentInfoModel, ccPaymentInfoData);
    }

    @Test
    public void testPopulateCreditCardHasCurrentYearAndExpiryYearSameInvalidMonth() {
        // when
        when(creditCardPaymentInfoModel.getNumber()).thenReturn("1111222233334444");
        when(creditCardPaymentInfoModel.getCcOwner()).thenReturn("Test User");
        when(creditCardPaymentInfoModel.getValidFromMonth()).thenReturn("05");
        when(creditCardPaymentInfoModel.getValidFromYear()).thenReturn("2019");
        when(creditCardPaymentInfoModel.getValidToMonth()).thenReturn("11");
        when(creditCardPaymentInfoModel.getValidToYear()).thenReturn("2018");

        distCreditCardPaymentInfoPopulator.populate(creditCardPaymentInfoModel, ccPaymentInfoData);
    }

    @Test
    public void testPopulateCreditCardWithInvalidYear() {
        // when
        when(creditCardPaymentInfoModel.getNumber()).thenReturn("1111222233334444");
        when(creditCardPaymentInfoModel.getCcOwner()).thenReturn("Test User");
        when(creditCardPaymentInfoModel.getValidFromMonth()).thenReturn("05");
        when(creditCardPaymentInfoModel.getValidFromYear()).thenReturn("2019");
        when(creditCardPaymentInfoModel.getValidToMonth()).thenReturn("11");
        when(creditCardPaymentInfoModel.getValidToYear()).thenReturn("2015");

        distCreditCardPaymentInfoPopulator.populate(creditCardPaymentInfoModel, ccPaymentInfoData);
    }

    @Test
    public void testSetDefaultPaymentInfoWhenIsB2BCustomerAndNotLimitedUserType() {
        // given
        B2BCustomerModel b2bCustomer = mock(B2BCustomerModel.class);

        // when
        when(checkoutFacade.isNotLimitedUserType()).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(b2bCustomer);
        when(b2bCustomer.getDefaultPaymentInfo()).thenReturn(creditCardPaymentInfoModel);

        distCreditCardPaymentInfoPopulator.populate(creditCardPaymentInfoModel, ccPaymentInfoData);

        // then
        assertThat(ccPaymentInfoData.isDefaultPaymentInfo(), equalTo(true));
    }

    @Test
    public void testSetDefaultPaymentInfoWhenIsNotB2BCustomerAndLimitedUserType() {
        // when
        when(checkoutFacade.isNotLimitedUserType()).thenReturn(false);

        distCreditCardPaymentInfoPopulator.populate(creditCardPaymentInfoModel, ccPaymentInfoData);

        // then
        assertThat(ccPaymentInfoData.isDefaultPaymentInfo(), equalTo(false));
    }

    @Test
    public void testSetDefaultPaymentInfoWhenIsNotB2BCustomerAndNotLimitedUserType() {
        // when
        when(checkoutFacade.isNotLimitedUserType()).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(null);

        distCreditCardPaymentInfoPopulator.populate(creditCardPaymentInfoModel, ccPaymentInfoData);

        // then
        assertThat(ccPaymentInfoData.isDefaultPaymentInfo(), equalTo(false));
    }

    @Test
    public void testSetDefaultPaymentInfoWhenIsB2BCustomerAndLimitedUserType() {
        // given
        B2BCustomerModel b2bCustomer = mock(B2BCustomerModel.class);

        // when
        when(checkoutFacade.isNotLimitedUserType()).thenReturn(false);

        distCreditCardPaymentInfoPopulator.populate(creditCardPaymentInfoModel, ccPaymentInfoData);

        // then
        assertThat(ccPaymentInfoData.isDefaultPaymentInfo(), equalTo(false));
    }
}
