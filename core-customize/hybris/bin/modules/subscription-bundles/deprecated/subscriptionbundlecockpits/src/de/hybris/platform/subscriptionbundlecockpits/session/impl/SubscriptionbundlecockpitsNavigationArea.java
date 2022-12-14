/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.subscriptionbundlecockpits.session.impl;

import de.hybris.platform.cockpit.components.sectionpanel.SectionPanelModel;
import de.hybris.platform.cockpit.session.impl.BaseUICockpitNavigationArea;


import de.hybris.platform.subscriptionbundlecockpits.components.navigationarea.SubscriptionbundlecockpitsNavigationAreaModel;


/**
 * Subscriptionbundlecockpits navigation area.
 */
public class SubscriptionbundlecockpitsNavigationArea extends BaseUICockpitNavigationArea
{

	@Override
	public SectionPanelModel getSectionModel()
	{
		if (super.getSectionModel() == null)
		{
			final SubscriptionbundlecockpitsNavigationAreaModel model = new SubscriptionbundlecockpitsNavigationAreaModel(this);
			model.initialize();
			super.setSectionModel(model);
		}
		return super.getSectionModel();
	}
}
