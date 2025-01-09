package net.fpsboost.screen.clickgui.components;

import net.fpsboost.screen.clickgui.utils.Rect;
import net.fpsboost.screen.clickgui.utils.UIComponent;
import net.fpsboost.util.font.FontManager;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class CommandBox extends UIComponent {

	StringBuilder commands;

	@Override
	public void init(double positionX, double positionY) {
		this.commands = new StringBuilder();
	}

	@Override
	public void draw(int mouseX, int mouseY, double positionX, double positionY) {
		String text;
		int width;
		text = this.getText();
		width = FontManager.arial(16).getStringWidth(text);

		int diff = 2;
		if (width > diff)
			diff = width;

		new Rect(this.getScaledWidth() - 3 - diff, this.getScaledHeight() - 10, diff, 8, this.getColor(28),
				Rect.RenderType.Expand).draw();
		FontManager.arial(16).drawString(text, this.getScaledWidth() - 3 - width,
				this.getScaledHeight() - 8, this.getColor(29));
	}

	public void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 14) {
			this.deleteText();
			return;
		}
		if (keyCode == Keyboard.KEY_GRAVE) {
			this.commands = new StringBuilder();
			return;
		}
		if (keyCode == Keyboard.KEY_RETURN) {
			dispatch(this.getText());
			this.commands = new StringBuilder();
			return;
		}
		if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
			this.addText(Character.toString(typedChar));
		}
	}

	public void dispatch(String s) {
		/*
		String[] command = s.split(" ");
		if (command.length > 0) {
			boolean hasRun = false;
			for (Command c : this.getCommandMap().values()) {
				for (String handle : c.getHandles()) {
					if (handle.equalsIgnoreCase(command[0])) {
						c.onRun(command);
						hasRun = true;
					}
				}
			}
			if (!hasRun) {
				Tritium.instance.getMessagemanager().addMessage("That command does not exist.", MessageType.Error,
						3000);
			}
		} else {
			Tritium.instance.getMessagemanager().addMessage("You can't do it without anything...", MessageType.Error,
					3000);
		}

		 */
	}

	public void addText(String text) {
		this.commands.append(text);
	}

	public void deleteText() {
		if (this.commands.length() > 0)
			this.commands.deleteCharAt(this.commands.length() - 1);
	}

	public String getText() {
		return this.commands.toString();
	}
}
