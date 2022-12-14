/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.synchronization.populator;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.SyncJobData;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;

import java.util.Date;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;


@UnitTest
public class SyncJobPopulatorTest
{

	@Test
	public void testSuccessfulPopulation()
	{
		final SyncJobDataPopulator syncJobDataPopulator = new SyncJobDataPopulator();
		final CronJobModel cronJob = new CronJobModel();

		final CronJobStatus status = CronJobStatus.RUNNING;
		final CronJobResult result = CronJobResult.SUCCESS;
		final String statusString = status.toString();
		final String resultString = result.toString();
		final Date currentDate = new Date();

		cronJob.setStatus(status);
		cronJob.setModifiedtime(currentDate);
		cronJob.setCreationtime(currentDate);
		cronJob.setEndTime(currentDate);
		cronJob.setResult(result);
		cronJob.setStartTime(currentDate);

		final SyncJobData syncJob = new SyncJobData();

		final Optional<CronJobModel> cronJobOpt = Optional.of(cronJob);



		syncJobDataPopulator.populate(cronJobOpt, syncJob);

		Assert.assertEquals(statusString, syncJob.getSyncStatus());
		Assert.assertEquals(resultString, syncJob.getSyncResult());
		Assert.assertEquals(currentDate, syncJob.getLastModifiedDate());
		Assert.assertEquals(currentDate, syncJob.getCreationDate());
		Assert.assertEquals(currentDate, syncJob.getEndDate());
		Assert.assertEquals(currentDate, syncJob.getStartDate());
	}

}
