/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.excel.classification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.ListUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


@RunWith(MockitoJUnitRunner.class)
public class DefaultExcelClassificationServiceTest
{

	@Mock
	private CatalogService catalogService;
	@Mock
	private ClassificationService classificationService;
	@Mock
	private PermissionCRUDService permissionCRUDService;

	private final DefaultExcelClassificationService service = new DefaultExcelClassificationService();

	public static final String HARDWARE = "Hardware";

	@Mock
	private ClassificationClassModel hardwareModel;
	@Mock
	private ClassificationAttributeModel name;
	@Mock
	private ClassificationAttributeModel manufacturer;

	@Mock
	private ClassificationClassModel cpu;
	@Mock
	private ClassificationAttributeModel speed;
	@Mock
	private ClassificationAttributeModel cores;
	@Mock
	private ClassificationSystemVersionModel hardwareSystemVersion;
	@Mock
	private ClassificationSystemVersionModel cpuSystemVersion;
	@Mock
	private ClassificationSystemModel hardwareSystem;
	@Mock
	private ClassificationSystemModel cpuSystem;

	@Before
	public void setUp()
	{
		service.setCatalogService(catalogService);
		service.setClassificationService(classificationService);
		service.setPermissionCRUDService(permissionCRUDService);

		given(permissionCRUDService.canReadType(ClassificationClassModel._TYPECODE)).willReturn(true);
		given(permissionCRUDService.canReadType(ClassificationAttributeModel._TYPECODE)).willReturn(true);
		given(permissionCRUDService.canReadType(ClassAttributeAssignmentModel._TYPECODE)).willReturn(true);
	}

	protected Collection<ClassificationSystemModel> createClassificationSystems(
			final ClassificationSystemPOJO... classificationSystemPOJOS)
	{
		return Stream.of(classificationSystemPOJOS).map(classificationSystemPOJO -> {
			final ClassificationSystemModel classificationSystem = mock(ClassificationSystemModel.class);

			final List<CategoryModel> categoryModels = classificationSystemPOJO.getCategoryPOJOS().stream().map(this::mockCategory)
					.collect(Collectors.toList());
			CatalogVersionModel catalogVersionModel = mock(CatalogVersionModel.class);
			given(classificationSystem.getCatalogVersions()).willReturn(Sets.newHashSet(catalogVersionModel));
			given(catalogVersionModel.getRootCategories()).willReturn(categoryModels);

			return classificationSystem;
		}).collect(Collectors.toList());
	}

	private CategoryModel mockCategory(final CategoryPOJO categoryPOJO)
	{
		final CategoryModel category = mock(ClassificationClassModel.class);
		Mockito.lenient().when(category.getName()).thenReturn(categoryPOJO.getName());
		given(category.getCode()).willReturn(categoryPOJO.getName());
		given(category.getCatalogVersion()).willReturn(categoryPOJO.getCatalogVersionModel());
		given(categoryPOJO.getCatalogVersionModel().getCatalog()).willReturn(mock(ClassificationSystemModel.class));
		final List<CategoryModel> subcategories = new ArrayList<>();
		for (final CategoryPOJO subcategory : categoryPOJO.getSubcategories())
		{
			final CategoryModel subcategoryModel = mockCategory(subcategory);
			subcategories.add(subcategoryModel);
			subcategories.addAll(subcategoryModel.getAllSubcategories());
		}
		given(category.getAllSubcategories()).willReturn(subcategories);
		return category;
	}

