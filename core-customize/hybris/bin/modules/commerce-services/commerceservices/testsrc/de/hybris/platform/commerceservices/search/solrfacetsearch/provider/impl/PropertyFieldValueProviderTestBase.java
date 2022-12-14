/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.internal.i18n.LocalizationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.DefaultRangeNameProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@Ignore
@RunWith(MockitoJUnitRunner.class)
public abstract class PropertyFieldValueProviderTestBase
{
	protected abstract String getPropertyName();

	protected abstract void configure();

	protected static final String TEST_EN_LANG_CODE = "en";
	protected static final String TEST_DE_LANG_CODE = "de";

	//Required by Test class
	private AbstractPropertyFieldValueProvider propertyFieldValueProvider;
	@Mock
	protected FieldNameProvider fieldNameProvider;
	@Mock(lenient = true)
	protected CommonI18NService commonI18NService;
	@Mock(lenient = true)
	protected I18NService i18nService;
	@Mock
	protected ModelService modelService;
	@Mock
	protected LocalizationService localeService;

	//Objects used by test class (some as params)
	@Mock(lenient = true)
	protected LanguageModel enLanguageModel;
	@Mock(lenient = true)
	protected LanguageModel deLanguageModel;
	@Mock(lenient = true)
	protected IndexConfig indexConfig;

	protected final Locale enLocale = Locale.ENGLISH;
	protected final Locale deLocale = Locale.GERMANY;
	protected final String nullString = null;


	protected void configureBase()
	{
		given(enLanguageModel.getIsocode()).willReturn(TEST_EN_LANG_CODE);
		given(deLanguageModel.getIsocode()).willReturn(TEST_DE_LANG_CODE);
		final Collection<LanguageModel> languages = new ArrayList<LanguageModel>();
		languages.add(enLanguageModel);
		languages.add(deLanguageModel);
		given(indexConfig.getLanguages()).willReturn(languages);

		given(i18nService.getCurrentLocale()).willReturn(enLocale);
		given(commonI18NService.getLocaleForLanguage(enLanguageModel)).willReturn(enLocale);
		given(commonI18NService.getLocaleForLanguage(deLanguageModel)).willReturn(deLocale);

		getPropertyFieldValueProvider().setRangeNameProvider(new DefaultRangeNameProvider());
		getPropertyFieldValueProvider().setLocaleService(localeService);
		getPropertyFieldValueProvider().setI18nService(i18nService);
		getPropertyFieldValueProvider().setModelService(modelService);
	}

	protected AbstractPropertyFieldValueProvider getPropertyFieldValueProvider()
	{
		return propertyFieldValueProvider;
	}

	protected void setPropertyFieldValueProvider(final AbstractPropertyFieldValueProvider propertyFieldValueProvider)
	{
		this.propertyFieldValueProvider = propertyFieldValueProvider;
	}
}
