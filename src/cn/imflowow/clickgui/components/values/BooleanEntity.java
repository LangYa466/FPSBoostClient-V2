package cn.imflowow.clickgui.components.values;

import cn.imflowow.clickgui.utils.ClickEntity;
import cn.imflowow.clickgui.utils.MouseBounds;
import net.fpsboost.util.font.FontManager;
import net.fpsboost.value.impl.BooleanValue;

public class BooleanEntity extends ValueEntity {
	private ClickEntity rect;
	private boolean isHover;

	public BooleanEntity(BooleanValue value) {
		super(value);
	}

	@Override
	public void init(double positionX, double positionY) {
		this.rect = new ClickEntity(positionX + 120, positionY + 2, 10, 10, MouseBounds.CallType.Expand, () -> {
			if(!this.isClickable())
				return;
			this.getValue().setValue(!this.getValue().getValue());
		}, () -> {
		}, () -> {
			isHover = true;
		}, () -> {
		}, () -> {
			isHover = false;
		});
	}

	@Override
	public void draw(int mouseX, int mouseY, double positionX, double positionY) {
		this.rect.setX(positionX + 120);
		this.rect.setY(positionY + 2);
		this.rect.tick();
		FontManager.client(14).drawString(value.cnName, positionX, positionY + 4,
				this.getColor(20));
		FontManager.logo(24).drawString("g", positionX + 120, positionY + 2,
				isHover ? this.getColor(22) : this.getColor(21));
		if (this.getValue().getValue()) {
			FontManager.logo(16).drawString("r", positionX + 122, positionY + 4, this.getColor(20));
		}
	}

	@Override
	public double getHeight() {
		return 8;
	}

	@Override
	public BooleanValue getValue() {
		return (BooleanValue) super.getValue();
	}

}
