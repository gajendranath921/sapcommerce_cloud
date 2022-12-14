/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.workflows.actions;

import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;

/**
 * Action called when a registration request has been rejected
 */
public class RegistrationRejectedAutomatedWorkflowTemplateJob extends AbstractAutomatedWorkflowTemplateJob
{
	/*
	 * In this workflow step, we do nothing to reject registration, the registered customer will be removed in the next
	 * send email workflow step
	 * 
	 * @see de.hybris.platform.workflow.jobs.AutomatedWorkflowTemplateJob#perform(de.hybris.platform.workflow.model.
	 * WorkflowActionModel)
	 */
	@Override
	public WorkflowDecisionModel perform(final WorkflowActionModel workflowAction)
	{
		return defaultDecision(workflowAction);
	}

}
