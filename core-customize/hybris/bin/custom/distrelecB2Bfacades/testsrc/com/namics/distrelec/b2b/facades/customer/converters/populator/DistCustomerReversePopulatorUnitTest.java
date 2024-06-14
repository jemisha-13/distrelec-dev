package com.namics.distrelec.b2b.facades.customer.converters.populator;

import com.namics.distrelec.b2b.core.model.DistFunctionModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.company.B2BCommerceB2BUserGroupService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.strategies.B2BUserGroupsLookUpStrategy;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.converters.populator.AddressReversePopulator;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistCustomerReversePopulatorUnitTest {

    @Mock
    private DistrelecCodelistService codelistService;

    @Mock
    private CustomerNameStrategy customerNameStrategy;

    @Mock
    private B2BUnitService<B2BUnitModel, UserModel> b2BUnitService;

    @Mock
    private B2BUserGroupsLookUpStrategy b2BUserGroupsLookUpStrategy;

    @Mock
    private B2BCommerceB2BUserGroupService b2BCommerceB2BUserGroupService;

    @Mock
    private AddressReversePopulator addressReversePopulator;

    @Mock
    private ModelService modelService;

    @InjectMocks
    private DistCustomerReversePopulator distCustomerReversePopulator;

    @Test
    public void testPopulate() {
        // given
        CustomerData source = mock(CustomerData.class);
        String functionCode = "functionCode";
        DistFunctionModel distFunctionModel = mock(DistFunctionModel.class);
        B2BUnitData b2bUnitData = mock(B2BUnitData.class);
        B2BCustomerModel target = new B2BCustomerModel();
        AddressModel addressModel = new AddressModel();
        target.setContactAddress(addressModel);
        target.setNewsletter(false);
        target.setPhoneMarketingConsent(false);
        PrincipalGroupModel principalGroupModel = mock(PrincipalGroupModel.class);
        target.setGroups(Set.of(principalGroupModel));

        // when
        when(source.getFunctionCode()).thenReturn(functionCode);
        when(codelistService.getDistFunction(functionCode)).thenReturn(distFunctionModel);
        when(source.getUnit()).thenReturn(b2bUnitData);
        when(source.isNewsletter()).thenReturn(true);
        when(source.isSubscribePhoneMarketing()).thenReturn(true);

        distCustomerReversePopulator.populate(source, target);

        // then
        assertThat(target.getDistFunction(), equalTo(distFunctionModel));
        assertThat(target.isNewsletter(), is(true));
        assertThat(target.isPhoneMarketingConsent(), is(true));
    }

    @Test
    public void testPopulateContactAddressIsNull() {
        // given
        CustomerData source = mock(CustomerData.class);
        B2BCustomerModel target = new B2BCustomerModel();
        String functionCode = "functionCode";
        DistFunctionModel distFunctionModel = mock(DistFunctionModel.class);
        B2BUnitData b2bUnitData = mock(B2BUnitData.class);
        target.setNewsletter(false);
        target.setPhoneMarketingConsent(false);
        PrincipalGroupModel principalGroupModel = mock(PrincipalGroupModel.class);
        target.setGroups(Set.of(principalGroupModel));
        AddressModel addressModel = mock(AddressModel.class);
        when(modelService.create(AddressModel.class)).thenReturn(addressModel);

        // when
        when(source.getFunctionCode()).thenReturn(functionCode);
        when(codelistService.getDistFunction(functionCode)).thenReturn(distFunctionModel);
        when(source.getUnit()).thenReturn(b2bUnitData);
        when(source.isNewsletter()).thenReturn(true);
        when(source.isSubscribePhoneMarketing()).thenReturn(true);

        distCustomerReversePopulator.populate(source, target);

        // then
        assertThat(target.getDistFunction(), equalTo(distFunctionModel));
        assertThat(target.isNewsletter(), is(true));
        assertThat(target.isPhoneMarketingConsent(), is(true));
        verify(addressModel).setOwner(target);
    }
}
