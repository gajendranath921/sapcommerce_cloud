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
package de.hybris.platform.ruleengineservices.rule.interceptors;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.Mockito;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.campaigns.model.CampaignModel;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ItemModelContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.ImmutableSet;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CampaignRuleValidateInterceptorUnitTest
{
	@InjectMocks
	private CampaignRuleValidateInterceptor campaignRuleValidateInterceptor;
	@Mock
	private InterceptorContext interceptorContext;
	@Mock
	private CampaignModel campaign;
	@Mock
	private ItemModelContext itemModelContext;
	@Mock
	private L10NService l10NService;

	@Before
	public void setUp()
	{
		Mockito.lenient().when(interceptorContext.isNew(campaign)).thenReturn(false);
	}

	/**
	 * if the {@link CampaignModel} is new, don't trigger interceptor
	 */
	@Test
	public void onPrepareNewCampaignDoNotTrigger() throws InterceptorException
	{
		Mockito.lenient().when(interceptorContext.isNew(campaign)).thenReturn(true);

		campaignRuleValidateInterceptor.onValidate(campaign, interceptorContext);
		verify(campaign, times(0)).getSourceRules();
	}

	/**
	 * If there was no associated source rules - allow for modification
	 */
	@Test
	public void onPrepareNoSourceRules() throws InterceptorException
	{
		campaignRuleValidateInterceptor = spy(campaignRuleValidateInterceptor);
		Mockito.lenient().when(campaign.getItemModelContext()).thenReturn(itemModelContext);
		final SourceRuleModel sourceRuleWithPublishedStatus = createSourceRuleWithStatus(RuleStatus.UNPUBLISHED);
		Mockito.lenient().when(campaign.getSourceRules()).thenReturn(ImmutableSet.of(sourceRuleWithPublishedStatus));
		Mockito.lenient().when(itemModelContext.getOriginalValue(CampaignModel.SOURCERULES)).thenReturn(emptySet());
		
		campaignRuleValidateInterceptor.onValidate(campaign, interceptorContext);
		verify(campaign, times(1)).getSourceRules();
		verify(campaignRuleValidateInterceptor, times(1))
				.getFrozenAssociatedSourceRules(any(CampaignModel.class));
	}

	/**
	 * Verify if the removal of ever published rule is denied
	 */
	@Test
	public void onPreparePublishedRuleWasRemoved() throws InterceptorException
	{
		campaignRuleValidateInterceptor = spy(campaignRuleValidateInterceptor);
		Mockito.lenient().when(campaign.getSourceRules()).thenReturn(emptySet());
		Mockito.lenient().when(campaign.getItemModelContext()).thenReturn(itemModelContext);
		final SourceRuleModel sourceRuleWithPublishedStatus = createSourceRuleWithStatus(RuleStatus.PUBLISHED);
		Mockito.lenient().when(itemModelContext.getOriginalValue(CampaignModel.SOURCERULES))
				.thenReturn(ImmutableSet.of(sourceRuleWithPublishedStatus));
		Mockito.lenient().when(l10NService.getLocalizedString("exception.campaignrulevalidateinterceptor",new Object[]{RuleStatus.UNPUBLISHED})).
				thenReturn("Only rules with " + RuleStatus.UNPUBLISHED + " status could be removed");

		assertThatThrownBy(() -> campaignRuleValidateInterceptor.onValidate(campaign, interceptorContext))
				.isInstanceOf(InterceptorException.class)
				.hasMessageContaining("Only rules with " + RuleStatus.UNPUBLISHED + " status could be removed");
		verify(campaign, times(1)).getSourceRules();
		verify(campaignRuleValidateInterceptor, times(1))
				.getFrozenAssociatedSourceRules(any(CampaignModel.class));
	}

	/**
	 * Verify if the removal of unpublished rule is allowed
	 */
	@Test
	public void onPrepareAllowForRemovalOfUnpublishedRule() throws InterceptorException
	{
		campaignRuleValidateInterceptor = spy(campaignRuleValidateInterceptor);
		Mockito.lenient().when(campaign.getSourceRules()).thenReturn(emptySet());
		Mockito.lenient().when(campaign.getItemModelContext()).thenReturn(itemModelContext);
		final SourceRuleModel sourceRuleWithPublishedStatus = createSourceRuleWithStatus(RuleStatus.UNPUBLISHED);
		Mockito.lenient().when(itemModelContext.getOriginalValue(CampaignModel.SOURCERULES))
				.thenReturn(ImmutableSet.of(sourceRuleWithPublishedStatus));

		campaignRuleValidateInterceptor.onValidate(campaign, interceptorContext);
		verify(campaign, times(1)).getSourceRules();
		verify(campaignRuleValidateInterceptor, times(1))
				.getFrozenAssociatedSourceRules(any(CampaignModel.class));
	}

	private SourceRuleModel createSourceRuleWithStatus(final RuleStatus ruleStatus)
	{
		final SourceRuleModel sourceRule = mock(SourceRuleModel.class);
		Mockito.lenient().when(sourceRule.getStatus()).thenReturn(ruleStatus);
		return sourceRule;
	}

}
