package com.namics.distrelec.b2b.facades.order.converters.populator;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.delivery.AbstractDistDeliveryModeModel;
import com.namics.distrelec.b2b.core.service.user.DistUserService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistDeliveryModePopulatorUnitTest {

    @Mock
    private DistUserService userService;

    @Mock
    private CartService cartService;

    @InjectMocks
    DistDeliveryModePopulator distDeliveryModePopulator = new DistDeliveryModePopulator();

    @Test
    public void testPopulate() {
        // given
        AbstractDistDeliveryModeModel source = mock(AbstractDistDeliveryModeModel.class);
        DeliveryModeData target = new DeliveryModeData();
        B2BCustomerModel customerModel = mock(B2BCustomerModel.class);
        CartModel cartModel = mock(CartModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(customerModel);
        when(cartService.hasSessionCart()).thenReturn(true);
        when(cartService.getSessionCart()).thenReturn(cartModel);
        when(source.getName()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);
        when(source.getDescription()).thenReturn("Standard delivery description");

        distDeliveryModePopulator.populate(source, target);

        // then
        assertThat(target.getName(), equalTo(DistConstants.Shipping.METHOD_STANDARD));
        assertThat(target.getDescription(), equalTo("Standard delivery description"));
        assertThat(target.getTranslationKey(), is(nullValue()));
        assertThat(target.getTranslation(), is(nullValue()));
    }
}
