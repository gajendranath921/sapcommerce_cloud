/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.common.function.impl;

import de.hybris.platform.cmsfacades.common.function.Validator;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

/**
 * Default composite validator is a composite of {@link Validator} <br>
 * Iterates and executes validate for each {@link Validator}
 * @param <T> the type of the validated object
 */
public class DefaultCompositeValidator<T> implements Validator<T>
{
	
	private List<Validator<T>> validators;

	@Override
	public void validate(final T validatee)
	{
		getValidators().forEach(validator -> validator.validate(validatee));
	}

	protected List<Validator<T>> getValidators()
	{
		return validators;
	}

	@Required
	public void setValidators(final List<Validator<T>> validators)
	{
		this.validators = validators;
	}
}
