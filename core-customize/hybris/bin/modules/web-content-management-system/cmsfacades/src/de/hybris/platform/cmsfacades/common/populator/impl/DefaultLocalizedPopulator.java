/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.common.populator.impl;

import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.users.services.CMSUserService;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.Locale;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for the <code>LocalizedPopulator</code> interface.
 */
public class DefaultLocalizedPopulator implements LocalizedPopulator
{
	private LanguageFacade languageFacade;
	private CommonI18NService commonI18NService;
	private CMSUserService cmsUserService;

	@Override
	public <T> void populate(final BiConsumer<Locale, T> consumer, final Function<Locale, T> function)
	{
		Set<String> readableLanguages = getCmsUserService().getReadableLanguagesForCurrentUser();
		getAllLanguages()
				.filter(language -> readableLanguages.contains(language.getIsocode()))
				.map(language -> getCommonI18NService().getLocaleForIsoCode(language.getIsocode()))
				.forEach(locale -> consumer.accept(locale, function.apply(locale)));
	}

	/**
	 * Gets all the languages supported.
	 *
	 * @return - a Map containing all the required locales where the keys maps the {@code Locale.getLanguage()} to a
	 *         boolean value, indicating if the language is the default language.
	 */
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

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected CMSUserService getCmsUserService()
	{
		return cmsUserService;
	}

	@Required
	public void setCmsUserService(final CMSUserService cmsUserService)
	{
		this.cmsUserService = cmsUserService;
	}
}
