/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorcms.action.renderer.impl;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;


/**
 */
public interface CMSActionRenderer<T extends AbstractCMSComponentModel>
{
	/**
	 * Render a CMS Action into a action
	 *
	 * @param pageContext
	 *           The page context to render into
	 * @param action
	 *           The action to render
	 * @throws javax.servlet.ServletException
	 * @throws java.io.IOException
	 */
	void renderAction(PageContext pageContext, T action) throws ServletException, IOException; 
	// It is possible for this class to be extended or for methods to be used in other extensions - so no sonar added
}