	@Test
	public void shouldReturnAllClassificationClasses()
	{
		// given
		final ClassificationSystemVersionModel classificationSystemVersionModel = mock(ClassificationSystemVersionModel.class);

		final ClassificationSystemPOJO system1 = new ClassificationSystemPOJO( //
				new CategoryPOJO(classificationSystemVersionModel, "name1"), //
				new CategoryPOJO(classificationSystemVersionModel, "name2"), //
				new CategoryPOJO(classificationSystemVersionModel, "name3") //
		);

		final ClassificationSystemPOJO system2 = new ClassificationSystemPOJO( //
				new CategoryPOJO(classificationSystemVersionModel, "name4",
						Arrays.asList(new CategoryPOJO(classificationSystemVersionModel, "name5",
								Arrays.asList(new CategoryPOJO(classificationSystemVersionModel, "name6",
										Arrays.asList(new CategoryPOJO(classificationSystemVersionModel, "name7"))))))));

		final Collection<ClassificationSystemModel> classificationSystemModels = createClassificationSystems(system1, system2);
		given(catalogService.getAllCatalogsOfType(ClassificationSystemModel.class)).willReturn(classificationSystemModels);

		// when
		final Map<ClassificationSystemVersionModel, List<ClassificationClassModel>> returnedValue = service
				.getAllClassificationClasses();

		// then
		assertThat(returnedValue.size()).isEqualTo(1);
		assertThat(returnedValue.values().stream().reduce(ListUtils::union).get().size()).isEqualTo(7);
	}

	@Test
	public void shouldMergeValuesByCategoryName()
	{
		// given
		final ClassificationSystemVersionModel classificationSystemVersionModel = mock(ClassificationSystemVersionModel.class);
		final String name1 = "name1";
		final String name2 = "name2";
		final String name3 = "name3";

		final ClassificationSystemPOJO system1 = new ClassificationSystemPOJO( //
				new CategoryPOJO(classificationSystemVersionModel, name1), //
				new CategoryPOJO(classificationSystemVersionModel, name2), //
				new CategoryPOJO(classificationSystemVersionModel, name3) //
		);

		final ClassificationSystemPOJO system2 = new ClassificationSystemPOJO( //
				new CategoryPOJO(classificationSystemVersionModel, name1), //
				new CategoryPOJO(classificationSystemVersionModel, name1), //
				new CategoryPOJO(classificationSystemVersionModel, name2), //
				new CategoryPOJO(classificationSystemVersionModel, name3) //
		);

		// when
		final Collection<ClassificationSystemModel> classificationSystemModels = createClassificationSystems(system1, system2);
		given(catalogService.getAllCatalogsOfType(ClassificationSystemModel.class)).willReturn(classificationSystemModels);

		// when
		final Map<ClassificationSystemVersionModel, List<ClassificationClassModel>> returnedValue = service
				.getAllClassificationClasses();

		// then
		assertThat(returnedValue.values().stream().reduce(ListUtils::union).get().size()).isEqualTo(3);
	}

	@Test
	public void shouldReturnedValuesBeResultOfIntersection()
	{
		// given
		initializeTestData();
		final ProductModel intelCore = createProductWithClassificationClasses(hardwareModel, cpu);
		final ProductModel amdRyzen = createProductWithClassificationClasses(hardwareModel);

		// when
		final Map<ClassificationSystemVersionModel, List<ClassificationClassModel>> returnedValue = service
				.getItemsIntersectedClassificationClasses(Lists.newArrayList(intelCore, amdRyzen));

		// then
		assertThat(returnedValue.size()).isEqualTo(1);
		assertThat(new ArrayList<>(returnedValue.values()).get(0).get(0)).isEqualTo(hardwareModel);
	}

	@Test
	public void shouldReturnedValuesBeResultOfUnion()
	{
		// given
		initializeTestData();
		final ProductModel intelCore = createProductWithClassificationClasses(hardwareModel, cpu);
		final ProductModel amdRyzen = createProductWithClassificationClasses(hardwareModel);

		// when
		final Map<ClassificationSystemVersionModel, List<ClassificationClassModel>> returnedValue = service
				.getItemsAddedClassificationClasses(Lists.newArrayList(intelCore, amdRyzen));

		// then
		assertThat(returnedValue.size()).isEqualTo(2);
		assertThat(returnedValue).containsValues(List.of(cpu), List.of(hardwareModel));
	}

	private class ClassificationSystemPOJO
	{
		private final Collection<CategoryPOJO> categoryPOJOS;

		public ClassificationSystemPOJO(final CategoryPOJO... categoryPOJOS)
		{
			this.categoryPOJOS = Arrays.asList(categoryPOJOS);
		}

