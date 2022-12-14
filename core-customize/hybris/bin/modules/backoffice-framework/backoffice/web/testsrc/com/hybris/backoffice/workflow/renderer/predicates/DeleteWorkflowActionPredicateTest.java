/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.workflow.renderer.predicates;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

import de.hybris.platform.workflow.WorkflowStatus;
import de.hybris.platform.workflow.model.WorkflowModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.workflow.WorkflowFacade;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;


@RunWith(MockitoJUnitRunner.class)
public class DeleteWorkflowActionPredicateTest
{
	@Mock
	private WorkflowFacade workflowFacade;

	@Mock
	private PermissionFacade permissionFacade;

	@Mock
	private WorkflowModel workflowModel;

	private final DeleteWorkflowActionPredicate predicate = new DeleteWorkflowActionPredicate();

	@Before
	public void setUp()
	{
		predicate.setWorkflowFacade(workflowFacade);
		predicate.setPermissionFacade(permissionFacade);
	}

	@Test
	public void testPredicateWhenWorkflowCanBeDeleted()
	{
		// given
		given(workflowFacade.getWorkflowStatus(workflowModel)).willReturn(WorkflowStatus.PLANNED);
		given(Boolean.valueOf(permissionFacade.canRemoveInstance(workflowModel))).willReturn(Boolean.TRUE);

		// when
		final boolean decision = predicate.test(workflowModel);

		// then
		assertTrue(decision);
	}

	@Test
	public void testPredicateWhenWorkflowCannotBeDeleted()
	{
		// given
		given(workflowFacade.getWorkflowStatus(workflowModel)).willReturn(WorkflowStatus.TERMINATED);
		Mockito.lenient().when(Boolean.valueOf(permissionFacade.canRemoveInstance(workflowModel))).thenReturn(Boolean.TRUE);

		// when
		final boolean decision = predicate.test(workflowModel);

		// then
		assertFalse(decision);
	}

	@Test
	public void testPredicateWhenWorkflowCannotBeDeletedAndUserHasntPermissions()
	{
		// given
		given(workflowFacade.getWorkflowStatus(workflowModel)).willReturn(WorkflowStatus.TERMINATED);
		Mockito.lenient().when(Boolean.valueOf(permissionFacade.canRemoveInstance(workflowModel))).thenReturn(Boolean.FALSE);

		// when
		final boolean decision = predicate.test(workflowModel);

		// then
		assertFalse(decision);
	}
}
