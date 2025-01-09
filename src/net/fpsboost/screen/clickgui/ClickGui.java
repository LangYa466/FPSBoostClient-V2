package net.fpsboost.screen.clickgui;

import java.awt.Color;
import java.io.IOException;

import net.fpsboost.handler.MessageHandler;
import net.fpsboost.screen.clickgui.utils.Opacity;
import net.fpsboost.screen.clickgui.utils.Rect;
import net.fpsboost.screen.clickgui.utils.Scissor;
import lombok.Getter;
import lombok.Setter;
import net.fpsboost.config.ConfigManager;
import net.fpsboost.module.Module;
import net.fpsboost.module.impl.ClickGUIModule;
import net.fpsboost.util.font.FontManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.fpsboost.screen.clickgui.components.CommandBox;
import net.fpsboost.screen.clickgui.components.ModuleList;
import net.fpsboost.screen.clickgui.components.ValueList;
import net.fpsboost.screen.clickgui.components.Window;
import net.minecraft.client.gui.GuiScreen;

// https://github.com/ImFl0wow/Tritium-Open
public class ClickGui extends GuiScreen {
	public static final ClickGui INSTANCE = new ClickGui();
    @Setter
    @Getter
    private double positionX, positionY;

	@Setter
    @Getter
    private int alpha;
	private Opacity alpha_animation = new Opacity(0);

	private boolean hasInited;
	private boolean hasClosed;

	@Setter
    @Getter
    private Scissor wholeScreenScissor;

	@Setter
    @Getter
    private Window mainWindow;
	@Setter
    @Getter
    private ModuleList moduleList;
	@Setter
    @Getter
    private ValueList valueList;

	private final CommandBox commandBox;
	private boolean onCommandBox;

	@Setter
    @Getter
    private Module onBindingModule;

	@Getter
    @Setter
    private int wheel;

	public ClickGui() {
		this.alpha = 0;
		this.positionX = 20;
		this.positionY = 20;
		this.hasClosed = true;
		this.hasInited = false;
		this.wholeScreenScissor = new Scissor(0, 0, this.width, this.height);
		this.mainWindow = new Window();
		this.moduleList = new ModuleList();
		this.valueList = new ValueList();
		this.commandBox = new CommandBox();
		this.onCommandBox = false;
	}

