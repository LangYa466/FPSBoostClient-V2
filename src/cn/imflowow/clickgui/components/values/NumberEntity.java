package cn.imflowow.clickgui.components.values;

import cn.imflowow.clickgui.utils.ClickEntity;
import cn.imflowow.clickgui.utils.MouseBounds;
import cn.imflowow.clickgui.utils.Rect;
import cn.imflowow.clickgui.utils.Translate;
import net.fpsboost.util.font.FontManager;
import net.fpsboost.value.impl.NumberValue;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.input.Mouse;

import java.math.BigDecimal;

public class NumberEntity extends ValueEntity {

	private Translate anim;
	private float curX;
	private float tarX;

	private ClickEntity clickentity;

	private int mouseX;
	private double positionX;
	private boolean locked;
	private final FontRenderer fr = FontManager.client(14);

	public NumberEntity(NumberValue value) {
		super(value);
	}

	@Override
	public void init(double positionX, double positionY) {
		this.clickentity = new ClickEntity(0, 0, 0, 0, MouseBounds.CallType.Expand, () -> {
			this.locked = true;
		}, () -> {
		}, () -> {
		}, () -> {
		}, () -> {
		});

		double inc = this.getValue().incValue;
		double max = this.getValue().maxValue;
		double min = this.getValue().minValue;
		double value = this.getValue().getValue();
		this.tarX = this.curX = (float) ((125 / (max - min)) * (value - min));
		this.anim = new Translate(this.tarX, 0);
		this.locked = false;
	}

	@Override
	public void draw(int mouseX, int mouseY, double positionX, double positionY) {
		if (!Mouse.isButtonDown(0))
			this.locked = false;

		this.mouseX = mouseX;
		this.positionX = positionX;
		this.clickentity.setX(positionX - 2);
		this.clickentity.setX1(134);
		this.clickentity.setY(positionY + 12);
		this.clickentity.setY1(6);
		this.clickentity.tick();

		double inc = this.getValue().incValue;
		double max = this.getValue().maxValue;
		double min = this.getValue().minValue;
		double value = this.getValue().getValue();

		this.tarX = (float) ((125 / (max - min)) * (value - min));
		this.anim.interpolate(this.tarX, 0, 0.3f);
		this.curX = (float) this.anim.getX();
		fr.drawString(this.value.cnName, positionX, positionY + 4, this.getColor(20));

		String text;
		int width;
		text = this.getValue().getValue().toString();
		width = fr.getStringWidth(text);
		fr.drawString(text, positionX + 130 - width, positionY + 4, this.getColor(20));

		new Rect(positionX, positionY + 14, 130, 2, this.getColor(25), Rect.RenderType.Expand).draw();
		new Rect(positionX, positionY + 14, this.curX, 2, this.getColor(27), Rect.RenderType.Expand).draw();
		FontManager.logo(14).drawString("t", positionX + this.curX, positionY + 14,
				this.getColor(26));

		if (this.isClickable() && this.locked) {
			double pos = this.mouseX - (this.positionX);
			if (pos < 0) {
				pos = 0;
			}
			if (pos > 130) {
				pos = 130;
			}
			double result = new BigDecimal((((int) (pos / (130 / (max - min)) / inc)) * inc) + min)
					.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
			value = result;
			this.getValue().setValue(value);
			/*
			if (this.getValue().getValue() instanceof Double) {
				this.getValue().setValue(value);
			} else if (this.getValue().getValue() instanceof Integer) {
				this.getValue().setValue((int) value);
			} else if (this.getValue().getValue() instanceof Float) {
				this.getValue().setValue((float) value);
			} else if (this.getValue().getValue() instanceof Long) {
				this.getValue().setValue((long) value);
			}
			 */
		}
	}

	@Override
	public double getHeight() {
		return 16;
	}

	@Override
	public NumberValue getValue() {
		return (NumberValue) super.getValue();
	}

}
