package com.isoview.datastructures.tile;

import java.awt.*;

public class Tile {
	private final String name;
	private final Image tile;

	public Tile(String name, Image tile) {
		this.name = name;
		this.tile = tile;
	}

	public String getName() {
		return this.name;
	}

	public Image getTile() {
		return this.tile;
	}
}
