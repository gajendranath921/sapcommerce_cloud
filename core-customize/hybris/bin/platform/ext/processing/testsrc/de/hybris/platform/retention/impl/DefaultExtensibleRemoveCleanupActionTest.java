/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.retention.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.persistence.audit.AuditScopeInvalidator;
import de.hybris.platform.persistence.audit.gateway.WriteAuditGateway;
import de.hybris.platform.processing.model.FlexibleSearchRetentionRuleModel;
import de.hybris.platform.retention.ItemToCleanup;
import de.hybris.platform.retention.hook.ItemCleanupHook;
import de.hybris.platform.retention.job.AfterRetentionCleanupJobPerformable;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Class DefaultExtensibleRemoveCleanupActionTest.
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultExtensibleRemoveCleanupActionTest
{
	@InjectMocks
	private final DefaultExtensibleRemoveCleanupAction cleanupAction = new DefaultExtensibleRemoveCleanupAction();

	@Mock
	private ItemCleanupHook itemCleanupHook;
	@Mock
	private ModelService modelService;
	@Mock
	private WriteAuditGateway writeAuditGateway;
	@Mock
	private AuditScopeInvalidator auditScopeInvalidator;

	private FlexibleSearchRetentionRuleModel rule;
	private ItemToCleanup item;
	private AfterRetentionCleanupJobPerformable retentionJob;


	@Before
	public void setup()
	{
		cleanupAction.setItemCleanupHooks(Collections.singletonList(itemCleanupHook));
		rule = new FlexibleSearchRetentionRuleModel();
		item = mock(ItemToCleanup.class);
		retentionJob = new AfterRetentionCleanupJobPerformable();
	}

	@Test
	public void shouldCleanupAndInvokeHooks()
	{
		final ItemModel itemModel = mock(ItemModel.class);

		final PK itemPK = PK.parse("1111");
		given(item.getPk()).willReturn(itemPK);
		given(item.getItemType()).willReturn(ItemModel._TYPECODE);
		given(modelService.get(itemPK)).willReturn(itemModel);

		cleanupAction.cleanup(retentionJob, rule, item);
		verify(itemCleanupHook).cleanupRelatedObjects(itemModel);
		verify(modelService).remove(itemPK);
		verify(writeAuditGateway).removeAuditRecordsForType(any(String.class), any(PK.class));
		verify(auditScopeInvalidator).clearCurrentAuditForPK(any(PK.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCleanupIfItemIsNull()
	{
		cleanupAction.cleanup(retentionJob, rule, null);
	}
}
