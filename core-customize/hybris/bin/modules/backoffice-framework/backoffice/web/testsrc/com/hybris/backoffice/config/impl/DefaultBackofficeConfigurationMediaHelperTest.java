/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.config.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hybris.backoffice.media.MediaUtil;
import org.mockito.Mockito;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.jalo.media.MediaManager;
import de.hybris.platform.media.storage.MediaStorageConfigService;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.daos.BackofficeConfigurationDao;


@RunWith(MockitoJUnitRunner.class)
public class DefaultBackofficeConfigurationMediaHelperTest {

	private static final String MEDIA_CODE = "code";
	private static final String MEDIA_MIME = "mime";
	private static final String FOLDER_NAME = "backofficeconfiguration";

	@InjectMocks
	@Spy
	private DefaultBackofficeConfigurationMediaHelper backofficeConfigurationMediaHelper;
	@Mock
	private MediaService mediaService;
	@Mock
	private ModelService modelService;
	@Mock
	private SessionService sessionService;
	@Mock
	private UserService userService;
	@Mock
	private SearchRestrictionService searchRestrictionService;
	@Mock
	private BackofficeConfigurationDao configurationDao;
	@Mock
	private MediaFolderModel secureMediaFolder;
	@Mock
	private MediaFolderModel insecureMediaFolder;
	@Mock
	private MediaModel insecureMedia;
	@Mock
	private MediaFolderModel rootFolder;
	@Mock
	private MediaStorageConfigService mediaStorageConfigService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private CatalogVersionModel catalogVersion;
	@InjectMocks
	@Spy
	private MediaUtil backofficeMediaUtil;

	@Before
	public void setUp()
	{
		doAnswer(invocationOnMock -> {
			SessionExecutionBody body = (SessionExecutionBody) invocationOnMock.getArguments()[0];
			return body.execute();
		}).when(sessionService).executeInLocalView(any(), any());

		when(catalogVersionService.getCatalogVersion(any(), any())).thenReturn(catalogVersion);
		backofficeConfigurationMediaHelper.setBackofficeMediaUtil(backofficeMediaUtil);
	}

	@Test
	public void shouldVerifySecureFolderAssignmentOnGetWidgetsConfigMedia()
	{
		//given
		final MediaModel media = mock(MediaModel.class);
		when(mediaService.getMedia(eq(catalogVersion), anyString())).thenReturn(media);
		when(mediaService.getFolder(anyString())).thenReturn(mock(MediaFolderModel.class));

		//when
		backofficeConfigurationMediaHelper.findOrCreateWidgetsConfigMedia(MEDIA_CODE, MEDIA_MIME);

		//then
		verify(backofficeConfigurationMediaHelper).assureSecureFolderAssignment(media);
		verify(media).getFolder();
		verify(mediaService).getFolder(DefaultBackofficeConfigurationMediaHelper.PROPERTY_BACKOFFICE_CONFIGURATION_SECURE_MEDIA_FOLDER);
	}

	@Test
	public void testCreateWidgetsConfigMedia()
	{
		final MediaModel media = new MediaModel();
		when(modelService.create(MediaModel.class)).thenReturn(media);
		final MediaFolderModel folder = mock(MediaFolderModel.class);
		when(folder.getQualifier())
				.thenReturn(DefaultBackofficeConfigurationMediaHelper.PROPERTY_BACKOFFICE_CONFIGURATION_SECURE_MEDIA_FOLDER);
		when(mediaService
				.getFolder(DefaultBackofficeConfigurationMediaHelper.PROPERTY_BACKOFFICE_CONFIGURATION_SECURE_MEDIA_FOLDER))
				.thenReturn(folder);

		final MediaModel widgetsConfigMedia = backofficeConfigurationMediaHelper.createWidgetsConfigMedia(MEDIA_CODE, MEDIA_MIME);

		assertThat(widgetsConfigMedia.getMime()).isEqualTo(MEDIA_MIME);
		assertThat(widgetsConfigMedia.getCode()).isEqualTo(MEDIA_CODE);
		assertThat(widgetsConfigMedia.getFolder().getQualifier())
				.isEqualTo(DefaultBackofficeConfigurationMediaHelper.PROPERTY_BACKOFFICE_CONFIGURATION_SECURE_MEDIA_FOLDER);
		verify(modelService).save(media);
		verify(backofficeConfigurationMediaHelper).getSecureFolder();
	}

