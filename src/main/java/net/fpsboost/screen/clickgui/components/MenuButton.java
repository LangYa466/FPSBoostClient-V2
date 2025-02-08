package net.fpsboost.screen.clickgui.components;

import net.fpsboost.screen.clickgui.utils.*;
import lombok.Getter;
import lombok.Setter;
import net.fpsboost.util.font.FontManager;

import java.awt.*;

public class MenuButton extends UIComponent {
    private ClickEntity clickentity;
    private final String icon;
    private final String text;
    @Setter
    @Getter
    private double x;
    @Setter
    @Getter
    private double y;
    @Setter
    @Getter
    private boolean chosen;
    private final int id;

    private final Translate anim = new Translate(0, 0);
    private final Translate alpha = new Translate(0, 0);

    private RoundedRect frame;
    private RoundedRect chosenFrame;

    public MenuButton(int id, String icon, String text, double x, double y, boolean chosen) {
        super();
        this.id = id;
        this.icon = icon;
        this.text = text;
        this.x = x;
        this.y = y;
        this.chosen = chosen;
    }

    @Override
    public void init(double positionX, double positionY) {
        this.frame = new RoundedRect(positionX - 10, positionY - 10, 40, 40, 4, this.getColor(6),
                RoundedRect.RenderType.Expand);
        this.chosenFrame = new RoundedRect(positionX + 10, positionY + 10, 0, 0, 4, this.getColor(7),
                RoundedRect.RenderType.Expand);
        this.clickentity = new ClickEntity(positionX - 10, positionY - 10, 40, 40, MouseBounds.CallType.Expand, () -> {
            this.clickModulesButton(id);
        }, () -> {
        }, () -> {
            if (!chosen) {
                this.frame.draw();
            }
        }, () -> {
        }, () -> {
        });

    }

    @Override
    public void draw(int mouseX, int mouseY, double positionX, double positionY) {
        this.clickentity.setX(positionX - 10);
        this.clickentity.setY(positionY - 10);
        this.clickentity.tick();

        this.frame.setColor(this.getColor(6));
        this.frame.setX(positionX - 10);
        this.frame.setY(positionY - 10);

        if (chosen) {
            anim.interpolate(15, 0, .25F);
            alpha.interpolate(255, 0, .15F);
        } else {
            anim.interpolate(0, 0, .25F);
            alpha.interpolate(0, 0, .15F);
        }

        int color = this.getColor(7);

        int red = (color >> 16 & 255);
        int green = (color >> 8 & 255);
        int blue = (color & 255);

        final Color orgColor = new Color(red, green, blue, (int) Math.min(Math.round(alpha.getX()), (color >> 24 & 255)));

        chosenFrame.setColor(orgColor.getRGB());
        chosenFrame.setX(positionX + 5 - anim.getX());
        chosenFrame.setY(positionY + 5 - anim.getX());
        chosenFrame.setWidth(10 + anim.getX() * 2);
        chosenFrame.setHeight(10 + anim.getX() * 2);
        chosenFrame.draw();

        FontManager.logo(42).drawString(icon, positionX, positionY,
                this.isChosen() ? this.getColor(5) : this.getColor(4));
        FontManager.client(14).drawString(text, positionX + 10 - FontManager.client(14).getStringWidth(text) / 2F, positionY + 21.5F,
                this.isChosen() ? this.getColor(5) : this.getColor(4));
    }
}
