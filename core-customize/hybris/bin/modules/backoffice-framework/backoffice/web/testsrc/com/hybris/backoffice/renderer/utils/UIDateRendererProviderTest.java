/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.renderer.utils;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatcher;
import org.mockito.Spy;


@RunWith(Parameterized.class)
public class UIDateRendererProviderTest
{
	@Parameterized.Parameter(0)
	public Date currentDate;
	@Parameterized.Parameter(1)
	public Date givenDate;
	@Spy
	private UIDateRendererProvider rendererProvider;

	@Parameterized.Parameters
	public static Collection<Date[]> data() throws ParseException
	{
		final String format = "yyyy-MM-dd'T'HH:mm:ss";

		return Arrays.asList(new Date[][]
		{
				{ DateUtils.parseDateStrictly("2017-06-16T01:14:33", format),
						DateUtils.parseDateStrictly("2017-06-16T12:14:33", format) },
				{ DateUtils.parseDateStrictly("2017-06-16T02:24:33", format),
						DateUtils.parseDateStrictly("2017-06-16T00:14:33", format) },
				{ DateUtils.parseDateStrictly("2017-06-16T11:44:33", format),
						DateUtils.parseDateStrictly("2017-06-16T03:18:53", format) } });
	}

	@Before
	public void setUp()
	{
		initMocks(this);
	}

	@Test
	public void shouldGivenDateBeTodaysDate()
	{
		// when
		rendererProvider.getFormattedDateLabel(currentDate, givenDate);

		// then
		verify(rendererProvider).getLabel(argThat(new ArgumentMatcher<String>()
		{
			@Override
			public boolean matches(String s) {
				return StringUtils.equals(UIDateRendererProvider.LABEL_TODAY, s);
			}
		}), anyString(), anyString());
	}

	@Test
	public void shouldGivenDateBeYesterdayDate()
	{
		// when
		rendererProvider.getFormattedDateLabel(currentDate, DateUtils.addDays(givenDate, -1));

		// then
		verify(rendererProvider).getLabel(argThat(new ArgumentMatcher<String>()
		{
			@Override
			public boolean matches(String s) {
				return StringUtils.equals(UIDateRendererProvider.LABEL_YESTERDAY, s);
			}
		}), anyString(), anyString());
	}

}
