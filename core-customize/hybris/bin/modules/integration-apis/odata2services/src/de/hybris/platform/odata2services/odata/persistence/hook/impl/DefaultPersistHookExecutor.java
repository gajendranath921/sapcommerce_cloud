/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence.hook.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.odata2services.odata.persistence.hook.PersistHookExecutor;
import de.hybris.platform.odata2services.odata.persistence.hook.PostPersistHook;
import de.hybris.platform.odata2services.odata.persistence.hook.PostPersistHookException;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHookException;

import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * @deprecated use {@link de.hybris.platform.inboundservices.persistence.hook.impl.DefaultPersistenceHookExecutor} instead
 */
@Deprecated(since = "2205", forRemoval = true)
public class DefaultPersistHookExecutor implements PersistHookExecutor
{
	private static final Logger LOG = Log.getLogger(DefaultPersistHookExecutor.class);

	private PersistenceHookRegistry persistHookRegistry;

	@Override
	public Optional<ItemModel> runPrePersistHook(final String hookName, final ItemModel item, final String integrationKey)
	{
		final PrePersistHook hook = persistHookRegistry.getPrePersistHook(hookName, integrationKey);

		return hook != null
				? executePrePersistHook(hookName, hook, item, integrationKey)
				: Optional.of(item);
	}

	@Override
	public void runPostPersistHook(final String hookName, final ItemModel item, final String integrationKey)
	{
		final PostPersistHook hook = persistHookRegistry.getPostPersistHook(hookName, integrationKey);

		if (hook != null)
		{
			executePostPersistHook(hookName, hook, item, integrationKey);
		}
	}

	protected Optional<ItemModel> executePrePersistHook(final String hookName, final PrePersistHook hook, final ItemModel item,
	                                                    final String integrationKey)
	{
		try
		{
			LOG.debug("Executing PrePersistHook ['{}': {}] with item [{}]", hookName, hook.getClass(), item);
			return hook.execute(item);
		}
		catch (final RuntimeException e)
		{
			LOG.error("Exception occurred during the execution of Pre-Persist-Hook: [{}] with item: [{}]", hookName, item, e);
			throw new PrePersistHookException(hookName, e, integrationKey);
		}
	}

	protected void executePostPersistHook(final String hookName, final PostPersistHook hook, final ItemModel item,
	                                      final String integrationKey)
	{
		try
		{
			LOG.debug("Executing PostPersistHook ['{}': {}] with item [{}]", hookName, hook.getClass(), item);
			hook.execute(item);
		}
		catch (final RuntimeException e)
		{
			LOG.error("Exception occurred during the execution of Post-Persist-Hook: [{}] with item: [{}]", hookName, item, e);
			throw new PostPersistHookException(hookName, e, integrationKey);
		}
	}

	protected PersistenceHookRegistry getPersistHookRegistry()
	{
		return persistHookRegistry;
	}

	@Required
	public void setPersistHookRegistry(final PersistenceHookRegistry registry)
	{
		persistHookRegistry = registry;
	}
}
