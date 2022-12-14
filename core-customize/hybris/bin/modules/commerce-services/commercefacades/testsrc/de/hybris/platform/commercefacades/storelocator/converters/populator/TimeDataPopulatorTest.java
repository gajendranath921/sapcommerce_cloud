/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.storelocator.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storelocator.data.TimeData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TimeDataPopulatorTest
{
	private final DateFormat dateFormat = new SimpleDateFormat("hh --- mm");

	private final DateFormat dfKorea = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.KOREA);

	private TimeDataPopulator timeDataPopulator;

	@Test
	public void testDataConvertWithCustomFormat()
	{
		timeDataPopulator = new TimeDataPopulator()
		{
			@Override
			protected DateFormat getDateFormat()
			{
				return dateFormat;
			}
		};

		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, 10);
		cal.set(Calendar.MINUTE, 45);
		final Date someTime = cal.getTime();
		final TimeData data = new TimeData();
		timeDataPopulator.populate(someTime, data);

		Assert.assertEquals(10, data.getHour());
		Assert.assertEquals(45, data.getMinute());
		Assert.assertEquals(dateFormat.format(someTime), data.getFormattedHour());
	}

	@Test
	public void testDataConvertWithCustomLocale()
	{
		timeDataPopulator = new TimeDataPopulator()
		{
			@Override
			protected Locale getCurrentLocale()
			{
				return Locale.KOREA;
			}
		};

		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, 10);
		cal.set(Calendar.MINUTE, 45);
		final Date someTime = cal.getTime();
		final TimeData data = new TimeData();
		timeDataPopulator.populate(someTime, data);

		Assert.assertEquals(10, data.getHour());
		Assert.assertEquals(45, data.getMinute());
		Assert.assertEquals(dfKorea.format(someTime), data.getFormattedHour());
	}
}
