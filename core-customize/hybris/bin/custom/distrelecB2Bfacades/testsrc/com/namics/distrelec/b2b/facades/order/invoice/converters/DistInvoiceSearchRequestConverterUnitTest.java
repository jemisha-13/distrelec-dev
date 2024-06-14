package com.namics.distrelec.b2b.facades.order.invoice.converters;

import com.distrelec.webservice.sap.v1.InvoiceSearchRequest;
import com.distrelec.webservice.sap.v1.ObjectFactory;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistInvoiceHistoryPageableData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistInvoiceSearchRequestConverterUnitTest {

    @Mock
    private UserService userService;

    @Mock
    private DistSalesOrgService distSalesOrgService;

    @Mock
    private ObjectFactory sapObjectFactory;

    @InjectMocks
    private DistInvoiceSearchRequestConverter converter;

    @Test
    public void testConvert() {
        // given
        DistInvoiceHistoryPageableData source = mock(DistInvoiceHistoryPageableData.class);
        InvoiceSearchRequest prototype = new InvoiceSearchRequest();
        B2BCustomerModel b2bCustomerModel = mock(B2BCustomerModel.class);
        B2BUnitModel b2bUnitModel = mock(B2BUnitModel.class);
        CurrencyModel currencyModel = mock(CurrencyModel.class);
        DistSalesOrgModel salesOrgModel = mock(DistSalesOrgModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(b2bCustomerModel);
        when(b2bCustomerModel.getDefaultB2BUnit()).thenReturn(b2bUnitModel);
        when(b2bUnitModel.getErpCustomerID()).thenReturn("testCustomerId");
        when(b2bCustomerModel.getSessionCurrency()).thenReturn(currencyModel);
        when(currencyModel.getIsocode()).thenReturn("CHF");
        when(b2bCustomerModel.isShowAllOrderhistory()).thenReturn(false);
        when(salesOrgModel.getCode()).thenReturn("7371");
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(salesOrgModel);
        when(source.getSort()).thenReturn("byStatus");

        InvoiceSearchRequest result = converter.convert(source, prototype);

        // then
        assertThat(result.getCustomerId(), equalTo("testCustomerId"));
        assertThat(result.getSalesOrganization(), equalTo("7371"));
    }
}
