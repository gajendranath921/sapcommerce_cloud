/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gridfs.media.storage;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.media.storage.MediaStorageConfigService;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.MongoDbFactory;

import com.google.common.collect.Sets;
import com.mongodb.DB;
import com.mongodb.DBCollection;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class GridFSMediaStorageCleanerTest
{
	private GridFSMediaStorageCleaner cleaner;
	@Mock
	private MongoDbFactory dbFactory;
	@Mock
	private DB mongDb;
	@Mock
	private MediaStorageConfigService config;
	@Mock
	private DBCollection col1, col2, col3;


	@Before
	public void setUp() throws Exception
	{
		cleaner = new GridFSMediaStorageCleaner()
		{
			@Override
			public void setTenantPrefix()
			{
				tenantPrefix = "sys-master";
			}
		};
		cleaner.setDbFactory(dbFactory);
		cleaner.setStorageConfigService(config);
		cleaner.setTenantPrefix();
	}

	@Test
	public void shouldDropAllCollectionsPrefixedWithTenantIdDuringCleaningOnInit()
	{
		// given
		final Set<String> collectionNames = Sets.newHashSet("sys-master-foo", "sys-master-bar", "baz");
		given(Boolean.valueOf(config.isStorageStrategyConfigured("gridFsStorageStrategy"))).willReturn(Boolean.TRUE);
		given(dbFactory.getLegacyDb()).willReturn(mongDb);
		given(mongDb.getCollectionNames()).willReturn(collectionNames);
		given(mongDb.getCollection("sys-master-foo")).willReturn(col1);
		given(mongDb.getCollection("sys-master-bar")).willReturn(col2);
		Mockito.lenient().when(mongDb.getCollection("baz")).thenReturn(col3);

		// when
		cleaner.onInitialize();

		// then
		verify(col1).drop();
		verify(col2).drop();
		verify(col3, times(0)).drop();
	}
}
