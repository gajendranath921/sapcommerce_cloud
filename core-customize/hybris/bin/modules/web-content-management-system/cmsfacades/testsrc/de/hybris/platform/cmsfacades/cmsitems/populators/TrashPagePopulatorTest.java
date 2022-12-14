/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class TrashPagePopulatorTest
{
	@Mock
	protected ContentPageModel homepage;

	@Mock
	protected Map<String, Object> map;
	
	@Mock
	protected ModelService modelService;

	@InjectMocks
	protected TrashPagePopulator cmsTrashPagePopulator;

	@Before
	public void setup()
	{
		cmsTrashPagePopulator = new TrashPagePopulator();
	}

	@Test(expected = ConversionException.class)
	public void shouldNotRemoveNavigationEntriesFromNavigationNodesWhenContentPageIsNull()
	{
		// WHEN
		cmsTrashPagePopulator.populate(map, null);
	}

	@Test(expected = ConversionException.class)
	public void shouldNotRemoveNavigationEntriesFromNavigationNodesWhenMapIsNull()
	{
		// WHEN
		cmsTrashPagePopulator.populate(null, homepage);
	}

	@Test
	public void shouldNotRemoveNavigationEntriesFromNavigationNodesWhenMapHasNoPageStatus()
	{
		// WHEN
		cmsTrashPagePopulator.populate(map, homepage);

		Mockito.verify(modelService, Mockito.times(0)).removeAll();
	}
}
