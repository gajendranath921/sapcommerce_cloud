/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.marketplaceservices.dataimport.batch.task;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.model.ImpExMediaModel;
import de.hybris.platform.impex.model.cronjob.ImpExImportCronJobModel;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.ImportService;
import de.hybris.platform.servicelayer.session.SessionService;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class MarketplaceImpexRunnerTaskTest
{
	private static final String ERROR_PREVIEW = "error";

	private AbstractMarketplaceImpexRunnerTask task;

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Mock
	private ImpExMediaModel imm;

	private final ImportResult failedImportResult = new ImportResult()
	{
		@Override
		public boolean isSuccessful()
		{
			return false;
		}

		@Override
		public boolean isError()
		{
			return true;
		}

		@Override
		public boolean isRunning()
		{
			return false;
		}

		@Override
		public boolean isFinished()
		{
			return true;
		}

		@Override
		public boolean hasUnresolvedLines()
		{
			return true;
		}

		@Override
		public ImpExMediaModel getUnresolvedLines()
		{
			return imm;
		}

		@Override
		public ImpExImportCronJobModel getCronJob()
		{
			return null;
		}
	};

	private final ImportResult successImportResult = new ImportResult()
	{
		@Override
		public boolean isSuccessful()
		{
			return true;
		}

		@Override
		public boolean isError()
		{
			return false;
		}

		@Override
		public boolean isRunning()
		{
			return false;
		}

		@Override
		public boolean isFinished()
		{
			return true;
		}

		@Override
		public boolean hasUnresolvedLines()
		{
			return false;
		}

		@Override
		public ImpExMediaModel getUnresolvedLines()
		{
			return null;
		}

		@Override
		public ImpExImportCronJobModel getCronJob()
		{
			return null;
		}
	};

	@Before
	public void prepare() throws IOException
	{
		task = new AbstractMarketplaceImpexRunnerTask()
		{
			@Override
			public SessionService getSessionService()
			{
				return null;
			}

			@Override
			public ImportService getImportService()
			{
				return null;
			}

			@Override
			public ImportConfig getImportConfig()
			{
				return null;
			}
		};
	}

	@Test
	public void testFailedResult() throws Exception
	{
		Mockito.when(imm.getPreview()).thenReturn(ERROR_PREVIEW);
		final List<ImportResult> results = Arrays.asList(new ImportResult[]
		{ failedImportResult, successImportResult, failedImportResult });

		final String msg = task.createLogMsg(results);
		assertEquals(ERROR_PREVIEW + System.lineSeparator() + ERROR_PREVIEW, msg);
	}

	@Test
	public void testSuccessResult() throws Exception
	{
		final List<ImportResult> results = Arrays.asList(new ImportResult[]
		{ successImportResult, successImportResult, successImportResult });

		final String msg = task.createLogMsg(results);
		assertEquals(AbstractMarketplaceImpexRunnerTask.LOG_DEFAULT_SUCCESS_MSG, msg);
	}


}
