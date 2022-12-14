/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.excel.imp.wizard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.platform.media.services.MimeService;
import de.hybris.platform.servicelayer.cronjob.CronJobService;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.base.Charsets;
import com.hybris.backoffice.excel.ExcelConstants;
import com.hybris.backoffice.excel.importing.ExcelImportService;
import com.hybris.backoffice.excel.jobs.ExcelCronJobService;
import com.hybris.backoffice.excel.jobs.FileContent;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;
import com.hybris.backoffice.widgets.notificationarea.DefaultNotificationService;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.cockpitng.core.events.CockpitEventQueue;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.editor.defaultfileupload.FileUploadResult;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;


@RunWith(MockitoJUnitRunner.class)
public class ExcelImportHandlerTest
{

	@Mock
	private ExcelCronJobService excelCronJobService;
	@Mock
	private CronJobService cronJobService;
	@Mock
	private CockpitEventQueue cockpitEventQueue;
	@Mock
	private ExcelImportService excelImportService;
	@Mock
	private MimeService mimeService;
	@Spy
	private DefaultNotificationService notificationService;
	@Spy
	private final ExcelImportHandler excelImportHandler = new ExcelImportHandler();

	@Before
	public void setUp() throws Exception
	{
		excelImportHandler.setCockpitEventQueue(cockpitEventQueue);
		excelImportHandler.setCronJobService(cronJobService);
		excelImportHandler.setExcelCronJobService(excelCronJobService);
		excelImportHandler.setMimeService(mimeService);
		excelImportHandler.setNotificationService(notificationService);
		excelImportHandler.setExcelImportService(excelImportService);
	}

	@Test
	public void shouldFailureNotificationBeDisplayedWhenWizardFormIsNull()
	{
		// given
		final FlowActionHandlerAdapter adapter = mockAdapter(mock(WidgetModel.class));

		// when
		excelImportHandler.perform(null, adapter, null);

		// then
		verify(notificationService).notifyUser(ExcelConstants.NOTIFICATION_SOURCE_EXCEL_IMPORT,
				ExcelConstants.NOTIFICATION_EVENT_TYPE_EXCEL_FORM_IN_MODEL, NotificationEvent.Level.FAILURE);
	}

	@Test
	public void shouldFailureNotificationBeDisplayedWhenFileContentIsNull()
	{
		// given
		final WidgetModel widgetModel = mock(WidgetModel.class);
		given(widgetModel.getValue(ExcelConstants.EXCEL_FORM_PROPERTY, ExcelImportWizardForm.class))
				.willReturn(mock(ExcelImportWizardForm.class));
		final FlowActionHandlerAdapter adapter = mockAdapter(widgetModel);

		// when
		excelImportHandler.perform(null, adapter, null);

		// then
		verify(notificationService).notifyUser(ExcelConstants.NOTIFICATION_SOURCE_EXCEL_IMPORT,
				ExcelConstants.NOTIFICATION_EVENT_TYPE_MISSING_EXCEL_FILE, NotificationEvent.Level.FAILURE);
	}

	@Test
	public void shouldWizardNotAllowForImportingWhenValidationFoundErrors()
	{
		// given
		final WidgetModel widgetModel = mock(WidgetModel.class);
		final ExcelImportWizardForm excelImportWizardForm = mock(ExcelImportWizardForm.class);

		given(widgetModel.getValue(ExcelConstants.EXCEL_FORM_PROPERTY, ExcelImportWizardForm.class))
				.willReturn(excelImportWizardForm);

		final FlowActionHandlerAdapter adapter = mockAdapter(widgetModel);

		final FileUploadResult excelFileUploadResult = mock(FileUploadResult.class);
		given(excelImportWizardForm.getExcelFile()).willReturn(excelFileUploadResult);
		final FileUploadResult zipFileUploadResult = mock(FileUploadResult.class);
		given(excelImportWizardForm.getZipFile()).willReturn(zipFileUploadResult);

		final FileContent excelFileContent = mock(FileContent.class);
		final FileContent zipFileContent = mock(FileContent.class);
		doReturn(excelFileContent).when(excelImportHandler).toFileContent(excelFileUploadResult);
		doReturn(zipFileContent).when(excelImportHandler).toFileContent(zipFileUploadResult);

		doReturn(Lists.newArrayList(mock(ExcelValidationResult.class))).when(excelImportHandler).validateExcel(excelFileContent,
				zipFileContent);

		// when
		excelImportHandler.perform(null, adapter, null);

		// then
		verify(adapter).next();
		verify(adapter, never()).done();
	}

