/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.ordercancel;

import de.hybris.platform.ordercancel.events.CancelFinishedEvent;
import de.hybris.platform.ordercancel.events.CancelPendingEvent;
import de.hybris.platform.ordercancel.impl.SendEventOrderCancelNotification;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.servicelayer.event.EventService;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class SendEventOrderCancelNotificationTest
{
	@InjectMocks
	private final SendEventOrderCancelNotification sendEventOrderCancelNotification = new SendEventOrderCancelNotification();

	@Mock
	private EventService eventService;



	/**
	 * Test method for
	 * {@link SendEventOrderCancelNotification#sendCancelFinishedNotifications(OrderCancelRecordEntryModel)} .
	 */
	@Test
	public void testSendCancelFinishedNotifications()
	{
		final OrderCancelRecordEntryModel cancelRequestRecordEntry = new OrderCancelRecordEntryModel();
		sendEventOrderCancelNotification.sendCancelFinishedNotifications(cancelRequestRecordEntry);

		final Matcher<CancelFinishedEvent> matcher = new BaseMatcher()
		{

			@Override
			public boolean matches(final Object arg0)
			{
				if (arg0 instanceof CancelFinishedEvent)
				{
					final CancelFinishedEvent event = (CancelFinishedEvent) arg0;
					return event.getCancelRequestRecordEntry() == cancelRequestRecordEntry;
				}
				return false;
			}

			@Override
			public void describeTo(final Description description)
			{
				description.appendText("event content schould be equal to " + cancelRequestRecordEntry);
			}
		};

		Mockito.verify(eventService).publishEvent(MockitoHamcrest.argThat(matcher));
	}

	/**
	 * Test method for
	 * {@link SendEventOrderCancelNotification#sendCancelPendingNotifications(OrderCancelRecordEntryModel)} .
	 */
	@Test
	public void testSendCancelPendingNotifications()
	{
		final OrderCancelRecordEntryModel cancelRequestRecordEntry = new OrderCancelRecordEntryModel();
		sendEventOrderCancelNotification.sendCancelPendingNotifications(cancelRequestRecordEntry);

		final Matcher<CancelPendingEvent> matcher = new BaseMatcher()
		{

			@Override
			public boolean matches(final Object arg0)
			{
				if (arg0 instanceof CancelPendingEvent)
				{
					final CancelPendingEvent event = (CancelPendingEvent) arg0;
					return event.getCancelRequestRecordEntry() == cancelRequestRecordEntry;
				}
				return false;
			}

			@Override
			public void describeTo(final Description description)
			{
				description.appendText("event content schould be equal to " + cancelRequestRecordEntry);
			}
		};

		Mockito.verify(eventService).publishEvent(MockitoHamcrest.argThat(matcher));
	}

}
