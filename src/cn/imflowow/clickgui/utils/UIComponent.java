package cn.imflowow.clickgui.utils;

import cn.imflowow.clickgui.ClickGui;

public class UIComponent extends RenderEntity {
	public void init(double positionX, double positionY) {

	}

	public void draw(int mouseX, int mouseY, double positionX, double positionY) {

	}

	protected void setClickGuiPosition(double x, double y) {
		ClickGui.INSTANCE.setPositionX(x);
		ClickGui.INSTANCE.setPositionY(y);
	}

	protected int getMenuAlpha() {
		return ClickGui.INSTANCE.getAlpha();
	}

	protected int getColor(int type) {
		return ClickGui.INSTANCE.getColor(type);
	}

	protected String getResource(int type) {
		return ClickGui.INSTANCE.getResource(type);
	}

	protected void clickModulesButton(int id) {
		ClickGui.INSTANCE.clickModulesButton(id);
	}
	
	protected int getWheel()
	{
		return ClickGui.INSTANCE.getWheel();
	}
	
	protected ClickGui getClickGui()
	{
		return ClickGui.INSTANCE;
	}
}
