/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.product.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPriceDataFactoryTest
{

	@InjectMocks
	private final DefaultPriceDataFactory defaultPriceDataFactory = new DefaultPriceDataFactory();
	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;
	@Mock
	private CommonI18NService commonI18NService;

	@Test
	public void testFormatPrice()
	{

		Assert.assertEquals(
				"$33.30",
				defaultPriceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(33.3D),
						setUpPriceFactory("USD", "en", Locale.US)).getFormattedValue());


		// \p{Zs} is regex to match any "whitespace character that is invisible, but does take up space" including html spaces
		Assert.assertEquals("33,30 €",
		defaultPriceDataFactory
				.create(PriceDataType.BUY, BigDecimal.valueOf(33.3D), setUpPriceFactory("EUR", "de", Locale.GERMANY))
				.getFormattedValue().replaceAll("\\p{Zs}", " "));


	}


	@Test
	public void test1000Threads() throws InterruptedException, ExecutionException
	{
		multiThreadedTest(1000);

	}



	private CurrencyModel setUpPriceFactory(final String currencyIso, final String languageIso, final Locale locale)
	{
		final CurrencyModel currencyModel = Mockito.mock(CurrencyModel.class);
		final LanguageModel languageModel = mock(LanguageModel.class, withSettings().lenient());

		given(currencyModel.getIsocode()).willReturn(currencyIso);
		given(currencyModel.getDigits()).willReturn(Integer.valueOf(2));
		given(languageModel.getIsocode()).willReturn(languageIso);
		given(commonI18NService.getCurrentLanguage()).willReturn(languageModel);
		given(commerceCommonI18NService.getLocaleForLanguage(languageModel)).willReturn(locale);
		return currencyModel;
	}


	private void multiThreadedTest(final int threadCount) throws InterruptedException, ExecutionException
	{
		final CurrencyModel currencyModel = setUpPriceFactory("USD", "en", Locale.US);

		final Callable<String> task = new Callable<String>()
		{
			@Override
			public String call()
			{
				return defaultPriceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(33.3D), currencyModel)
						.getFormattedValue();
			}
		};

		final List<Callable<String>> tasks = Collections.nCopies(threadCount, task);
		final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		final List<Future<String>> futures = executorService.invokeAll(tasks);
		final List<String> resultList = new ArrayList<>(futures.size());
		// Check for exceptions
		for (final Future<String> future : futures)
		{
			// Throws an exception if an exception was thrown by the task.
			resultList.add(future.get());
		}
		// Validate the IDs
		Assert.assertEquals(threadCount, futures.size());
	}

}
