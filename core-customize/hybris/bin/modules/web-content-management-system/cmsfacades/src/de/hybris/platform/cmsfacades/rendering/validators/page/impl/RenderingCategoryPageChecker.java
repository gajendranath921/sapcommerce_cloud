/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.rendering.validators.page.impl;

import de.hybris.platform.cmsfacades.common.predicate.CategoryCodeExistsPredicate;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.dto.RenderingPageValidationDto;
import de.hybris.platform.cmsfacades.rendering.validators.page.RenderingPageChecker;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;

import java.util.function.Predicate;


/**
 * Implementation of {@link RenderingPageChecker} to validate Category page attributes of {@link RenderingPageValidationDto}.
 */
public class RenderingCategoryPageChecker implements RenderingPageChecker
{
	private Predicate<String> pagePredicate;
	private CategoryCodeExistsPredicate categoryCodeExistsPredicate;

	@Override
	public Predicate<String> getConstrainedBy()
	{
		return pagePredicate;
	}

	@Override
	public void verify(RenderingPageValidationDto validationDto, Errors errors)
	{
		if (getCategoryCodeExistsPredicate().negate().test(validationDto.getCode()))
		{
			errors.rejectValue("code", CmsfacadesConstants.FIELD_NOT_ALLOWED);
		}
	}

	protected Predicate<String> getPagePredicate()
	{
		return pagePredicate;
	}

	@Required
	public void setPagePredicate(Predicate<String> pagePredicate)
	{
		this.pagePredicate = pagePredicate;
	}

	protected CategoryCodeExistsPredicate getCategoryCodeExistsPredicate()
	{
		return categoryCodeExistsPredicate;
	}

	@Required
	public void setCategoryCodeExistsPredicate(
			CategoryCodeExistsPredicate categoryCodeExistsPredicate)
	{
		this.categoryCodeExistsPredicate = categoryCodeExistsPredicate;
	}
}