	@Test
	public void testGetWidgetsConfigMedia()
	{
		final MediaModel media = new MediaModel();
		media.setCode(MEDIA_CODE);
		when(mediaService.getMedia(catalogVersion, MEDIA_CODE)).thenReturn(media);

		final MediaModel widgetsConfigMedia = backofficeConfigurationMediaHelper.findWidgetsConfigMedia(MEDIA_CODE);

		assertEquals(MEDIA_CODE, widgetsConfigMedia.getCode());
		verify(mediaService).getMedia(catalogVersion, MEDIA_CODE);
	}

	@Test
	public void shouldCleanUpWhenConfigurationIsAmbiguous()
	{
		// given
		doThrow(AmbiguousIdentifierException.class).when(mediaService).getMedia(catalogVersion, MEDIA_CODE);

		// when
		final MediaModel widgetModel = backofficeConfigurationMediaHelper.findWidgetsConfigMedia(MEDIA_CODE);

		// then
		verify(backofficeConfigurationMediaHelper).removeAmbiguousConfiguration(MEDIA_CODE);
		assertNull(widgetModel);
	}

	@Test
	public void testGetOrCreateWidgetsConfigMedia()
	{
		final MediaModel media = mock(MediaModel.class);
		when(modelService.create(MediaModel.class)).thenReturn(media);
		Mockito.lenient().when(mediaService.getMedia(MEDIA_CODE)).thenReturn(null);
		when(mediaService.getFolder(any())).thenReturn(mock(MediaFolderModel.class));

		backofficeConfigurationMediaHelper.findOrCreateWidgetsConfigMedia(MEDIA_CODE, MEDIA_MIME);

		verify(backofficeConfigurationMediaHelper).createWidgetsConfigMedia(any(), any());
	}

	@Test
	public void shouldReturnSecureFolderOnGetSecureFolder() {
		//when
		backofficeConfigurationMediaHelper.getSecureFolder();

		//then
		verify(mediaService).getFolder(FOLDER_NAME);
	}

	@Test
	public void shouldCreateSecureFolderOnLoadFailure() {
		//given
		doThrow(UnknownIdentifierException.class).when(mediaService).getFolder(FOLDER_NAME);
		when(modelService.create(MediaFolderModel.class)).thenReturn(secureMediaFolder);

		//when
		backofficeConfigurationMediaHelper.getSecureFolder();

		//then
		verify(backofficeConfigurationMediaHelper).createSecureFolder();
		verify(secureMediaFolder).setPath(FOLDER_NAME);
		verify(secureMediaFolder).setQualifier(FOLDER_NAME);
		verify(modelService).save(secureMediaFolder);
	}

