package com.isoview.datastructures.map;

import com.isoview.datastructures.Vector2D;

import java.io.Serializable;

public class IsometricMap implements Serializable {
	private Vector2D<Integer> size;
	private Vector2D<Integer> origin;
	private int[] tiles;

	public IsometricMap(Vector2D<Integer> size, Vector2D<Integer> origin) {
		this.size = size;
		this.origin = origin;

		this.tiles = new int[this.size.getX() * this.size.getY()];
	}

	public int getTileAt(int x, int y) {
		return this.tiles[y * getWidth() + x];
	}

	public void setTileAt(int x, int y, int tileId) {
		this.tiles[y * getWidth() + x] = tileId;
	}

	public int getWidth() {
		return this.size.getX();
	}

	public int getHeight() {
		return this.size.getY();
	}

	public Vector2D<Integer> getOrigin() {
		return this.origin;
	}
}
