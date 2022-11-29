/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.testframework;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.Importer;
import de.hybris.platform.impex.jalo.imp.DefaultDumpHandler;
import de.hybris.platform.impex.jalo.media.DefaultMediaDataHandler;
import de.hybris.platform.impex.jalo.media.MediaDataTranslator;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.JaloConnection;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.JaloSystemNotInitializedException;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.util.CSVReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;


@Ignore
public class ImpexRunListener extends RunListener
{
	private static final Logger LOG = Logger.getLogger(ImpexRunListener.class.getName());
	/**
	 * Reference to current session.
	 */
	private JaloSession jaloSession;

	@Override
	public void testStarted(final Description description) throws Exception
	{
		final ImpexData impexData = description.getAnnotation(ImpexData.class);
		if (impexData != null)
		{
			jaloSession = JaloConnection.getInstance().createAnonymousCustomerSession();
			jaloSession.setUser(UserManager.getInstance().getAdminEmployee());
			new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
			final List<String> scripts = Arrays.asList(impexData.value());
			for (final String script : scripts)
			{
				try
				{
					importCsv(script, "UTF-8");
				}
				catch (final ImpExException e)
				{
					throw new IllegalStateException(e);
				}
			}
		}
	}

	@Override
	public void testRunStarted(final Description description) throws Exception
	{
		Registry.activateStandaloneMode();
		// Utilities.setJUnitTenant();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Setting Cluster and Tenant");
		}
		final JaloConnection con = JaloConnection.getInstance();
		final boolean sysIni = con.isSystemInitialized();
		if (!sysIni)
		{
			final Exception exception = new JaloSystemNotInitializedException(null, "Test system is not initialized", -1);
			final StackTraceElement[] trimmedStack = new StackTraceElement[Math.min(exception.getStackTrace().length, 3)];
			System.arraycopy(exception.getStackTrace(), 0, trimmedStack, 0, trimmedStack.length);
			exception.setStackTrace(trimmedStack);
			LOG.error(exception.getMessage(), exception);
			throw exception;
		}
	}

	@Override
	public void testFinished(final Description description) throws Exception
	{
		if (jaloSession != null)
		{
			jaloSession.close();
		}
	}

	/**
	 * Imports given csv file from classpath using given encoding. Fails in case of import errors.
	 *
	 * @param csvFile
	 *           name of file to import from classpath
	 * @param encoding
	 *           encoding to use
	 * @throws de.hybris.platform.impex.jalo.ImpExException
	 */
	protected void importCsv(final String csvFile, final String encoding) throws ImpExException
	{
		LOG.info("importing resource " + csvFile);
		assertNotNull("Given file is null", csvFile);
		// get file stream
		try (final InputStream inputStream = ImpexRunListener.class.getResourceAsStream(csvFile))
		{
			importStream(inputStream, encoding, csvFile);
		}
		catch (final IOException e)
		{
			LOG.error("Given file " + csvFile + "can not be found in classpath", e);
		}

	}

	protected void importStream(final InputStream inputStream, final String encoding, final String resourceName)
			throws ImpExException
	{
		// create stream reader
		CSVReader reader = null;
		try
		{
			reader = new CSVReader(inputStream, encoding);
		}
		catch (final UnsupportedEncodingException e)
		{
			fail("Given encoding " + encoding + " is not supported");
		}
		// import
		MediaDataTranslator.setMediaDataHandler(new DefaultMediaDataHandler());
		Importer importer = null;
		ImpExException error = null;
		try
		{
			importer = new Importer(reader);
			importer.getReader().enableCodeExecution(true);
			importer.setMaxPass(-1);
			importer.setDumpHandler(new FirstLinesDumpReader());
			importer.importAll();
		}
		catch (final ImpExException e)
		{
			error = e;
		}
		finally
		{
			MediaDataTranslator.unsetMediaDataHandler();
		}
		// failure handling
		if (importer != null && importer.hasUnresolvedLines())
		{
			fail("Import has " + importer.getDumpedLineCountPerPass() + "+unresolved lines, first lines are:\n"
					+ importer.getDumpHandler().getDumpAsString());
		}
		assertNull("Import of resource " + resourceName + " failed" + (error == null ? "" : error.getMessage()), error);
		assertFalse("Import of resource " + resourceName + " failed", importer.hadError());
	}

	private static class FirstLinesDumpReader extends DefaultDumpHandler
	{
		@Override
		public String getDumpAsString()
		{
			final StringBuffer result = new StringBuffer(100);
			try (final FileReader fileReader = new FileReader(getDumpAsFile());
					final BufferedReader reader = new BufferedReader(fileReader);)
			{
				result.append(reader.readLine()).append("\n");
				result.append(reader.readLine()).append("\n");
				result.append(reader.readLine()).append("\n");
			}
			catch (final FileNotFoundException e)
			{
				result.append("Error while reading dump ").append(e.getMessage());
			}
			catch (final IOException e)
			{
				result.append("Error while reading dump ").append(e.getMessage());
			}
			return result.toString();
		}
	}
}
