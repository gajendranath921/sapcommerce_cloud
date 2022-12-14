/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.marketplaceservices.dataimport.batch.translator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.impex.jalo.header.AbstractDescriptor.DescriptorParams;
import de.hybris.platform.impex.jalo.header.StandardColumnDescriptor;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.marketplaceservices.model.VendorPageModel;
import de.hybris.platform.marketplaceservices.strategies.VendorCMSStrategy;
import de.hybris.platform.marketplaceservices.vendor.VendorCMSPageService;
import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.ordersplitting.model.VendorModel;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.ImmutableMap;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class MarketplaceSlotsTranslatorTest
{
	private static final String VENDOR_CODE = "Canon";
	private static final String CONTENT_CATAGLOG = "marketplaceContentCatalog";
	private static final String VERSION = "STAGED";

	private static final String PRODUCT_CODE_1 = "Product1";
	private static final String PRODUCT_CODE_2 = "Product2";
	private static final String SLOT_POSITION_FOR_PRODUCT_CAROUSEL = "Section3";
	private static final String IMPORT_PRODUCTS = String.join(",", PRODUCT_CODE_1, PRODUCT_CODE_2);

	@Spy
	private final MarketplaceSlotsTranslator translator = new MarketplaceSlotsTranslator();

	private TestDescriptorParams params;
	private Item item;
	private CatalogVersionModel catalogVersion;

	@Mock
	private VendorService vendorService;

	@Mock
	private VendorCMSPageService vendorCMSPageService;

	@Mock
	private VendorCMSStrategy vendorCMSStrategy;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private VendorModel vendor;

	@Mock
	private VendorPageModel vendorPageModel;

	@Mock
	private ContentSlotModel productCarouselSlot;
	@Mock
	private AbstractCMSComponentModel productCarouselComponent;

	@Mock
	private StandardColumnDescriptor descriptor;

	@Mock
	private ApplicationContext applicationContext;

	@Before
	public void setUp() throws CMSItemNotFoundException
	{
		Mockito.when(translator.getApplicationContext()).thenReturn(applicationContext);
		translator.init(descriptor);
		translator.setVendorService(vendorService);
		translator.setVendorCMSPageService(vendorCMSPageService);
		translator.setVendorCMSStrategy(vendorCMSStrategy);
		translator.setCatalogVersionService(catalogVersionService);

		params = new TestDescriptorParams(
				ImmutableMap.of("vendor", VENDOR_CODE, "contentCatalog", CONTENT_CATAGLOG, "version", VERSION));


		Mockito.when(descriptor.getDescriptorData()).thenReturn(params);

		Mockito.lenient().when(applicationContext.getBean("vendorService")).thenReturn(vendorService);
		Mockito.lenient().when(applicationContext.getBean("vendorCMSPageService")).thenReturn(vendorCMSPageService);
		Mockito.lenient().when(applicationContext.getBean("vendorCMSStrategy")).thenReturn(vendorCMSStrategy);

		Mockito.when(vendorService.getVendorByCode(VENDOR_CODE)).thenReturn(Optional.of(vendor));
		Mockito.when(vendorCMSPageService.getPageForVendor(vendor, catalogVersion)).thenReturn(Optional.of(vendorPageModel));
		Mockito.when(catalogVersionService.getCatalogVersion(CONTENT_CATAGLOG, VERSION)).thenReturn(catalogVersion);
		Mockito.when(vendorCMSStrategy.getContentSlotByPositionAndCatalogVersion(vendor, SLOT_POSITION_FOR_PRODUCT_CAROUSEL,
				catalogVersion)).thenReturn(productCarouselSlot);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoPosition()
	{
		translator.importValue("", item);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoVendor()
	{
		Mockito.when(vendorService.getVendorByCode(VENDOR_CODE)).thenReturn(Optional.empty());
		translator.importValue(IMPORT_PRODUCTS, item);
	}

	@Test
	public void testImportValueWithValidValue()
	{
		
		final
		Set<ContentSlotModel> acturalSlots = (Set<ContentSlotModel>) translator.importValue(SLOT_POSITION_FOR_PRODUCT_CAROUSEL,
				item);
		assertEquals(1, acturalSlots.size());
		assertTrue(acturalSlots.contains(productCarouselSlot));
	}

	private static final class TestDescriptorParams extends DescriptorParams
	{
		public TestDescriptorParams(final Map<String, String> m)
		{
			this.addAllModifier(m);
		}
	}
}
