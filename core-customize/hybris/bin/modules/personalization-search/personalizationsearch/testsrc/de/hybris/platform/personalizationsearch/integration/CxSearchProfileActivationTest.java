/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationsearch.integration;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContext;
import de.hybris.platform.adaptivesearch.context.impl.DefaultAsSearchProfileContext;
import de.hybris.platform.adaptivesearch.data.AsSearchProfileActivationGroup;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchProfileModel;
import de.hybris.platform.adaptivesearch.services.AsSearchProfileActivationService;
import de.hybris.platform.adaptivesearch.strategies.AsSearchProfileActivationStrategy;
import de.hybris.platform.adaptivesearch.strategies.AsSearchProfileRegistry;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationsearch.strategies.impl.CxSearchProfileActivationStrategy;
import de.hybris.platform.personalizationservices.customization.CxCustomizationService;
import de.hybris.platform.personalizationservices.model.CxAbstractActionModel;
import de.hybris.platform.personalizationservices.model.CxCustomizationModel;
import de.hybris.platform.personalizationservices.model.CxCustomizationsGroupModel;
import de.hybris.platform.personalizationservices.model.CxVariationModel;
import de.hybris.platform.personalizationservices.service.CxService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class CxSearchProfileActivationTest extends ServicelayerTransactionalTest
{
	private static final String CX_USER = "cxuser@hybris.com";

	private static final String SEARCH_PROFILE1_CODE = "searchProfile1";
	private static final String SEARCH_PROFILE2_CODE = "searchProfile2";
	private static final String SEARCH_PROFILE3_CODE = "searchProfile3";
	private static final String SEARCH_PROFILE4_CODE = "searchProfile4";

	private static final String INDEX_CONFIGURATION = "indexConfiguration";
	private static final String INDEX_TYPE = "testIndex";

	private static final String CX_CATALOG_ID = "cxCatalog";
	private static final String SUMMER_CATALOG_ID = "summerCatalog";
	private static final String WINTER_CATALOG_ID = "winterCatalog";

	private static final String VERSION_ONLINE = "Online";

	@Resource
	private ModelService modelService;

	@Resource
	private UserService userService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private CxService cxService;

	@Resource
	private CxCustomizationService cxCustomizationService;

	@Resource
	private AsSearchProfileActivationService asSearchProfileActivationService;

	@Resource
	private AsSearchProfileActivationStrategy cxSearchProfileActivationStrategy;

	@Resource
	private AsSearchProfileRegistry asSearchProfileRegistry;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		importCsv("/personalizationsearch/test/cxSearchProfileActivationTest.impex", "UTF-8");
	}

	@Test
	public void noActiveSearchProfile() throws Exception
	{
		// when
		final List<AsSearchProfileActivationGroup> activationGroups = activateSearchProfiles(Collections.emptyList());

		// then
		final Optional<AsSearchProfileActivationGroup> mainGroupOptional = findPersonalizationActivationGroup(activationGroups);
		assertThat(mainGroupOptional).isEmpty();
	}

	@Test
	public void noActiveSearchProfileBecauseOfNonMatchingCatalog() throws Exception
	{
		// given
		final CatalogVersionModel summerCatalogVersion = catalogVersionService.getCatalogVersion(SUMMER_CATALOG_ID, VERSION_ONLINE);
		final CatalogVersionModel winterCatalogVersion = catalogVersionService.getCatalogVersion(WINTER_CATALOG_ID, VERSION_ONLINE);

		// when
		importCsv(
				"/personalizationsearch/test/cxSearchProfileActivationTest_noActiveSearchProfileBecauseOfNonMatchingCatalog.impex",
				"UTF-8");

		final List<AsSearchProfileActivationGroup> activationGroups = activateSearchProfiles(
				Arrays.asList(summerCatalogVersion, winterCatalogVersion));

		// then
		final Optional<AsSearchProfileActivationGroup> mainGroupOptional = findPersonalizationActivationGroup(activationGroups);
		assertThat(mainGroupOptional).isEmpty();
	}

	@Test
	public void noActiveSearchProfileBecauseOfNonMatchingIndexType() throws Exception
	{
		// given
		final CatalogVersionModel summerCatalogVersion = catalogVersionService.getCatalogVersion(SUMMER_CATALOG_ID, VERSION_ONLINE);

		// when
		importCsv(
				"/personalizationsearch/test/cxSearchProfileActivationTest_noActiveSearchProfileBecauseOfNonMatchingIndexType.impex",
				"UTF-8");

		final List<AsSearchProfileActivationGroup> activationGroups = activateSearchProfiles(Arrays.asList(summerCatalogVersion));

		// then
		final Optional<AsSearchProfileActivationGroup> mainGroupOptional = findPersonalizationActivationGroup(activationGroups);
		assertThat(mainGroupOptional).isEmpty();
	}

	@Test
	public void activateSearchProfileForVariation() throws Exception
	{
		// given
		final CatalogVersionModel summerCatalogVersion = catalogVersionService.getCatalogVersion(SUMMER_CATALOG_ID, VERSION_ONLINE);

		// when
		importCsv("/personalizationsearch/test/cxSearchProfileActivationTest_activateSearchProfileForVariation.impex", "UTF-8");

		final List<AsSearchProfileActivationGroup> activationGroups = activateSearchProfiles(Arrays.asList(summerCatalogVersion));

		// then
		final Optional<AsSearchProfileActivationGroup> mainGroupOptional = findPersonalizationActivationGroup(activationGroups);
		assertThat(mainGroupOptional).isNotEmpty();

		final AsSearchProfileActivationGroup mainGroup = mainGroupOptional.get();
		assertThat(mainGroup.getSearchProfiles()).isEmpty();
		assertThat(mainGroup.getGroups()).hasSize(1);

		final AsSearchProfileActivationGroup group1 = mainGroup.getGroups().get(0);
		assertThat(group1.getSearchProfiles()).hasSize(1);
		assertThat(group1.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE1_CODE);
		assertThat(group1.getGroups()).isEmpty();
	}

	@Test
	public void activateMultipleSearchProfilesForVariation() throws Exception
	{
		// given
		final CatalogVersionModel summerCatalogVersion = catalogVersionService.getCatalogVersion(SUMMER_CATALOG_ID, VERSION_ONLINE);

		// when
		importCsv("/personalizationsearch/test/cxSearchProfileActivationTest_activateMultipleSearchProfilesForVariation.impex",
				"UTF-8");

		final List<AsSearchProfileActivationGroup> activationGroups = activateSearchProfiles(Arrays.asList(summerCatalogVersion));

		// then
		final Optional<AsSearchProfileActivationGroup> mainGroupOptional = findPersonalizationActivationGroup(activationGroups);
		assertThat(mainGroupOptional).isNotEmpty();

		final AsSearchProfileActivationGroup mainGroup = mainGroupOptional.get();
		assertThat(mainGroup.getSearchProfiles()).isEmpty();
		assertThat(mainGroup.getGroups()).hasSize(1);

		final AsSearchProfileActivationGroup group1 = mainGroup.getGroups().get(0);
		assertThat(group1.getSearchProfiles()).hasSize(2);
		assertThat(group1.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE1_CODE,
				SEARCH_PROFILE3_CODE);
	}

	@Test
	public void activateMultipleSearchProfilesForMultipleVariations() throws Exception
	{
		// given
		final CatalogVersionModel winterCatalogVersion = catalogVersionService.getCatalogVersion(WINTER_CATALOG_ID, VERSION_ONLINE);

		// when
		importCsv(
				"/personalizationsearch/test/cxSearchProfileActivationTest_activateMultipleSearchProfilesForMultipleVariations.impex",
				"UTF-8");

		final List<AsSearchProfileActivationGroup> activationGroups = activateSearchProfiles(Arrays.asList(winterCatalogVersion));

		// then
		final Optional<AsSearchProfileActivationGroup> mainGroupOptional = findPersonalizationActivationGroup(activationGroups);
		assertThat(mainGroupOptional).isNotEmpty();

		final AsSearchProfileActivationGroup mainGroup = mainGroupOptional.get();
		assertThat(mainGroup.getSearchProfiles()).isEmpty();
		assertThat(mainGroup.getGroups()).hasSize(2);

		final AsSearchProfileActivationGroup group1 = mainGroup.getGroups().get(1);
		assertThat(group1.getSearchProfiles()).hasSize(2);
		assertThat(group1.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE1_CODE,
				SEARCH_PROFILE2_CODE);

		final AsSearchProfileActivationGroup group2 = mainGroup.getGroups().get(0);
		assertThat(group2.getSearchProfiles()).hasSize(2);
		assertThat(group2.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE3_CODE,
				SEARCH_PROFILE4_CODE);
	}

	@Test
	public void activateMultipleSearchProfilesForMultipleVariationsWithCustomCustomizationsRank() throws Exception
	{
		// given
		final CatalogVersionModel cxCatalogVersion = catalogVersionService.getCatalogVersion(CX_CATALOG_ID, VERSION_ONLINE);
		final CatalogVersionModel winterCatalogVersion = catalogVersionService.getCatalogVersion(WINTER_CATALOG_ID, VERSION_ONLINE);

		// when
		importCsv(
				"/personalizationsearch/test/cxSearchProfileActivationTest_activateMultipleSearchProfilesForMultipleVariations.impex",
				"UTF-8");

		final CxCustomizationsGroupModel customizationGroup = cxCustomizationService.getDefaultGroup(cxCatalogVersion);

		final List<CxCustomizationModel> customizations = new ArrayList<>(customizationGroup.getCustomizations());
		Collections.reverse(customizations);
		customizationGroup.setCustomizations(customizations);

		modelService.save(customizationGroup);

		final List<AsSearchProfileActivationGroup> activationGroups = activateSearchProfiles(Arrays.asList(winterCatalogVersion));

		// then
		final Optional<AsSearchProfileActivationGroup> mainGroupOptional = findPersonalizationActivationGroup(activationGroups);
		assertThat(mainGroupOptional).isNotEmpty();

		final AsSearchProfileActivationGroup mainGroup = mainGroupOptional.get();
		assertThat(mainGroup.getSearchProfiles()).isEmpty();
		assertThat(mainGroup.getGroups()).hasSize(2);

		final AsSearchProfileActivationGroup group1 = mainGroup.getGroups().get(1);
		assertThat(group1.getSearchProfiles()).hasSize(2);
		assertThat(group1.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE3_CODE,
				SEARCH_PROFILE4_CODE);

		final AsSearchProfileActivationGroup group2 = mainGroup.getGroups().get(0);
		assertThat(group2.getSearchProfiles()).hasSize(2);
		assertThat(group2.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE1_CODE,
				SEARCH_PROFILE2_CODE);
	}

	@Test
	public void activateMultipleSearchProfilesForMultipleVariationsWithCustomVariationsRank() throws Exception
	{
		// given
		final CatalogVersionModel cxCatalogVersion = catalogVersionService.getCatalogVersion(CX_CATALOG_ID, VERSION_ONLINE);
		final CatalogVersionModel winterCatalogVersion = catalogVersionService.getCatalogVersion(WINTER_CATALOG_ID, VERSION_ONLINE);

		// when
		importCsv(
				"/personalizationsearch/test/cxSearchProfileActivationTest_activateMultipleSearchProfilesForMultipleVariations.impex",
				"UTF-8");

		final CxCustomizationsGroupModel customizationGroup = cxCustomizationService.getDefaultGroup(cxCatalogVersion);

		for (final CxCustomizationModel customization : customizationGroup.getCustomizations())
		{
			final List<CxVariationModel> variations = new ArrayList<>(customization.getVariations());
			Collections.reverse(variations);
			customization.setVariations(variations);

			modelService.save(customization);
		}

		final List<AsSearchProfileActivationGroup> activationGroups = activateSearchProfiles(Arrays.asList(winterCatalogVersion));

		// then
		final Optional<AsSearchProfileActivationGroup> mainGroupOptional = findPersonalizationActivationGroup(activationGroups);
		assertThat(mainGroupOptional).isNotEmpty();

		final AsSearchProfileActivationGroup mainGroup = mainGroupOptional.get();
		assertThat(mainGroup.getSearchProfiles()).isEmpty();
		assertThat(mainGroup.getGroups()).hasSize(2);

		final AsSearchProfileActivationGroup group1 = mainGroup.getGroups().get(1);
		assertThat(group1.getSearchProfiles()).hasSize(2);
		assertThat(group1.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE1_CODE,
				SEARCH_PROFILE2_CODE);

		final AsSearchProfileActivationGroup group2 = mainGroup.getGroups().get(0);
		assertThat(group2.getSearchProfiles()).hasSize(2);
		assertThat(group2.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE3_CODE,
				SEARCH_PROFILE4_CODE);
	}

	@Test
	public void activateMultipleSearchProfilesForMultipleVariationsWithCustomActionsRank() throws Exception
	{
		// given
		final CatalogVersionModel cxCatalogVersion = catalogVersionService.getCatalogVersion(CX_CATALOG_ID, VERSION_ONLINE);
		final CatalogVersionModel winterCatalogVersion = catalogVersionService.getCatalogVersion(WINTER_CATALOG_ID, VERSION_ONLINE);

		// when
		importCsv(
				"/personalizationsearch/test/cxSearchProfileActivationTest_activateMultipleSearchProfilesForMultipleVariations.impex",
				"UTF-8");

		final CxCustomizationsGroupModel customizationGroup = cxCustomizationService.getDefaultGroup(cxCatalogVersion);

		for (final CxCustomizationModel customization : customizationGroup.getCustomizations())
		{
			for (final CxVariationModel variation : customization.getVariations())
			{
				final List<CxAbstractActionModel> actions = new ArrayList<>(variation.getActions());
				Collections.reverse(actions);
				variation.setActions(actions);

				modelService.save(variation);
			}
		}

		final List<AsSearchProfileActivationGroup> activationGroups = activateSearchProfiles(Arrays.asList(winterCatalogVersion));

		// then
		final Optional<AsSearchProfileActivationGroup> mainGroupOptional = findPersonalizationActivationGroup(activationGroups);
		assertThat(mainGroupOptional).isNotEmpty();

		final AsSearchProfileActivationGroup mainGroup = mainGroupOptional.get();
		assertThat(mainGroup.getSearchProfiles()).isEmpty();
		assertThat(mainGroup.getGroups()).hasSize(2);

		final AsSearchProfileActivationGroup group1 = mainGroup.getGroups().get(1);
		assertThat(group1.getSearchProfiles()).hasSize(2);
		assertThat(group1.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE2_CODE,
				SEARCH_PROFILE1_CODE);

		final AsSearchProfileActivationGroup group2 = mainGroup.getGroups().get(0);
		assertThat(group2.getSearchProfiles()).hasSize(2);
		assertThat(group2.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE4_CODE,
				SEARCH_PROFILE3_CODE);
	}

	@Test
	public void activateMultipleSearchProfilesForMultipleVariationsWithCustomRank() throws Exception
	{
		// given
		final CatalogVersionModel cxCatalogVersion = catalogVersionService.getCatalogVersion(CX_CATALOG_ID, VERSION_ONLINE);
		final CatalogVersionModel winterCatalogVersion = catalogVersionService.getCatalogVersion(WINTER_CATALOG_ID, VERSION_ONLINE);

		// when
		importCsv(
				"/personalizationsearch/test/cxSearchProfileActivationTest_activateMultipleSearchProfilesForMultipleVariations.impex",
				"UTF-8");

		final CxCustomizationsGroupModel customizationGroup = cxCustomizationService.getDefaultGroup(cxCatalogVersion);

		final List<CxCustomizationModel> customizations = new ArrayList<>(customizationGroup.getCustomizations());
		Collections.reverse(customizations);
		customizationGroup.setCustomizations(customizations);

		modelService.save(customizationGroup);

		for (final CxCustomizationModel customization : customizations)
		{
			final List<CxVariationModel> variations = new ArrayList<>(customization.getVariations());
			Collections.reverse(variations);
			customization.setVariations(variations);

			modelService.save(customization);

			for (final CxVariationModel variation : variations)
			{
				final List<CxAbstractActionModel> actions = new ArrayList<>(variation.getActions());
				Collections.reverse(actions);
				variation.setActions(actions);

				modelService.save(variation);
			}
		}

		final List<AsSearchProfileActivationGroup> activationGroups = activateSearchProfiles(Arrays.asList(winterCatalogVersion));

		// then
		final Optional<AsSearchProfileActivationGroup> mainGroupOptional = findPersonalizationActivationGroup(activationGroups);
		assertThat(mainGroupOptional).isNotEmpty();

		final AsSearchProfileActivationGroup mainGroup = mainGroupOptional.get();
		assertThat(mainGroup.getSearchProfiles()).isEmpty();
		assertThat(mainGroup.getGroups()).hasSize(2);

		final AsSearchProfileActivationGroup group1 = mainGroup.getGroups().get(1);
		assertThat(group1.getSearchProfiles()).hasSize(2);
		assertThat(group1.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE4_CODE,
				SEARCH_PROFILE3_CODE);

		final AsSearchProfileActivationGroup group2 = mainGroup.getGroups().get(0);
		assertThat(group2.getSearchProfiles()).hasSize(2);
		assertThat(group2.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE2_CODE,
				SEARCH_PROFILE1_CODE);
	}

	protected Optional<AsSearchProfileActivationGroup> findPersonalizationActivationGroup(
			final List<AsSearchProfileActivationGroup> activationGroups)
	{
		if (CollectionUtils.isNotEmpty(activationGroups))
		{
			return activationGroups.stream().filter(activationGroup -> StringUtils.equals(activationGroup.getId(),
					CxSearchProfileActivationStrategy.ACTIVATION_GROUP_ID)).findFirst();
		}

		return Optional.empty();
	}

	protected List<AsSearchProfileActivationGroup> activateSearchProfiles(final List<CatalogVersionModel> catalogVersions)
	{
		final UserModel cxUser = userService.getUserForUID(CX_USER);
		final CatalogVersionModel cxCatalogVersion = catalogVersionService.getCatalogVersion(CX_CATALOG_ID, VERSION_ONLINE);

		final List<CatalogVersionModel> sessionCatalogVersions = new ArrayList<>();
		sessionCatalogVersions.add(cxCatalogVersion);
		CollectionUtils.addAll(sessionCatalogVersions, catalogVersions);

		userService.setCurrentUser(cxUser);
		catalogVersionService.setSessionCatalogVersions(sessionCatalogVersions);

		cxService.calculateAndStorePersonalization(cxUser, cxCatalogVersion);
		cxService.loadPersonalizationInSession(cxUser, Collections.singleton(cxCatalogVersion));

		final AsSearchProfileContext context = DefaultAsSearchProfileContext.builder().withIndexConfiguration(INDEX_CONFIGURATION)
				.withIndexType(INDEX_TYPE).withCatalogVersions(sessionCatalogVersions).build();

		return asSearchProfileActivationService.getSearchProfileActivationGroupsForContext(context);
	}
}