	@Override
	public void initGui() {
		hasClosed = false;
		this.alpha_animation = new Opacity(0);
		if (!this.hasInited) {
			this.mainWindow.init(this.positionX, this.positionY);
			this.moduleList.init(this.positionX, this.positionY);
			this.valueList.init(this.positionX, this.positionY);
			this.commandBox.init(this.positionX, this.positionY);
			this.hasInited = true;
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//		this.setPositionX((sr.getScaledWidth_double() - 400.0) / 2);S
//		this.setPositionY((sr.getScaledHeight_double() - 300.0) / 2);

		this.wheel = Mouse.getDWheel() / 10;

		// Logger.debug(String.valueOf(wheel));

		this.wholeScreenScissor.setX(0);
		this.wholeScreenScissor.setY(0);
		this.wholeScreenScissor.setWidth(this.width);
		this.wholeScreenScissor.setHeight(this.height);
		this.wholeScreenScissor.doScissor();

		if (this.onCommandBox) {
			commandBox.draw(mouseX, mouseY, mouseY, partialTicks);
		}

		GL11.glEnable(GL11.GL_SCISSOR_TEST);

		mainWindow.draw(mouseX, mouseY, this.getPositionX(), this.getPositionY());
		moduleList.draw(mouseX, mouseY, this.getPositionX(), this.getPositionY());
		valueList.draw(mouseX, mouseY, this.getPositionX(), this.getPositionY());

		if (moduleList.getOnHover() != null) {
			String text = "绑定按键：" + Keyboard.getKeyName(moduleList.getOnHover().keyCode);
			if (moduleList.getOnHover().keyCode == 0) {
				text = "此模块尚未绑定按键. (中键点击进行绑定)";
			}
			int width = FontManager.client(16).getStringWidth(text);
            new Rect(mouseX, mouseY - 12, width + 4, 16, new Color(40, 40, 40, this.getAlpha()).getRGB(),
                    Rect.RenderType.Expand).draw();
            FontManager.client(16).drawString(text, mouseX + 2, mouseY - 8, -1);
        }

		this.setPositionX(mainWindow.getPositionX());
		this.setPositionY(mainWindow.getPositionY());

		GL11.glDisable(GL11.GL_SCISSOR_TEST);

		if (hasClosed) {
			alpha_animation.interpolate(0, 20);
		} else {
			alpha_animation.interpolate(255, 20);
		}
		this.alpha = (int) alpha_animation.getOpacity();
		if (this.alpha == 0) {
			this.mc.displayGuiScreen(null);

			if (this.mc.currentScreen == null) this.mc.setIngameFocus();
		}
	}


	@Override
	public void keyTyped(char typedChar, int keyCode) throws IOException {
		if (this.onBindingModule != null) {
            if (keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_DELETE) {
                this.onBindingModule.keyCode = 0;
				MessageHandler.addMessage(this.onBindingModule.cnName + "现在是未绑定的.", MessageHandler.MessageType.Info, 3000);
            } else {
                this.onBindingModule.keyCode = keyCode;
                MessageHandler.addMessage(this.onBindingModule.cnName
                        + " is now bound to \"" + Keyboard.getKeyName(keyCode) + "\".", MessageHandler.MessageType.Info, 3000);
            }
            this.onBindingModule = null;
			return;
		}

		if (keyCode == 1) {
			ConfigManager.saveConfig("Module.json");
			this.hasClosed = true;
			this.onCommandBox = false;
		}

		if (this.onCommandBox) {
			if (keyCode == Keyboard.KEY_RETURN) {
				this.onCommandBox = false;
			}
			this.commandBox.keyTyped(typedChar, keyCode);
		}

		// System.out.println(keyCode);
		if (keyCode == Keyboard.KEY_GRAVE || keyCode == Keyboard.CHAR_NONE) {
			this.onCommandBox = !this.onCommandBox;
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public boolean IsOnCommandBox() {
		return this.onCommandBox;
	}

	public void clickModulesButton(int id) {
		switch (id) {
		case 0:
			this.mainWindow.modules.setChosen(true);
			this.mainWindow.global.setChosen(false);
			break;
		case 1:
			this.mainWindow.modules.setChosen(false);
			this.mainWindow.global.setChosen(true);
			break;
		}
		this.moduleList.setTarY(0);
		this.moduleList.refreshList(id);
	}

	public int getColor(int type) {
		switch (ClickGUIModule.theme) {
		case Dark:
			switch (type) {
			case 0:
				return new Color(21, 21, 21, this.alpha).getRGB();// Menu
			case 1:
				return new Color(255, 255, 255, this.alpha).getRGB();// Top Title
			case 2:
				return new Color(21, 21, 21, this.alpha).getRGB();// Top
			case 3:
				return new Color(28, 28, 28, this.alpha).getRGB();// Main
			case 4:
				return new Color(200, 200, 200, this.alpha).getRGB();// Menu Icon off
			case 5:
				return new Color(255, 255, 255, this.alpha).getRGB();// Menu Icon on
			case 6:
				return new Color(28, 28, 28, this.alpha).getRGB();// Menu Frame
			case 7:
				return new Color(60, 81, 249, this.alpha).getRGB();// Menu Chosen Frame
			case 8:
				return new Color(21, 21, 21, this.alpha).getRGB();// ModuleList rect
			case 9:
				return new Color(255, 255, 255, this.alpha).getRGB();// ModuleList text
			case 10:
				return new Color(37, 38, 43, this.alpha).getRGB();// ModuleList rect focus
			case 11:
				return new Color(24, 24, 24, this.alpha).getRGB();// ModuleList setting
			case 12:
				return new Color(37, 38, 43, this.alpha).getRGB();// ModuleList setting focus
			case 13:
				return new Color(21, 21, 21, this.alpha).getRGB();// Module On Hover Rect
			case 14:
				return new Color(255, 255, 255, this.alpha).getRGB();// Module On Hover Text
			case 15:
				return new Color(32, 32, 32, this.alpha).getRGB();// Value List Menu
			case 16:
				return new Color(255, 255, 255, this.alpha).getRGB();// ModuleList settings
			case 17:
				return new Color(255, 255, 255, this.alpha).getRGB();// Value List Back
			case 18:
				return new Color(0, 111, 255, this.alpha).getRGB();// Value List back focus
			case 19:
				return new Color(255, 255, 255, this.alpha).getRGB();// Value List title
			case 20:
				return new Color(255, 255, 255, this.alpha).getRGB();// Value List Label
			case 21:
				return new Color(38, 38, 38, this.alpha).getRGB();// Boolean Value rect
			case 22:
				return new Color(44, 44, 44, this.alpha).getRGB();// Boolean Value focus
			case 23:
				return new Color(38, 38, 38, this.alpha).getRGB();// Enum Value rect
			case 24:
				return new Color(44, 44, 44, this.alpha).getRGB();// Enum Value focus
			case 25:
				return new Color(52, 52, 52, this.alpha).getRGB();// Number Value rect
			case 26:
				return new Color(255, 255, 255, this.alpha).getRGB();// Number Value rounded
			case 27:
				return new Color(0, 111, 255, this.alpha).getRGB();// Number Value rect value
			case 28:
				return new Color(32, 32, 32, this.alpha).getRGB();// Command Box Rect
			case 29:
				return new Color(255, 255, 255, this.alpha).getRGB();// Command Box Text
			}
		case Light:
			switch (type) {
			case 0:
				return new Color(236, 240, 241, this.alpha).getRGB();// Menu
			case 1:
				return new Color(23, 32, 42, this.alpha).getRGB();// Top Title
			case 2:
				return new Color(236, 240, 241, this.alpha).getRGB();// Top
			case 3:
				return new Color(240, 243, 244, this.alpha).getRGB();// Main
			case 4:
				return new Color(28, 40, 51, this.alpha).getRGB();// Menu Icon off
			case 5:
				return new Color(240, 243, 244, this.alpha).getRGB();// Menu Icon on
			case 6:
				return new Color(244, 246, 247, this.alpha).getRGB();// Menu Frame
			case 7:
				return new Color(52, 73, 94, this.alpha).getRGB();// Menu Chosen Frame
			case 8:
				return new Color(234, 236, 238, this.alpha).getRGB();// ModuleList rect
			case 9:
				return new Color(23, 32, 42, this.alpha).getRGB();// ModuleList text
			case 10:
				return new Color(244, 246, 246, this.alpha).getRGB();// ModuleList rect focus
			case 11:
				return new Color(213, 216, 220, this.alpha).getRGB();// ModuleList setting
			case 12:
				return new Color(214, 219, 223, this.alpha).getRGB();// ModuleList setting focus
			case 13:
				return new Color(234, 236, 238, this.alpha).getRGB();// Module On Hover Rect
			case 14:
				return new Color(23, 32, 42, this.alpha).getRGB();// Module On Hover Text
			case 15:
				return new Color(251, 252, 252, this.alpha).getRGB();// Value List Menu
			case 16:
				return new Color(23, 32, 42, this.alpha).getRGB();// ModuleList settings
			case 17:
				return new Color(23, 32, 42, this.alpha).getRGB();// Value List Back
			case 18:
				return new Color(0, 111, 255, this.alpha).getRGB();// Value List back focus
			case 19:
				return new Color(23, 32, 42, this.alpha).getRGB();// Value List title
			case 20:
				return new Color(23, 32, 42, this.alpha).getRGB();// Value List Label
			case 21:
				return new Color(234, 237, 237, this.alpha).getRGB();// Boolean Value rect
			case 22:
				return new Color(242, 243, 244, this.alpha).getRGB();// Boolean Value focus
			case 23:
				return new Color(242, 244, 244, this.alpha).getRGB();// Enum Value rect
			case 24:
				return new Color(244, 246, 246, this.alpha).getRGB();// Enum Value focus
			case 25:
				return new Color(213, 219, 219, this.alpha).getRGB();// Number Value rect
			case 26:
				return new Color(174, 182, 191, this.alpha).getRGB();// Number Value rounded
			case 27:
				return new Color(0, 111, 255, this.alpha).getRGB();// Number Value rect value
			case 28:
				return new Color(251, 252, 252, this.alpha).getRGB();// Command Box Rect
			case 29:
				return new Color(23, 32, 42, this.alpha).getRGB();// Command Box Text
			}
		}
		return 0;
	}

	public String getResource(int type) {
		switch (ClickGUIModule.theme) {
		case Dark:
            if (type == 0) {
                return "d";
            }
		case Light:
		}
		return "";
	}
}
