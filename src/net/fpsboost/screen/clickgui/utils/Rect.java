package net.fpsboost.screen.clickgui.utils;

import net.minecraft.client.gui.Gui;

public class Rect extends RenderEntity {
	double x;
	double y;
	double width;
	double height;
	int color;
	RenderType type;

	public Rect(double x, double y, double width, double height, int color, RenderType type) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = color;
		this.type = type;
	}

	public void draw() {
		switch (type) {
		case Expand:
			this.drawRect();
			break;
		case Position:
			this.drawRect2();
			break;
		default:
			break;
		}
	}

	private void drawRect() {
		Gui.drawRect(x, y, x + width, y + height, color);
	}

	private void drawRect2() {
		Gui.drawRect(x, y, width, height, color);
	}

	public enum RenderType {
		Expand, Position
    }

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public RenderType getType() {
		return type;
	}

	public void setType(RenderType type) {
		this.type = type;
	}
}
