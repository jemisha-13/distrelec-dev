package com.namics.distrelec.b2b.facades.order.invoice.converters.if12;

import com.distrelec.webservice.if12.v1.SearchRequest;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOnlineInvoiceHistoryPageableData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistInvoiceSearchRequestIF12ConverterUnitTest {

    @Mock
    private UserService userService;

    @Mock
    private DistSalesOrgService distSalesOrgService;

    @Mock
    private B2BCustomerModel currentUser;

    @Mock
    private DistSalesOrgModel currentSalesOrg;

    @Mock
    private B2BUnitModel currentB2BUnit;

    @InjectMocks
    private DistInvoiceSearchRequestIF12Converter converter;

    @Test
    public void testConvert() {
        // given
        DistOnlineInvoiceHistoryPageableData source = mock(DistOnlineInvoiceHistoryPageableData.class);
        SearchRequest prototype = new SearchRequest();


        // when
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(currentSalesOrg);

        when(currentSalesOrg.getCode()).thenReturn("SalesOrgCode");
        when(currentB2BUnit.getErpCustomerID()).thenReturn("ErpCustomerID");
        when(currentUser.getDefaultB2BUnit()).thenReturn(currentB2BUnit);

        when(currentB2BUnit.getErpCustomerID()).thenReturn("TestCustomerID");
        when(source.getCustomerID()).thenReturn("customerId");
        when(source.getInvoiceStatusType()).thenReturn("InvoiceStatus");
        when(source.getInvoiceDateFrom()).thenReturn(new Date());
        when(source.getInvoiceDateTo()).thenReturn(new Date());
        when(source.getDueDateFrom()).thenReturn(new Date());
        when(source.getDueDateTo()).thenReturn(new Date());
        when(source.getTotalAmountFrom()).thenReturn(new BigDecimal("100.00"));
        when(source.getTotalAmountTo()).thenReturn(new BigDecimal("200.00"));
        when(source.getSalesOrderNumbers()).thenReturn(Arrays.asList("SO123", "SO456"));
        when(source.getContactPersonIDs()).thenReturn(Arrays.asList("CP123", "CP456"));
        when(source.getInvoiceNumbers()).thenReturn(Arrays.asList("INV123", "INV456"));
        when(source.getSalesOrderReferenceNumbers()).thenReturn(Arrays.asList("SOR123", "SOR456"));
        when(source.getInvoicesContainingArticle()).thenReturn(Arrays.asList("ART123", "ART456"));
        when(source.getPageSize()).thenReturn(10);
        when(source.getResultOffset()).thenReturn(0);
        when(source.getSort()).thenReturn("byStatus");
        when(source.isSortAscending()).thenReturn(true);

        SearchRequest result = converter.convert(source, prototype);

        // then
        assertThat(result.getCustomerID(), equalTo("TestCustomerID"));
        assertThat(result.getContactPersonFilter().getContactPerson().get(0),equalTo("CP123"));
        assertThat(result.getContactPersonFilter().getContactPerson().get(1),equalTo("CP456"));
        assertThat(result.getInvoiceNumberFilter().getInvoiceNumber().get(1), equalTo("INV456"));
        assertThat(result.getSalesOrderFilter().getSalesOrder().get(1), equalTo("SO456"));
    }
}
