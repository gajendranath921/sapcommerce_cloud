/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.dataimport.batch.aop;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.Assert;


/**
 * Aspect to activate the tenant in the current thread.
 */
public class TenantActivationAspect
{
	private static final Logger LOG = Logger.getLogger(TenantActivationAspect.class);
	private final Tenant currentTenant;

	public TenantActivationAspect()
	{
		currentTenant = Registry.getCurrentTenant();
		Assert.notNull(currentTenant, "Tenant should not be null");

		if (LOG.isInfoEnabled())
		{
			LOG.info(String.format("Configured tenant %s for aspect", currentTenant));
		}
	}

	/**
	 * Invokes a method and with an activated tenant. If no tenant is activated, the tenant set during initialization is
	 * used.
	 * 
	 * @param pjp
	 *           proceeding join point
	 * @return result of the invocation
	 * @throws Throwable
	 */
	public Object execute(final ProceedingJoinPoint pjp) throws Throwable 
	{
		if (Registry.hasCurrentTenant() && Registry.isCurrentTenant(currentTenant))
		{
			return pjp.proceed();
		}

		// Thread does not have a tenant, setup our tenant on the thread
		Registry.setCurrentTenant(currentTenant);

		if (LOG.isInfoEnabled())
		{
			LOG.info(String.format("Setting tenant %s on the current thread %s", currentTenant, Thread.currentThread()));
		}
		try
		{
			return pjp.proceed();
		}
		finally
		{
			Registry.unsetCurrentTenant();
		}
	}
}
