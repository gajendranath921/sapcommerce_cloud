/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.textfieldconfiguratortemplateocctest.setup;

import de.hybris.platform.ycommercewebservicestest.setup.YCommerceWebServicesTestSetup;


public class TextfieldConfiguratorOCCTestSetup extends YCommerceWebServicesTestSetup
{
	public void loadData()
	{
		getSetupImpexService().importImpexFile(
				"/textfieldconfiguratortemplateocctest/import/sampledata/productCatalogs/wsTestProductCatalog/standaloneTestData.impex",
				false);
		getSetupSolrIndexerService().executeSolrIndexerCronJob(String.format("%sIndex", WS_TEST), true);
	}
}