	@Test
	public void shouldTryToReLoadFolderOnFailedCreation() throws InterruptedException {
		//given
		doThrow(UnknownIdentifierException.class).when(backofficeConfigurationMediaHelper).loadSecureFolder();
		when(modelService.create(MediaFolderModel.class)).thenReturn(secureMediaFolder);

		final BackofficeConfigurationMediaHelper concurrentHelperProxy = new DefaultBackofficeConfigurationMediaHelper() {

			private final Semaphore sem = new Semaphore(0);

			@Override
			protected MediaFolderModel loadSecureFolder() {
				try {
					return backofficeConfigurationMediaHelper.loadSecureFolder();
				} finally {
					sem.release();
				}
			}

			@Override
			protected MediaFolderModel createSecureFolder() {
				try {
					sem.acquire(4);
				} catch (final InterruptedException e) {
					throw new IllegalStateException(e);
				}

				final MediaFolderModel secureFolder = backofficeConfigurationMediaHelper.createSecureFolder();
				doThrow(ModelSavingException.class).when(modelService).save(any());
				sem.release(12);
				return secureFolder;
			}
		};

		final ExecutorService pool = Executors.newFixedThreadPool(4);

		//when
		for (int i = 0; i < 4; i++) {
			pool.submit(concurrentHelperProxy::getSecureFolder);
		}
		pool.shutdown();
		pool.awaitTermination(10, TimeUnit.SECONDS);

		//then
		verify(backofficeConfigurationMediaHelper, times(7)).loadSecureFolder();
		verify(backofficeConfigurationMediaHelper, times(4)).createSecureFolder();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailOnNullMediaOnAssureSecureFolderAssignment() {
		backofficeConfigurationMediaHelper.assureSecureFolderAssignment(null);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailWhenMediaAssignedToInsecureNonRootFolderOnAssureSecureFolderAssignment() {
		//given
		when(insecureMedia.getFolder()).thenReturn(insecureMediaFolder);
		when(insecureMediaFolder.getQualifier()).thenReturn("insecure");

		when(mediaStorageConfigService.getSecuredFolders()).thenReturn(Collections.singleton("secure"));

		//when
		backofficeConfigurationMediaHelper.assureSecureFolderAssignment(insecureMedia);
	}

	@Test
	public void shouldAssignSecureFolderOnNullAssignmentOnAssureSecureFolderAssignment() {
		//given
		when(insecureMedia.getFolder()).thenReturn(null);
		when(backofficeConfigurationMediaHelper.getSecureFolder()).thenReturn(secureMediaFolder);

		//when
		backofficeConfigurationMediaHelper.assureSecureFolderAssignment(insecureMedia);

		//then
		verify(insecureMedia).setFolder(secureMediaFolder);
	}

	@Test
	public void shouldReplaceRootFolderWithSecureFolderOnAssureSecureFolderAssignment() {
		//given
		when(insecureMedia.getFolder()).thenReturn(rootFolder);
		Mockito.lenient().when(insecureMedia.getCode()).thenReturn(MediaManager.ROOT_FOLDER_QUALIFIER);
		when(rootFolder.getQualifier()).thenReturn(MediaManager.ROOT_FOLDER_QUALIFIER);

		when(backofficeConfigurationMediaHelper.getSecureFolder()).thenReturn(secureMediaFolder);
		Mockito.lenient().when(secureMediaFolder.getQualifier()).thenReturn("secure");

		//when
		backofficeConfigurationMediaHelper.assureSecureFolderAssignment(insecureMedia);

		//then
		verify(insecureMedia).setFolder(secureMediaFolder);
	}

	@Test
	public void shouldReloadMediaOnSavingExceptionOnAssureSecureFolderAssignment() {
		//given
		when(insecureMedia.getFolder()).thenReturn(rootFolder);
		Mockito.lenient().when(insecureMedia.getCode()).thenReturn(MediaManager.ROOT_FOLDER_QUALIFIER);
		when(rootFolder.getQualifier()).thenReturn(MediaManager.ROOT_FOLDER_QUALIFIER);

		when(backofficeConfigurationMediaHelper.getSecureFolder()).thenReturn(secureMediaFolder);
		Mockito.lenient().when(secureMediaFolder.getQualifier()).thenReturn("secure");
		doThrow(ModelSavingException.class).when(modelService).save(any());
		doNothing().when(backofficeConfigurationMediaHelper).failOnInsecureFolderAssignment(MediaManager.ROOT_FOLDER_QUALIFIER);

		//when
		backofficeConfigurationMediaHelper.assureSecureFolderAssignment(insecureMedia);

		//then
		verify(insecureMedia).setFolder(secureMediaFolder);
		verify(backofficeConfigurationMediaHelper).failOnInsecureFolderAssignment(MediaManager.ROOT_FOLDER_QUALIFIER);
	}

	@Test
	public void findBackofficeMediaCatalogShouldBeDoneWithAdminRights()
	{
		//given
		final EmployeeModel admin = mock(EmployeeModel.class);
		doReturn(admin).when(userService).getAdminUser();

		//when
		backofficeConfigurationMediaHelper.findBackofficeMediaCatalog();

		//then
		verify(sessionService).executeInLocalView(any(), eq(admin));
	}
}
