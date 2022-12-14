/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.validation.constraints;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class SnCollectionPatternValidator implements ConstraintValidator<SnCollectionPattern, Object>
{

	private Pattern pattern;

	@Override
	public void initialize(final SnCollectionPattern constraintAnnotation)
	{
		pattern = generatePattern(constraintAnnotation.regexp(), constraintAnnotation.flags());
	}

	protected Pattern generatePattern(final String regexp, final javax.validation.constraints.Pattern.Flag[] flags)
	{
		int intFlag = 0;

		for (final javax.validation.constraints.Pattern.Flag flag : flags)
		{
			intFlag |= flag.getValue();
		}

		return Pattern.compile(regexp, intFlag);
	}

	@Override
	public boolean isValid(final Object o, final ConstraintValidatorContext constraintValidatorContext)
	{
		if (!(o instanceof Collection))
		{
			return true;
		}

		final Collection<Object> col = ((Collection) o);
		for (final Object obj : col)
		{
			final Matcher m = pattern.matcher(obj.toString());
			if (!m.matches())
			{
				return false;
			}
		}
		return true;
	}
}
