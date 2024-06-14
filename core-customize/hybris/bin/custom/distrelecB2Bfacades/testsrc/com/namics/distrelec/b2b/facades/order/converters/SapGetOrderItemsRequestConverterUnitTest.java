package com.namics.distrelec.b2b.facades.order.converters;

import com.distrelec.webservice.if19.v1.RMAGetOrderItemsRequest;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOrderHistoryPageableData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
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
public class SapGetOrderItemsRequestConverterUnitTest {

    @Mock
    private UserService userService;

    @Mock
    private DistSalesOrgService distSalesOrgService;

    @InjectMocks
    private SapGetOrderItemsRequestConverter sapGetOrderItemsRequestConverter;

    @Test
    public void testPopulate() {
        // given
        DistOrderHistoryPageableData source = mock(DistOrderHistoryPageableData.class);
        RMAGetOrderItemsRequest target = new RMAGetOrderItemsRequest();
        B2BCustomerModel b2bCustomerModel = mock(B2BCustomerModel.class);
        B2BUnitModel b2bUnitModel = mock(B2BUnitModel.class);
        DistSalesOrgModel salesOrg = mock(DistSalesOrgModel.class);
        LanguageModel languageModel = mock(LanguageModel.class);

        // when
        when(b2bUnitModel.getErpCustomerID()).thenReturn("12345");
        when(source.getOrderNumber()).thenReturn("ORDER123");
        when(userService.getCurrentUser()).thenReturn(b2bCustomerModel);
        when(b2bCustomerModel.getDefaultB2BUnit()).thenReturn(b2bUnitModel);
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(salesOrg);
        when(salesOrg.getCode()).thenReturn("SAP123");
        when(b2bCustomerModel.getSessionLanguage()).thenReturn(languageModel);
        when(languageModel.getIsocode()).thenReturn("en");

        sapGetOrderItemsRequestConverter.populate(source, target);

        // then
        assertThat(target.getCustomerId(), equalTo("12345"));
        assertThat(target.getOrderId(), equalTo("ORDER123"));
        assertThat(target.getSalesOrganization(), equalTo("SAP123"));
        assertThat(target.getSessionLanguage(), equalTo("en"));
    }
}
