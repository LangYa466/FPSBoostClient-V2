package net.fpsboost.screen.clickgui.components.values;

import net.fpsboost.screen.clickgui.ClickGui;
import net.fpsboost.screen.clickgui.utils.ClickEntity;
import net.fpsboost.screen.clickgui.utils.MouseBounds;
import net.fpsboost.util.ClientInputGUI;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.util.font.FontManager;
import net.fpsboost.value.impl.TextValue;
import net.minecraft.client.gui.FontRenderer;

/**
 * @author LangYa466
 * @since 2025/1/9
 */
public class TextEntity extends ValueEntity {
    public TextEntity(TextValue value) {
        super(value);
    }
    private ClickEntity rect;

    @Override
    public void init(double positionX, double positionY) {
        this.rect = new ClickEntity(positionX + 180, positionY + 2, 50, 10, MouseBounds.CallType.Expand, () -> {
            if (!this.isClickable())
                return;
            final ClientInputGUI[] clientInputGUIHolder = new ClientInputGUI[1];
            clientInputGUIHolder[0] = new ClientInputGUI(
                    mc.displayWidth / 4,
                    mc.displayHeight / 4,
                    getValue().getValue(),
                    () -> {
                        if (clientInputGUIHolder[0] != null) {
                            getValue().setValue(clientInputGUIHolder[0].text);
                        }
                    }
            );
            ClientInputGUI clientInputGUI = clientInputGUIHolder[0];

            mc.displayGuiScreen(clientInputGUI);
        }, () -> {
        }, () -> {
        }, () -> {
        }, () -> {
        });
    }

    @Override
    public void draw(int mouseX, int mouseY, double positionX, double positionY) {
        this.rect.setX(positionX + 120);
        this.rect.setY(positionY + 2);
        this.rect.tick();
        FontManager.client(14).drawString(value.cnName, positionX, positionY + 4,
                this.getColor(20));

        String v = getValue().getValue();
        FontRenderer fr = FontManager.client(16);
        int width = fr.getStringWidth(v);
        int textX = 60;
        RenderUtil.drawRect((int) positionX + textX, (int) positionY, width, 10, this.getColor(21));
        fr.drawString(v, positionX + textX, positionY + 2, this.getColor(20));
    }

    @Override
    public double getHeight() {
        return 8;
    }

    @Override
    public TextValue getValue() {
        return (TextValue) super.getValue();
    }
}
