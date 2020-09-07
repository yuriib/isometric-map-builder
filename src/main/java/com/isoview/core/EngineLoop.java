package com.isoview.core;

import javafx.animation.AnimationTimer;

public abstract class EngineLoop extends AnimationTimer {
	private final double TARGET_FPS = 60.0;
	private final long NANOS_IN_SEC = 1000_000_000;
	private final long TARGET_NPF = (long) ((1.0 / TARGET_FPS) * NANOS_IN_SEC); // nanos per frame
	private int actualFps;
	private long previous;
	private long elapsed;

	public EngineLoop() {
		this.previous = System.nanoTime();
	}

	@Override
	public void handle(long now) {
		long elapsed = now - this.previous;
		if (elapsed < TARGET_NPF) {
			return;
		}

		EngineTime.delta = elapsed / (double) NANOS_IN_SEC;

		this.previous = now;

		this.elapsed += elapsed;
		this.actualFps++;

		if (this.elapsed >= NANOS_IN_SEC) {
			EngineLoopStats.fps = this.actualFps;
			this.elapsed = 0;
			this.actualFps = 0;
		}

		onClear();
		onUpdate();
		onRender();
	}

	protected abstract void onClear();

	protected abstract void onUpdate();

	protected abstract void onRender();
}
