/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.pcmbackoffice.widgets.charts.facetchart;


import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zul.Div;

import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class DefaultFacetChartFiltersRendererTest
{
	@InjectMocks
	private DefaultFacetChartFiltersRenderer defaultFacetChartFiltersRenderer;
	private WidgetInstanceManager widgetInstanceManager;
	@Mock
	private Div filterContainer;
	@Mock
	private BiConsumer<String, Set<String>> facetSelectionListener;
	@Mock
	private FacetChartFiltersRenderer renderer;

	@Before
	public void setUp()
	{
		CockpitTestUtil.mockZkEnvironment();
		widgetInstanceManager = CockpitTestUtil.mockWidgetInstanceManager();
		final List<FacetChartFiltersRenderer> renderers = new ArrayList<>();
		renderers.add(renderer);
		defaultFacetChartFiltersRenderer.setRenderers(renderers);
	}

	@Test
	public void shouldCallAllRenderers()
	{
		defaultFacetChartFiltersRenderer.renderFilters(widgetInstanceManager, filterContainer, facetSelectionListener);

		verify(renderer).renderFilters(any(), any(), any());
	}

}
