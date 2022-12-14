/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.populators;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_MASTER_TEMPLATE_ID;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cmsfacades.cmsitems.CMSItemAttributeFilterEnablerService;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class CMSItemPageModelToDataAttributePopulatorTest
{

	private static final String PAGE_TEMPLATE_UID = "template-uid";

	@InjectMocks
	private CMSItemPageModelToDataAttributePopulator populator;

	@Mock
	private AbstractPageModel pageModel;

	@Mock
	private CMSItemAttributeFilterEnablerService cmsItemAttributeFilterEnablerService;

	@Mock
	private PageTemplateModel pageTemplateModel;

	private Map<String, Object> itemMap = new HashMap<>();

	@Before
	public void setUp()
	{
		when(cmsItemAttributeFilterEnablerService.isAttributeAllowed(any(), anyString())).thenReturn(true);
	}

	@Test
	public void testTemplateNameIsPopulated()
	{
		// GIVEN
		when(pageModel.getMasterTemplate()).thenReturn(pageTemplateModel);
		when(pageTemplateModel.getUid()).thenReturn(PAGE_TEMPLATE_UID);

		// WHEN
		populator.populate(pageModel, itemMap);

		// THEN
		assertThat(itemMap.get(FIELD_MASTER_TEMPLATE_ID), is(PAGE_TEMPLATE_UID));
	}

}
