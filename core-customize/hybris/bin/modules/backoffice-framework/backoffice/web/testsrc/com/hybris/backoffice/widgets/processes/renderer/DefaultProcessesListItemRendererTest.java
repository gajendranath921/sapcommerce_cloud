/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.widgets.processes.renderer;

import static com.hybris.backoffice.widgets.processes.renderer.DefaultProcessesListItemRenderer.LABEL_PROCESS_ABORTED;
import static com.hybris.backoffice.widgets.processes.renderer.DefaultProcessesListItemRenderer.LABEL_PROCESS_FINISHED;
import static com.hybris.backoffice.widgets.processes.renderer.DefaultProcessesListItemRenderer.LABEL_PROCESS_NO_ACCESS;
import static com.hybris.backoffice.widgets.processes.renderer.DefaultProcessesListItemRenderer.LABEL_PROCESS_PAUSED;
import static com.hybris.backoffice.widgets.processes.renderer.DefaultProcessesListItemRenderer.LABEL_PROCESS_PROCESSING;
import static com.hybris.backoffice.widgets.processes.renderer.DefaultProcessesListItemRenderer.LABEL_PROCESS_STATUS_INFO;
import static com.hybris.backoffice.widgets.processes.renderer.DefaultProcessesListItemRenderer.LABEL_PROCESS_STATUS_INFO_RUNNING;
import static com.hybris.backoffice.widgets.processes.renderer.DefaultProcessesListItemRenderer.LABEL_PROCESS_UNKNOWN;
import static com.hybris.backoffice.widgets.processes.renderer.DefaultProcessesListItemRenderer.SCLASS_PROCESSES_NO_ACCESS;
import static com.hybris.backoffice.widgets.processes.renderer.DefaultProcessesListItemRenderer.SCLASS_STATUS_INFO_LABEL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobHistoryModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.servicelayer.security.permissions.PermissionCheckResult;
import de.hybris.platform.servicelayer.security.permissions.PermissionCheckingService;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;

