package com.isoview.scripting;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class ScriptObject {
	private ScriptObjectMirror object;

	public ScriptObject(ScriptObjectMirror object) {
		this.object = object;
	}

	public <T> T get(String key, Class<T> type) {
		return type.cast(this.object.get(key));
	}

	public ScriptObject get(String key) {
		return new ScriptObject((ScriptObjectMirror) this.object.get(key));
	}

	public ScriptObject get(int index) {
		return get(String.valueOf(index));
	}

	public Integer getAsInteger(String key) {
		return get(key, Integer.class);
	}

	public String getAsString(String key) {
		return get(key, String.class);
	}

	public int size() {
		return this.object.values().size();
	}
}
