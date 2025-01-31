package net.fpsboost.screen.clickgui.utils;

import lombok.Getter;
import lombok.Setter;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;

@Setter
@Getter
public class RoundedRect extends RenderEntity {
	double x;
	double y;
	double width;
	double height;
	double radius;
	int color;
	RenderType type;

	public RoundedRect(double x, double y, double width, double height, double radius, int color, RenderType type) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.radius = radius;
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
		this.drawFastRoundedRect(x, y, x + width, y + height, radius, color);
	}

	private void drawRect2() {
		this.drawFastRoundedRect(x, y, width, height, radius, color);
	}

	private void drawFastRoundedRect(double x, double y, double width, double height, double radius, int color) {
		final int Semicircle = 18;
		final float f = 90.0f / Semicircle;
		final float f2 = (color >> 24 & 0xFF) / 255.0f;
		final float f3 = (color >> 16 & 0xFF) / 255.0f;
		final float f4 = (color >> 8 & 0xFF) / 255.0f;
		final float f5 = (color & 0xFF) / 255.0f;
		GL11.glDisable(2884);
		GL11.glDisable(3553);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glColor4f(f3, f4, f5, f2);
		GL11.glBegin(5);
		GL11.glVertex2d(x + radius, y);
		GL11.glVertex2d(x + radius, height);
		GL11.glVertex2d(width - radius, y);
		GL11.glVertex2d(width - radius, height);
		GL11.glEnd();
		GL11.glBegin(5);
		GL11.glVertex2d(x, y + radius);
		GL11.glVertex2d(x + radius, y + radius);
		GL11.glVertex2d(x, height - radius);
		GL11.glVertex2d(x + radius, height - radius);
		GL11.glEnd();
		GL11.glBegin(5);
		GL11.glVertex2d(width, y + radius);
		GL11.glVertex2d(width - radius, y + radius);
		GL11.glVertex2d(width, height - radius);
		GL11.glVertex2d(width - radius, height - radius);
		GL11.glEnd();
		GL11.glBegin(6);
		double f6 = width - radius;
		double f7 = y + radius;
		GL11.glVertex2d(f6, f7);
		int j = 0;
		for (j = 0; j <= Semicircle; ++j) {
			final double f8 = j * f;
			GL11.glVertex2d( (f6 + radius * Math.cos(Math.toRadians(f8))),
					 (f7 - radius * Math.sin(Math.toRadians(f8))));
		}
		GL11.glEnd();
		GL11.glBegin(6);
		f6 = x + radius;
		f7 = y + radius;
		GL11.glVertex2d(f6, f7);
		for (j = 0; j <= Semicircle; ++j) {
			final double f9 = j * f;
			GL11.glVertex2d( (f6 - radius * Math.cos(Math.toRadians(f9))),
					 (f7 - radius * Math.sin(Math.toRadians(f9))));
		}
		GL11.glEnd();
		GL11.glBegin(6);
		f6 = x + radius;
		f7 = height - radius;
		GL11.glVertex2d(f6, f7);
		for (j = 0; j <= Semicircle; ++j) {
			final double f10 = j * f;
			GL11.glVertex2d( (f6 - radius * Math.cos(Math.toRadians(f10))),
					 (f7 + radius * Math.sin(Math.toRadians(f10))));
		}
		GL11.glEnd();
		GL11.glBegin(6);
		f6 = width - radius;
		f7 = height - radius;
		GL11.glVertex2d(f6, f7);
		for (j = 0; j <= Semicircle; ++j) {
			final double f11 = j * f;
			GL11.glVertex2d( (f6 + radius * Math.cos(Math.toRadians(f11))),
					 (f7 + radius * Math.sin(Math.toRadians(f11))));
		}
		GL11.glEnd();
		GL11.glEnable(3553);
		GL11.glEnable(2884);
		GL11.glDisable(3042);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.blendFunc(770, 771);
	}

	public enum RenderType {
		Expand, Position
    }

}
