/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.namics.distrelec.b2b.fulfilmentprocess.test;

import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.namics.distrelec.b2b.fulfilmentprocess.actions.order.NotifyCustomerAboutFraudAction;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.events.FraudErrorEvent;
import de.hybris.platform.orderprocessing.events.OrderFraudCustomerNotificationEvent;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import junit.framework.Assert;

public class SendOrderFraudCustomerNotificationEventTest
{
	@InjectMocks
	private final NotifyCustomerAboutFraudAction action = new NotifyCustomerAboutFraudAction();

	@Mock
	private EventService eventService;
	@Mock
	private ModelService modelService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testExecuteAction()
	{
		final OrderProcessModel process = new OrderProcessModel();
		final OrderModel order = new OrderModel();
		process.setOrder(order);
		action.executeAction(process);
		Mockito.verify(eventService).publishEvent(argThat(new BaseMatcher<FraudErrorEvent>()
		{

			@Override
			public boolean matches(final Object item)
			{
				if (item instanceof OrderFraudCustomerNotificationEvent)
				{
					final OrderFraudCustomerNotificationEvent event = (OrderFraudCustomerNotificationEvent) item;
					if (event.getProcess().equals(process))
					{
						return true;
					}
				}
				return false;
			}

			@Override
			public void describeTo(final Description description)
			{//nothing to do
			}
		}));

		Mockito.verify(modelService).save(order);
		Assert.assertEquals(OrderStatus.SUSPENDED, order.getStatus());
	}
}
