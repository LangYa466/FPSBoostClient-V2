package net.fpsboost.screen.clickgui.components.values;

import lombok.Getter;
import lombok.Setter;
import net.fpsboost.screen.clickgui.utils.ClickableRect;
import net.fpsboost.screen.clickgui.utils.Rect;
import net.fpsboost.screen.clickgui.utils.UIComponent;
import net.fpsboost.util.font.FontManager;
import net.minecraft.client.gui.FontRenderer;

/**
 * @author LangYa466
 * @since 2025/1/4
 */
public class StringButton extends UIComponent {

    @Setter
    @Getter
    ClickableRect rect;
    private final StringArrayEntity values;
    @Getter
    @Setter
    private String value;
    private final FontRenderer fr = FontManager.client(14);

    // 构造函数：初始化按钮并设置点击事件
    public StringButton(StringArrayEntity values, String value) {
        super();
        this.values = values;
        this.value = value;
        this.rect = new ClickableRect(0, 0, 0, 0, this.getColor(23), Rect.RenderType.Expand, () -> {
            if (!this.values.isClickable())
                return;
            this.values.setLocked();
            this.values.getValue().setValue(this.value);  // 设置当前值为按钮值
            this.values.setOpen(false);  // 关闭菜单
        }, () -> {
        }, () -> {
            this.rect.setColor(this.getColor(24));
        }, () -> {
        }, () -> {
            this.rect.setColor(this.getColor(23));
        });
    }

    @Override
    public void draw(int mouseX, int mouseY, double positionX, double positionY) {
        // 更新按钮的位置和大小
        this.rect.setX(positionX);
        this.rect.setY(positionY);
        this.rect.setWidth(this.values.getWidth());
        this.rect.setHeight(10);
        this.rect.draw();  // 绘制按钮

        // 绘制按钮文本
        int width = fr.getStringWidth(value) + 2;
        fr.drawString(value,
                positionX + 1 + (this.values.getWidth() / 2F - width / 2F), positionY + 3.5, this.getColor(20));
    }
}
