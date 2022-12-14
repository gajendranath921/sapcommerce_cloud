/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.common.validator.impl;

import de.hybris.platform.cmsfacades.common.validator.LocalizedValidator;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;


/**
 * Default validator to use to validating localized attributes. This implementation uses the {@link LanguageFacade} to
 * extract available languages.
 */
public class DefaultLocalizedValidator implements LocalizedValidator
{
	private LanguageFacade languageFacade;

	@Override
	public <T> void validateAllLanguages(final BiConsumer<String, T> consumer, final Function<String, T> function,
			final Errors errors)
	{
		getAllLanguages() //
				.map(languageData -> languageData.getIsocode()) //
				.forEach(isoCode -> consumer.accept(isoCode, function.apply(isoCode)));
	}

	@Override
	public <T> void validateRequiredLanguages(final BiConsumer<String, T> consumer, final Function<String, T> function,
			final Errors errors)
	{
		getAllLanguages() //
				.filter(language -> language.isRequired()) //
				.map(languageData -> languageData.getIsocode()) //
				.forEach(isoCode -> consumer.accept(isoCode, function.apply(isoCode)));
	}

	protected Stream<LanguageData> getAllLanguages()
	{
		return getLanguageFacade().getLanguages().stream();
	}

	protected LanguageFacade getLanguageFacade()
	{
		return languageFacade;
	}

	@Required
	public void setLanguageFacade(final LanguageFacade languageFacade)
	{
		this.languageFacade = languageFacade;
	}

}
