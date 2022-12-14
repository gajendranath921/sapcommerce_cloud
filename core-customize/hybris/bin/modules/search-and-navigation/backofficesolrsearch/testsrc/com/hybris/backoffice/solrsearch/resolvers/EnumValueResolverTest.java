/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.solrsearch.resolvers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.product.ProductModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class EnumValueResolverTest
{
	protected static final String UNAPPROVED = "unapproved";
	@InjectMocks
	EnumValueResolver enumValueResolver;
	@Mock
	private ProductModel product;
	@Mock
	private HybrisEnumValue hybrisEnum;

	@Test
	public void shouldWorkOnlyForHybrisEnumValues() throws Exception
	{
		//when
		final boolean resultForHybrisEnum = enumValueResolver.isHybrisEnum(hybrisEnum);
		final boolean resultForProductModel = enumValueResolver.isHybrisEnum(product);

		//then
		assertThat(resultForHybrisEnum).isTrue();
		assertThat(resultForProductModel).isFalse();
	}

	@Test
	public void shouldReturnCodeValueForEnum()
	{
		//given
		when(hybrisEnum.getCode()).thenReturn(UNAPPROVED);

		//when
		final String result = enumValueResolver.getEnumValue(hybrisEnum);

		//then
		assertThat(result).isEqualTo(UNAPPROVED);
	}
}
