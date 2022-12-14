/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.util.models;

import de.hybris.platform.cms2.model.CMSComponentTypeModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cms2lib.model.components.BannerComponentModel;
import de.hybris.platform.cmsfacades.util.builder.CMSComponentTypeModelBuilder;
import de.hybris.platform.cmsfacades.util.dao.CMSComponentTypeDao;

import org.springframework.beans.factory.annotation.Required;


public class CMSComponentTypeModelMother extends AbstractModelMother<CMSComponentTypeModel>
{
	private CMSComponentTypeDao cmsComponentTypeDao;

	public CMSComponentTypeModel CMSParagraphComponent()
	{
		return getOrSaveAndReturn( //
				() -> cmsComponentTypeDao.getCMSComponentTypeByCode(CMSParagraphComponentModel._TYPECODE), //
				() -> CMSComponentTypeModelBuilder.aModel() //
						.withCode(CMSParagraphComponentModel._TYPECODE) //
						.build());
	}

	public CMSComponentTypeModel BannerComponent()
	{
		return getOrSaveAndReturn( //
				() -> cmsComponentTypeDao.getCMSComponentTypeByCode(BannerComponentModel._TYPECODE), //
				() -> CMSComponentTypeModelBuilder.aModel() //
						.withCode(BannerComponentModel._TYPECODE) //
						.build());
	}

	public CMSComponentTypeModel LinkComponent()
	{
		return getOrSaveAndReturn( //
				() -> cmsComponentTypeDao.getCMSComponentTypeByCode(CMSLinkComponentModel._TYPECODE), //
				() -> CMSComponentTypeModelBuilder.aModel() //
						.withCode(CMSLinkComponentModel._TYPECODE) //
						.build());
	}

	protected CMSComponentTypeDao getCmsComponentTypeDao()
	{
		return cmsComponentTypeDao;
	}

	@Required
	public void setCmsComponentTypeDao(final CMSComponentTypeDao cmsComponentTypeDao)
	{
		this.cmsComponentTypeDao = cmsComponentTypeDao;
	}

}
