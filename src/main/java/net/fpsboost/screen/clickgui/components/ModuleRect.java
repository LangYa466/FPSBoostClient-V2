package net.fpsboost.screen.clickgui.components;

import net.fpsboost.handler.MessageHandler;
import net.fpsboost.screen.clickgui.ClickGui;
import net.fpsboost.screen.clickgui.utils.*;
import lombok.Getter;
import lombok.Setter;
import net.fpsboost.module.Module;
import net.fpsboost.util.font.FontManager;

import java.awt.*;

public class ModuleRect extends UIComponent {
	@Setter @Getter
	private Module module;
	private final boolean hasValues;
	private ClickableRect rect;
	private final Translate anim = new Translate(0, 0);

	private Rect setting;

	private Rect enable_rect;
	private ColorAnimations coloranim;

	private ClickableRect setting_rect;

	public ModuleRect(Module module) {
		this.module = module;
		this.hasValues = !this.module.values.isEmpty();
		this.init(0, 0);
	}

	@Override
	public void init(double positionX, double positionY) {
		Color color = this.module.isEnabled() ? new Color(1, 223, 1, this.getMenuAlpha())
				: new Color(223, 1, 1, this.getMenuAlpha());
		this.setting = new Rect(0, 0, 1, 1, this.getColor(16), Rect.RenderType.Expand);
		this.coloranim = new ColorAnimations(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
		this.setting_rect = new ClickableRect(0, 0, 10, 20, this.getColor(8), Rect.RenderType.Expand, () -> {
			if (!this.getClickGui().getModuleList().isClickable())
				return;
			if (ClickGui.INSTANCE.getModuleList().getOnSetting() == this.module)
				return;
			if (ClickGui.INSTANCE.getModuleList().getLastOnSetting() == null) {
				ClickGui.INSTANCE.getModuleList().setLastOnSetting(this.module);
			} else if (ClickGui.INSTANCE.getModuleList()
					.getLastOnSetting() == ClickGui.INSTANCE.getModuleList()
					.getOnSetting()) {
				ClickGui.INSTANCE.getModuleList().setLastOnSetting(
						ClickGui.INSTANCE.getModuleList().getOnSetting());
			}

			ClickGui.INSTANCE.getModuleList().setOnSetting(this.module);
		}, () -> {

		}, () -> {
			if (!this.getClickGui().getModuleList().isClickable())
				return;
			this.setting_rect.setColor(this.getColor(12));
		}, () -> {
		}, () -> this.setting_rect.setColor(this.getColor(11)));
		this.rect = new ClickableRect(0, 0, this.hasValues ? 120 : 130, 20, this.getColor(8), Rect.RenderType.Expand,
				() -> {
					if (!this.getClickGui().getModuleList().isClickable())
						return;
					this.module.toggle();
				}, () -> {
		}, () -> {
			if (!this.getClickGui().getModuleList().isClickable())
				return;
			this.rect.setColor(this.getColor(10));
			ClickGui.INSTANCE.getModuleList().setOnHover(this.module);
			anim.interpolate(5, 0, 0.3f);
		}, () -> {
		}, () -> {
			this.rect.setColor(this.getColor(8));
			anim.interpolate(0, 0, 0.3f);
		});
		this.enable_rect = new Rect(0, 0, 2, 20,
				this.module.isEnabled() ? new Color(1, 223, 1, this.getMenuAlpha()).getRGB()
						: new Color(223, 1, 1, this.getMenuAlpha()).getRGB(),
				Rect.RenderType.Expand);
	}

	@Override
	public void draw(int mouseX, int mouseY, double positionX, double positionY) {

//		this.init(positionX, positionY);

		this.rect.setX(positionX);
		this.rect.setY(positionY);
		this.rect.draw();

		if (this.rect.isInArea()) {
			if (this.rect.isMiddlePressed()) {
				this.getClickGui().setOnBindingModule(this.module);
				MessageHandler.addMessage(
						this.getModule().cnName
								+ ": 按你想要绑定的任意键",
						MessageHandler.MessageType.Warning, 3000);
			}
		}

		if (this.module.isEnabled()) {
			this.enable_rect.setColor(coloranim.getColor(1, 223, 1, this.getMenuAlpha()));
		} else {
			this.enable_rect.setColor(coloranim.getColor(223, 1, 1, this.getMenuAlpha()));
		}
		this.enable_rect.setX(positionX);
		this.enable_rect.setY(positionY);
		this.enable_rect.draw();

		if (this.hasValues) {
			this.setting_rect.setX(positionX + 120);
			this.setting_rect.setY(positionY);
			this.setting_rect.draw();

			this.setting.setColor(this.getColor(16));
			this.setting.setX(positionX + 124.5);
			this.setting.setY(positionY + 7);
			this.setting.draw();

			this.setting.setX(positionX + 124.5);
			this.setting.setY(positionY + 10);
			this.setting.draw();

			this.setting.setX(positionX + 124.5);
			this.setting.setY(positionY + 13);
			this.setting.draw();
		}

		FontManager.client(18).drawString(this.module.getDisplayName(), positionX + 8 + anim.getX(), positionY + 8, this.getColor(9));
	}

	// 新增 getWidth 方法
	public double getWidth() {
		double width = this.rect.getWidth();
		if (this.hasValues) {
			// 如果有值，增加设置区域的宽度
			width += 20; // 你可以根据需要调整这个值
		}
		return width;
	}
}
