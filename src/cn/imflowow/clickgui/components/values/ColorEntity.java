package cn.imflowow.clickgui.components.values;

import cn.imflowow.clickgui.utils.ClickEntity;
import cn.imflowow.clickgui.utils.GradientRect;
import cn.imflowow.clickgui.utils.MouseBounds;
import cn.imflowow.clickgui.utils.Rect;
import net.fpsboost.util.font.FontManager;
import net.fpsboost.value.impl.ColorValue;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ColorEntity extends ValueEntity {
	ClickEntity hue;
	ClickEntity alpha;
	ClickEntity colorrect;

	boolean hue_;
	boolean alpha_;
	boolean colorrect_;

	List<Color> huemap;

    public ColorEntity(ColorValue value) {
		super(value);
	}

	@Override
	public void init(double positionX, double positionY) {
		this.huemap = new ArrayList<>();
		this.refreshHue();
		this.hue = new ClickEntity(0, 0, 0, 0, MouseBounds.CallType.Expand, () -> {
			hue_ = true;
		}, () -> {
		}, () -> {
		}, () -> {
		}, () -> {
		});
		this.colorrect = new ClickEntity(0, 0, 0, 0, MouseBounds.CallType.Expand, () -> {
			colorrect_ = true;
		}, () -> {
		}, () -> {
		}, () -> {
		}, () -> {
		});
		this.alpha = new ClickEntity(0, 0, 0, 0, MouseBounds.CallType.Expand, () -> {
			alpha_ = true;
		}, () -> {
		}, () -> {
		}, () -> {
		}, () -> {
		});
	}

	@Override
	public void draw(int mouseX, int mouseY, double positionX, double positionY) {
		FontManager.client(14).drawString(value.cnName, positionX, positionY + 4,
				this.getColor(20));

        if (!Mouse.isButtonDown(0)) {
			hue_ = false;
			alpha_ = false;
			colorrect_ = false;
		}

		float h = this.getValue().getValue().getHue();
		new Rect(positionX, positionY + 14, 100, 100, this.resetAlpha(Color.getHSBColor(h, 1, 1), this.getMenuAlpha()),
				Rect.RenderType.Expand).draw();
		new GradientRect(positionX, positionY + 14, 100, 100,
				this.resetAlpha(Color.getHSBColor(h, 0, 1), this.getMenuAlpha()), 0x00F, GradientRect.RenderType.Expand,
				GradientRect.GradientType.Horizontal).draw();
		new GradientRect(positionX, positionY + 14, 100, 100, 0x00F,
				this.resetAlpha(Color.getHSBColor(h, 1, 0), this.getMenuAlpha()), GradientRect.RenderType.Expand,
				GradientRect.GradientType.Vertical).draw();
		this.drawOutsideRect(positionX + this.getValue().getValue().getSaturation() * 100.0f,
				positionY + 14 + (1.0f - this.getValue().getValue().getBrightness()) * 100.0f, 1, 1, 0.5,
				new Color(32, 32, 32, this.getMenuAlpha()).getRGB());

		this.colorrect.setX(positionX - 2);
		this.colorrect.setY(positionY + 14 - 2);
		this.colorrect.setX1(100 + 4);
		this.colorrect.setY1(100 + 4);
		this.colorrect.tick();

		for (int index = 0; index < 100; index++) {
			new Rect(positionX + 105, positionY + 14 + index, 8, 1,
					this.resetAlpha(this.huemap.get(index), this.getMenuAlpha()), Rect.RenderType.Expand).draw();
		}

		new Rect(positionX + 105, positionY + 14 + this.getValue().getValue().getHue() * 100.0f, 8, 1,
				new Color(32, 32, 32, Math.min(200, this.getMenuAlpha())).getRGB(), Rect.RenderType.Expand).draw();

		this.hue.setX(positionX + 105 - 2);
		this.hue.setY(positionY + 14 - 2);
		this.hue.setX1(8 + 4);
		this.hue.setY1(100 + 4);
		this.hue.tick();

		for (int yExt = 0; yExt < 50; yExt++)
			for (int xExt = 0; xExt < 4; xExt++)
				new Rect(positionX + 116.5F + (xExt * 2), positionY + 14 + (yExt * 2), 2, 2,
						this.resetAlpha((((yExt % 2 == 0) == (xExt % 2 == 0)) ? Color.WHITE : new Color(190, 190, 190)),
								this.getMenuAlpha()),
						Rect.RenderType.Expand).draw();

		new GradientRect(positionX + 116.5F, positionY + 14, 8, 100, 0x00F,
				this.resetAlpha(this.getValue().getValue().getColor(), this.getMenuAlpha()),
				GradientRect.RenderType.Expand, GradientRect.GradientType.Vertical).draw();

		new Rect(positionX + 116.5F, positionY + 14 + (this.getValue().getValue().getAlpha() / 255.0f) * 100.0f, 8, 1,
				new Color(32, 32, 32, Math.min(200, this.getMenuAlpha())).getRGB(), Rect.RenderType.Expand).draw();

		this.alpha.setX(positionX + 118 - 2);
		this.alpha.setY(positionY + 14 - 2);
		this.alpha.setX1(8 + 4);
		this.alpha.setY1(100 + 4);
		this.alpha.tick();

		for (int yExt = 0; yExt < 50; yExt++)
			for (int xExt = 0; xExt < 2; xExt++)
				new Rect(positionX + 128 + (xExt * 2), positionY + 14 + (yExt * 2), 2, 2,
						this.resetAlpha((((yExt % 2 == 0) == (xExt % 2 == 0)) ? Color.WHITE : new Color(190, 190, 190)),
								this.getMenuAlpha()),
						Rect.RenderType.Expand).draw();

		new Rect(positionX + 128, positionY + 14, 4, 100,
				this.resetAlpha(this.getValue().getValue().getColor(),
						Math.min(this.getMenuAlpha(), this.getValue().getValue().getColor().getAlpha())),
				Rect.RenderType.Expand).draw();

		if (this.isClickable() && alpha_) {
			float pos = (float) (mouseY - (positionY + 14));
			if (pos < 0) {
				pos = 0;
			}
			if (pos > 100) {
				pos = 100;
			}
			this.getValue().getValue().setAlpha((int) ((pos / 100.0f) * 255.0f));
		}

		if (this.isClickable() && colorrect_) {
			float posX = (float) (mouseX - (positionX));
			float posY = (float) (mouseY - (positionY + 14));
			if (posX < 0) {
				posX = 0;
			}
			if (posX > 100) {
				posX = 100;
			}
			if (posY < 0) {
				posY = 0;
			}
			if (posY > 100) {
				posY = 100;
			}
			this.getValue().getValue().setSaturation(posX / 100.0f);
			this.getValue().getValue().setBrightness((100.0f - posY) / 100.0f);
		}
		if (this.isClickable() && hue_) {
			float pos = (float) (mouseY - (positionY + 14));
			if (pos < 0) {
				pos = 0;
			}
			if (pos > 100) {
				pos = 100;
			}
			this.getValue().getValue().setHue(pos / 100.0f);
		}
	}

	@Override
	public double getHeight() {
		return 120;
	}

	@Override
	public ColorValue getValue() {
		return (ColorValue) super.getValue();
	}

	private void refreshHue() {
		this.huemap.clear();
		for (int index = 0; index < 100; index++) {
			this.huemap.add(Color.getHSBColor(index / 100.0f, 1.0f, 1.0f));
		}
	}

	private int resetAlpha(Color color, int alpha) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha).getRGB();
	}

	private void drawOutsideRect(double x, double y, double x2, double y2, double width, int color) {
		this.drawOutsideRect2(x, y, x + x2, y + y2, width, color);
	}

	private void drawOutsideRect2(double x, double y, double x2, double y2, double width, int color) {
		if (x > x2) {
			double i = x;
			x = x2;
			x2 = i;
		}

		if (y > y2) {
			double j = y;
			y = y2;
			y2 = j;
		}

		new Rect(x, y - width, x - width, y2, color, Rect.RenderType.Position).draw();
		new Rect(x, y, x2 + width, y - width, color, Rect.RenderType.Position).draw();
		new Rect(x2, y, x2 + width, y2 + width, color, Rect.RenderType.Position).draw();
		new Rect(x - width, y2, x2, y2 + width, color, Rect.RenderType.Position).draw();
	}
}
