/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.configurablebundlefacades.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchFilterQueryData;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.search.facetdata.BreadcrumbData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * JUnit test suite for {@link DefaultBundleCommerceCartFacade}
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultBundleCommerceCartFacadeTest
{
	@Mock
	private EntryGroupService entryGroupService;
	@Mock
	private CartService cartService;
	@Mock
	private ProductSearchFacade productSearchFacade;
	@Mock
	private BundleTemplateService bundleTemplateService;
	@Mock
	private CommerceCartService commerceCartService;
	@Mock
	private Converter<CommerceCartModification, CartModificationData> cartModificationConverter;
	@Mock
	private ProductService productService;
	@InjectMocks
	private DefaultBundleCommerceCartFacade defaultBundleCommerceCartFacade;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private PageableData pageableData;

	@Before
	public void setUp()
	{
		final EntryGroup entryGroup = new EntryGroup();
		entryGroup.setGroupNumber(Integer.valueOf(1));
		entryGroup.setGroupType(GroupType.CONFIGURABLEBUNDLE);
		entryGroup.setExternalReferenceId("bundleId");
		final CartModel cartModel = new CartModel();
		final ProductSearchPageData<SearchStateData, ProductData> productSearchPageData = new ProductSearchPageData<>();
		productSearchPageData.setBreadcrumbs(Collections.emptyList());
		productSearchPageData.setFacets(Collections.emptyList());
		final SearchStateData searchStateData = new SearchStateData();
		final SearchQueryData searchQuery = new SearchQueryData();
		searchQuery.setValue("");
		searchStateData.setQuery(searchQuery);
		productSearchPageData.setCurrentQuery(searchStateData);
		pageableData = new PageableData();

		Mockito.lenient().when(cartService.getSessionCart()).thenReturn(cartModel);
		Mockito.lenient().when(entryGroupService.getGroup(any(CartModel.class), any(Integer.class))).thenReturn(entryGroup);
		Mockito.lenient().when(productSearchFacade.textSearch(any(SearchStateData.class), any(PageableData.class)))
				.thenReturn(productSearchPageData);
		Mockito.lenient().when(entryGroupService.getGroupOfType(any(CartModel.class), anyCollection(), any(GroupType.class))).thenReturn(entryGroup);
		defaultBundleCommerceCartFacade.setAutoPickEnabled(false);
	}

	@After
	public void tearDown()
	{
		defaultBundleCommerceCartFacade.setAutoPickEnabled(false);
	}

	@Test
	public void shouldFailIfEntryGroupIsNotBundle()
	{
		final EntryGroup testTypeEntryGroup = new EntryGroup();
		testTypeEntryGroup.setGroupNumber(Integer.valueOf(1));
		testTypeEntryGroup.setGroupType(GroupType.valueOf("TEST"));

		Mockito.lenient().when(entryGroupService.getGroupOfType(any(CartModel.class), anyCollection(), eq(GroupType.CONFIGURABLEBUNDLE))).thenThrow(IllegalArgumentException.class);
		thrown.expect(IllegalArgumentException.class);

		defaultBundleCommerceCartFacade.getAllowedProducts(Integer.valueOf(1), "query", new PageableData());
	}

	@Test
	public void shouldCreateFilterQueryForBundleWhenInitQueryEmpty()
	{
		defaultBundleCommerceCartFacade.getAllowedProducts(Integer.valueOf(1), "", pageableData);

		final ArgumentCaptor<SearchStateData> searchStateDataCaptor = ArgumentCaptor.forClass(SearchStateData.class);
		verify(productSearchFacade).textSearch(searchStateDataCaptor.capture(), eq(pageableData));
		Assertions.assertThat(searchStateDataCaptor.getValue().getQuery().getValue()).isEqualTo("");
		Assertions.assertThat(searchStateDataCaptor.getValue().getQuery().getFilterQueries()).hasSize(1);

		final SearchFilterQueryData searchFilterQueryData = searchStateDataCaptor.getValue().getQuery().getFilterQueries().get(0);
		Assertions.assertThat(searchFilterQueryData.getKey()).isEqualTo("bundleTemplates");
		Assertions.assertThat(searchFilterQueryData.getValues().iterator().next()).isEqualTo("bundleId");
	}

	@Test
	public void shouldCreateFilterQueryForBundleWhenInitQueryNotEmpty()
	{
		defaultBundleCommerceCartFacade.getAllowedProducts(Integer.valueOf(1), ":oldQuery", pageableData);

		final ArgumentCaptor<SearchStateData> searchStateDataCaptor = ArgumentCaptor.forClass(SearchStateData.class);
		verify(productSearchFacade).textSearch(searchStateDataCaptor.capture(), eq(pageableData));
		Assertions.assertThat(searchStateDataCaptor.getValue().getQuery().getValue()).isEqualTo(":oldQuery");
		Assertions.assertThat(searchStateDataCaptor.getValue().getQuery().getFilterQueries()).hasSize(1);

		final SearchFilterQueryData searchFilterQueryData = searchStateDataCaptor.getValue().getQuery().getFilterQueries().get(0);
		Assertions.assertThat(searchFilterQueryData.getKey()).isEqualTo("bundleTemplates");
		Assertions.assertThat(searchFilterQueryData.getValues().iterator().next()).isEqualTo("bundleId");
	}

	@Test
	public void testStartBundle() throws CommerceCartModificationException
	{
		final ArgumentCaptor<CommerceCartParameter> captor = ArgumentCaptor.forClass(CommerceCartParameter.class);
		Mockito.lenient().when(commerceCartService.addToCart(captor.capture())).thenReturn(null);
		Mockito.lenient().when(bundleTemplateService.getBundleTemplateForCode(any(String.class))).thenAnswer(invocationOnMock -> {
			final BundleTemplateModel result = new BundleTemplateModel();
			result.setId((String) invocationOnMock.getArguments()[0]);
			return result;
		});
		Mockito.lenient().when(productService.getProductForCode(any(String.class))).thenAnswer(invocationOnMock -> {
			final ProductModel product = new ProductModel();
			product.setCode((String) invocationOnMock.getArguments()[0]);
			return product;
		});

		defaultBundleCommerceCartFacade.startBundle("bundleTemplate", "code", 1L);

		assertEquals("bundleTemplate", captor.getValue().getBundleTemplate().getId());
		assertEquals("code", captor.getValue().getProduct().getCode());
		assertEquals(1L, captor.getValue().getQuantity());
		assertTrue(captor.getValue().isEnableHooks());
		assertTrue(CollectionUtils.isEmpty(captor.getValue().getEntryGroupNumbers()));
	}

	@Test
	public void testAddToCart() throws CommerceCartModificationException
	{
		final ArgumentCaptor<CommerceCartParameter> captor = ArgumentCaptor.forClass(CommerceCartParameter.class);
		Mockito.lenient().when(commerceCartService.addToCart(captor.capture())).thenReturn(null);
		Mockito.lenient().when(bundleTemplateService.getBundleTemplateForCode(any(String.class))).thenAnswer(
				invocationOnMock -> new BundleTemplateModel());
		Mockito.lenient().when(productService.getProductForCode(any(String.class))).thenAnswer(invocationOnMock -> {
			final ProductModel product = new ProductModel();
			product.setCode((String) invocationOnMock.getArguments()[0]);
			return product;
		});

		defaultBundleCommerceCartFacade.addToCart("code", 1L, 100);

		assertNull(captor.getValue().getBundleTemplate());
		assertEquals("code", captor.getValue().getProduct().getCode());
		assertEquals(1L, captor.getValue().getQuantity());
		assertTrue(captor.getValue().isEnableHooks());
		assertThat(captor.getValue().getEntryGroupNumbers(), contains(Integer.valueOf(100)));
		verify(bundleTemplateService, never()).getBundleTemplateForCode(any(String.class));
	}

	@Test
	public void shouldAssignBundleTemplateForNewBundle() throws CommerceCartModificationException
	{
		final ArgumentCaptor<CommerceCartParameter> captor = ArgumentCaptor.forClass(CommerceCartParameter.class);
		Mockito.lenient().when(commerceCartService.addToCart(captor.capture())).thenReturn(null);
		Mockito.lenient().when(bundleTemplateService.getBundleTemplateForCode(any(String.class))).thenAnswer(invocationOnMock -> {
			final BundleTemplateModel result = new BundleTemplateModel();
			result.setId((String) invocationOnMock.getArguments()[0]);
			return result;
		});

		defaultBundleCommerceCartFacade.startBundle("bundleTemplate", "product", 1L);
		
		assertEquals("bundleTemplate", captor.getValue().getBundleTemplate().getId());
	}
	
	@Test
	public void shouldCheckBundleIntegrity() throws CommerceCartModificationException
	{
		final CartModel cart = new CartModel();
		cart.setCode("A");
		final CartEntryModel entry = new CartEntryModel();
		entry.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(3))));
		entry.setEntryNumber(Integer.valueOf(4));
		cart.setEntries(Collections.singletonList(entry));
		final EntryGroup group = new EntryGroup();
		group.setGroupNumber(Integer.valueOf(3));
		group.setExternalReferenceId("bundleTemplate");
		Mockito.lenient().when(cartService.getSessionCart()).thenReturn(cart);
		Mockito.lenient().when(bundleTemplateService.getBundleEntryGroup(entry)).thenReturn(group);
		Mockito.lenient().when(entryGroupService.getGroup(cart, Integer.valueOf(3))).thenReturn(group);
		Mockito.lenient().when(entryGroupService.getGroup(cart, Integer.valueOf(2)))
				.thenThrow(new IllegalArgumentException("No group with number #2 in the order with code A"));
		Mockito.lenient().when(entryGroupService.getLeaves(any())).thenReturn(Collections.singletonList(group));
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("No group with number #2 in the order with code A");

		defaultBundleCommerceCartFacade.addToCart("product", 1L, 2);
	}

	@Test
	public void deleteShouldCheckEntryGroupNumber() throws CommerceCartModificationException
	{
		final CartModel cart = new CartModel();
		cart.setCode("A");
		cart.setEntries(Collections.emptyList());
		Mockito.lenient().when(cartService.getSessionCart()).thenReturn(cart);
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Entry group #1 was not found in cart A");

		defaultBundleCommerceCartFacade.removeEntriesByGroupNumber(cart,1);
	}

	@Test
	public void shouldDeleteBundleByEntryGroup() throws CommerceCartModificationException
	{
		final CartModel cart = new CartModel();
		final CartEntryModel entry = new CartEntryModel();
		entry.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(3))));
		entry.setEntryNumber(Integer.valueOf(4));
		cart.setEntries(Collections.singletonList(entry));
		cart.setCode("A");
		final EntryGroup group = new EntryGroup();
		group.setGroupNumber(Integer.valueOf(3));
		final EntryGroup root = new EntryGroup();
		root.setGroupNumber(Integer.valueOf(1));
		Mockito.lenient().when(cartService.getSessionCart()).thenReturn(cart);
		Mockito.lenient().when(bundleTemplateService.getBundleEntryGroup(entry)).thenReturn(group);
		Mockito.lenient().when(entryGroupService.getRoot(cart, Integer.valueOf(3))).thenReturn(root);
		final ArgumentCaptor<CommerceCartParameter> captor = ArgumentCaptor.forClass(CommerceCartParameter.class);
		Mockito.lenient().when(commerceCartService.updateQuantityForCartEntry(captor.capture())).thenReturn(null);

		defaultBundleCommerceCartFacade.removeEntriesByGroupNumber(cart,3);

		assertEquals(cart, captor.getValue().getCart());
		assertEquals(4, captor.getValue().getEntryNumber());
		assertTrue(captor.getValue().isEnableHooks());
	}

	@Test
	public void testProcessFacetData() throws UnsupportedEncodingException
	{
		final FacetData<SearchStateData> facetData = new FacetData<>();
		final FacetValueData<SearchStateData> topValue = new FacetValueData<>();
		final SearchStateData topQuery = new SearchStateData();
		final SearchQueryData topQData = new SearchQueryData();
		topQData.setValue("<test>:<div>");
		topQuery.setQuery(topQData);
		topValue.setQuery(topQuery);
		facetData.setTopValues(Collections.singletonList(topValue));
		final FacetValueData<SearchStateData> value = new FacetValueData<>();
		facetData.setValues(Collections.singletonList(value));
		facetData.setValues(Collections.emptyList());

		defaultBundleCommerceCartFacade.processFacetData(Collections.singletonList(facetData));

		assertEquals("&lt;test&gt;:<div>", topValue.getQuery().getQuery().getValue());
	}

	@Test
	public void shouldPathURLs()
	{
		final FacetData<SearchStateData> facetData = new FacetData<>();
		final FacetValueData<SearchStateData> topValue = new FacetValueData<>();
		final SearchStateData topQuery = new SearchStateData();
		final SearchQueryData topQData = new SearchQueryData();
		topQData.setValue("test:value");
		topQuery.setQuery(topQData);
		topValue.setQuery(topQuery);
		facetData.setTopValues(Collections.singletonList(topValue));
		final FacetValueData<SearchStateData> value = new FacetValueData<>();
		facetData.setValues(Collections.singletonList(value));
		facetData.setValues(Collections.emptyList());
		final ProductSearchPageData<SearchStateData, ProductData> searchPageData = new ProductSearchPageData<>();
		searchPageData.setFacets(Collections.singletonList(facetData));
		final BreadcrumbData<SearchStateData> breadcrumbData = new BreadcrumbData<>();
		final SearchStateData removeQueryData = new SearchStateData();
		breadcrumbData.setRemoveQuery(removeQueryData);
		final SearchQueryData removeSearchQuery = new SearchQueryData();
		removeSearchQuery.setValue("check:1");
		removeQueryData.setQuery(removeSearchQuery);
		searchPageData.setBreadcrumbs(Collections.singletonList(breadcrumbData));
		final SearchStateData currentQuery = new SearchStateData();
		final SearchQueryData query = new SearchQueryData();
		query.setValue("");
		currentQuery.setQuery(query);
		searchPageData.setCurrentQuery(currentQuery);

		defaultBundleCommerceCartFacade.patchURLs("prefix/", searchPageData);

		assertEquals("prefix/check%3a1", breadcrumbData.getRemoveQuery().getUrl());
		assertEquals("prefix/test%3avalue", topQuery.getUrl());
	}

	@Test
	public void pathURLsShouldSurviveNullFacetList()
	{
		final ProductSearchPageData<SearchStateData, ProductData> searchPageData = new ProductSearchPageData<>();
		final SearchStateData currentQuery = new SearchStateData();
		final SearchQueryData query = new SearchQueryData();
		query.setValue("");
		currentQuery.setQuery(query);
		searchPageData.setCurrentQuery(currentQuery);
		searchPageData.setBreadcrumbs(Collections.emptyList());
		defaultBundleCommerceCartFacade.patchURLs("prefix", searchPageData);
	}

	@Test
	public void pathURLsShouldSurviveNullBreadcrumbs()
	{
		final ProductSearchPageData<SearchStateData, ProductData> searchPageData = new ProductSearchPageData<>();
		final SearchStateData currentQuery = new SearchStateData();
		final SearchQueryData query = new SearchQueryData();
		query.setValue("");
		currentQuery.setQuery(query);
		searchPageData.setCurrentQuery(currentQuery);
		searchPageData.setFacets(Collections.emptyList());
		defaultBundleCommerceCartFacade.patchURLs("prefix", searchPageData);
	}

	@Test
	public void allowedProductsShouldAcceptNullQuery()
	{
		defaultBundleCommerceCartFacade.getAllowedProducts(Integer.valueOf(1), null, new PageableData());
	}

	@Test
	public void allowedProductsShouldCheckRefId()
	{
		final EntryGroup group = new EntryGroup();
		group.setGroupType(GroupType.CONFIGURABLEBUNDLE);
		Mockito.lenient().when(entryGroupService.getGroupOfType(any(), anyCollection(), eq(GroupType.CONFIGURABLEBUNDLE))).thenReturn(group);
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("componentId");
		defaultBundleCommerceCartFacade.getAllowedProducts(Integer.valueOf(1), "test", new PageableData());
	}

	@Test
	public void allowedProductsShouldCheckGroupType()
	{
		final EntryGroup group = new EntryGroup();
		group.setGroupType(GroupType.STANDALONE);
		Mockito.lenient().when(entryGroupService.getGroup(any(), any())).thenReturn(group);
		Mockito.lenient().when(entryGroupService.getGroupOfType(any(CartModel.class), anyCollection(), eq(GroupType.CONFIGURABLEBUNDLE))).thenThrow(
				IllegalArgumentException.class);
		thrown.expect(IllegalArgumentException.class);
		defaultBundleCommerceCartFacade.getAllowedProducts(Integer.valueOf(1), "test", new PageableData());
	}

	@Test
	public void testStartBundleWithAutoPick() throws CommerceCartModificationException
	{
		defaultBundleCommerceCartFacade.setAutoPickEnabled(true);
		final ArgumentCaptor<CommerceCartParameter> captor = ArgumentCaptor.forClass(CommerceCartParameter.class);

		Mockito.lenient().when(commerceCartService.addToCart(captor.capture())).thenReturn(null);
		final BundleTemplateModel result = new BundleTemplateModel();
		result.setId("bundleTemplate");
		Mockito.lenient().when(bundleTemplateService.getBundleTemplateForCode("bundleTemplate")).thenReturn(result);

		final ProductModel product = new ProductModel();
		String productCode1 = "code1";
		product.setCode(productCode1);
		Mockito.lenient().when(productService.getProductForCode(productCode1)).thenReturn(product);

		final EntryGroup root = new EntryGroup();
		final EntryGroup child = new EntryGroup();

		child.setExternalReferenceId("child");
		root.setExternalReferenceId("root");
		root.setChildren(List.of(child));
		child.setChildren(List.of());
		child.setGroupNumber(1);

		final ProductModel product2 = new ProductModel();
		product2.setCode("code2");

		final BundleTemplateModel autoPick = new BundleTemplateModel();
		autoPick.setProducts(List.of(product2));
		autoPick.setId("autoPickBundleTemplate");

		Mockito.lenient().when(entryGroupService.getRoot(any(), any())).thenReturn(root);
		Mockito.lenient().when(bundleTemplateService.getBundleTemplateForCode("child")).thenReturn(autoPick);
		Mockito.lenient().when(bundleTemplateService.getBundleEntryGroup(any(), any())).thenReturn(root);
		Mockito.lenient().when(bundleTemplateService.isAutoPickComponent(autoPick)).thenReturn(true);

		defaultBundleCommerceCartFacade.startBundle("bundleTemplate", productCode1, 1L);

		List<CommerceCartParameter> captorAllValues = captor.getAllValues();

		assertEquals(2, captorAllValues.size());
	}

	@Test
	public void testStartBundleWithAutoPickForStarter() throws CommerceCartModificationException
	{
		defaultBundleCommerceCartFacade.setAutoPickEnabled(true);
		final ArgumentCaptor<CommerceCartParameter> captor = ArgumentCaptor.forClass(CommerceCartParameter.class);

		Mockito.lenient().when(commerceCartService.addToCart(captor.capture())).thenReturn(null);
		final BundleTemplateModel result = new BundleTemplateModel();
		result.setId("bundleTemplate");

		final ProductModel product = new ProductModel();
		String productCode1 = "code1";
		product.setCode(productCode1);

		Mockito.lenient().when(bundleTemplateService.getBundleTemplateForCode("bundleTemplate")).thenReturn(result);
		Mockito.lenient().when(productService.getProductForCode(productCode1)).thenReturn(product);

		final EntryGroup root = new EntryGroup();
		final EntryGroup child = new EntryGroup();

		child.setExternalReferenceId("child");
		root.setExternalReferenceId("root");
		root.setChildren(List.of(child));
		child.setChildren(List.of());
		child.setGroupNumber(1);

		final ProductModel product2 = new ProductModel();
		product2.setCode("code2");

		final BundleTemplateModel autoPick = new BundleTemplateModel();
		autoPick.setProducts(List.of(product, product2));
		autoPick.setId("autoPickBundleTemplate");

		Mockito.lenient().when(entryGroupService.getRoot(any(), any())).thenReturn(root);
		Mockito.lenient().when(bundleTemplateService.getBundleTemplateForCode("child")).thenReturn(autoPick);
		Mockito.lenient().when(bundleTemplateService.getBundleEntryGroup(any(), any())).thenReturn(root);
		Mockito.lenient().when(bundleTemplateService.isAutoPickComponent(autoPick)).thenReturn(true);
		Mockito.lenient().when(bundleTemplateService.isAutoPickComponent(result)).thenReturn(true);

		defaultBundleCommerceCartFacade.startBundle("bundleTemplate", productCode1, 1L);

		List<CommerceCartParameter> captorAllValues = captor.getAllValues();

		assertEquals(3, captorAllValues.size());
	}

}
