package net.fpsboost.screen.clickgui.components;

import net.fpsboost.screen.clickgui.utils.ClickableRect;
import net.fpsboost.screen.clickgui.utils.ClickableString;
import net.fpsboost.screen.clickgui.utils.Rect;
import net.fpsboost.screen.clickgui.utils.UIComponent;
import lombok.Getter;
import lombok.Setter;
import net.fpsboost.util.font.FontManager;
import net.minecraft.client.gui.GuiChat;
import org.lwjgl.input.Mouse;

public class Window extends UIComponent {
	public int mouseX, mouseY;
	public int lastMouseX, lastMouseY;
	@Setter
    @Getter
    public double positionX, positionY;

	public ClickableRect top;
	public boolean top_;

	public Rect menu;
	public MenuButton modules;
	public MenuButton global;
	public ClickableString edit;
	@Setter
    private boolean hover;

	@Setter
    @Getter
    public int onChosen;

	public Rect main;

	@Override
	public void init(double positionX, double positionY) {
		this.positionX = positionX;
		this.positionY = positionY;
		this.onChosen = 0;
		this.top_ = false;
		this.top = new ClickableRect(this.positionX, this.positionY, 100, 40, this.getColor(0), Rect.RenderType.Expand,
				() -> this.top_ = true, () -> {
				}, () -> {
				}, () -> {
				}, () -> {
				});


				this.edit = new ClickableString(FontManager.logo(42), "b", positionX + 40, positionY + 260,
				this.getColor(4), () -> {
					this.getMinecraft().displayGuiScreen(new GuiChat());
				}, () -> {
				}, () -> {
				}, () -> {
				}, () -> {});

		this.menu = new Rect(positionX, positionY + 40, 100, 260, this.getColor(2), Rect.RenderType.Expand);
		this.main = new Rect(positionX + 100, positionY, 150, 300, this.getColor(4), Rect.RenderType.Expand);
		this.modules = new MenuButton(0, "e", "辅助功能", positionX + 40, positionY + 98, true);
		this.modules.init(positionX, positionY);
		this.global = new MenuButton(1, "f", "视觉UI", positionX + 40, positionY + 158, false);
		this.global.init(positionX, positionY);
	}

	@Override
	public void draw(int mouseX, int mouseY, double positionX, double positionY) {
		this.drawTop(mouseX, mouseY, positionX, positionY);
		this.drawMenu(mouseX, mouseY, positionX, positionY);
		this.drawMain(mouseX, mouseY, positionX, positionY);
	}

	private void drawMenu(int mouseX, int mouseY, double positionX, double positionY) {
		this.menu.setColor(this.getColor(2));
		this.menu.setX(positionX);
		this.menu.setY(positionY + 40);
		this.menu.draw();

		this.modules.setX(positionX + 40);
		this.modules.setY(positionY + 102);
		this.modules.draw(mouseX, mouseY, positionX + 40, positionY + 80);

		this.global.setX(positionX + 40);
		this.global.setY(positionY + 162);
		this.global.draw(mouseX, mouseY, positionX + 40, positionY + 140);

		this.edit.setX(positionX + 40);
		this.edit.setY(positionY + 260);
		this.edit.draw(false);
		FontManager.client(14).drawString("修改GUI", positionX + 44, positionY + 282,
				this.hover ? this.getColor(5) : this.getColor(4));
	}

	private void drawMain(int mouseX, int mouseY, double positionX, double positionY) {
		this.main.setColor(this.getColor(3));
		this.main.setX(positionX + 100);
		this.main.setY(positionY);
		this.main.draw();
	}

	private void drawTop(int mouseX, int mouseY, double positionX, double positionY) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;

		if (!Mouse.isButtonDown(0))
			this.top_ = false;

		this.top.setColor(this.getColor(0));
		this.top.setX(positionX);
		this.top.setY(positionY);
		this.top.draw();

		if (this.top_) {
			double w = this.lastMouseX - this.positionX;
			double h = this.lastMouseY - this.positionY;

			this.positionX = (mouseX - w);
			this.positionY = (mouseY - h);
		}

		int diffY = 10;

		// FontManager.logo(24).drawString("a", positionX + 8 + diffX, positionY + 6.4 + diffY, this.getColor(1));
		FontManager.arialBold(38).drawString("FPSBoost", positionX + 8,
				positionY + 7.5 + diffY, this.getColor(1));

		this.lastMouseX = mouseX;
		this.lastMouseY = mouseY;

	}
}
