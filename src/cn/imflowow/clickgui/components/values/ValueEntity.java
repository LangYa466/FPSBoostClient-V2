package cn.imflowow.clickgui.components.values;

import cn.imflowow.clickgui.utils.UIComponent;
import net.fpsboost.value.Value;

public class ValueEntity extends UIComponent {
	Value value;
	
	public ValueEntity(Value value) {
		this.value = value;
		this.init(0, 0);
	}

	@Override
	public void init(double positionX, double positionY) {
	}

	@Override
	public void draw(int mouseX, int mouseY, double positionX, double positionY) {
	}

	protected boolean isClickable() {
		return this.getClickGui().getValueList().isClickable();
	}
	
	protected void setLocked(boolean locked) {
		this.getClickGui().getValueList().setLocked(locked);
	}
	public double getHeight()
	{
		return 0.0;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}

}
