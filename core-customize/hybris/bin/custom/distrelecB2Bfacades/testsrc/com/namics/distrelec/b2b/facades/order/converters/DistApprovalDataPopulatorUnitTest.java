package com.namics.distrelec.b2b.facades.order.converters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderHistoryEntryData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistApprovalDataPopulatorUnitTest {

    @Mock
    private B2BWorkflowIntegrationService b2bWorkflowIntegrationService;

    @Mock
    private Converter<OrderModel, OrderData> orderConverter;

    @Mock
    private I18NService i18NService;

    @Mock
    private Converter<OrderHistoryEntryModel, B2BOrderHistoryEntryData> b2bOrderHistoryEntryConverter;

    @InjectMocks
    private DistApprovalDataPopulator distApprovalDataPopulator;

    @Test
    public void testPopulate() {
        // given
        WorkflowActionModel source = mock(WorkflowActionModel.class);
        B2BOrderApprovalData target = new B2BOrderApprovalData();
        OrderData b2bOrderData = mock(OrderData.class);
        OrderModel orderModel = mock(OrderModel.class);
        B2BPermissionResultModel b2bPermissionResultModel = mock(B2BPermissionResultModel.class);
        B2BCustomerModel b2bCustomerModel = mock(B2BCustomerModel.class);
        PrincipalModel principalModel = mock(PrincipalModel.class);
        String customerUid = "customer@distrelec.com";
        WorkflowDecisionModel workflowDecisionModel = mock(WorkflowDecisionModel.class);

        // when
        when(b2bWorkflowIntegrationService.getOrderFromAction(source)).thenReturn(new OrderModel());
        when(orderConverter.convert(orderModel)).thenReturn(b2bOrderData);
        when(i18NService.getSupportedLocales()).thenReturn(Collections.emptySet());
        when(b2bWorkflowIntegrationService.getOrderFromAction(source)).thenReturn(orderModel);
        when(orderModel.getPermissionResults()).thenReturn(Collections.singletonList(b2bPermissionResultModel));
        when(b2bPermissionResultModel.getApprover()).thenReturn(b2bCustomerModel);
        when(b2bCustomerModel.getUid()).thenReturn(customerUid);
        when(source.getPrincipalAssigned()).thenReturn(principalModel);
        when(principalModel.getUid()).thenReturn(customerUid);
        when(source.getSelectedDecision()).thenReturn(workflowDecisionModel);
        when(source.getStatus()).thenReturn(WorkflowActionStatus.IN_PROGRESS);
        when(source.getModifiedtime()).thenReturn(new Date());
        when(source.getCode()).thenReturn("WorkflowActionModelCode");

        distApprovalDataPopulator.populate(source, target);

        // then
        assertThat(target.getWorkflowActionModelCode(), equalTo("WorkflowActionModelCode"));
        assertThat(target.getB2bOrderData(), equalTo(b2bOrderData));
    }

    @Test
    public void testPopulateSetAllDecisions() {
        // given
        WorkflowActionModel source = mock(WorkflowActionModel.class);
        B2BOrderApprovalData target = new B2BOrderApprovalData();
        OrderData b2bOrderData = mock(OrderData.class);
        OrderModel orderModel = mock(OrderModel.class);
        B2BPermissionResultModel b2bPermissionResultModel = mock(B2BPermissionResultModel.class);
        B2BCustomerModel b2bCustomerModel = mock(B2BCustomerModel.class);
        PrincipalModel principalModel = mock(PrincipalModel.class);
        String customerUid = "customer@distrelec.com";
        WorkflowDecisionModel workflowDecisionModel = mock(WorkflowDecisionModel.class);
        List<WorkflowDecisionModel> decisions = new ArrayList<>();
        decisions.add(createDecision("decision1"));
        decisions.add(createDecision("decision2"));


        // when
        when(b2bWorkflowIntegrationService.getOrderFromAction(source)).thenReturn(new OrderModel());
        when(orderConverter.convert(orderModel)).thenReturn(b2bOrderData);
        when(i18NService.getSupportedLocales()).thenReturn(Collections.emptySet());
        when(b2bWorkflowIntegrationService.getOrderFromAction(source)).thenReturn(orderModel);
        when(orderModel.getPermissionResults()).thenReturn(Collections.singletonList(b2bPermissionResultModel));
        when(b2bPermissionResultModel.getApprover()).thenReturn(b2bCustomerModel);
        when(b2bCustomerModel.getUid()).thenReturn(customerUid);
        when(source.getPrincipalAssigned()).thenReturn(principalModel);
        when(principalModel.getUid()).thenReturn(customerUid);
        when(source.getSelectedDecision()).thenReturn(workflowDecisionModel);
        when(source.getStatus()).thenReturn(WorkflowActionStatus.IN_PROGRESS);
        when(source.getModifiedtime()).thenReturn(new Date());
        when(source.getDecisions()).thenReturn(decisions);


        distApprovalDataPopulator.populate(source, target);

        // then
        assertThat(target.getAllDecisions(), hasSize(2));
        assertThat(target.getAllDecisions().get(0), equalTo("DECISION1"));
        assertThat(target.getAllDecisions().get(1), equalTo("DECISION2"));
    }

    @Test
    public void testPopulateWithNoteForSupportedLocal() {
        // given
        WorkflowActionModel source = mock(WorkflowActionModel.class);
        B2BOrderApprovalData target = new B2BOrderApprovalData();
        OrderData b2bOrderData = mock(OrderData.class);
        OrderModel orderModel = mock(OrderModel.class);
        B2BPermissionResultModel b2bPermissionResultModel = mock(B2BPermissionResultModel.class);
        B2BCustomerModel b2bCustomerModel = mock(B2BCustomerModel.class);
        PrincipalModel principalModel = mock(PrincipalModel.class);
        String customerUid = "customer@distrelec.com";
        WorkflowDecisionModel workflowDecisionModel = mock(WorkflowDecisionModel.class);

        // when
        when(b2bWorkflowIntegrationService.getOrderFromAction(source)).thenReturn(new OrderModel());
        when(orderConverter.convert(orderModel)).thenReturn(b2bOrderData);
        when(i18NService.getSupportedLocales()).thenReturn(Collections.emptySet());
        when(b2bWorkflowIntegrationService.getOrderFromAction(source)).thenReturn(orderModel);
        when(orderModel.getPermissionResults()).thenReturn(Collections.singletonList(b2bPermissionResultModel));
        when(b2bPermissionResultModel.getApprover()).thenReturn(b2bCustomerModel);
        when(b2bCustomerModel.getUid()).thenReturn(customerUid);
        when(source.getPrincipalAssigned()).thenReturn(principalModel);
        when(principalModel.getUid()).thenReturn(customerUid);
        when(source.getSelectedDecision()).thenReturn(workflowDecisionModel);
        when(source.getStatus()).thenReturn(WorkflowActionStatus.IN_PROGRESS);
        when(source.getModifiedtime()).thenReturn(new Date());
        when(i18NService.getSupportedLocales()).thenReturn(Set.of(Locale.ENGLISH, Locale.GERMAN));
        when(b2bPermissionResultModel.getNote(Locale.ENGLISH)).thenReturn("EN");

        distApprovalDataPopulator.populate(source, target);

        // then
        assertThat(target.getApprovalComments(), equalTo("EN"));
    }

    private WorkflowDecisionModel createDecision(String qualifier) {
        WorkflowDecisionModel decision = mock(WorkflowDecisionModel.class);
        when(decision.getQualifier()).thenReturn(qualifier);
        return decision;
    }
}
