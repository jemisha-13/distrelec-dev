package com.namics.distrelec.b2b.facades.order.converters;

import com.distrelec.webservice.if15.v1.AddressWithId;
import com.distrelec.webservice.if15.v1.OpenOrders;
import com.distrelec.webservice.if15.v1.ShippingMethodCode;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.erp.PaymentOptionService;
import com.namics.distrelec.b2b.core.inout.erp.ShippingOptionService;
import com.namics.distrelec.b2b.core.inout.erp.converters.ErpOpenOrderExtModelConvertor;
import com.namics.distrelec.b2b.core.service.order.model.ErpOpenOrderExtModel;
import com.namics.distrelec.b2b.core.service.user.DistAddressService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.i18n.daos.CountryDao;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ErpOpenOrderExtModelConvertorUnitTest {

    @Mock
    private UserService userService;

    @Mock
    private CountryDao countryDao;

    @Mock
    private DistAddressService addressService;

    @Mock
    private PaymentOptionService paymentOptionService;

    @Mock
    private ShippingOptionService shippingOptionService;

    @InjectMocks
    private ErpOpenOrderExtModelConvertor erpOpenOrderExtModelConvertor;

    @Test
    public void testPopulate() {
        // given
        OpenOrders source = createMockOpenOrders();
        ErpOpenOrderExtModel target = new ErpOpenOrderExtModel();

        // when
        erpOpenOrderExtModelConvertor.populate(source, target);

        // then
        assertThat(target.getErpCustomerId(), equalTo("12345"));
        assertThat(target.getErpContactId(), equalTo("789"));
        assertThat(target.getOrderStatus(), equalTo("In Progress"));
        assertThat(target.getErpOrderId(), equalTo("ABC123"));
        assertThat(target.getErpBillingAddressId(), equalTo("address"));
        assertThat(target.getErpShippingAddressId(), equalTo("address"));
    }

    private OpenOrders createMockOpenOrders() {
        OpenOrders source = mock(OpenOrders.class);
        AddressWithId billingAddress = createMockAddressWithId("address");
        AddressWithId shippingAddress = createMockAddressWithId("address");

        when(source.isEditableByAllContacts()).thenReturn(true);
        when(source.getCustomerId()).thenReturn("12345");
        when(source.getContactId()).thenReturn("789");
        when(source.getOrderStatus()).thenReturn("In Progress");
        when(source.getOrderId()).thenReturn("ABC123");
        when(source.getBillingAddressId()).thenReturn(billingAddress);
        when(billingAddress.getAddressId()).thenReturn("address");
        when(source.getShippingAddressId()).thenReturn(shippingAddress);
        when(source.getPaymentMethodCode()).thenReturn(DistConstants.PaymentMethods.CREDIT_CARD);
        when(source.getShippingMethodCode()).thenReturn(ShippingMethodCode.N_1);
        when(source.getCustomerReferenceHeaderLevel()).thenReturn("Reference123");

        return source;
    }

    private AddressWithId createMockAddressWithId(String addressId) {
        AddressWithId address = mock(AddressWithId.class);
        when(address.getAddressId()).thenReturn(addressId);
        return address;
    }
}