	@Test
	public void shouldReturnValidationErrorWhenFileIsIncorrect()
	{
		// given
		final FileContent excelFileContent = mock(FileContent.class);
		final FileContent zipFileContent = mock(FileContent.class);
		doThrow(NotOfficeXmlFileException.class).when(excelImportHandler).createWorkbook(any());

		// when
		final List<ExcelValidationResult> validationResults = excelImportHandler.validateExcel(excelFileContent, zipFileContent);

		// then
		assertThat(validationResults).hasSize(1);
		assertThat(validationResults.get(0).hasErrors()).isTrue();
		assertThat(validationResults.get(0).getHeader().getMessageKey()).isEqualTo("excel.import.validation.incorrect.file.header");
		assertThat(validationResults.get(0).getValidationErrors()).hasSize(1);
		assertThat(validationResults.get(0).getValidationErrors().get(0).getMessageKey())
				.isEqualTo("excel.import.validation.incorrect.file.description");
	}

	@Test
	public void shouldUTF8BePresentEvenIfFallbacksAreEmpty()
	{
		// given
		willReturn(StringUtils.EMPTY).given(excelImportHandler).getZipFilenameEncodings();

		// when
		final Collection<Charset> charsets = excelImportHandler.getSupportedCharsets();

		// then
		assertThat(charsets).hasSize(1);
		assertThat(charsets.iterator().next()).isEqualTo(Charsets.UTF_8);
	}

	@Test
	public void shouldOrderOfEncodingsBeRespected()
	{
		// given
		final String firstFallback = "Cp852";
		final String secondFallback = "Cp437";
		final String thirdFallback = "UTF16";
		willReturn(firstFallback + "," + secondFallback + ";" + thirdFallback).given(excelImportHandler).getZipFilenameEncodings();

		// when
		final List<Charset> charsets = new ArrayList<>(excelImportHandler.getSupportedCharsets());

		// then
		assertThat(charsets).hasSize(4);
		assertThat(charsets).containsExactly(Charsets.UTF_8, Charset.forName(firstFallback), Charset.forName(secondFallback),
				Charsets.UTF_16);
	}

	@Test
	public void shouldFallbackEncodingBeUsedInCaseOfProblemWithUnzippingTheFileDueToSpecialChars()
	{
		// given
		final Charset base = Charsets.UTF_8;
		final Charset fallback = Charset.forName("Cp852");
		willReturn(List.of(base, fallback)).given(excelImportHandler).getSupportedCharsets();

		willThrow(IllegalArgumentException.class).given(excelImportHandler).getFallbackZipEntry(any(), eq(base));
		final Set<String> output = Set.of("someFile");
		willReturn(output).given(excelImportHandler).getFallbackZipEntry(any(), eq(fallback));

		// when
		final Set<String> zipEntries = excelImportHandler.getZipEntries(mock(FileContent.class));

		// then
		then(excelImportHandler).should(times(2)).getFallbackZipEntry(any(), any());
		assertThat(zipEntries).isEqualTo(output);
	}

	protected FlowActionHandlerAdapter mockAdapter(final WidgetModel widgetModel)
	{
		final FlowActionHandlerAdapter adapter = mock(FlowActionHandlerAdapter.class);
		final WidgetInstanceManager wim = mock(WidgetInstanceManager.class);

		given(wim.getModel()).willReturn(widgetModel);
		given(adapter.getWidgetInstanceManager()).willReturn(wim);
		return adapter;
	}

}
