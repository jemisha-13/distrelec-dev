package com.namics.distrelec.b2b.facades.rma.converters;

import com.distrelec.webservice.if19.v1.RMACreateRequest;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.rma.CreateRMAOrderEntryDataForm;
import com.namics.distrelec.b2b.core.rma.CreateRMARequestForm;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistCreateRMARequestConverterUnitTest {

    @Mock
    private B2BCustomerService b2bCustomerService;

    @Mock
    private DistSalesOrgService distSalesOrgService;

    @Mock
    private B2BCustomerModel customerModel;

    @Mock
    private B2BUnitModel b2bUnitModel;

    @Mock
    private DistSalesOrgModel distSalesOrgModel;

    @Mock
    private LanguageModel languageModel;

    @Mock
    private CreateRMARequestForm createRMARequestForm;

    @InjectMocks
    private DistCreateRMARequestConverter converter;

    private RMACreateRequest rmaCreateRequest;

    @Before
    public void setUp() {
        when(b2bCustomerService.getCurrentB2BCustomer()).thenReturn(customerModel);
        when(customerModel.getDefaultB2BUnit()).thenReturn(b2bUnitModel);
        when(b2bUnitModel.getErpCustomerID()).thenReturn("0004421434");
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn("7640");
        when(customerModel.getSessionLanguage()).thenReturn(languageModel);
        when(languageModel.getIsocode()).thenReturn("en");
        when(createRMARequestForm.getOrderId()).thenReturn("7120202021122");
        rmaCreateRequest = new RMACreateRequest();
    }

    @Test
    public void testPopulateCustomerId() {
        // when
        converter.populate(createRMARequestForm, rmaCreateRequest);

        // then
        assertThat(rmaCreateRequest.getCustomerId(), equalTo("0004421434"));
    }

    @Test
    public void testPopulateSalesOrganization() {
        // when
        converter.populate(createRMARequestForm, rmaCreateRequest);

        // then
        assertThat(rmaCreateRequest.getSalesOrganization(), equalTo("7640"));
    }

    @Test
    public void testPopulateSessionLanguage() {
        // when
        converter.populate(createRMARequestForm, rmaCreateRequest);

        // then
        assertThat(rmaCreateRequest.getSessionLanguage(), equalTo("en"));
    }

    @Test
    public void testPopulateOrderId() {
        // when
        converter.populate(createRMARequestForm, rmaCreateRequest);

        // then
        assertThat(rmaCreateRequest.getOrders().getOrderId(), equalTo("7120202021122"));
    }

    @Test
    public void testCustomerTextSetCorrectly() {
        // when
        List<CreateRMAOrderEntryDataForm> forms = createTestOrderEntryDataForms();
        prepareOrderEntryDataForms(forms);

        converter.populate(createRMARequestForm, rmaCreateRequest);

        // then
        assertThat(rmaCreateRequest.getOrders().getItems().get(0).getCustomerText(), equalTo("returnSubReason                                             ||customerText"));
    }

    @Test
    public void testRefundTypeSetCorrectly() {
        // when
        List<CreateRMAOrderEntryDataForm> forms = createTestOrderEntryDataForms();
        prepareOrderEntryDataForms(forms);

        converter.populate(createRMARequestForm, rmaCreateRequest);

        // then
        assertThat(rmaCreateRequest.getOrders().getItems().get(0).getRefundType(), equalTo("RefundType1"));
    }

    @Test
    public void testReturnReasonIDSetCorrectly() {
        // when
        List<CreateRMAOrderEntryDataForm> forms = createTestOrderEntryDataForms();
        prepareOrderEntryDataForms(forms);

        converter.populate(createRMARequestForm, rmaCreateRequest);

        // then
        assertThat(rmaCreateRequest.getOrders().getItems().get(0).getReturnReasonID(), equalTo("ReasonID1"));
    }

    @Test
    public void testQuantitySetCorrectly() {
        // when
        List<CreateRMAOrderEntryDataForm> forms = createTestOrderEntryDataForms();
        prepareOrderEntryDataForms(forms);

        converter.populate(createRMARequestForm, rmaCreateRequest);

        // then
        assertThat(rmaCreateRequest.getOrders().getItems().get(0).getQuantity(), equalTo(1L));
    }

    @Test
    public void testItemNumberSetCorrectly() {
        // when
        List<CreateRMAOrderEntryDataForm> forms = createTestOrderEntryDataForms();
        prepareOrderEntryDataForms(forms);

        converter.populate(createRMARequestForm, rmaCreateRequest);

        // then
        assertThat(rmaCreateRequest.getOrders().getItems().get(0).getItemNumber(), equalTo("Item1"));
    }

    private List<CreateRMAOrderEntryDataForm> createTestOrderEntryDataForms() {
        List<CreateRMAOrderEntryDataForm> forms = new ArrayList<>();
        forms.add(createEntryForm("Item1", 1, "RefundType1", "ReasonID1", "returnSubReason", "customerText"));
        return forms;
    }

    private CreateRMAOrderEntryDataForm createEntryForm(String itemNumber, long quantity, String refundType, String returnReasonID, String returnSubReason, String customerText) {
        CreateRMAOrderEntryDataForm form = mock(CreateRMAOrderEntryDataForm.class);
        when(form.getItemNumber()).thenReturn(itemNumber);
        when(form.getQuantity()).thenReturn(quantity);
        when(form.getRefundType()).thenReturn(refundType);
        when(form.getReturnReasonID()).thenReturn(returnReasonID);
        when(form.getReturnSubReason()).thenReturn(returnSubReason);
        when(form.getCustomerText()).thenReturn(customerText);
        return form;
    }

    private void prepareOrderEntryDataForms(List<CreateRMAOrderEntryDataForm> forms) {
        when(createRMARequestForm.getOrderItems()).thenReturn(forms);
    }
}
