package net.fpsboost.screen.clickgui.components.values;

import lombok.Getter;
import net.fpsboost.screen.clickgui.utils.UIComponent;
import net.fpsboost.value.Value;

@Getter
public class ValueEntity extends UIComponent {
	Value<?> value;
	
	public ValueEntity(Value<?> value) {
		this.value = value;
		this.init(0, 0);
	}

	@Override
	public void init(double positionX, double positionY) {
        super.init(positionX, positionY);
    }

	@Override
	public void draw(int mouseX, int mouseY, double positionX, double positionY) {
        super.draw(mouseX, mouseY, positionX, positionY);
    }

	protected boolean isClickable() {
		return this.getClickGui().getValueList().isClickable();
	}
	
	protected void setLocked() {
		this.getClickGui().getValueList().setLocked(true);
	}
	public double getHeight()
	{
		return 0.0;
	}

    public void setValue(Value<?> value) {
		this.value = value;
	}

}
