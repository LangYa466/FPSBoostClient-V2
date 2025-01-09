package net.fpsboost.screen.clickgui.utils;

import java.awt.Color;

public class ColorAnimations {

	public ColorAnimations(int r, int g, int b, int a) {
		this.red = new Opacity(r);
		this.green = new Opacity(g);
		this.blue = new Opacity(b);
		this.alpha = new Opacity(a);
	}

	Opacity red;
	Opacity green;
	Opacity blue;
	Opacity alpha;

	public int getColor(int r, int g, int b, int a) {
		this.red.interpolate(r, 20);
		this.green.interpolate(g, 20);
		this.blue.interpolate(b, 20);
		this.alpha.interpolate(a, 20);
		int red = (int) this.red.getOpacity();
		int green = (int) this.green.getOpacity();
		int blue = (int) this.blue.getOpacity();
		int alpha = (int) this.alpha.getOpacity();
		return new Color(red, green, blue, alpha).getRGB();
	}
}
