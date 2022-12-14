/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.configurablebundleservices.bundle.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.daos.BundleRuleDao;
import de.hybris.platform.configurablebundleservices.daos.ChangeProductPriceBundleRuleDao;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.configurablebundleservices.model.DisableProductBundleRuleModel;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;


/**
 * JUnit test suite for {@link DefaultBundleCommerceRuleService}
 */
@UnitTest
public class DefaultBundleCommerceRuleServiceTest
{
	@InjectMocks
	private DefaultBundleCommerceRuleService bundleCommerceRuleService;
	@Mock
	private ChangeProductPriceBundleRuleDao changeProductPriceBundleRuleDao;
	@Mock
	private BundleRuleDao disableProductBundleRuleDao;
	@Mock
	private BundleTemplateService bundleTemplateService;
	@Mock
	private SearchRestrictionService searchRestrictionService;
	@Mock
	private ModelService modelService;
	@Mock
	private SessionService sessionService;
	@Mock
	private EntryGroupService entryGroupService;
	@Mock
	private L10NService l10NService;
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private AbstractOrderEntryModel entry;
	private AbstractOrderModel order;
	private ProductModel product;
	private CurrencyModel currency;
	private EntryGroup entryGroup;
	MockitoSession mockito;

	@Before
	public void setUp()
	{

		bundleCommerceRuleService = new DefaultBundleCommerceRuleService();

		mockito = Mockito.mockitoSession()
				.initMocks(this)
				.startMocking();

		entry = new AbstractOrderEntryModel();
		product = new ProductModel();
		entry.setProduct(product);
		order = new OrderModel();
		currency = new CurrencyModel();
		order.setCurrency(currency);
		entry.setOrder(order);
		entry.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(2))));
		order.setEntries(Collections.singletonList(entry));
		entryGroup = new EntryGroup();
		entryGroup.setGroupNumber(Integer.valueOf(2));
		entryGroup.setGroupType(GroupType.CONFIGURABLEBUNDLE);
		entryGroup.setExternalReferenceId("BUNDLE_COMP");

		Mockito.lenient().when(bundleTemplateService.getBundleEntryGroup(entry)).thenReturn(entryGroup);
	}

	@After
	public void tearDown() {
		//It is necessary to finish the session so that Mockito
		// can detect incorrect stubbing and validate Mockito usage
		//'finishMocking()' is intended to be used in your test framework's 'tear down' method.
		mockito.finishMocking();
	}
	@Test
	public void getChangePriceForEntryShouldValidateBundle()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("entry");

		Mockito.lenient().when(bundleTemplateService.getBundleEntryGroup(entry)).thenReturn(null);

		bundleCommerceRuleService.getChangePriceBundleRuleForOrderEntry(entry);
	}

	@Test
	public void testGetChangePriceNoPriceRules()
	{
		final EntryGroup rootGroup = new EntryGroup();
		final EntryGroup entryGroup3 = new EntryGroup();
		final EntryGroup entryGroup4 = new EntryGroup();
		entryGroup3.setGroupNumber(Integer.valueOf(3));
		entryGroup4.setGroupNumber(Integer.valueOf(4));
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();

		Mockito.lenient().when(entryGroupService.getRoot(eq(order), eq(2))).thenReturn(rootGroup);
		Mockito.lenient().when(entryGroupService.getLeaves(eq(rootGroup))).thenReturn(Arrays.asList(entryGroup, entryGroup3, entryGroup4));
		Mockito.lenient().when(bundleTemplateService.getBundleTemplateForCode(eq("BUNDLE_COMP"))).thenReturn(bundleTemplate);
		Mockito.lenient().when(
				changeProductPriceBundleRuleDao.findBundleRulesByTargetProductAndTemplateAndCurrency(eq(product), eq(bundleTemplate),
				eq(currency))).thenReturn(Collections.emptyList());

		final ChangeProductPriceBundleRuleModel changePriceRule = bundleCommerceRuleService
				.getChangePriceBundleRuleForOrderEntry(entry);

		assertThat(changePriceRule).isNull();
	}

	@Test
	public void testGetChangePriceWithPriceRules()
	{
		final EntryGroup rootGroup = new EntryGroup();
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();
		final ChangeProductPriceBundleRuleModel changePriceModel1 = new ChangeProductPriceBundleRuleModel();
		final ChangeProductPriceBundleRuleModel changePriceModel2 = new ChangeProductPriceBundleRuleModel();
		final OrderEntryModel entry2 = new OrderEntryModel();
		final ProductModel product2 = new ProductModel();
		entry2.setProduct(product2);
		entry2.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(2))));
		changePriceModel1.setConditionalProducts(Collections.emptyList());
		changePriceModel2.setConditionalProducts(Collections.emptyList());
		changePriceModel1.setPrice(new BigDecimal(50));
		changePriceModel2.setPrice(new BigDecimal(40));

		Mockito.lenient().when(entryGroupService.getRoot(eq(order), eq(2))).thenReturn(rootGroup);
		Mockito.lenient().when(entryGroupService.getLeaves(eq(rootGroup))).thenReturn(Collections.singletonList(entryGroup));
		Mockito.lenient().when(bundleTemplateService.getBundleTemplateForCode(eq("BUNDLE_COMP"))).thenReturn(bundleTemplate);
		Mockito.lenient().when(
				changeProductPriceBundleRuleDao.findBundleRulesByTargetProductAndTemplateAndCurrency(eq(product), eq(bundleTemplate),
				eq(currency))).thenReturn(Arrays.asList(changePriceModel1, changePriceModel2));
		Mockito.lenient().when(searchRestrictionService.isSearchRestrictionsEnabled()).thenReturn(false);

		final ChangeProductPriceBundleRuleModel changePriceRule = bundleCommerceRuleService
				.getChangePriceBundleRuleForOrderEntry(entry);

		assertThat(changePriceRule).isEqualTo(changePriceModel2);
	}

	@Test
	public void testGetDisableProductBundleRules()
	{
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();
		final EntryGroup rootGroup = new EntryGroup();
		final EntryGroup entryGroup1 = new EntryGroup();
		entryGroup1.setGroupNumber(Integer.valueOf(3));

		final OrderEntryModel entry1 = new OrderEntryModel();
		final ProductModel product1 = new ProductModel();
		entry1.setProduct(product1);
		entry1.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(3))));

		order.setEntries(Arrays.asList(entry, entry1));

		// Rule with conditional product that is in the cart
		final DisableProductBundleRuleModel disableRule1 = new DisableProductBundleRuleModel();
		disableRule1.setTargetProducts(Collections.singletonList(product));
		disableRule1.setConditionalProducts(Collections.singletonList(product1));

		// Rule with conditional product that is not in the cart
		final ProductModel product2 = new ProductModel();
		final DisableProductBundleRuleModel disableRule2 = new DisableProductBundleRuleModel();
		disableRule2.setTargetProducts(Collections.singletonList(product));
		disableRule2.setConditionalProducts(Collections.singletonList(product2));

		bundleTemplate.setDisableProductBundleRules(Arrays.asList(disableRule1, disableRule2));

		final List<EntryGroup> leaves = Arrays.asList(entryGroup, entryGroup1);

		Mockito.lenient().when(bundleTemplateService.getBundleTemplateForCode(eq("BUNDLE_COMP"))).thenReturn(bundleTemplate);
		Mockito.lenient().when(entryGroupService.getRoot(eq(order), eq(2))).thenReturn(rootGroup);
		Mockito.lenient().when(entryGroupService.getLeaves(eq(rootGroup))).thenReturn(leaves);

		final List<DisableProductBundleRuleModel> disableRules = bundleCommerceRuleService.getDisableProductBundleRules(product,
				entryGroup, order);

		assertThat(disableRules).hasSize(1);
		assertThat(disableRules.get(0)).isEqualTo(disableRule1);
	}

	@Test
	public void testGetDisableProductBundleRulesNoCondProductsInCart()
	{
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();
		final EntryGroup rootGroup = new EntryGroup();
		final EntryGroup entryGroup1 = new EntryGroup();
		entryGroup1.setGroupNumber(Integer.valueOf(3));

		final ProductModel product1 = new ProductModel();
		final DisableProductBundleRuleModel disableRule1 = new DisableProductBundleRuleModel();
		disableRule1.setTargetProducts(Collections.singletonList(product));
		disableRule1.setConditionalProducts(Collections.singletonList(product1));


		bundleTemplate.setDisableProductBundleRules(Collections.singletonList(disableRule1));

		final List<EntryGroup> leaves = Arrays.asList(entryGroup, entryGroup1);

		Mockito.lenient().when(bundleTemplateService.getBundleTemplateForCode(eq("BUNDLE_COMP"))).thenReturn(bundleTemplate);
		Mockito.lenient().when(entryGroupService.getRoot(eq(order), eq(2))).thenReturn(rootGroup);
		Mockito.lenient().when(entryGroupService.getLeaves(eq(rootGroup))).thenReturn(leaves);

		final List<DisableProductBundleRuleModel> disableRules = bundleCommerceRuleService.getDisableProductBundleRules(product,
				entryGroup, order);

		assertThat(disableRules).hasSize(0);
	}

	@Test
	public void testGetDisableProductBundleRulesNoRules()
	{
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();
		final EntryGroup rootGroup = new EntryGroup();
		final EntryGroup entryGroup1 = new EntryGroup();
		entryGroup1.setGroupNumber(Integer.valueOf(3));

		final OrderEntryModel entry1 = new OrderEntryModel();
		final ProductModel product1 = new ProductModel();
		entry1.setProduct(product1);
		entry1.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(3))));

		order.setEntries(Arrays.asList(entry, entry1));

		bundleTemplate.setDisableProductBundleRules(null);

		final List<EntryGroup> leaves = Arrays.asList(entryGroup, entryGroup1);

		Mockito.lenient().when(bundleTemplateService.getBundleTemplateForCode(eq("BUNDLE_COMP"))).thenReturn(bundleTemplate);
		Mockito.lenient().when(entryGroupService.getRoot(eq(order), eq(2))).thenReturn(rootGroup);
		Mockito.lenient().when(entryGroupService.getLeaves(eq(rootGroup))).thenReturn(leaves);

		final List<DisableProductBundleRuleModel> disableRules = bundleCommerceRuleService.getDisableProductBundleRules(product,
				entryGroup, order);

		assertThat(disableRules).hasSize(0);
	}
}
