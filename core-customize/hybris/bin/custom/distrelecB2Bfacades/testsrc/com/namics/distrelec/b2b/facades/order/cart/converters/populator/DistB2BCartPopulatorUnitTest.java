package com.namics.distrelec.b2b.facades.order.cart.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.b2bacceleratorfacades.order.populators.B2BPaymentTypePopulator;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistB2BCartPopulatorUnitTest {

    @Mock
    private UserService userService;

    @Mock
    private B2BOrderService b2bOrderService;

    @Mock
    private B2BPaymentTypePopulator b2bPaymentTypePopulator;

    @Mock
    private AbstractPopulatingConverter<UserModel, CustomerData> b2bCustomerConverter;

    @InjectMocks
    private DistB2BCartPopulator populator;

    @Test
    public void testPopulateWithNonAnonymousUser() {
        // given
        CartModel cartModel = mock(CartModel.class);
        CartData cartData = new CartData();
        UserModel user = mock(UserModel.class);
        CustomerData customerData = mock(CustomerData.class);

        // when
        when(cartModel.getUser()).thenReturn(user);
        when(userService.isAnonymousUser(user)).thenReturn(false);
        when(b2bCustomerConverter.convert(user)).thenReturn(customerData);

        populator.populate(cartModel, cartData);

        // then
        verify(b2bCustomerConverter).convert(user);
        assertNotNull(cartData.getB2bCustomerData());
    }

    @Test
    public void testPopulateWithAnonymousUser() {
        // given
        CartModel cartModel = mock(CartModel.class);
        CartData cartData = new CartData();
        UserModel user = mock(UserModel.class);

        // when
        when(cartModel.getUser()).thenReturn(user);
        when(userService.isAnonymousUser(user)).thenReturn(true);

        populator.populate(cartModel, cartData);

        // then
        verify(b2bCustomerConverter, never()).convert(user);
        assertNull(cartData.getB2bCustomerData());
    }
}
