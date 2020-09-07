package com.isoview.core;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class BaseController {
	@FXML
	private Canvas viewport;

	private Graphics graphics;

	private BufferedImage raster;

	@FXML
	protected final void initialize() throws Exception {
		this.raster = new BufferedImage((int) this.viewport.getWidth(), (int) this.viewport.getHeight(), BufferedImage.TYPE_INT_ARGB);
		this.graphics = this.raster.createGraphics();

		this.viewport.setOnMouseMoved(event -> {
			event.consume();
			Mouse.xScreen = (int) event.getX();
			Mouse.yScreen = (int) event.getY();
		});

		this.viewport.setOnMousePressed(event -> {
			event.consume();
			if (event.getButton() == MouseButton.PRIMARY) {
				Mouse.isLeftButtonPressed = true;
			} else if (event.getButton() == MouseButton.SECONDARY) {
				Mouse.isRightButtonPressed = true;
			}

			onEvent();
		});

		this.viewport.setOnMouseReleased(event -> {
			event.consume();
			if (event.getButton() == MouseButton.PRIMARY) {
				Mouse.isLeftButtonPressed = false;
			} else if (event.getButton() == MouseButton.SECONDARY) {
				Mouse.isRightButtonPressed = false;
			}

			onEvent();
		});

		new EngineLoop() {
			@Override
			protected void onClear() {
				BaseController.this.onClear();
			}

			@Override
			protected void onUpdate() {
				BaseController.this.onUpdate();
			}

			@Override
			protected void onRender() {
				BaseController.this.onRender();
				viewport.getGraphicsContext2D().drawImage(SwingFXUtils.toFXImage(getRaster(), null), 0, 0);
			}
		}.start();

		onCreated();
	}

	protected void onCreated() throws Exception {

	}

	protected void onClear() {
		this.graphics.clearRect(0, 0, this.raster.getWidth(), this.raster.getHeight());
	}

	protected abstract void onUpdate();

	protected abstract void onRender();

	protected abstract void onEvent();

	protected Graphics getGraphics() {
		return this.graphics;
	}

	public BufferedImage getRaster() {
		return this.raster;
	}
}
