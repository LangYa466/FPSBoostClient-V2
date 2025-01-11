package net.fpsboost.screen.clickgui.utils;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.Gui;

@Setter
@Getter
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

}
