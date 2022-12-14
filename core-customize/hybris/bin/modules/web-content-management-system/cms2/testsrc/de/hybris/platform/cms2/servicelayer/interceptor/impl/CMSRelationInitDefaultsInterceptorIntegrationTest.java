/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.servicelayer.interceptor.impl;

import de.hybris.platform.cms2.model.relations.CMSRelationModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import javax.annotation.Resource;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class CMSRelationInitDefaultsInterceptorIntegrationTest extends ServicelayerTest // NOPMD: Junit4 allows any name for test method
{
	@Resource
	ModelService modelService;

	@Test
	public void shouldCreateCMSRelationModelWithGeneratedUid()
	{
		// given
		final CMSRelationModel cmsRelation = modelService.create(CMSRelationModel.class);

		// when
		final String uid = cmsRelation.getUid();

		// then
		assertThat(uid).isNotNull();
		assertThat(uid).isNotEmpty();
	}

}
