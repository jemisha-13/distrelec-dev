package com.namics.distrelec.b2b.facades.user.converters.populator;

import com.namics.distrelec.b2b.core.service.user.DistUserService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.CustomerModel;
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
public class DistPrincipalPopulatorUnitTest {

    @Mock
    private DistUserService userService;

    @Mock
    private CustomerModel source;

    @InjectMocks
    private DistPrincipalPopulator populator;

    @Test
    public void testPopulateNotCustomerModel() {
        // given
        PrincipalData target = new PrincipalData();
        PrincipalModel source = mock(PrincipalModel.class);

        // when
        populator.populate(source, target);

        // then
        assertThat(target.getType(), is(nullValue()));
    }

    @Test
    public void testPopulateCustomerModelWithoutType() {
        // given
        PrincipalData target = new PrincipalData();

        // when
        when(source.getCustomerType()).thenReturn(null);

        populator.populate(source, target);

        // then
        assertThat(target.getType(), is(nullValue()));
    }

    @Test
    public void testPopulateCustomerModelWithType() {
        // given
        CustomerType customerType = mock(CustomerType.class);
        PrincipalData target = new PrincipalData();

        // when
        when(source.getCustomerType()).thenReturn(customerType);
        when(customerType.getCode()).thenReturn("TYPE_CODE");
        when(userService.getCustomerType(source)).thenReturn(customerType);

        populator.populate(source, target);

        // then
        assertThat(target.getType(), equalTo("TYPE_CODE"));
    }
}
