/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.dataimport.batch.task;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.converter.ImpexConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Test for {@link ImpexTransformerTask}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ImpexTransformerTaskTest
{
	private static final String TEST_PREFIX = "prefix";
	private static final String TEST_PREFIX_2 = "different_prefix";

	@Mock
	private File file;
	@Mock
	private ImpexConverter converter;
	private BatchHeader header;
	private ImpexTransformerTask task;

	@Before
	public void setUp()
	{
		task = new ImpexTransformerTask();
		final Map<String, List<ImpexConverter>> converterMap = new HashMap<String, List<ImpexConverter>>();
		converterMap.put(TEST_PREFIX, Collections.singletonList(converter));
		task.setConverterMap(converterMap);
		header = new BatchHeader();
		header.setFile(file);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMissingFile() throws UnsupportedEncodingException, FileNotFoundException
	{
		header.setFile(null);
		task.execute(header);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMissingConverter() throws UnsupportedEncodingException, FileNotFoundException
	{
		given(file.getName()).willReturn(TEST_PREFIX_2);
		task.execute(header);
	}

}