		public Collection<CategoryPOJO> getCategoryPOJOS()
		{
			return categoryPOJOS;
		}
	}

	private class CategoryPOJO
	{
		private final CatalogVersionModel catalogVersionModel;
		private final String name;
		private final List<CategoryPOJO> subcategories;

		public CategoryPOJO(final CatalogVersionModel catalogVersionModel, final String name)
		{
			this.catalogVersionModel = catalogVersionModel;
			this.name = name;
			this.subcategories = new ArrayList<>();
		}

		public CategoryPOJO(final CatalogVersionModel catalogVersionModel, final String name,
				final List<CategoryPOJO> subcategories)
		{
			this.catalogVersionModel = catalogVersionModel;
			this.name = name;
			this.subcategories = subcategories;
		}

		public CatalogVersionModel getCatalogVersionModel()
		{
			return catalogVersionModel;
		}

		public String getName()
		{
			return name;
		}

		public List<CategoryPOJO> getSubcategories()
		{
			return subcategories;
		}
	}

	protected void initializeTestData()
	{
		initializeClassificationClass(hardwareModel, hardwareSystemVersion, hardwareSystem, HARDWARE);
		initializeClassificationClass(cpu, cpuSystemVersion, cpuSystem, "Cpu");

		Mockito.lenient().when(name.getName()).thenReturn("Name");
		Mockito.lenient().when(name.getCode()).thenReturn("Name");
		Mockito.lenient().when(manufacturer.getName()).thenReturn("Manufacturer");
		Mockito.lenient().when(manufacturer.getCode()).thenReturn("Manufacturer");
		Mockito.lenient().when(speed.getName()).thenReturn("Speed");
		Mockito.lenient().when(speed.getCode()).thenReturn("Speed");
		Mockito.lenient().when(cores.getName()).thenReturn("Cores");
		Mockito.lenient().when(cores.getCode()).thenReturn("Cores");
	}

	private ProductModel createProductWithClassificationClasses(final ClassificationClassModel... classes)
	{
		final ProductModel product = mock(ProductModel.class);
		final FeatureList features = mock(FeatureList.class);
		when(classificationService.getFeatures(product)).thenReturn(features);
		when(features.getClassificationClasses()).thenReturn(Sets.newHashSet(classes));
		return product;
	}

	private void initializeClassificationClass(final ClassificationClassModel classificationClass,
			final ClassificationSystemVersionModel systemVersion, final ClassificationSystemModel system, final String name)
	{
		Mockito.lenient().when(classificationClass.getName()).thenReturn(name);
		when(classificationClass.getCode()).thenReturn(name);
		when(classificationClass.getCatalogVersion()).thenReturn(systemVersion);
		when(systemVersion.getCatalog()).thenReturn(system);
		when(system.getId()).thenReturn(name);
	}

	@Test
	public void shouldNotGetItemsIntersectedClassificationClassesIfHasNoPermissions()
	{
		// given
		final Collection<ItemModel> anyItems = null;
		given(permissionCRUDService.canReadType(ClassificationClassModel._TYPECODE)).willReturn(false);

		// when
		final Map<ClassificationSystemVersionModel, List<ClassificationClassModel>> result = service
				.getItemsIntersectedClassificationClasses(anyItems);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	public void shouldNotGetItemsAddedClassificationClassesIfHasNoPermissions()
	{
		// given
		final Collection<ItemModel> anyItems = null;
		given(permissionCRUDService.canReadType(ClassificationClassModel._TYPECODE)).willReturn(false);

		// when
		final Map<ClassificationSystemVersionModel, List<ClassificationClassModel>> result = service
				.getItemsAddedClassificationClasses(anyItems);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	public void shouldNotGetAllClassificationClassesIfHasNoPermissions()
	{
		// given
		given(permissionCRUDService.canReadType(ClassificationClassModel._TYPECODE)).willReturn(false);

		// when
		final Map<ClassificationSystemVersionModel, List<ClassificationClassModel>> result = service.getAllClassificationClasses();

		// then
		assertThat(result).isEmpty();
	}
}
