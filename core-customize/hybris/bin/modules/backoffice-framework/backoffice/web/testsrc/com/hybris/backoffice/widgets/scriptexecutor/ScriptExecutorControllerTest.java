/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.widgets.scriptexecutor;

import static com.hybris.backoffice.widgets.scriptexecutor.ScriptExecutorController.INPUT_SOCKET_DATA;
import static com.hybris.backoffice.widgets.scriptexecutor.ScriptExecutorController.SETTING_SCRIPT_CONTENT;
import static com.hybris.backoffice.widgets.scriptexecutor.ScriptExecutorController.SETTING_SCRIPT_CONTENT_LANG;
import static com.hybris.backoffice.widgets.scriptexecutor.ScriptExecutorController.SETTING_SCRIPT_URI;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.scripting.engine.ScriptExecutable;
import de.hybris.platform.scripting.engine.ScriptingLanguagesService;
import de.hybris.platform.scripting.engine.content.ScriptContent;
import de.hybris.platform.scripting.engine.impl.DefaultScriptExecutionResult;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Spy;

import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;


@DeclaredInput(value = INPUT_SOCKET_DATA)
@NullSafeWidget
public class ScriptExecutorControllerTest extends AbstractWidgetUnitTest<ScriptExecutorController>
{

	@Spy
	private ScriptExecutorController controller;

	@Mock
	private ScriptingLanguagesService scriptingLanguagesService;

	@Before
	public void setUp()
	{
		doReturn(scriptingLanguagesService).when(controller).getScriptingLanguagesService();
	}

	@Test
	public void testURILocatedScript()
	{
		final Object data = new Object();
		final String uri = "media://scriptId";
		widgetSettings.put(SETTING_SCRIPT_URI, uri);
		final ScriptExecutable executable = mock(ScriptExecutable.class);
		when(scriptingLanguagesService.getExecutableByURI(uri)).thenReturn(executable);
		when(executable.execute(anyMap())).thenReturn(new DefaultScriptExecutionResult(null, null));

		controller.input(data);

		verify(executable).execute(argThat(new ArgumentMatcher<Map<String, Object>>()
		{
			@Override
			public boolean matches(Map<String, Object> stringObjectMap) {
				return stringObjectMap.size() == 1 && data == stringObjectMap.get("data");
			}
		}));
	}

	@Test
	public void testWithInlineScriptContent()
	{
		final Object data = new Object();
		widgetSettings.put(SETTING_SCRIPT_CONTENT_LANG, "jruby");
		widgetSettings.put(SETTING_SCRIPT_CONTENT, "dummy content");
		final ScriptExecutable executable = mock(ScriptExecutable.class);
		when(scriptingLanguagesService.getExecutableByContent(argThat(new ArgumentMatcher<ScriptContent>()
		{
			@Override
			public boolean matches(ScriptContent scriptContent) {
				return "dummy content".equals(scriptContent.getContent())
						&& "jruby".equals(scriptContent.getEngineName());
			}
		}))).thenReturn(executable);
		when(executable.execute(anyMap())).thenReturn(new DefaultScriptExecutionResult(null, null));

		controller.input(data);

		verify(executable).execute(argThat(new ArgumentMatcher<Map<String, Object>>()
		{
			@Override
			public boolean matches(Map<String, Object> stringObjectMap) {
				return stringObjectMap.size() == 1 && data == stringObjectMap.get("data");
			}
		}));
	}


	@Override
	protected ScriptExecutorController getWidgetController()
	{
		return controller;
	}
}
