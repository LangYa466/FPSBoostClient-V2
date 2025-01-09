package net.fpsboost.screen.clickgui.components.values;

import net.fpsboost.screen.clickgui.utils.*;
import lombok.Getter;
import lombok.Setter;
import net.fpsboost.util.font.FontManager;
import net.fpsboost.value.impl.ModeValue;
import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author LangYa466
 * @since 2025/1/4
 */
public class StringArrayEntity extends ValueEntity {

    Translate animation;

    private Scissor scissors;
    private ClickableRect rect;
    @Setter
    @Getter
    private ClickEntity clickarea;
    private boolean clickable;

    private boolean isOpen;

    private List<StringButton> values;

    private final FontRenderer fr = FontManager.client(14);

    public StringArrayEntity(ModeValue value) {
        super(value);
    }

    @Override
    public void init(double positionX, double positionY) {
        try {


        this.animation = new Translate(30
                + 2, 10);
        this.isOpen = false;
        this.scissors = new Scissor(0, 0, 0, 0);
        this.clickarea = new ClickEntity(0, 0, 0, 0, MouseBounds.CallType.Expand, () -> {
        }, () -> {
        }, () -> this.clickable = true, () -> {
        }, () -> this.clickable = false);
        this.rect = new ClickableRect(0, 0, 0, 0, this.getColor(23), Rect.RenderType.Expand, () -> {
            if (!this.isClickable()) return;
            this.isOpen = !this.isOpen;
        }, () -> {
        }, () -> this.rect.setColor(this.getColor(24)), () -> {
        }, () -> this.rect.setColor(this.getColor(23)));
        this.values = new ArrayList<>();
        for (String e : this.getValue().modes) {
            values.add(new StringButton(this, e));
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, double positionX, double positionY) {
        fr.drawString(value.cnName, positionX, positionY + 4, this.getColor(20));
        int width = fr.getStringWidth(this.getValue().getValue()) + 2;

        this.scissors.setX(positionX + 131 - this.animation.getX());
        this.scissors.setY(Math.max(positionY, this.getClickGui().getValueList().getClickarea().getY()));

        double diff = positionY - this.getClickGui().getValueList().getClickarea().getY();

        this.scissors.setWidth(Math.max(Math.min(this.animation.getX(),
                this.getClickGui().getValueList().getRectanim().getX() - (150 - this.animation.getX())), 0));
        this.scissors.setHeight(Math.max(diff < 0 ? this.animation.getY() + diff : this.animation.getY(), 0));
        this.scissors.doScissor();

        this.clickarea.setX(positionX + 131 - this.animation.getX());
        this.clickarea.setY(Math.max(positionY, this.getClickGui().getValueList().getClickarea().getY()));
        this.clickarea.setX1(Math.max(Math.min(this.animation.getX(),
                this.getClickGui().getValueList().getRectanim().getX() - (150 - this.animation.getX())), 0));
        this.clickarea.setY1(Math.max(Math.min(this.animation.getY(), this.animation.getY()), 0));
        this.clickarea.tick();

        this.rect.setX(positionX + 131 - this.getWidth());
        this.rect.setY(positionY);
        this.rect.setWidth(this.getWidth());
        this.rect.setHeight(15);
        this.rect.draw();

        fr.drawString(this.getValue().getValue(), positionX + 132 - (getWidth() / 2F + width / 2F), positionY + 3.5, this.getColor(20));
        int y = 15;
        for (StringButton button : values) {
            if (button.getValue().equals(this.getValue().getValue()))
                continue;
            button.draw(mouseX, mouseY, positionX + 131 - this.getWidth(), positionY + y);
            y += 15;
        }

        if (this.isOpen) {
            this.animation.interpolate(this.getWidth(), values.size() * 15, 0.3f);
        } else {
            this.animation.interpolate(this.getWidth(), 15, 0.3f);
        }

        this.getClickGui().getWholeScreenScissor().doScissor();
    }

    public int getWidth() {
        int width = fr.getStringWidth(this.getValue().getValue()) + 2;
        return this.isOpen ? this.getLongestWidth() + 2 : width;
    }

    public int getLongestWidth() {
        List<Integer> nums = new ArrayList<>();
        for (String e : this.getValue().modes) {
            nums.add(fr.getStringWidth(e));
        }
        return Collections.max(nums);
    }

    @Override
    public double getHeight() {
        return this.animation.getY() - 2;
    }

    @Override
    public ModeValue getValue() {
        return (ModeValue) super.getValue();
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    @Override
    protected boolean isClickable() {
        return super.isClickable() && this.clickable;
    }
}
