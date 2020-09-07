package com.isoview.scripting;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptUtils;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

public final class Scripting {
	private static final ScriptEngine ENGINE = new NashornScriptEngineFactory().getScriptEngine("--language=es6");

	static {
		ENGINE.setBindings(new SimpleBindings(), ScriptContext.GLOBAL_SCOPE);
	}

	public static ScriptObject runScript(URL path, Map<String, Object>... contexts) {
		InputStreamReader scriptFile;

		for (Map<String, Object> context : contexts) {
			ENGINE.getBindings(ScriptContext.GLOBAL_SCOPE).putAll(context);
		}

		try {
			scriptFile = new InputStreamReader(path.openStream());
		} catch (IOException exception) {
			throw new RuntimeException(exception.getMessage(), exception);
		}

		try {
			return new ScriptObject(ScriptUtils.wrap(ENGINE.eval(scriptFile)));
		} catch (ScriptException e) {
			e.printStackTrace();
		}

		return null;
	}
}
