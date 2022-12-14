/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.excel.imp.wizard.renderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import de.hybris.platform.validation.enums.Severity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zhtml.Li;
import org.zkoss.zk.ui.HtmlBasedComponent;

import com.hybris.backoffice.excel.validators.data.ValidationMessage;


@RunWith(MockitoJUnitRunner.class)
public class DefaultExcelValidationDetailsComponentFactoryTest
{

	@Spy
	private DefaultExcelValidationDetailsComponentFactory factory = new DefaultExcelValidationDetailsComponentFactory();

	@Test
	public void shouldLiBeCreatedWithoutHeaderLabel()
	{
		// given
		final Object columnHeader = null;
		final ValidationMessage validationMessage = mock(ValidationMessage.class);
		doReturn("some label").when(factory).getMessageValue(validationMessage);

		// when
		final Li li = factory.createValidationDetails(columnHeader, validationMessage);

		// then
		assertThat(li.getChildren().size()).isEqualTo(1);
	}

	@Test
	public void shouldStylingDependOnSeverity()
	{
		// given
		final Object columnHeader = "someHeader";
		final Severity severity = Severity.INFO;
		final ValidationMessage validationMessage = new ValidationMessage("key", severity);
		doReturn("some label").when(factory).getMessageValue(validationMessage);

		// when
		final Li li = factory.createValidationDetails(columnHeader, validationMessage);

		// then
		assertThat(((HtmlBasedComponent) li.getFirstChild()).getSclass()).containsIgnoringCase(severity.getCode());
	}

}
