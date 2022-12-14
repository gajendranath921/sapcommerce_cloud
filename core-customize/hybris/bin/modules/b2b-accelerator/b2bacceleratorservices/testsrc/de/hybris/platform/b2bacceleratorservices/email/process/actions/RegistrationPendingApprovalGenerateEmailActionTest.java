/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.email.process.actions;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.email.CMSEmailPageService;
import de.hybris.platform.acceleratorservices.email.EmailGenerationService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.acceleratorservices.process.strategies.ProcessContextResolutionStrategy;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.b2bacceleratorservices.registration.B2BRegistrationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Lists;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RegistrationPendingApprovalGenerateEmailActionTest
{
	private RegistrationPendingApprovalGenerateEmailAction registrationPendingApprovalGenerateEmailAction;
	private BusinessProcessModel businessProcessModel;
	private ProcessContextResolutionStrategy contextResolutionStrategy;

	private CMSEmailPageService cmsEmailPageService;
	private CatalogVersionModel contentCatalogVersion;
	private EmailPageModel emailPageModel;

	private EmailGenerationService emailGenerationService;
	private EmailMessageModel emailMessageModel;

	private ModelService modelService;
	private B2BRegistrationService registrationService;



	@Before
	public void setUp()
	{
		registrationPendingApprovalGenerateEmailAction = new RegistrationPendingApprovalGenerateEmailAction();

		contextResolutionStrategy = Mockito.mock(ProcessContextResolutionStrategy.class);

		cmsEmailPageService = Mockito.mock(CMSEmailPageService.class);
		contentCatalogVersion = Mockito.mock(CatalogVersionModel.class);
		emailPageModel = Mockito.mock(EmailPageModel.class);
		emailGenerationService = Mockito.mock(EmailGenerationService.class);
		modelService = Mockito.mock(ModelService.class);

		registrationService = Mockito.mock(B2BRegistrationService.class);

		registrationPendingApprovalGenerateEmailAction.setContextResolutionStrategy(contextResolutionStrategy);
		registrationPendingApprovalGenerateEmailAction.setCmsEmailPageService(cmsEmailPageService);
		registrationPendingApprovalGenerateEmailAction.setEmailGenerationService(emailGenerationService);
		registrationPendingApprovalGenerateEmailAction.setModelService(modelService);
		registrationPendingApprovalGenerateEmailAction.setRegistrationService(registrationService);
	}


	@Test
	public void recipientsIsEmptyRecipients() throws RetryLaterException
	{

		businessProcessModel = Mockito.mock(BusinessProcessModel.class);

		Mockito.when(contextResolutionStrategy.getContentCatalogVersion(businessProcessModel)).thenReturn(contentCatalogVersion);

		Mockito.when(cmsEmailPageService.getEmailPageForFrontendTemplate(nullable(String.class), nullable(contentCatalogVersion.getClass())))
				.thenReturn(emailPageModel);


		emailMessageModel = Mockito.mock(EmailMessageModel.class);
		Mockito.when(emailGenerationService.generate(businessProcessModel, emailPageModel)).thenReturn(emailMessageModel);


		final List<EmailAddressModel> recipients = new ArrayList<>();
		Mockito.when(registrationService.getEmailAddressesOfEmployees(nullable(List.class))).thenReturn(recipients);

		assertEquals("Transaction must be NOK", Transition.NOK,
				registrationPendingApprovalGenerateEmailAction.executeAction(businessProcessModel));

	}

	@Test
	public void emailMessageContainsRecipients() throws RetryLaterException
	{

		final int NUM_OF_EMAILS = 100;

		businessProcessModel = new BusinessProcessModel();
		Mockito.when(contextResolutionStrategy.getContentCatalogVersion(businessProcessModel)).thenReturn(contentCatalogVersion);


		Mockito.when(cmsEmailPageService.getEmailPageForFrontendTemplate(nullable(String.class), nullable(contentCatalogVersion.getClass())))
				.thenReturn(emailPageModel);

		emailMessageModel = new EmailMessageModel();
		Mockito.when(emailGenerationService.generate(businessProcessModel, emailPageModel)).thenReturn(emailMessageModel);


		//create a list of NUM_OF_EMAILS recipients
		final List<EmailAddressModel> recipients = new ArrayList<>();
		for (int i = 0; i < NUM_OF_EMAILS; i++)
		{
			recipients.add(Mockito.mock(EmailAddressModel.class));
		}

		Mockito.when(registrationService.getEmailAddressesOfEmployees(nullable(List.class))).thenReturn(recipients);

		final List<EmailMessageModel> emailMessageModelList = Lists.newArrayList(emailMessageModel);
		businessProcessModel.setEmails(emailMessageModelList);

		assertEquals("Transaction must be OK", Transition.OK,
				registrationPendingApprovalGenerateEmailAction.executeAction(businessProcessModel));

		assertEquals("Number of email recipients must match the number of recipients to the email message ", NUM_OF_EMAILS,
				emailMessageModel.getToAddresses().size());

	}

}
