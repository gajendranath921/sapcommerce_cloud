/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.externaltax.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.externaltax.CalculateExternalTaxesStrategy;
import de.hybris.platform.commerceservices.externaltax.DecideExternalTaxesStrategy;
import de.hybris.platform.commerceservices.externaltax.RecalculateExternalTaxesStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.externaltax.ApplyExternalTaxesStrategy;
import de.hybris.platform.externaltax.ExternalTaxDocument;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.TaxValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultExternalTaxesServiceTest
{
	private DefaultExternalTaxesService defaultExternalTaxesService;

	@Mock
	private CalculateExternalTaxesStrategy calculateExternalTaxesStrategy;

	@Mock
	private ApplyExternalTaxesStrategy applyExternalTaxesStrategy;

	@Mock(lenient = true)
	private DecideExternalTaxesStrategy decideExternalTaxesStrategy;

	@Mock(lenient = true)
	private RecalculateExternalTaxesStrategy recalculateExternalTaxesStrategy;

	@Mock
	private ModelService modelService;

	@Mock
	private SessionService sessionService;

	@Before
	public void setUp()
	{
		defaultExternalTaxesService = new DefaultExternalTaxesService();
		defaultExternalTaxesService.setApplyExternalTaxesStrategy(applyExternalTaxesStrategy);
		defaultExternalTaxesService.setCalculateExternalTaxesStrategy(calculateExternalTaxesStrategy);
		defaultExternalTaxesService.setDecideExternalTaxesStrategy(decideExternalTaxesStrategy);
		defaultExternalTaxesService.setModelService(modelService);
		defaultExternalTaxesService.setRecalculateExternalTaxesStrategy(recalculateExternalTaxesStrategy);
		defaultExternalTaxesService.setSessionService(sessionService);
	}

	@Test
	public void shouldCalculateExternalTaxes()
	{
		final AbstractOrderModel abstractOrder = Mockito.mock(AbstractOrderModel.class, withSettings().lenient());
		given(abstractOrder.getNet()).willReturn(Boolean.TRUE);
		final ExternalTaxDocument externalTaxDocument = Mockito.mock(ExternalTaxDocument.class);
		final List<TaxValue> taxValues = new ArrayList<TaxValue>();
		final TaxValue taxValue = Mockito.mock(TaxValue.class, withSettings().lenient());
		taxValues.add(taxValue);

		given(Double.valueOf(taxValue.getAppliedValue())).willReturn(Double.valueOf(2.5));
		given(externalTaxDocument.getShippingCostTaxes()).willReturn(taxValues);
		given(externalTaxDocument.getAllTaxes()).willReturn(
				Collections.<Integer, List<TaxValue>> singletonMap(Integer.valueOf(0), taxValues));

		given(Boolean.valueOf(decideExternalTaxesStrategy.shouldCalculateExternalTaxes(abstractOrder))).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(recalculateExternalTaxesStrategy.recalculate(abstractOrder))).willReturn(Boolean.TRUE);
		given(calculateExternalTaxesStrategy.calculateExternalTaxes(abstractOrder)).willReturn(externalTaxDocument);

		defaultExternalTaxesService.calculateExternalTaxes(abstractOrder);
		verify(applyExternalTaxesStrategy).applyExternalTaxes(abstractOrder, externalTaxDocument);
	}

	@Test
	public void shouldNotCalculatExternalTaxes()
	{
		final AbstractOrderModel abstractOrder = Mockito.mock(AbstractOrderModel.class, withSettings().lenient());
		given(abstractOrder.getNet()).willReturn(Boolean.TRUE);
		final ExternalTaxDocument externalTaxDocument = Mockito.mock(ExternalTaxDocument.class);
		given(Boolean.valueOf(decideExternalTaxesStrategy.shouldCalculateExternalTaxes(abstractOrder))).willReturn(Boolean.FALSE);
		given(Boolean.valueOf(recalculateExternalTaxesStrategy.recalculate(abstractOrder))).willReturn(Boolean.TRUE);

		defaultExternalTaxesService.calculateExternalTaxes(abstractOrder);
		verify(applyExternalTaxesStrategy, never()).applyExternalTaxes(abstractOrder, externalTaxDocument);
	}

}
