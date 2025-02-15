package net.fpsboost.screen.newClickGUI;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

/**
 * @author LangYa466
 * @since 2/15/2025
 */
public class SearchBox {
    @Setter
    private int x, y, width, height;
    @Getter
    private String text = "";
    private boolean focused = false;
    private FontRenderer fontRenderer;

    // 控制光标闪烁的参数
    private long lastCursorUpdate;
    private boolean cursorVisible = true;

    // 新增光标和选区相关变量
    // 光标位置：0~text.length()（注意：当text为空时，值为0）
    private int cursorPosition = 0;
    // 选区锚点和结束位置（若两者相等，则没有选中任何内容）
    private int selectionStart = 0;
    private int selectionEnd = 0;

    public SearchBox(int x, int y, int width, int height, FontRenderer fontRenderer) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.fontRenderer = fontRenderer;
        this.lastCursorUpdate = System.currentTimeMillis();
    }

    /**
     * 绘制搜索框（背景、边框、文本、选区高亮以及闪烁光标）
     */
    public void draw() {
        // 更新光标闪烁状态（500毫秒闪烁一次）
        int cursorBlinkRate = 500;
        if (System.currentTimeMillis() - lastCursorUpdate >= cursorBlinkRate) {
            cursorVisible = !cursorVisible;
            lastCursorUpdate = System.currentTimeMillis();
        }

        // 绘制背景
        GuiScreen.drawRect(x, y, x + width, y + height, 0xFF111111);
        // 绘制边框（上、下、左、右）
        GuiScreen.drawRect(x, y, x + width, y + 1, 0xFF000000);               // 上边框
        GuiScreen.drawRect(x, y + height - 1, x + width, y + height, 0xFF000000); // 下边框
        GuiScreen.drawRect(x, y, x + 1, y + height, 0xFF000000);              // 左边框
        GuiScreen.drawRect(x + width - 1, y, x + width, y + height, 0xFF000000);  // 右边框

        int textX = x + 4;
        int fontHeight = fontRenderer.getHeight();
        int textY = y + (height - fontHeight) / 2;
        String displayText = text;

        // 绘制选区高亮（仅当处于焦点状态且有选中内容时绘制）
        if (focused && selectionStart != selectionEnd) {
            int selStart = Math.min(selectionStart, selectionEnd);
            int selEnd = Math.max(selectionStart, selectionEnd);
            int selXStart = textX + fontRenderer.getStringWidth(displayText.substring(0, selStart));
            int selXEnd = textX + fontRenderer.getStringWidth(displayText.substring(0, selEnd));
            // 使用半透明蓝色作为选区背景
            GuiScreen.drawRect(selXStart, textY, selXEnd, textY + fontHeight, 0x803399FF);
        }

        // 绘制文本
        fontRenderer.drawStringWithShadow(displayText, textX, textY, 0xFFFFFFFF);

        // 绘制光标（仅当处于焦点状态且光标闪烁为可见时绘制）
        if (focused && cursorVisible) {
            int cursorX = textX + fontRenderer.getStringWidth(displayText.substring(0, cursorPosition));
            GuiScreen.drawRect(cursorX, textY, cursorX + 1, textY + fontHeight, 0xFFFFFFFF);
        }
    }

    /**
     * 处理鼠标点击，判断是否获得焦点，
     * 同时点击时可更新光标位置（这里简单设为文本末尾）
     * @param mouseX 鼠标 X 坐标
     * @param mouseY 鼠标 Y 坐标
     * @return 如果点击在搜索框内，则返回 true
     */
    public boolean mouseClicked(int mouseX, int mouseY) {
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            focused = true;
            // 简单处理：点击时将光标置于文本末尾，并取消选区
            cursorPosition = text.length();
            selectionStart = selectionEnd = cursorPosition;
        } else {
            focused = false;
        }
        return focused;
    }

    /**
     * 处理键盘输入（仅当搜索框处于焦点状态时处理）
     * 支持：
     * - Ctrl+A 全选文本
     * - 左右箭头（203：左，205：右）移动光标（支持 Shift 扩展选区）
     * - 退格键（14）删除字符或选区
     * - 回车/ESC 取消焦点
     * - 其他合法字符输入（插入到光标位置，若存在选区则先替换选区）
     */
    public void keyTyped(char typedChar, int keyCode) {
        if (!focused) {
            return;
        }
        // 处理左右箭头键（203: 左箭头，205: 右箭头）
        if (keyCode == 203) { // 左箭头
            if (GuiScreen.isShiftKeyDown()) {
                // Shift+左箭头：扩展选区
                if (cursorPosition > 0) {
                    cursorPosition--;
                    selectionEnd = cursorPosition;
                }
            } else {
                // 不按 Shift：移动光标并取消选区
                if (cursorPosition != selectionEnd) {
                    // 当有选区时，按左键则将光标定位到选区的起始端
                    cursorPosition = Math.min(selectionStart, selectionEnd);
                } else if (cursorPosition > 0) {
                    cursorPosition--;
                }
                selectionStart = selectionEnd = cursorPosition;
            }
            return;
        }
        if (keyCode == 205) { // 右箭头
            if (GuiScreen.isShiftKeyDown()) {
                // Shift+右箭头：扩展选区
                if (cursorPosition < text.length()) {
                    cursorPosition++;
                    selectionEnd = cursorPosition;
                }
            } else {
                // 不按 Shift：移动光标并取消选区
                if (cursorPosition != selectionEnd) {
                    // 当有选区时，按右键则将光标定位到选区的末尾
                    cursorPosition = Math.max(selectionStart, selectionEnd);
                } else if (cursorPosition < text.length()) {
                    cursorPosition++;
                }
                selectionStart = selectionEnd = cursorPosition;
            }
            return;
        }

        // 处理退格键（keyCode 14）
        if (keyCode == 14) {
            if (selectionStart != selectionEnd) {
                // 如果有选区，则删除选中部分
                int start = Math.min(selectionStart, selectionEnd);
                int end = Math.max(selectionStart, selectionEnd);
                text = text.substring(0, start) + text.substring(end);
                cursorPosition = start;
            } else if (cursorPosition > 0) {
                // 删除光标前一个字符
                text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                cursorPosition--;
            }
            selectionStart = selectionEnd = cursorPosition;
            return;
        }

        // 回车或 ESC 取消焦点（keyCode 28, 156, 1）
        if (keyCode == 28 || keyCode == 156 || keyCode == 1) {
            focused = false;
            return;
        }

        // 处理其他合法字符输入
        if (isAllowedCharacter(typedChar)) {
            int maxLength = 8;
            // 若存在选区，则先删除选区内容
            if (selectionStart != selectionEnd) {
                int start = Math.min(selectionStart, selectionEnd);
                int end = Math.max(selectionStart, selectionEnd);
                text = text.substring(0, start) + text.substring(end);
                cursorPosition = start;
            }
            // 判断插入字符后是否超出最大长度
            if (text.length() < maxLength) {
                text = text.substring(0, cursorPosition) + typedChar + text.substring(cursorPosition);
                cursorPosition++;
            }
            selectionStart = selectionEnd = cursorPosition;
        }
    }

    /**
     * 判断字符是否合法（允许字母、数字、空格和常见标点）
     */
    private boolean isAllowedCharacter(char c) {
        return Character.isLetterOrDigit(c) || Character.isSpaceChar(c) || "!@#$%^&*()_+-=[]{}|;':\",./<>?".indexOf(c) >= 0;
    }

    /**
     * 设置搜索框的位置
     */
    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }
}