import com.hybris.backoffice.BackofficeTestUtil;
import com.hybris.cockpitng.components.Stopwatch;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.core.user.CockpitUserService;
import com.hybris.cockpitng.core.util.impl.TypedSettingsMap;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class DefaultProcessesListItemRendererTest
{

	@Mock
	private PermissionCheckResult checkResult;
	@Mock
	private CockpitUserService cockpitUserService;
	@Mock
	private LabelService labelService;
	@Mock
	private PermissionFacade permissionFacade;
	@Mock
	private ProcessItemRenderingStrategyRegistry processItemRenderingStrategyRegistry;
	@Mock
	private CronJobService cronJobService;

	@InjectMocks
	@Spy
	private DefaultProcessesListItemRenderer renderer;

	private final DefaultProcessItemRenderingStrategy strategy = new DefaultProcessItemRenderingStrategy();

	private final Locale testLocale = Locale.ENGLISH;
	private final ZoneId testZoneId = ZoneId.of("UTC");
	private final TimeZone testTimeZone = TimeZone.getTimeZone(testZoneId);
	private final Date startDate = Date.from(ZonedDateTime.of(2019, 1, 2, 12, 0, 0, 0, testZoneId).toInstant());
	private final Date endDate = Date.from(ZonedDateTime.of(2019, 1, 2, 13, 0, 0, 0, testZoneId).toInstant());
	private final String expectedTimeLabel = "Wed, Jan 2, 1:00 PM";
	private final int jobPk = 1;
	private final CronJobHistoryModel cronJobHistory = createCronJobHistory(jobPk);
	private final WidgetInstanceManager wim = mock(WidgetInstanceManager.class);
	private final WidgetModel widgetModel = mock(WidgetModel.class);
	private final TypedSettingsMap widgetSettings = new TypedSettingsMap();
	private TimeZone timeZoneBeforeTest;
	private Locale localeBeforeTest;


	@Before
	public void before()
	{
		CockpitTestUtil.mockZkEnvironment();
		saveTimeZoneAndLocaleBeforeTest();
		TimeZones.setThreadLocal(testTimeZone);
		Locales.setThreadLocal(testLocale);

		Mockito.lenient().when(wim.getModel()).thenReturn(widgetModel);
		Mockito.lenient().when(wim.getWidgetSettings()).thenReturn(widgetSettings);

		doAnswer(inv -> getLabel(inv.getArguments()[0])).when(renderer).getLabel(anyString());
		doAnswer(inv -> getLabel(inv.getArguments()[0], inv.getArguments()[1].toString())).when(renderer).getLabel(anyString(),
				any());

		final PermissionCheckingService permissionCheckingService = mock(PermissionCheckingService.class);
		when(checkResult.isGranted()).thenReturn(true);
		when(permissionCheckingService.checkItemPermission(any(), any())).thenReturn(checkResult);
		strategy.setPermissionCheckingService(permissionCheckingService);
		strategy.setLabelService(labelService);
		when(processItemRenderingStrategyRegistry.getStrategy(any())).thenReturn(strategy);
	}

	@After
	public void tearDown()
	{
		restoreTimeZoneAndLocale();
	}

	private void saveTimeZoneAndLocaleBeforeTest()
	{
		timeZoneBeforeTest = TimeZones.getThreadLocal();
		localeBeforeTest = Locales.getThreadLocal();
	}

	private void restoreTimeZoneAndLocale()
	{
		TimeZones.setThreadLocal(timeZoneBeforeTest);
		Locales.setThreadLocal(localeBeforeTest);
	}

	protected String getLabel(final Object key, final Object... args)
	{
		final StringBuilder label = new StringBuilder(key.toString());
		for (final Object arg : args)
		{
			label.append(arg.toString());
		}
		return label.toString();
	}

	@Test
	public void getTimeInfoRunningTest()
	{
		cronJobHistory.setStatus(CronJobStatus.RUNNING);

		final Component timeInfo = renderer.createTimeComponent(cronJobHistory);

		assertThat(timeInfo).isInstanceOf(Stopwatch.class);
		assertThat(((Stopwatch) timeInfo).getStartTime()).isEqualTo(cronJobHistory.getStartTime().getTime());
		assertThat(((Stopwatch) timeInfo).isRunning()).isTrue();
	}

	@Test
	public void getTimeInfoRunningRestartTest()
	{
		cronJobHistory.setStatus(CronJobStatus.RUNNINGRESTART);

		final Component timeInfo = renderer.createTimeComponent(cronJobHistory);

		assertThat(timeInfo).isInstanceOf(Stopwatch.class);
		assertThat(((Stopwatch) timeInfo).getStartTime()).isEqualTo(cronJobHistory.getStartTime().getTime());
		assertThat(((Stopwatch) timeInfo).isRunning()).isTrue();
	}

	@Test
	public void getTimeInfoUnknownTest()
	{
		cronJobHistory.setStatus(CronJobStatus.UNKNOWN);

		final Component endTimeLabel = renderer.createTimeComponent(cronJobHistory);

		assertThat(endTimeLabel).isInstanceOf(Label.class);
		assertThat(((Label) endTimeLabel).getValue()).isEqualTo(getLabel(LABEL_PROCESS_UNKNOWN, expectedTimeLabel));

	}

	@Test
	public void getTimeInfoFinishedTest()
	{
		makeFinished(CronJobResult.SUCCESS);
		final Component timeInfo = renderer.createTimeComponent(cronJobHistory);

		assertThat(timeInfo).isInstanceOf(Label.class);
		assertThat(((Label) timeInfo).getValue()).isEqualTo(getLabel(LABEL_PROCESS_FINISHED, expectedTimeLabel));
	}

	@Test
	public void getTimeInfoAbortedTest()
	{
		cronJobHistory.setStatus(CronJobStatus.ABORTED);
		final Component timeInfo = renderer.createTimeComponent(cronJobHistory);

		assertThat(timeInfo).isInstanceOf(Label.class);
		assertThat(((Label) timeInfo).getValue()).isEqualTo(getLabel(LABEL_PROCESS_ABORTED, expectedTimeLabel));
	}

	@Test
	public void getTimeInfoPausedTest()
	{
		cronJobHistory.setStatus(CronJobStatus.PAUSED);
		final Component timeInfo = renderer.createTimeComponent(cronJobHistory);

		assertThat(timeInfo).isInstanceOf(Label.class);
		assertThat(((Label) timeInfo).getValue()).isEqualTo(getLabel(LABEL_PROCESS_PAUSED, expectedTimeLabel));
	}

	@Test
	public void shouldPrintTimeInLocalTimeZoneAndLocale()
	{
		// given
		TimeZones.setThreadLocal(TimeZone.getTimeZone("GMT+3"));
		Locales.setThreadLocal(Locale.GERMAN);
		overrideDateFormatSymbolsForGermanLocale();
		makeFinished(CronJobResult.SUCCESS);

		// when
		final Component timeInfo = renderer.createTimeComponent(cronJobHistory);

		// then
		assertThat(timeInfo).isInstanceOf(Label.class);
		assertThat(((Label) timeInfo).getValue()).isEqualTo(getLabel(LABEL_PROCESS_FINISHED, "Mi, Jan 2, 4:00 PM"));
	}

	/**
	 * The reason for this method is that the localization strings of SimpleDateFormat (like January / Januar in german) are
	 * dependent on JVM property java.locale.providers (see its documentation for more info). There are multiple locale
	 * providers and they contain different localizations, e.g. AM/PM is localized to german either AM/PM or vorm./nachm. To
	 * avoid having different test results depending on JVM settings, this override is done.
	 */
	private void overrideDateFormatSymbolsForGermanLocale()
	{
		doAnswer(inv -> {
			final DateFormatSymbols newFormatSymbols = new DateFormatSymbols(Locale.GERMAN);
			newFormatSymbols.setShortWeekdays(new String[]
			{ "", "", "", "", "Mi" });
			newFormatSymbols.setShortMonths(new String[]
			{ "Jan" });
			newFormatSymbols.setAmPmStrings(new String[]
			{ "AM", "PM" });

			final SimpleDateFormat format = (SimpleDateFormat) inv.callRealMethod();
			format.setDateFormatSymbols(newFormatSymbols);
			return format;
		}).when(renderer).createSimpleDateFormatForEndTimeLabel();
	}

	@Test
	public void getStatusReplacementRunningTest()
	{
		cronJobHistory.setStatus(CronJobStatus.RUNNING);
		assertThat(renderer.getStatusReplacementLabelKey(cronJobHistory))
				.isEqualTo(DefaultProcessesListItemRenderer.LABEL_PROCESS_PROCESSING);
	}

	@Test
	public void getStatusReplacementRestartTest()
	{
		cronJobHistory.setStatus(CronJobStatus.RUNNINGRESTART);
		assertThat(renderer.getStatusReplacementLabelKey(cronJobHistory))
				.isEqualTo(DefaultProcessesListItemRenderer.LABEL_PROCESS_PROCESSING);
	}

	@Test
	public void getStatusReplacementUnknownTest()
	{
		cronJobHistory.setStatus(CronJobStatus.UNKNOWN);
		assertThat(renderer.getStatusReplacementLabelKey(cronJobHistory))
				.isEqualTo(DefaultProcessesListItemRenderer.LABEL_PROCESS_UNKNOWN);
	}

	@Test
	public void getStatusReplacementFinishedTest()
	{
		makeFinished(CronJobResult.SUCCESS);
		assertThat(renderer.getStatusReplacementLabelKey(cronJobHistory))
				.isEqualTo(DefaultProcessesListItemRenderer.LABEL_PROCESS_FINISHED);
	}

	@Test
	public void getStatusReplacementPausedTest()
	{
		cronJobHistory.setStatus(CronJobStatus.PAUSED);
		assertThat(renderer.getStatusReplacementLabelKey(cronJobHistory)).isEqualTo(LABEL_PROCESS_PAUSED);
	}

	@Test
	public void getStatusReplacementAbortedTest()
	{
		cronJobHistory.setStatus(CronJobStatus.ABORTED);
		assertThat(renderer.getStatusReplacementLabelKey(cronJobHistory))
				.isEqualTo(DefaultProcessesListItemRenderer.LABEL_PROCESS_ABORTED);
	}

	@Test
	public void getStatusInfoRunningTest()
	{
		cronJobHistory.setStatus(CronJobStatus.RUNNING);
		assertThat(renderer.getStatusInfo(cronJobHistory))
				.isEqualTo(getLabel(LABEL_PROCESS_STATUS_INFO_RUNNING, LABEL_PROCESS_PROCESSING));
	}

	@Test
	public void getStatusInfoRestartRunningTest()
	{
		cronJobHistory.setStatus(CronJobStatus.RUNNINGRESTART);
		assertThat(renderer.getStatusInfo(cronJobHistory))
				.isEqualTo(getLabel(LABEL_PROCESS_STATUS_INFO_RUNNING, LABEL_PROCESS_PROCESSING));
	}

	@Test
	public void getStatusInfoFinishedTest()
	{
		makeFinished(CronJobResult.SUCCESS);
		assertThat(renderer.getStatusInfo(cronJobHistory)).isEqualTo(getLabel(LABEL_PROCESS_STATUS_INFO, LABEL_PROCESS_FINISHED));
	}

	@Test
	public void getStatusInfoAbortedTest()
	{
		cronJobHistory.setStatus(CronJobStatus.ABORTED);
		assertThat(renderer.getStatusInfo(cronJobHistory)).isEqualTo(getLabel(LABEL_PROCESS_STATUS_INFO, LABEL_PROCESS_ABORTED));
	}

	@Test
	public void getStatusInfoPausedTest()
	{
		cronJobHistory.setStatus(CronJobStatus.PAUSED);
		assertThat(renderer.getStatusInfo(cronJobHistory)).isEqualTo(getLabel(LABEL_PROCESS_STATUS_INFO, LABEL_PROCESS_PAUSED));
	}

	@Test
	public void getStatusInfoUnknownTest()
	{
		cronJobHistory.setStatus(CronJobStatus.UNKNOWN);
		assertThat(renderer.getStatusInfo(cronJobHistory)).isEqualTo(getLabel(LABEL_PROCESS_STATUS_INFO, LABEL_PROCESS_UNKNOWN));
	}

	@Test
	public void isRerunApplicableFinishedTest()
	{
		final CronJobHistoryModel cjh = getCronJobHistoryModel();
		when(cjh.getStatus()).thenReturn(CronJobStatus.FINISHED);
		when(cjh.getResult()).thenReturn(CronJobResult.ERROR);
		assertThat(renderer.isReRunApplicable(cjh)).isTrue();
	}

	@Test
	public void isRerunApplicableFinishedAnotherUser()
	{
		final CronJobHistoryModel cjh = getCronJobHistoryModel();
		when(checkResult.isGranted()).thenReturn(false);
		when(cjh.getStatus()).thenReturn(CronJobStatus.FINISHED);
		when(cjh.getResult()).thenReturn(CronJobResult.ERROR);
		assertThat(renderer.isReRunApplicable(cjh)).isFalse();
	}

	@Test
	public void isRerunApplicableFinishedTheSameNonAdminUser()
	{
		final CronJobHistoryModel cjh = getCronJobHistoryModel();
		when(cjh.getStatus()).thenReturn(CronJobStatus.FINISHED);
		when(cjh.getResult()).thenReturn(CronJobResult.FAILURE);
		assertThat(renderer.isReRunApplicable(cjh)).isTrue();
	}

	@Test
	public void isAbortApplicableAbortableRunning()
	{
		final CronJobHistoryModel cjh = getCronJobHistoryModel();
		final CronJobModel cj = cjh.getCronJob();
		when(cjh.getCronJob()).thenReturn(cj);
		when(cronJobService.isAbortable(cj)).thenReturn(true);
		when(cjh.getStatus()).thenReturn(CronJobStatus.RUNNING);
		assertThat(renderer.isAbortApplicable(cjh)).isTrue();
	}

	@Test
	public void isAbortApplicableRemoved()
	{
		// given
		final CronJobHistoryModel cjh = getCronJobHistoryModel();
		final CronJobModel cj = mock(CronJobModel.class);
		final ItemModelContext ctx = mock(ItemModelContext.class);
		when(ctx.isRemoved()).thenReturn(true);
		when(cj.getItemModelContext()).thenReturn(ctx);
		when(cjh.getCronJob()).thenReturn(cj);
		Mockito.lenient().when(cronJobService.isAbortable(cj)).thenReturn(false);

		// when
		final boolean abortApplicable = renderer.isAbortApplicable(cjh);

		// then
		assertThat(abortApplicable).isFalse();
	}

	@Test
	public void isAbortApplicableNotAbortable()
	{
		final CronJobHistoryModel cjh = getCronJobHistoryModel();
		final CronJobModel cj = cjh.getCronJob();
		when(cjh.getCronJob()).thenReturn(cj);
		when(cronJobService.isAbortable(cj)).thenReturn(false);
		assertThat(renderer.isAbortApplicable(cjh)).isFalse();
	}

	@Test
	public void isAbortApplicableAbortableNotRunning()
	{
		final CronJobHistoryModel cjh = getCronJobHistoryModel();
		final CronJobModel cj = cjh.getCronJob();
		when(cjh.getCronJob()).thenReturn(cj);
		when(cronJobService.isAbortable(cj)).thenReturn(true);
		when(cjh.getStatus()).thenReturn(CronJobStatus.FINISHED);
		assertThat(renderer.isAbortApplicable(cjh)).isFalse();
	}

	@Test
	public void createEditPopupShowLogRerunTest()
	{
		final CronJobHistoryModel cjh = getCronJobHistoryModel();
		when(cjh.getStatus()).thenReturn(CronJobStatus.FINISHED);
		when(cjh.getResult()).thenReturn(CronJobResult.ERROR);
		testEditPopup(cjh, DefaultProcessesListItemRenderer.LABEL_PROCESS_SHOW_LOG,
				DefaultProcessesListItemRenderer.LABEL_PROCESS_RE_RUN);
	}

	@Test
	public void createEditPopupShowLogRunningStateTest()
	{
		final CronJobHistoryModel cjh = getCronJobHistoryModel();
		when(cjh.getStatus()).thenReturn(CronJobStatus.RUNNING);
		Mockito.lenient().when(cjh.getResult()).thenReturn(CronJobResult.ERROR);
		testEditPopup(cjh, DefaultProcessesListItemRenderer.LABEL_PROCESS_SHOW_LOG);
	}

	@Test
	public void createEditPopupShowLogWrongUserTest()
	{
		final CronJobHistoryModel cjh = getCronJobHistoryModel();
		when(checkResult.isGranted()).thenReturn(false);
		when(cjh.getStatus()).thenReturn(CronJobStatus.FINISHED);
		when(cjh.getResult()).thenReturn(CronJobResult.ERROR);
		testEditPopup(cjh, DefaultProcessesListItemRenderer.LABEL_PROCESS_SHOW_LOG);
	}

	protected CronJobHistoryModel getCronJobHistoryModel()
	{
		final CronJobHistoryModel cjh = mock(CronJobHistoryModel.class);
		final CronJobModel cj = mock(CronJobModel.class);
		final ItemModelContext imc = mock(ItemModelContext.class);
		when(cjh.getCronJob()).thenReturn(cj);
		when(cj.getItemModelContext()).thenReturn(imc);
		return cjh;
	}

	@Test
	public void createEditPopupShowLogAbortTest()
	{
		final CronJobHistoryModel cjh = getCronJobHistoryModel();
		final CronJobModel cj = cjh.getCronJob();
		when(cjh.getCronJob()).thenReturn(cj);
		when(cronJobService.isAbortable(cj)).thenReturn(true);
		Mockito.lenient().when(cronJobService.isRunning(cj)).thenReturn(true);
		when(cjh.getStatus()).thenReturn(CronJobStatus.RUNNING);
		Mockito.lenient().when(cjh.getResult()).thenReturn(CronJobResult.UNKNOWN);
		testEditPopup(cjh, DefaultProcessesListItemRenderer.LABEL_PROCESS_SHOW_LOG,
				DefaultProcessesListItemRenderer.LABEL_PROCESS_ABORT);
	}

	private void testEditPopup(final CronJobHistoryModel cronJobHistory, final String... values)
	{
		final WidgetInstanceManager widgetInstanceManager = mock(WidgetInstanceManager.class);
		final Menupopup editPopup = renderer.createEditPopup(cronJobHistory, widgetInstanceManager);

		assertThat(editPopup.getChildren().size()).isEqualTo(values.length);

		for (int i = 0; i < values.length; i++)
		{
			assertThat(editPopup.getChildren().get(i)).isInstanceOf(Menuitem.class);
			assertThat(((Menuitem) editPopup.getChildren().get(i)).getLabel()).isEqualTo(values[i]);
			assertThat(((Menuitem) editPopup.getChildren().get(0)).getIconSclass())
					.isEqualToIgnoringCase(DefaultProcessesListItemRenderer.SCLASS_MENU_NO_ICON);
			assertThat(StringUtils.isBlank(((Menuitem) editPopup.getChildren().get(0)).getIconSclass())).isTrue();
		}
	}

	@Test
	public void testIsFailedFalse()
	{
		makeFinished(CronJobResult.SUCCESS);
		assertThat(renderer.isFailed(cronJobHistory)).isFalse();
	}

	@Test
	public void testIsFailedFalseNotFinished()
	{
		cronJobHistory.setStatus(CronJobStatus.RUNNING);
		assertThat(renderer.isFailed(cronJobHistory)).isFalse();
	}

	@Test
	public void testIsFailedTrueResultFailure()
	{
		makeFinished(CronJobResult.FAILURE);
		assertThat(renderer.isFailed(cronJobHistory)).isTrue();
	}

	private void makeFinished(final CronJobResult result)
	{
		cronJobHistory.setStatus(CronJobStatus.FINISHED);
		cronJobHistory.setResult(result);
	}

	@Test
	public void testIsFailedTrueResultError()
	{
		makeFinished(CronJobResult.ERROR);
		assertThat(renderer.isFailed(cronJobHistory)).isTrue();
	}

	@Test
	public void testGetSclassSuffixForFailed()
	{
		makeFinished(CronJobResult.FAILURE);
		assertThat(renderer.getSclassSuffixFor(cronJobHistory))
				.isEqualToIgnoringCase(DefaultProcessesListItemRenderer.STATUS_FAILED);
	}

	@Test
	public void testGetSclassSuffixForSuccess()
	{
		cronJobHistory.setStatus(CronJobStatus.RUNNING);
		assertThat(renderer.getSclassSuffixFor(cronJobHistory)).isEqualToIgnoringCase(CronJobStatus.RUNNING.toString());
	}

	@Test
	public void testCreateStatusInfoComponent()
	{
		when(labelService.getObjectLabel(cronJobHistory)).thenReturn("theLabel");

		final Div statusInfoComponent = renderer.createStatusInfoComponent(cronJobHistory);

		final List<Component> labels = Selectors.find(statusInfoComponent, ".".concat(SCLASS_STATUS_INFO_LABEL));
		assertThat(labels.stream().map(cmp -> ((Label) cmp).getValue()).collect(Collectors.toSet())).contains("theLabel");
	}

	@Test
	public void testNoAccess()
	{
		final Listitem listitem = new Listitem();
		when(Boolean.valueOf(permissionFacade.canReadInstance(cronJobHistory))).thenReturn(Boolean.FALSE);

		renderer.render(listitem, null, cronJobHistory, null, null);

		final Label noAccessLabel = (Label) listitem.query('.' + SCLASS_PROCESSES_NO_ACCESS);
		assertThat(noAccessLabel).isNotNull();
		assertThat(noAccessLabel.getValue()).isEqualTo(LABEL_PROCESS_NO_ACCESS);
	}

	private CronJobHistoryModel createCronJobHistory(final int jobId)
	{
		final CronJobHistoryModel cronJobHistory = new CronJobHistoryModel();
		BackofficeTestUtil.setPk(cronJobHistory, jobId);
		cronJobHistory.setStatus(CronJobStatus.RUNNING);
		cronJobHistory.setStartTime(startDate);
		cronJobHistory.setEndTime(endDate);
		return cronJobHistory;
	}

}
