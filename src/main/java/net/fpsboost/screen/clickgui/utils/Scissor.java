package net.fpsboost.screen.clickgui.utils;

import org.lwjgl.opengl.GL11;

public class Scissor extends RenderEntity {
	double x, y, width, height, scale;

	public Scissor(double x, double y, double width, double height, double scale) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.scale = scale;
	}

	public Scissor(double x, double y, double width, double height) {
		this(x, y, width, height, 1.0);
	}

	/*
	 * GL11.glEnable(GL11.GL_SCISSOR_TEST); GL11.glDisable(GL11.GL_SCISSOR_TEST);
	 */
	public void doScissor() {
		final double scale_ = super.getGuiScaleFactor();

		double x = this.x * scale_;
		double y = (super.getScaledHeight() - this.y) * scale_;
		double width = this.width * scale_ * this.scale;
		double height = this.height * scale_ * this.scale;

		GL11.glScissor((int) x, (int) (y - height), (int) width, (int) height);
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

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}
}
