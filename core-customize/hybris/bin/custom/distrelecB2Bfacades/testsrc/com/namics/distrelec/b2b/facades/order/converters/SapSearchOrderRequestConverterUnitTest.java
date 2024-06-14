package com.namics.distrelec.b2b.facades.order.converters;

import com.distrelec.webservice.if15.v1.OrderSearchRequestV2;
import com.namics.distrelec.b2b.core.model.DistOrderStatusModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOrderHistoryPageableData;
import com.namics.distrelec.b2b.core.service.user.DistUserService;
import com.namics.distrelec.b2b.facades.order.Constants;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
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
public class SapSearchOrderRequestConverterUnitTest {

    @Mock
    private DistUserService distUserService;

    @Mock
    private DistrelecCodelistService codelistService;

    @InjectMocks
    SapSearchOrderRequestConverter sapSearchOrderRequestConverter;

    @Test
    public void testPopulate() {
        // given
        DistOrderHistoryPageableData source = mock(DistOrderHistoryPageableData.class);
        OrderSearchRequestV2 target = new OrderSearchRequestV2();
        B2BCustomerModel user = mock(B2BCustomerModel.class);
        B2BUnitModel currentUnit = mock(B2BUnitModel.class);
        DistSalesOrgModel salesOrg = mock(DistSalesOrgModel.class);
        DistOrderStatusModel orderStatus = mock(DistOrderStatusModel.class);

        // when
        when(source.getContactId()).thenReturn("222");
        when(distUserService.getCustomerByContactId("222", B2BCustomerModel.class)).thenReturn(user);
        when(user.getDefaultB2BUnit()).thenReturn(currentUnit);
        when(currentUnit.getSalesOrg()).thenReturn(salesOrg);
        when(salesOrg.getCode()).thenReturn("7371");
        when(currentUnit.getErpCustomerID()).thenReturn("713221");
        when(orderStatus.getCode()).thenReturn("00");
        when(source.getStatus()).thenReturn("ERP_STATUS_IN_PROGRESS");
        when(codelistService.getDistOrderStatusForHybrisOrderStatusCode("ERP_STATUS_IN_PROGRESS")).thenReturn(orderStatus);
        when(source.getSort()).thenReturn("byTotalPrice");
        sapSearchOrderRequestConverter.populate(source, target);

        // then
        assertThat(target.getSalesOrganization(), equalTo("7371"));
        assertThat(target.getCustomerId(), equalTo("713221"));
        assertThat(target.getFilterOrderStatus(), equalTo("00"));
        assertThat(target.getSortCriteria(), equalTo(Constants.ORDER_TOTAL));
    }
}
