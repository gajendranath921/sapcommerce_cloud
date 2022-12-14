/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.product.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.withSettings;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.commercefacades.converter.impl.DefaultConfigurablePopulator;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.converters.populator.FeaturePopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductCategoriesPopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductClassificationPopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductDescriptionPopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductFeatureListPopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductGalleryImagesPopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPricePopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPromotionsPopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductReviewsPopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductStockPopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductSummaryPopulator;
import de.hybris.platform.commercefacades.product.converters.populator.StockPopulator;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ClassificationData;
import de.hybris.platform.commercefacades.product.data.FeatureData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.commercefacades.product.data.PromotionData;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.order.ProductConfigurationHandler;
import de.hybris.platform.commerceservices.order.ProductConfigurationHandlerFactory;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.commerceservices.product.CommerceProductReferenceService;
import de.hybris.platform.commerceservices.product.CommerceProductService;
import de.hybris.platform.commerceservices.product.data.ReferenceData;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.CustomerReviewService;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.product.ConfiguratorSettingsService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.model.AbstractConfiguratorSettingModel;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.PriceValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Test suite for {@link DefaultProductFacade}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultProductFacadeTest
{
	private static final String TEST_PRODUCT_CODE = "1234";
	private static final Double TEST_RATING = Double.valueOf(2.5);
	private static final String TEST_HEADLINE = "Headline";
	private static final String TEST_COMMENT = "Comment";
	private static final String TEST_MANUFACTURER = "ManuFact";
	private static final Double TEST_AVG_RATING = Double.valueOf(5.5D);
	private static final Long TEST_STOCK_NUMBER = Long.valueOf(123);
	private static final String TEST_FEATURE_CODE = "testFeature";
	private static final String TEST_CLASSIFICATION_CODE = "testClassification";
	private static final String TEST_LANG_CODE = "en";
	private static final Integer TEST_NEGATIVE_NUMBER = Integer.valueOf(-1);
	private static final Integer TEST_REVIEW_QUANTITY = Integer.valueOf(2);
	private static final ConfiguratorType DEFAULT_CONFIGURATOR_TYPE = ConfiguratorType.valueOf("DEFAULT");


	@InjectMocks
	private final DefaultProductFacade<ProductModel> defaultProductFacade = new DefaultProductFacade<ProductModel>();
	@InjectMocks
	private final ProductSummaryPopulator productSummaryPopulator = new ProductSummaryPopulator();
	@InjectMocks
	private final ProductDescriptionPopulator productDescriptionPopulator = new ProductDescriptionPopulator();
	@InjectMocks
	private final ProductGalleryImagesPopulator productGalleryImagesPopulator = new ProductGalleryImagesPopulator();
	@InjectMocks
	private final ProductStockPopulator productStockPopulator = new ProductStockPopulator();
	@InjectMocks
	private final ProductCategoriesPopulator productCategoriesPopulator = new ProductCategoriesPopulator();
	@InjectMocks
	private final ProductPromotionsPopulator productPromotionsPopulator = new ProductPromotionsPopulator();
	@InjectMocks
	private final ProductReviewsPopulator productReviewsPopulator = new ProductReviewsPopulator();
	@InjectMocks
	private final ProductClassificationPopulator productClassificationPopulator = new ProductClassificationPopulator();
	@InjectMocks
	private final DefaultPriceDataFactory priceDataFactory = new DefaultPriceDataFactory();
	@InjectMocks
	private final ProductPricePopulator productPricePopulator = new ProductPricePopulator();

	@Mock
	private ProductService productService;
	@Mock
	private PromotionsService promotionsService;
	@Mock
	private ClassificationService classificationService;
	@Mock
	private CustomerReviewService customerReviewService;
	@Mock
	private UserService userService;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock(lenient = true)
	private ModelService modelService;
	@Mock
	private CommercePriceService commercePriceService;
	@Mock
	private CommerceProductService commerceProductService;
	@Mock(lenient = true)
	private CommerceStockService commerceStockService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private CategoryService categoryService;
	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;
	@Mock
	private TimeService timeService;
	@Mock
	private ProductReferenceData productReferenceData;
	@Mock
	private Converter<ReferenceData<ProductReferenceTypeEnum, ProductModel>, ProductReferenceData> referenceDataProductReferenceConverter;
	@Mock
	private CommerceProductReferenceService commerceProductReferenceService;
	@Mock
	private ConfiguratorSettingsService configuratorSettingsService;
	@Mock
	private Converter<AbstractOrderEntryProductInfoModel, List<ConfigurationInfoData>> productConfigurationConverter;
	@Mock
	private ProductConfigurationHandlerFactory productConfigurationHandlerFactory;
	@Mock
	private ProductConfigurationHandler productConfigurationHandler;

	private final ProductReferenceTypeEnum enumSimilar = ProductReferenceTypeEnum.SIMILAR;

	private InOrder referenceDataProductReferenceConverterOrder;

	private ProductModel productModel;
	private CustomerReviewModel customerReviewModel;

	@Before
	public void setUp()
	{
		productPricePopulator.setPriceDataFactory(priceDataFactory);

		given(timeService.getCurrentTime()).willReturn(new Date());

		defaultProductFacade.setCommerceProductReferenceService(commerceProductReferenceService);

		final DefaultConfigurablePopulator<ProductModel, ProductData, ProductOption> defaultConfigurablePopulator = new DefaultConfigurablePopulator<ProductModel, ProductData, ProductOption>();
		final LinkedHashMap<ProductOption, Populator<ProductModel, ProductData>> populators = new LinkedHashMap<ProductOption, Populator<ProductModel, ProductData>>();
		defaultConfigurablePopulator.setPopulators(populators);
		defaultProductFacade.setProductConfiguredPopulator(defaultConfigurablePopulator);
		defaultProductFacade.setReferenceProductConfiguredPopulator(defaultConfigurablePopulator);

		populators.put(ProductOption.PRICE, productPricePopulator);
		populators.put(ProductOption.GALLERY, productGalleryImagesPopulator);
		populators.put(ProductOption.SUMMARY, productSummaryPopulator);
		populators.put(ProductOption.DESCRIPTION, productDescriptionPopulator);
		populators.put(ProductOption.CATEGORIES, productCategoriesPopulator);
		populators.put(ProductOption.PROMOTIONS, productPromotionsPopulator);
		populators.put(ProductOption.STOCK, productStockPopulator);
		populators.put(ProductOption.REVIEW, productReviewsPopulator);
		populators.put(ProductOption.CLASSIFICATION, productClassificationPopulator);

		productModel = mock(ProductModel.class, withSettings().lenient());

		final AbstractPopulatingConverter<ProductModel, ProductData> productConverter = mock(AbstractPopulatingConverter.class, withSettings().lenient());

		final ProductFeatureListPopulator productFeatureListPopulator = mock(ProductFeatureListPopulator.class);
		customerReviewModel = mock(CustomerReviewModel.class, withSettings().lenient());
		final LanguageModel enModel = mock(LanguageModel.class, withSettings().lenient());

		defaultProductFacade.setProductConverter(productConverter);
		productClassificationPopulator.setProductFeatureListPopulator(productFeatureListPopulator);

		given(enModel.getIsocode()).willReturn(TEST_LANG_CODE);
		given(commonI18NService.getCurrentLanguage()).willReturn(enModel);
		given(productConverter.convert(productModel)).willReturn(new ProductData());
		given(productConverter.convertAll(Collections.singletonList(productModel)))
				.willReturn(Collections.singletonList(new ProductData()));
		given(productService.getProductForCode(TEST_PRODUCT_CODE)).willReturn(productModel);
		given(productModel.getCode()).willReturn(TEST_PRODUCT_CODE);
		given(productModel.getAverageRating()).willReturn(TEST_AVG_RATING);
		given(productModel.getManufacturerName()).willReturn(TEST_MANUFACTURER);
		given(customerReviewModel.getLanguage()).willReturn(enModel);
		given(customerReviewModel.getApprovalStatus()).willReturn(CustomerReviewApprovalType.APPROVED);
		given(modelService.<Object> getAttributeValue(productModel, ProductModel.MANUFACTURERNAME)).willReturn(TEST_MANUFACTURER);
		given(Boolean.valueOf(commerceStockService.isStockSystemEnabled(any(BaseStoreModel.class)))).willReturn(Boolean.TRUE);
		given(commerceStockService.getStockLevelStatusForProductAndBaseStore(Mockito.eq(productModel), any(BaseStoreModel.class)))
				.willReturn(StockLevelStatus.INSTOCK);
		given(commerceStockService.getStockLevelForProductAndBaseStore(Mockito.eq(productModel), any(BaseStoreModel.class)))
				.willReturn(TEST_STOCK_NUMBER);
		given(productConfigurationHandlerFactory.handlerOf(any(ConfiguratorType.class)))
				.willReturn(productConfigurationHandler);

		defaultProductFacade.setReferenceDataProductReferenceConverter(referenceDataProductReferenceConverter);
		referenceDataProductReferenceConverterOrder = Mockito.inOrder(referenceDataProductReferenceConverter);
	}


	@Test
	public void testGetProductForCodePrice()
	{
		final PriceInformation priceInformation = mock(PriceInformation.class);
		final PriceValue priceValue = new PriceValue("USD", 100d, true);
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setSymbol("$");
		currencyModel.setIsocode("USD");
		currencyModel.setDigits(Integer.valueOf(2));
		final LanguageModel languageModel = new LanguageModel();
		languageModel.setIsocode("en");

		given(productModel.getVariants()).willReturn(Collections.EMPTY_LIST);
		given(commercePriceService.getWebPriceForProduct(productModel)).willReturn(priceInformation);
		given(priceInformation.getPriceValue()).willReturn(priceValue);
		given(commonI18NService.getCurrency(anyString())).willReturn(currencyModel);
		given(commonI18NService.getCurrentLanguage()).willReturn(languageModel);
		given(commerceCommonI18NService.getLocaleForLanguage(languageModel)).willReturn(Locale.UK);

		final ProductData data = defaultProductFacade.getProductForCodeAndOptions(TEST_PRODUCT_CODE,
				Collections.singleton(ProductOption.PRICE));

		Assert.assertEquals(data.getPrice().getValue(), BigDecimal.valueOf(100.0));
		Assert.assertEquals(data.getPrice().getCurrencyIso(), "USD");
	}


	@Test
	public void testGetProductForCodeCategory()
	{
		final CategoryModel categoryModel = mock(CategoryModel.class);
		final AbstractPopulatingConverter<CategoryModel, CategoryData> categoryConverter = mock(AbstractPopulatingConverter.class);

		final CategoryData categoryData = mock(CategoryData.class);

		productCategoriesPopulator.setCategoryConverter(categoryConverter);

		given(commerceProductService.getSuperCategoriesExceptClassificationClassesForProduct(productModel))
				.willReturn(Collections.singletonList(categoryModel));
		given(categoryConverter.convertAll(Collections.singletonList(categoryModel)))
				.willReturn(Collections.singletonList(categoryData));

		final ProductData data = defaultProductFacade.getProductForCodeAndOptions(TEST_PRODUCT_CODE,
				Collections.singleton(ProductOption.CATEGORIES));

		Assert.assertEquals(data.getCategories().iterator().next(), categoryData);
	}

	@Test
	public void testGetReviews()
	{
		final AbstractPopulatingConverter<CustomerReviewModel, ReviewData> converter = mock(AbstractPopulatingConverter.class);
		final ReviewData reviewData = mock(ReviewData.class);

		defaultProductFacade.setCustomerReviewConverter(converter);
		given(converter.convertAll(Collections.singletonList(customerReviewModel)))
				.willReturn(Collections.singletonList(reviewData));
		final List<CustomerReviewModel> modelList = new LinkedList<CustomerReviewModel>();
		modelList.add(customerReviewModel);
		given(customerReviewService.getReviewsForProductAndLanguage(Matchers.eq(productModel), any(LanguageModel.class)))
				.willReturn(modelList);
		final List<ReviewData> result = defaultProductFacade.getReviews(TEST_PRODUCT_CODE);
		Assert.assertEquals(result.get(0), reviewData);

		final List<ReviewData> resultTwo = defaultProductFacade.getReviews(TEST_PRODUCT_CODE, null);
		Assert.assertEquals(resultTwo.get(0), reviewData);
	}

	@Test
	public void testGetFirstNumberOfReviews()
	{
		final List<ReviewData> reviewDataList = new LinkedList<ReviewData>();
		setupReviewData(reviewDataList);

		final List<ReviewData> result = defaultProductFacade.getReviews(TEST_PRODUCT_CODE, TEST_REVIEW_QUANTITY);
		Assert.assertTrue(result.size() == TEST_REVIEW_QUANTITY.intValue());
		Assert.assertEquals(result.get(0), reviewDataList.get(0));
		Assert.assertEquals(result.get(1), reviewDataList.get(1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNegativeNumberOfReviews()
	{
		final List<ReviewData> reviewDataList = new LinkedList<ReviewData>();
		setupReviewData(reviewDataList);

		defaultProductFacade.getReviews(TEST_PRODUCT_CODE, TEST_NEGATIVE_NUMBER);
	}

	private void setupReviewData(final List<ReviewData> reviewDataList)
	{
		final AbstractPopulatingConverter<CustomerReviewModel, ReviewData> converter = mock(AbstractPopulatingConverter.class, withSettings().lenient());

		final List<CustomerReviewModel> modelList = new LinkedList<CustomerReviewModel>();
		modelList.add(mock(CustomerReviewModel.class));
		modelList.add(mock(CustomerReviewModel.class));
		modelList.add(mock(CustomerReviewModel.class));

		reviewDataList.add(mock(ReviewData.class));
		reviewDataList.add(mock(ReviewData.class));
		reviewDataList.add(mock(ReviewData.class));

		defaultProductFacade.setCustomerReviewConverter(converter);
		given(converter.convertAll(modelList)).willReturn(reviewDataList);
		given(converter.convertAll(modelList.subList(0, TEST_REVIEW_QUANTITY.intValue())))
				.willReturn(reviewDataList.subList(0, TEST_REVIEW_QUANTITY.intValue()));
		given(customerReviewService.getReviewsForProductAndLanguage(Matchers.eq(productModel), any(LanguageModel.class)))
				.willReturn(modelList);
		given(productModel.getNumberOfReviews()).willReturn(TEST_REVIEW_QUANTITY);
	}

	@Test
	public void testPostReview()
	{
		final UserModel userModel = mock(UserModel.class);
		final CustomerReviewModel customerReviewModel = mock(CustomerReviewModel.class);
		final ReviewData returnedReviewData = mock(ReviewData.class);
		final AbstractPopulatingConverter<CustomerReviewModel, ReviewData> customerReviewConverter = mock(
				AbstractPopulatingConverter.class);

		defaultProductFacade.setCustomerReviewConverter(customerReviewConverter);
		given(customerReviewConverter.convert(customerReviewModel)).willReturn(returnedReviewData);
		given(userService.getCurrentUser()).willReturn(userModel);

		final ReviewData reviewData = mock(ReviewData.class);
		given(reviewData.getRating()).willReturn(TEST_RATING);
		given(reviewData.getHeadline()).willReturn(TEST_HEADLINE);
		given(reviewData.getComment()).willReturn(TEST_COMMENT);
		given(customerReviewService.createCustomerReview(TEST_RATING, TEST_HEADLINE, TEST_COMMENT, userModel, productModel))
				.willReturn(customerReviewModel);

		defaultProductFacade.postReview(TEST_PRODUCT_CODE, reviewData);
		verify(customerReviewService).createCustomerReview(TEST_RATING, TEST_HEADLINE, TEST_COMMENT, userModel, productModel);
	}


	@Test
	public void testGetProductForCodePromotions()
	{
		final AbstractPromotionModel abstractPromotionModel = mock(AbstractPromotionModel.class);
		final PromotionData promotionData = mock(PromotionData.class);
		final AbstractPopulatingConverter<AbstractPromotionModel, PromotionData> promotionsConverter = mock(
				AbstractPopulatingConverter.class);
		final BaseSiteModel baseSite = mock(BaseSiteModel.class);
		final PromotionGroupModel promotionGroup = mock(PromotionGroupModel.class);

		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSite);
		given(baseSite.getDefaultPromotionGroup()).willReturn(promotionGroup);
		Mockito
				.<List<? extends AbstractPromotionModel>> when(promotionsService.getAbstractProductPromotions(any(Collection.class),
						any(ProductModel.class), anyBoolean(), any(Date.class)))
				.thenReturn(Collections.singletonList(abstractPromotionModel));
		given(promotionsConverter.convertAll(Collections.singletonList(abstractPromotionModel)))
				.willReturn(Collections.singletonList(promotionData));

		productPromotionsPopulator.setPromotionsConverter(promotionsConverter);

		final ProductData data = defaultProductFacade.getProductForCodeAndOptions(TEST_PRODUCT_CODE,
				Collections.singleton(ProductOption.PROMOTIONS));

		Assert.assertEquals(data.getPotentialPromotions().iterator().next(), promotionData);
	}


	@Test
	public void testGetProductStock()
	{
		final BaseStoreModel baseStore = mock(BaseStoreModel.class);
		final BaseSiteModel baseSite = mock(BaseSiteModel.class);
		final Populator<ProductModel, StockData> stockPopulator = new StockPopulator<ProductModel, StockData>();
		final AbstractPopulatingConverter<ProductModel, StockData> stockConverter = new ConverterFactory<ProductModel, StockData, StockPopulator>()
				.create(StockData.class, (StockPopulator<ProductModel, StockData>) stockPopulator);

		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStore);
		stockConverter.setPopulators(Collections.singletonList(stockPopulator));
		given(commerceStockService.getStockLevelStatusForProductAndBaseStore(Mockito.any(ProductModel.class),
				Mockito.eq(baseStore))).willReturn(StockLevelStatus.INSTOCK);
		given(commerceStockService.getStockLevelForProductAndBaseStore(Mockito.any(ProductModel.class), Mockito.eq(baseStore)))
				.willReturn(TEST_STOCK_NUMBER);
		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSite);

		productStockPopulator.setStockConverter(stockConverter);
		((StockPopulator<ProductModel, StockData>) stockPopulator).setBaseStoreService(baseStoreService);
		((StockPopulator<ProductModel, StockData>) stockPopulator).setCommerceStockService(commerceStockService);
		((StockPopulator<ProductModel, StockData>) stockPopulator).setBaseSiteService(baseSiteService);
		((StockPopulator<ProductModel, StockData>) stockPopulator).setCategoryService(categoryService);

		final ProductData data = defaultProductFacade.getProductForCodeAndOptions(TEST_PRODUCT_CODE,
				Collections.singleton(ProductOption.STOCK));

		Assert.assertEquals(data.getStock().getStockLevel(), TEST_STOCK_NUMBER);
		Assert.assertEquals(data.getStock().getStockLevelStatus(), StockLevelStatus.INSTOCK);
	}


	@Test
	public void testGetProductForCodeReview()
	{
		final ReviewData reviewData = mock(ReviewData.class);
		final AbstractPopulatingConverter<CustomerReviewModel, ReviewData> customerReviewConverter = mock(
				AbstractPopulatingConverter.class);

		final List<CustomerReviewModel> modelList = new LinkedList<CustomerReviewModel>();
		modelList.add(customerReviewModel);

		defaultProductFacade.setCustomerReviewConverter(customerReviewConverter);
		productReviewsPopulator.setCustomerReviewConverter(customerReviewConverter);
		given(customerReviewService.getReviewsForProductAndLanguage(any(ProductModel.class), any(LanguageModel.class)))
				.willReturn(modelList);
		given(customerReviewConverter.convertAll(Collections.singletonList(customerReviewModel)))
				.willReturn(Collections.singletonList(reviewData));

		final ProductData data = defaultProductFacade.getProductForCodeAndOptions(TEST_PRODUCT_CODE,
				Collections.singleton(ProductOption.REVIEW));

		Assert.assertEquals(data.getReviews().iterator().next(), reviewData);
	}


	@Test
	public void testGetProductForCodeClassification()
	{
		final FeatureList featureList = mock(FeatureList.class);
		final Feature feature = mock(Feature.class);
		final ClassAttributeAssignmentModel attributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);
		final FeatureValue featureValue = mock(FeatureValue.class);
		final AbstractPopulatingConverter<ClassificationClassModel, ClassificationData> classificationConverter = mock(
				AbstractPopulatingConverter.class);

		final ClassificationData classificationData = new ClassificationData();
		final ClassificationClassModel classificationClass = mock(ClassificationClassModel.class);

		classificationData.setCode(TEST_CLASSIFICATION_CODE);

		final ProductFeatureListPopulator productFeatureListPopulator = new ProductFeatureListPopulator();
		productFeatureListPopulator.setClassificationConverter(classificationConverter);

		final AbstractPopulatingConverter<Feature, FeatureData> featureConverter = new ConverterFactory<Feature, FeatureData, FeaturePopulator>()
				.create(FeatureData.class, new FeaturePopulator());
		productFeatureListPopulator.setFeatureConverter(featureConverter);

		productClassificationPopulator.setProductFeatureListPopulator(productFeatureListPopulator);

		given(classificationClass.getCode()).willReturn(TEST_CLASSIFICATION_CODE);
		given(attributeAssignmentModel.getClassificationClass()).willReturn(classificationClass);
		given(feature.getCode()).willReturn(TEST_FEATURE_CODE);
		given(feature.getClassAttributeAssignment()).willReturn(attributeAssignmentModel);
		given(feature.getValues()).willReturn(Collections.singletonList(featureValue));
		given(featureList.getFeatures()).willReturn(Collections.singletonList(feature));
		given(classificationConverter.convert(any(ClassificationClassModel.class))).willReturn(classificationData);
		given(classificationService.getFeatures(productModel)).willReturn(featureList);

		final ProductData data = defaultProductFacade.getProductForCodeAndOptions(TEST_PRODUCT_CODE,
				Collections.singleton(ProductOption.CLASSIFICATION));

		final ClassificationData classification = data.getClassifications().iterator().next();
		Assert.assertEquals(classification.getCode(), TEST_CLASSIFICATION_CODE);
		Assert.assertEquals(classification.getFeatures().iterator().next().getCode(), TEST_FEATURE_CODE);
	}



	@Test
	public void testProductReferencesConversion()
	{
		final ReferenceData refOne = createReferenceData(1);
		final ReferenceData refTwo = createReferenceData(2);
		final ReferenceData refThree = createReferenceData(3);
		final ReferenceData refFour = createReferenceData(4);
		final List<ReferenceData> referencesList = Arrays.asList(refOne, refTwo, refThree, refFour);
		BDDMockito.given(commerceProductReferenceService.getProductReferencesForCode(eq("someCode"),
				any(ProductReferenceTypeEnum.class), any(Integer.class))).willReturn(referencesList);

		BDDMockito.given(referenceDataProductReferenceConverter.convert(Mockito.any(ReferenceData.class)))
				.willReturn(productReferenceData);

		final ProductData tmpProductData = new ProductData();
		BDDMockito.given(productReferenceData.getTarget()).willReturn(tmpProductData);

		final List<ProductOption> options = new ArrayList<>();
		options.add(ProductOption.BASIC);

		final List<ProductReferenceData> references = defaultProductFacade.getProductReferencesForCode("someCode", enumSimilar,
				options, Integer.valueOf(100));

		Assert.assertNotNull(references);

		referenceDataProductReferenceConverterOrder.verify(referenceDataProductReferenceConverter, Mockito.times(1))
				.convert(refOne);
		referenceDataProductReferenceConverterOrder.verify(referenceDataProductReferenceConverter, Mockito.times(1))
				.convert(refTwo);
		referenceDataProductReferenceConverterOrder.verify(referenceDataProductReferenceConverter, Mockito.times(1))
				.convert(refThree);
		referenceDataProductReferenceConverterOrder.verify(referenceDataProductReferenceConverter, Mockito.times(1))
				.convert(refFour);
	}

	@Test
	public void testGetConfiguratorSettingsForCode()
	{
		AbstractConfiguratorSettingModel settingModel = new AbstractConfiguratorSettingModel();
		settingModel.setConfiguratorType(DEFAULT_CONFIGURATOR_TYPE);
		AbstractOrderEntryProductInfoModel orderEntryProductInfoModel = new AbstractOrderEntryProductInfoModel();
		ConfigurationInfoData configurationInfoData = new ConfigurationInfoData();

		given(configuratorSettingsService.getConfiguratorSettingsForProduct(any(ProductModel.class)))
				.willReturn(Collections.singletonList(settingModel));
		given(productConfigurationHandler.createProductInfo(any(AbstractConfiguratorSettingModel.class)))
				.willReturn(Collections.singletonList(orderEntryProductInfoModel));
		given(productConfigurationConverter.convert(any(AbstractOrderEntryProductInfoModel.class)))
				.willReturn(Collections.singletonList(configurationInfoData));

		List<ConfigurationInfoData> settingsList = defaultProductFacade.getConfiguratorSettingsForCode(TEST_PRODUCT_CODE);
		Assert.assertEquals(settingsList.size(), 1);
		Assert.assertEquals(settingsList.get(0), configurationInfoData);

		verify(productService).getProductForCode(TEST_PRODUCT_CODE);
		verify(configuratorSettingsService).getConfiguratorSettingsForProduct(productModel);
		verify(productConfigurationHandlerFactory).handlerOf(DEFAULT_CONFIGURATOR_TYPE);
		verify(productConfigurationHandler).createProductInfo(settingModel);
		verify(productConfigurationConverter).convert(orderEntryProductInfoModel);

		verifyNoMoreInteractions(productService, configuratorSettingsService, productConfigurationHandlerFactory,
				productConfigurationHandler, productConfigurationConverter);
	}

	private ReferenceData createReferenceData(final int qty)
	{
		final ReferenceData<ProductReferenceTypeEnum, ProductModel> ref = new ReferenceData<ProductReferenceTypeEnum, ProductModel>();
		ref.setReferenceType(enumSimilar);
		ref.setQuantity(Integer.valueOf(qty));
		ref.setTarget(mock(ProductModel.class));
		return ref;
	}
}
