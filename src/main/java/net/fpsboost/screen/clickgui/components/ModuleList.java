package net.fpsboost.screen.clickgui.components;

import net.fpsboost.screen.clickgui.ClickGui;
import net.fpsboost.screen.clickgui.utils.*;
import lombok.Getter;
import lombok.Setter;
import net.fpsboost.element.ElementManager;
import net.fpsboost.module.Module;
import net.fpsboost.module.ModuleManager;
import net.fpsboost.util.font.FontManager;

import java.util.ArrayList;
import java.util.List;

public class ModuleList extends UIComponent {

	public Scissor scissor;
	public List<ModuleRect> lists;

	public Module lastOnHover;
	@Setter
	@Getter
	public Module onHover;
	@Getter
	@Setter
	public Module lastOnSetting;
	@Setter
	@Getter
	public Module onSetting;

	@Getter
	@Setter
	public ClickEntity clickarea;
	@Getter
	@Setter
	public boolean clickable;

	public Rect onHover_rect;
	public Translate anim_onhover = new Translate(0, 0);
	public double onHover_width;

	@Getter
	@Setter
	public double tarY;
	public double curY;
	public Translate animation = new Translate(0, 0);

	@Override
	public void init(double positionX, double positionY) {
		this.curY = 0;
		this.tarY = 0;
		this.onHover_width = 0;
		this.lists = new ArrayList<>();
		this.refreshList(0);
		this.clickarea = new ClickEntity(positionX + 110, positionY + 10, 130, 280, MouseBounds.CallType.Expand, () -> {
		}, () -> {
		}, () -> {
			clickable = true;
		}, () -> {
		}, () -> {
			clickable = false;
		});
		this.scissor = new Scissor(positionX + 110, positionY + 10, 130, 280);
		this.onHover_rect = new Rect(2, this.getScaledHeight() - 10, 0, 10, this.getColor(13), Rect.RenderType.Expand);
	}

	@Override
	public void draw(int mouseX, int mouseY, double positionX, double positionY) {
//		this.init(positionX, positionY);
		this.scissor.setX(positionX + 110);
		this.scissor.setY(positionY + 10);
		this.scissor.setWidth(130);
		this.scissor.setHeight(280);
		this.scissor.doScissor();

		this.clickarea.setX(positionX + 110);
		this.clickarea.setY(positionY + 10);
		this.clickarea.setX1(130);
		this.clickarea.setY1(280);
		this.clickarea.tick();

		this.setOnHover(null);
		int index = 0;
		for (ModuleRect mr : lists) {
			mr.draw(mouseX, mouseY, positionX + 110, positionY + this.curY + 10 + 25 * index);
			index++;
		}

		if (this.isClickable()) {
			if (this.tarY + this.getWheel() < 0) {
				if (this.tarY + this.getWheel() > (lists.size() - 1) * -25) {
					this.tarY += this.getWheel() * 2;
				} else {
					this.tarY = (lists.size() - 1) * -25;
				}
			} else {
				this.tarY = 0;
			}
		}

		this.animation.interpolate((float) this.tarY, 0, 0.3f);
		this.curY = animation.getX();

		if (onHover != null) {
			anim_onhover.interpolate((float) this.onHover_width + 2, 0, 0.3f);
		} else {
			anim_onhover.interpolate(0, 0, 0.3f);
		}

		ClickGui.INSTANCE.getWholeScreenScissor().doScissor();
		if (this.onHover != null || anim_onhover.getX() != 0) {
			if (this.onHover != null)
				this.lastOnHover = this.onHover;
			String text = this.lastOnHover.cnDescription;
			if (text.isEmpty()) text = "无";
            this.onHover_width = (float) FontManager.client(16).getStringWidth(text);
			this.scissor.setX(2);
			this.scissor.setY(this.getScaledHeight() - 2 - 10);
			this.scissor.setWidth(anim_onhover.getX());
			this.scissor.setHeight(10);
			this.scissor.doScissor();
			this.onHover_rect.setColor(this.getColor(13));
			this.onHover_rect.setWidth(anim_onhover.getX());
			this.onHover_rect.setY(this.getScaledHeight() - 2 - 10);
			this.onHover_rect.draw();

			FontManager.client(16).drawString(text, 3, this.getScaledHeight() - 12, this.getColor(14));
		}

		ClickGui.INSTANCE.getWholeScreenScissor().doScissor();

	}

	public void refreshList(int type) {
		switch (type) {
			case 0:
				lists.clear();
				ModuleManager.modules.forEach(module -> lists.add(new ModuleRect(module)));
				break;
			case 1:
				lists.clear();
				ElementManager.elements.forEach(element -> lists.add(new ModuleRect(element)));
				break;
		}
	}
}
