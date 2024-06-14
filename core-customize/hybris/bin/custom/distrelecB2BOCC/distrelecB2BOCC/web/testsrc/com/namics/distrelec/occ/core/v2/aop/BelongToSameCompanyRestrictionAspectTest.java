package com.namics.distrelec.occ.core.v2.aop;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.aspectj.lang.JoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.customer.CustomerService;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.user.UserService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BelongToSameCompanyRestrictionAspectTest {

    static final String customerId = "customerId";

    @InjectMocks
    BelongToSameCompanyRestrictionAspect aspect;

    @Mock
    B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;

    @Mock
    CustomerService customerService;

    @Mock
    UserService userService;

    @Mock
    B2BCustomerModel currentUser;

    @Mock
    B2BCustomerModel requestedCustomer;

    @Mock
    JoinPoint jp;

    @Before
    public void setUp() {
        when(jp.getArgs()).thenReturn(new Object[] {customerId});
        when(customerService.getCustomerByCustomerId(customerId)).thenReturn(requestedCustomer);
        when(userService.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    public void belongsToTheSameCompanyIfB2BUnitPksAreEqual() {
        B2BUnitModel sameCompany = mockB2BUnit(15L);

        when(b2BUnitService.getParent(currentUser)).thenReturn(sameCompany);
        when(b2BUnitService.getParent(requestedCustomer)).thenReturn(sameCompany);

        aspect.checkUser(jp);
    }

    @Test
    public void throwsAnExceptionIfTheyDoNotBelongToTheSameCompany() {
        B2BUnitModel firstCompany = mockB2BUnit(16L);
        B2BUnitModel secondCompany = mockB2BUnit(17L); // has a different pk

        when(b2BUnitService.getParent(currentUser)).thenReturn(firstCompany);
        when(b2BUnitService.getParent(requestedCustomer)).thenReturn(secondCompany);

        assertThatThrownBy(() -> aspect.checkUser(jp)).hasMessage("User is not allowed to access");
    }

    private B2BUnitModel mockB2BUnit(long pk) {
        PK pkInstance = PK.fromLong(pk);
        B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        when(b2bUnit.getPk()).thenReturn(pkInstance);
        return b2bUnit;
    }
}
