/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.permissionsfacades.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.permissionsfacades.data.CatalogPermissionsData;
import de.hybris.platform.permissionsfacades.data.SyncPermissionsData;
import de.hybris.platform.permissionsfacades.data.TypePermissionsData;
import de.hybris.platform.permissionsfacades.data.TypePermissionsDataList;
import de.hybris.platform.permissionsfacades.strategy.ApplyPermissionsStrategy;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.security.permissions.PermissionCheckResult;
import de.hybris.platform.servicelayer.security.permissions.PermissionCheckingService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static de.hybris.platform.servicelayer.security.permissions.PermissionsConstants.CHANGE;
import static de.hybris.platform.servicelayer.security.permissions.PermissionsConstants.CHANGE_PERMISSIONS;
import static de.hybris.platform.servicelayer.security.permissions.PermissionsConstants.CREATE;
import static de.hybris.platform.servicelayer.security.permissions.PermissionsConstants.READ;
import static de.hybris.platform.servicelayer.security.permissions.PermissionsConstants.REMOVE;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPermissionsFacadeTest
{
	private static final String PRINCIPAL_UID = "principalUid";
	private static final String ELECTRONICS_ID = "electronics";
	private static final String APPAREL_ID = "apparel";
	private static final String STAGED_VERSION = "staged";
	private static final String ONLINE_VERSION = "online";


	@InjectMocks
	private DefaultPermissionsFacade defaultPermissionsFacade;

	@Mock
	private FlexibleSearchService flexibleSearchService;
	@Mock
	private TypeService typeService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private SessionService sessionService;
	@Mock
	private SearchRestrictionService searchRestrictionService;
	@Mock
	private CatalogSynchronizationService catalogSynchronizationService;
	@Mock
	private ApplyPermissionsStrategy applyPermissionsStrategy;
	@Mock
	private PermissionCheckingService permissionCheckingService;

	@Mock
	private ComposedTypeModel composedType;
	@Mock
	private PrincipalModel principal;
	@Mock
	private CatalogModel electronics;
	@Mock
	private CatalogModel apparel;
	@Mock
	private CatalogVersionModel electronicsStaged; //Writable catalog for principal
	@Mock
	private CatalogVersionModel electronicsOnline; //Readable catalog for principal
	@Mock
	private CatalogVersionModel apparelStaged; //Writable catalog for principal
	@Mock
	private CatalogVersionModel apparelOnline; //Readable catalog for principal
	@Mock
	private PrincipalGroupModel principalGroup;

	@Mock
	private SyncItemJobModel electronicsStagedSyncJob;
	@Mock
	private SyncItemJobModel electronicsOnlineSyncJob;
	@Mock
	private SyncItemJobModel apparelStagedSyncJob;
	@Mock
	private SyncItemJobModel apparelOnlineSyncJob;

	@Before
	public void setUp()
	{
		when(flexibleSearchService.getModelByExample(any())).thenReturn(principal);

		when(catalogVersionService.getCatalogVersion(ELECTRONICS_ID, STAGED_VERSION)).thenReturn(electronicsStaged);
		when(catalogVersionService.getCatalogVersion(ELECTRONICS_ID, ONLINE_VERSION)).thenReturn(electronicsOnline);
		when(catalogVersionService.getCatalogVersion(APPAREL_ID, STAGED_VERSION)).thenReturn(apparelStaged);
		when(catalogVersionService.getCatalogVersion(APPAREL_ID, ONLINE_VERSION)).thenReturn(apparelOnline);
		when(catalogVersionService.getAllCatalogVersions())
				.thenReturn(Arrays.asList(electronicsStaged, electronicsOnline, apparelStaged, apparelOnline));

		doAnswer(invocation -> {
			final Object[] args = invocation.getArguments();
			final SessionExecutionBody executionBody = (SessionExecutionBody) args[0];
			return executionBody.execute();
		}).when(sessionService).executeInLocalView(any());

		doNothing().when(searchRestrictionService).disableSearchRestrictions();
		doNothing().when(searchRestrictionService).enableSearchRestrictions();
		doNothing().when(catalogVersionService).setSessionCatalogVersions(any());

		when(catalogVersionService.getAllWritableCatalogVersions(principal))
				.thenReturn(Arrays.asList(electronicsStaged, apparelStaged));
		when(catalogVersionService.getAllReadableCatalogVersions(principal))
				.thenReturn(Arrays.asList(electronicsOnline, apparelOnline, electronicsStaged, apparelStaged));

		when(electronics.getId()).thenReturn(ELECTRONICS_ID);
		when(apparel.getId()).thenReturn(APPAREL_ID);

		when(electronicsStaged.getCatalog()).thenReturn(electronics);
		when(electronicsOnline.getCatalog()).thenReturn(electronics);
		when(apparelStaged.getCatalog()).thenReturn(apparel);
		when(apparelOnline.getCatalog()).thenReturn(apparel);

		when(electronicsStaged.getVersion()).thenReturn(STAGED_VERSION);
		when(electronicsOnline.getVersion()).thenReturn(ONLINE_VERSION);
		when(apparelStaged.getVersion()).thenReturn(STAGED_VERSION);
		when(apparelOnline.getVersion()).thenReturn(ONLINE_VERSION);

		when(electronicsStagedSyncJob.getTargetVersion()).thenReturn(electronicsOnline);
		when(electronicsStaged.getSynchronizations()).thenReturn(Collections.singletonList(electronicsStagedSyncJob));

		when(electronicsOnlineSyncJob.getTargetVersion()).thenReturn(electronicsStaged);
		when(electronicsOnline.getSynchronizations()).thenReturn(Collections.singletonList(electronicsOnlineSyncJob));

		when(apparelStagedSyncJob.getTargetVersion()).thenReturn(apparelOnline);
		when(apparelStaged.getSynchronizations()).thenReturn(Collections.singletonList(apparelStagedSyncJob));

		when(apparelOnlineSyncJob.getTargetVersion()).thenReturn(apparelStaged);
		when(apparelOnline.getSynchronizations()).thenReturn(Collections.singletonList(apparelOnlineSyncJob));

		when(catalogSynchronizationService.canSynchronize(electronicsStagedSyncJob, principal)).thenReturn(true);
		when(catalogSynchronizationService.canSynchronize(electronicsOnlineSyncJob, principal)).thenReturn(false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateTypesPermissionsForNullPrincipalUid()
	{
		//when
		defaultPermissionsFacade.calculateTypesPermissions(null, Collections.emptyList(), Collections.emptyList());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateTypesPermissionsForNullTypeList()
	{
		//when
		defaultPermissionsFacade.calculateTypesPermissions(PRINCIPAL_UID, null, Collections.emptyList());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateTypesPermissionsForNullPermissionNames()
	{
		//when
		defaultPermissionsFacade.calculateTypesPermissions(PRINCIPAL_UID, Collections.emptyList(), null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testCalculateAttributesPermissionsWithWrongAttributeName()
	{
		//given
		Mockito.when(typeService.getComposedTypeForCode("Item")).thenReturn(composedType);
		Mockito.when(composedType.getInheritedattributedescriptors()).thenReturn(Collections.emptyList());
		Mockito.when(composedType.getDeclaredattributedescriptors()).thenReturn(Collections.emptyList());

		//when
		defaultPermissionsFacade.calculateAttributesPermissions(PRINCIPAL_UID,
				Collections.singletonList("Item.notExistingAttribute"), Collections.emptyList());
	}


	@Test(expected = UnknownIdentifierException.class)
	public void testCalculateAttributesPermissionsWithWrongAttributeNameFormat()
	{
		//when
		defaultPermissionsFacade.calculateAttributesPermissions(PRINCIPAL_UID, Collections.singletonList("wrongAttributeName"),
				Collections.emptyList());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testCalculateAttributesPermissionsWithWrongAttributeNameFormat1()
	{
		//when
		defaultPermissionsFacade.calculateAttributesPermissions(PRINCIPAL_UID,
				Collections.singletonList("wrong.attribute.name.format"), Collections.emptyList());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateAttributesPermissionsForNullPrincipalUid()
	{
		//when
		defaultPermissionsFacade.calculateAttributesPermissions(null, Collections.emptyList(), Collections.emptyList());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateAttributesPermissionsForNullTypesAttributes()
	{
		//when
		defaultPermissionsFacade.calculateAttributesPermissions(PRINCIPAL_UID, null, Collections.emptyList());
	}


	@Test(expected = IllegalArgumentException.class)
	public void testCalculateAttributesPermissionsForNullPermissionNames()
	{
		//when
		defaultPermissionsFacade.calculateAttributesPermissions(PRINCIPAL_UID, Collections.emptyList(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateGlobalPermissionsForNullPrincipalUid()
	{
		//when
		defaultPermissionsFacade.calculateGlobalPermissions(null, Collections.emptyList());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateGlobalPermissionsForNullPermissionNames()
	{
		//when
		defaultPermissionsFacade.calculateGlobalPermissions(PRINCIPAL_UID, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateCatalogPermissionsForNullPrincipalUid()
	{
		//when
		defaultPermissionsFacade.calculateCatalogPermissions(null, Collections.emptyList(), Collections.emptyList());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateCatalogPermissionsForNullCatalogList()
	{
		//when
		defaultPermissionsFacade.calculateCatalogPermissions(null, Collections.emptyList(), Collections.emptyList());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateCatalogPermissionsForNullCatalogVersionList()
	{
		//when
		defaultPermissionsFacade.calculateCatalogPermissions(null, Collections.emptyList(), Collections.emptyList());
	}

	@Test
	public void shouldReturn4CatalogPermissionCombinations()
	{
		//GIVEN
		final List<String> catalogIds = Arrays.asList(ELECTRONICS_ID, APPAREL_ID);
		final List<String> catalogVersions = Arrays.asList(STAGED_VERSION, ONLINE_VERSION);

		//WHEN
		final List<CatalogPermissionsData> catalogPermissionsDataList = defaultPermissionsFacade
				.calculateCatalogPermissions(PRINCIPAL_UID, catalogIds, catalogVersions);

		//THEN
		assertThat(catalogPermissionsDataList, hasSize(4));
	}

	@Test
	public void shouldHaveCatalogIdAndVersionInCatalogPermissionsData()
	{
		//GIVEN
		final List<String> catalogIds = Arrays.asList(ELECTRONICS_ID, APPAREL_ID);
		final List<String> catalogVersions = Arrays.asList(STAGED_VERSION, ONLINE_VERSION);

		//WHEN
		final List<CatalogPermissionsData> catalogPermissionsDataList = defaultPermissionsFacade
				.calculateCatalogPermissions(PRINCIPAL_UID, catalogIds, catalogVersions);

		//THEN
		assertThat(catalogPermissionsDataList.stream()
				.filter(data -> data.getCatalogId().equals(ELECTRONICS_ID) && data.getCatalogVersion().equals(STAGED_VERSION))
				.collect(Collectors.toList()), hasSize(1));
		assertThat(catalogPermissionsDataList.stream()
				.filter(data -> data.getCatalogId().equals(ELECTRONICS_ID) && data.getCatalogVersion().equals(ONLINE_VERSION))
				.collect(Collectors.toList()), hasSize(1));
		assertThat(catalogPermissionsDataList.stream()
				.filter(data -> data.getCatalogId().equals(APPAREL_ID) && data.getCatalogVersion().equals(STAGED_VERSION))
				.collect(Collectors.toList()), hasSize(1));
		assertThat(catalogPermissionsDataList.stream()
				.filter(data -> data.getCatalogId().equals(APPAREL_ID) && data.getCatalogVersion().equals(ONLINE_VERSION))
				.collect(Collectors.toList()), hasSize(1));
	}

	@Test
	public void shouldHaveReadAndWritePermissionsForElectronicsStaged()
	{
		//GIVEN
		final List<String> catalogIds = Collections.singletonList(ELECTRONICS_ID);
		final List<String> catalogVersions = Collections.singletonList(STAGED_VERSION);

		//WHEN
		final List<CatalogPermissionsData> catalogPermissionsDataList = defaultPermissionsFacade
				.calculateCatalogPermissions(PRINCIPAL_UID, catalogIds, catalogVersions);
		final Optional<CatalogPermissionsData> electronicsStagedDataOptional = catalogPermissionsDataList.stream()
				.filter(data -> data.getCatalogId().equals(ELECTRONICS_ID) && data.getCatalogVersion().equals(STAGED_VERSION))
				.findFirst();
		final Map<String, String> resultPermissions = electronicsStagedDataOptional.get().getPermissions();

		//THEN
		assertThat(resultPermissions, hasEntry("read", "true"));
		assertThat(resultPermissions, hasEntry("write", "true"));
		assertThat(resultPermissions.entrySet(), hasSize(2));
	}

	@Test
	public void shouldHaveReadOnlyPermissionsForElectronicsOnline()
	{
		//GIVEN
		final List<String> catalogIds = Collections.singletonList(ELECTRONICS_ID);
		final List<String> catalogVersions = Collections.singletonList(ONLINE_VERSION);

		//WHEN
		final List<CatalogPermissionsData> catalogPermissionsDataList = defaultPermissionsFacade
				.calculateCatalogPermissions(PRINCIPAL_UID, catalogIds, catalogVersions);
		final Optional<CatalogPermissionsData> electronicsOnlineDataOptional = catalogPermissionsDataList.stream()
				.filter(data -> data.getCatalogId().equals(ELECTRONICS_ID) && data.getCatalogVersion().equals(ONLINE_VERSION))
				.findFirst();
		final Map<String, String> resultPermissions = electronicsOnlineDataOptional.get().getPermissions();

		//THEN
		assertThat(resultPermissions, hasEntry("read", "true"));
		assertThat(resultPermissions, hasEntry("write", "false"));
		assertThat(resultPermissions.entrySet(), hasSize(2));
	}

	@Test
	public void shouldHaveSyncPermissionToSynchronizeFromElectronicsStagedToOnline()
	{
		//GIVEN
		final List<String> catalogIds = Collections.singletonList(ELECTRONICS_ID);
		final List<String> catalogVersions = Collections.singletonList(STAGED_VERSION);

		//WHEN
		final List<CatalogPermissionsData> catalogPermissionsDataList = defaultPermissionsFacade
				.calculateCatalogPermissions(PRINCIPAL_UID, catalogIds, catalogVersions);
		final Optional<CatalogPermissionsData> electronicsStagedDataOptional = catalogPermissionsDataList.stream()
				.filter(data -> data.getCatalogId().equals(ELECTRONICS_ID) && data.getCatalogVersion().equals(STAGED_VERSION))
				.findFirst();
		final SyncPermissionsData resultPermission = electronicsStagedDataOptional.get().getSyncPermissions().get(0);

		//THEN
		assertEquals(ONLINE_VERSION, resultPermission.getTargetCatalogVersion());
		assertTrue(resultPermission.isCanSynchronize());
	}

	@Test
	public void shouldHaveNoSyncPermissionToSynchronizeFromElectronicsOnlineToStaged()
	{
		//GIVEN
		final List<String> catalogIds = Collections.singletonList(ELECTRONICS_ID);
		final List<String> catalogVersions = Collections.singletonList(ONLINE_VERSION);

		//WHEN
		final List<CatalogPermissionsData> catalogPermissionsDataList = defaultPermissionsFacade
				.calculateCatalogPermissions(PRINCIPAL_UID, catalogIds, catalogVersions);
		final Optional<CatalogPermissionsData> electronicsOnlineDataOptional = catalogPermissionsDataList.stream()
				.filter(data -> data.getCatalogId().equals(ELECTRONICS_ID) && data.getCatalogVersion().equals(ONLINE_VERSION))
				.findFirst();
		final SyncPermissionsData resultPermission = electronicsOnlineDataOptional.get().getSyncPermissions().get(0);

		//THEN
		assertEquals(STAGED_VERSION, resultPermission.getTargetCatalogVersion());
		assertFalse(resultPermission.isCanSynchronize());
	}

	@Test
	public void testApplyPermissions()
	{
		//GIVEN
		final String type = "Item";
		lenient().when(typeService.getComposedTypeForCode(type)).thenReturn(composedType);

		final TypePermissionsDataList permissionsListToUpdate = new TypePermissionsDataList();
		permissionsListToUpdate.setPrincipalUid(PRINCIPAL_UID);

		final TypePermissionsData typePermissionsData = new TypePermissionsData();
		typePermissionsData.setType(type);

		permissionsListToUpdate.setPermissionsList(List.of(typePermissionsData));

		setupPermission(type, READ, Boolean.TRUE);
		setupPermission(type, CHANGE, Boolean.TRUE);
		setupPermission(type, REMOVE, Boolean.FALSE);
		setupPermission(type, CREATE, Boolean.FALSE);
		setupPermission(type, CHANGE_PERMISSIONS, Boolean.TRUE);

		//WHEN
		final var actualResponse = defaultPermissionsFacade.applyPermissions(permissionsListToUpdate);

		//THEN
		assertEquals(PRINCIPAL_UID, actualResponse.getPrincipalUid());
		assertEquals(1, actualResponse.getPermissionsList().size());
		assertEquals(actualResponse.getPermissionsList().get(0).getType(), type);
		assertTrue(actualResponse.getPermissionsList().get(0).getPermissions().getRead());
		assertTrue(actualResponse.getPermissionsList().get(0).getPermissions().getChange());
		assertFalse(actualResponse.getPermissionsList().get(0).getPermissions().getRemove());
		assertFalse(actualResponse.getPermissionsList().get(0).getPermissions().getCreate());
		assertTrue(actualResponse.getPermissionsList().get(0).getPermissions().getChangerights());
	}

	private void setupPermission(final String type, final String permissionName, final Boolean permission)
	{
		PermissionCheckResult permissionCheckResult = mock(PermissionCheckResult.class);
		when(permissionCheckResult.isGranted()).thenReturn(permission);
		when(permissionCheckingService.checkTypePermission(type, principal, permissionName)).thenReturn(permissionCheckResult);
	}
}
