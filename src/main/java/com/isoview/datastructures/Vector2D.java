package com.isoview.datastructures;

import java.io.Serializable;

public class Vector2D<T extends Number> implements Serializable {
	private T x;
	private T y;

	public Vector2D(T x, T y) {
		this.x = x;
		this.y = y;
	}

	public T getX() {
		return this.x;
	}

	public void setX(T x) {
		this.x = x;
	}

	public T getY() {
		return this.y;
	}

	public void setY(T y) {
		this.y = y;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Vector2D{");
		sb.append("x=").append(x);
		sb.append(", y=").append(y);
		sb.append('}');
		return sb.toString();
	}
}
