/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.order.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CardTypePopulatorTest
{
	private static final String CARD_TYPE_NAME = "El Maestro";

	@Mock
	private TypeService typeService;

	private final CardTypePopulator cardTypePopulator = new CardTypePopulator();

	private AbstractPopulatingConverter<CreditCardType, CardTypeData> cardTypeConverter;

	@Before
	public void setUp()
	{
		cardTypePopulator.setTypeService(typeService);

		cardTypeConverter = new ConverterFactory<CreditCardType, CardTypeData, CardTypePopulator>().create(CardTypeData.class,
				cardTypePopulator);
	}


	@Test
	public void testConvert()
	{
		final CreditCardType source = CreditCardType.MAESTRO;
		final EnumerationValueModel cardTypeName = mock(EnumerationValueModel.class);

		given(cardTypeName.getName()).willReturn(CARD_TYPE_NAME);
		given(typeService.getEnumerationValue(source)).willReturn(cardTypeName);

		final CardTypeData result = cardTypeConverter.convert(source);

		Assert.assertEquals(CreditCardType.MAESTRO.getCode(), result.getCode());
		Assert.assertEquals(CARD_TYPE_NAME, result.getName());
	}
}
