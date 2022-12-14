/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customercouponservices.retention.hook;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customercouponservices.impl.DefaultCustomerCouponService;
import de.hybris.platform.customercouponservices.model.CouponNotificationModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link CouponNotificationCleanupHook}
 */
@UnitTest
public class CouponNotificationCleanupHookTest
{
	private CouponNotificationCleanupHook couponNotificationCleanupHook;
	@Mock
	private DefaultCustomerCouponService customerCouponService;
	@Mock
	private ModelService modelService;
	@Mock
	private CustomerModel customer;
	@Mock
	private CouponNotificationModel notification;


	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		couponNotificationCleanupHook = new CouponNotificationCleanupHook();
		couponNotificationCleanupHook.setCustomerCouponService(customerCouponService);
		couponNotificationCleanupHook.setModelService(modelService);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testCleanupRelatedObjects_customerNull()
	{
		couponNotificationCleanupHook.cleanupRelatedObjects(null);
	}

	@Test()
	public void testCleanupRelatedObjects_customerWithNotification()
	{
		final List<CouponNotificationModel> couponNotifications = new ArrayList<CouponNotificationModel>();
		couponNotifications.add(notification);
		Mockito.lenient().when(customerCouponService.getCouponNotificationsForCustomer(customer)).thenReturn(couponNotifications);
		Mockito.doNothing().when(modelService).removeAll(couponNotifications);
		couponNotificationCleanupHook.cleanupRelatedObjects(customer);
		Mockito.verify(modelService, Mockito.times(1)).removeAll(couponNotifications);
	}

	@Test()
	public void testCleanupRelatedObjects_customerWithoutNotification()
	{
		final List<CouponNotificationModel> couponNotifications = Collections.emptyList();
		Mockito.lenient().when(customerCouponService.getCouponNotificationsForCustomer(customer)).thenReturn(couponNotifications);
		Mockito.doNothing().when(modelService).removeAll(couponNotifications);
		couponNotificationCleanupHook.cleanupRelatedObjects(customer);
		Mockito.verify(modelService, Mockito.times(0)).removeAll(couponNotifications);
	}
}
