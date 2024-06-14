/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;

/**
 * JUnit test class for the {@link DistApprovalDataPopulator} class
 *
 * @author <a href="mailto:nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec AG
 * @since Namics Extensions 1.0
 *
 */
@UnitTest
public class DistApprovalDataConverterTest {

    @Mock
    private B2BWorkflowIntegrationService b2bWorkflowIntegrationService;

    @Mock
    private Converter<OrderModel, OrderData> orderConverter;

    @Mock
    private WorkflowActionModel source;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OrderModel orderModel;

    @InjectMocks
    private final DistApprovalDataPopulator converter = new DistApprovalDataPopulator();

    @Before
    public void prepare() {

        MockitoAnnotations.initMocks(this);
        converter.setOrderConverter(orderConverter);
    }

    /**
     * Test method for {@link com.namics.distrelec.b2b.facades.order.converters.DistApprovalDataPopulator#createTarget()}.
     */
    @Test
    public void testCreateTarget() {
        final B2BOrderApprovalData data = converter.createTarget();
        assertNotNull("The data object must be not null", data);
        assertEquals("The data object must be of type " + B2BOrderApprovalData.class.getName(), data.getClass(), B2BOrderApprovalData.class);
    }

    /**
     * Test method for
     * {@link com.namics.distrelec.b2b.facades.order.converters.DistApprovalDataPopulator#populate(de.hybris.platform.workflow.model.WorkflowActionModel, de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData)}
     * .
     */
    @Test
    public void testPopulateWorkflowActionModelB2BOrderApprovalData() {

        final WorkflowDecisionModel wfOne = new WorkflowDecisionModel();
        wfOne.setCode("one");
        wfOne.setQualifier("oneQ");
        final WorkflowDecisionModel wfTwo = new WorkflowDecisionModel();
        wfTwo.setCode("two");
        wfTwo.setQualifier("twoQ");
        final List<WorkflowDecisionModel> decisions = Arrays.asList(wfOne, wfTwo);

        BDDMockito.given(b2bWorkflowIntegrationService.getOrderFromAction(source)).willReturn(orderModel);
        BDDMockito.given(orderModel.getHistoryEntries()).willReturn(Collections.EMPTY_LIST);
        BDDMockito.given(source.getDecisions()).willReturn(decisions);
        final B2BOrderApprovalData target = new B2BOrderApprovalData();
        converter.populate(source, target);
        assertNotNull("The target object must be not null", target);
        assertEquals("The data object must be of type " + B2BOrderApprovalData.class.getName(), target.getClass(), B2BOrderApprovalData.class);
        assertEquals("The two lists must be the same", target.getAllDecisions(), Arrays.asList("oneQ".toUpperCase(), "twoQ".toUpperCase()));

        // assertNotNull("The target status mut be not null", target.getStatus());

    }

}
