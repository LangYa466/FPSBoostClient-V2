package net.fpsboost.screen.newClickGUI;

import lombok.val;
import net.fpsboost.element.ElementManager;
import net.fpsboost.module.Module;
import net.fpsboost.module.ModuleManager;
import net.fpsboost.screen.clickgui.utils.Scissor;
import net.fpsboost.screen.clickgui.utils.Translate;
import net.fpsboost.util.HoveringUtil;
import net.fpsboost.util.font.FontManager;
import net.fpsboost.value.Value;
import net.fpsboost.value.impl.*;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;

/**
 * @author LangYa466
 * @since 2025/2/13
 */
public class NewClickGUIScreen extends GuiScreen {
    public static final NewClickGUIScreen INSTANCE = new NewClickGUIScreen();
    private final Translate translate = new Translate(0, 0);
    private double scrollY = 0;
    private final int moduleHeight = 20;
    private final int padding = 5;
    private final int viewHeight = 200;
    private final FontRenderer fontRenderer = FontManager.client();
    private int guiWidth = 200;
    private int guiHeight = 250; // 增加高度，给分类按钮留空间
    private int startX, startY;

    // 拖动窗口相关
    private boolean dragging = false;
    private int dragX, dragY;

    // 选中的分类
    private boolean isCategoryOne = true;

    // 模块 Values 面板相关变量
    private Module activeModuleValues = null;
    private boolean draggingValues = false;
    private int valuesDragOffsetX, valuesDragOffsetY;
    private int valuesPanelX, valuesPanelY;
    private final int valuesPanelWidth = 150;
    private final int valuesPanelHeight = 100;
    private boolean init;

    @Override
    public void initGui() {
        if (init) return;
        startX = (width - guiWidth) / 2;
        startY = (height - guiHeight) / 2;
        init = true;
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (dragging) {
            startX = mouseX - dragX;
            startY = mouseY - dragY;
        }
        // 更新 Values 面板拖动时的位置
        if (activeModuleValues != null && draggingValues) {
            valuesPanelX = mouseX - valuesDragOffsetX;
            valuesPanelY = mouseY - valuesDragOffsetY;
        }

        translate.interpolate(0, scrollY, 0.2f);
        double offsetY = translate.getY();

        // 获取当前要显示的模块列表
        List<? extends Module> currentModules = isCategoryOne ? ModuleManager.modules : ElementManager.elements;

        // 绘制窗口背景
        drawRect(startX, startY, startX + guiWidth, startY + guiHeight, 0xCC000000);

        // 绘制标题栏
        drawRect(startX, startY, startX + guiWidth, startY + 20, 0xFF222222);
        fontRenderer.drawStringWithShadow("FPSBoost-V2", startX + 10, startY + 6, 0xFFFFFFFF);

        // 绘制分类切换按钮
        int categoryBtnWidth = guiWidth / 2;
        drawRect(startX, startY + 20, startX + categoryBtnWidth, startY + 40, isCategoryOne ? 0xFF4444AA : 0xFF222222);
        drawRect(startX + categoryBtnWidth, startY + 20, startX + guiWidth, startY + 40, isCategoryOne ? 0xFF222222 : 0xFF4444AA);
        fontRenderer.drawStringWithShadow("辅助功能", startX + 10, startY + 26, isCategoryOne ? 0xFFFFFFFF : 0x999999);
        fontRenderer.drawStringWithShadow("GUI功能", startX + categoryBtnWidth + 10, startY + 26, isCategoryOne ? 0x999999 : 0xFFFFFFFF);

        // 开启裁剪，限制滑动区域
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        new Scissor(startX, startY + 40, guiWidth, viewHeight).doScissor();

        int currentY = startY + 50; // 模块从分类按钮下方开始绘制
        for (Module module : currentModules) {
            boolean hovered = HoveringUtil.isHovering(startX + 10, (float) (currentY + offsetY), guiWidth - 20, moduleHeight, mouseX, mouseY);
            int color = module.enable ? 0x8800FF00 : 0x88FF0000;
            if (hovered) color = 0x99FFFFFF;

            drawRect(startX + 10, (int) (currentY + offsetY), startX + guiWidth - 10, (int) (currentY + offsetY + moduleHeight), color);
            val cnDescription = module.cnDescription;
            if (cnDescription.isEmpty()) {
                fontRenderer.drawStringWithShadow(module.cnName, startX + 15, (int) (currentY + offsetY + 6), -1);
            } else {
                fontRenderer.drawStringWithShadow(String.format("%s(%s)", module.cnName, cnDescription), startX + 15, (int) (currentY + offsetY + 6), -1);
            }
            currentY += moduleHeight + padding;
        }

        // 关闭裁剪
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        // 绘制模块 Values 面板（如果已打开）
        // 绘制模块 Values 面板（如果已打开）
        if (activeModuleValues != null) {
            // 定义标题栏高度、内边距和行高参数
            int headerHeight = 20;
            int paddingTop = 5;
            int paddingBottom = 5;
            // 根据字体高度加上额外空间确定行高，保证“+”和“-”按钮能够完整显示
            int rowHeight = Math.max(15, fontRenderer.getHeight() + 4);

            // 计算标题栏文字宽度
            String headerText = "设置: " + activeModuleValues.cnName;
            int headerTextWidth = fontRenderer.getStringWidth(headerText);
            // 计算关闭按钮所需宽度并预留一定间距
            int closeButtonWidth = fontRenderer.getStringWidth("X") + 10;

            // -------------------------------
            // 预先计算每一行内容所需的最大宽度
            // -------------------------------
            int computedMaxValueWidth = 0;
            for (Value<?> valueObj : activeModuleValues.values) {
                // 从左侧5像素空白开始
                int lineWidth = 5;
                // 加上值名称及后面的间隔
                int nameWidth = fontRenderer.getStringWidth(valueObj.cnName);
                lineWidth += nameWidth + 5;
                // 根据不同类型添加后续控件的宽度
                if (valueObj instanceof BooleanValue) {
                    BooleanValue boolValue = (BooleanValue) valueObj;
                    String stateText = boolValue.getValue() ? "开启" : "关闭";
                    int stateTextWidth = fontRenderer.getStringWidth(stateText);
                    lineWidth += stateTextWidth + 5; // 再加右侧间距
                } else if (valueObj instanceof NumberValue) {
                    NumberValue numValue = (NumberValue) valueObj;
                    String numText = String.valueOf(numValue.getValue());
                    int numTextWidth = fontRenderer.getStringWidth(numText);
                    String minusSymbol = "-";
                    int btnWidth = fontRenderer.getStringWidth(minusSymbol);
                    // 数值行宽 = 左边空白+名称+间隔 + 数值文本 + 左侧按钮间距（10）+ 按钮宽度 + 右侧间距（5）
                    lineWidth += numTextWidth + 10 + btnWidth + 5;
                } else if (valueObj instanceof ModeValue) {
                    ModeValue modeValue = (ModeValue) valueObj;
                    String modeText = modeValue.getValue();
                    int modeTextWidth = fontRenderer.getStringWidth(modeText);
                    // 定义左右箭头，用于切换模式
                    String leftArrow = "<";
                    String rightArrow = ">";
                    int leftArrowWidth = fontRenderer.getStringWidth(leftArrow);
                    int rightArrowWidth = fontRenderer.getStringWidth(rightArrow);
                    // 行宽 = 左箭头 + 间隔 + 模式文本 + 间隔 + 右箭头 + 间距
                    lineWidth += leftArrowWidth + 5 + modeTextWidth + 5 + rightArrowWidth + 5;
                } else if (valueObj instanceof TextValue) {
                    TextValue textValue = (TextValue) valueObj;
                    String text = textValue.getValue();
                    int textWidth = fontRenderer.getStringWidth(text);
                    lineWidth += textWidth + 5;
                } else if (valueObj instanceof ColorValue) {
                    ColorValue colorValue = (ColorValue) valueObj;
                    // 为颜色值预留：颜色小块（20像素宽）+ 间隔+ 十六进制文本
                    int boxWidth = 20;
                    String hexColor = "0x" + Integer.toHexString(colorValue.getValueC()).toUpperCase();
                    int hexTextWidth = fontRenderer.getStringWidth(hexColor);
                    lineWidth += boxWidth + 5 + hexTextWidth + 5;
                }
                computedMaxValueWidth = Math.max(computedMaxValueWidth, lineWidth);
            }

            // 面板宽度至少需要容纳标题和关闭按钮，或内容区域的最大宽度，左右各预留5像素
            int calculatedWidth = Math.max(headerTextWidth + closeButtonWidth + 10, computedMaxValueWidth);
            int adaptivePanelWidth = Math.max(100, calculatedWidth);

            // 根据值的数量计算内容区域高度
            int valuesCount = activeModuleValues.values != null ? activeModuleValues.values.size() : 0;
            int contentHeight = valuesCount * rowHeight;
            int adaptivePanelHeight = Math.max(100, headerHeight + paddingTop + contentHeight + paddingBottom);

            // 绘制面板背景和标题栏背景
            drawRect(valuesPanelX, valuesPanelY, valuesPanelX + adaptivePanelWidth, valuesPanelY + adaptivePanelHeight, 0xCC000000);
            drawRect(valuesPanelX, valuesPanelY, valuesPanelX + adaptivePanelWidth, valuesPanelY + headerHeight, 0xFF222222);

            // 绘制标题文字和关闭按钮
            fontRenderer.drawStringWithShadow(headerText, valuesPanelX + 5, valuesPanelY + 5, 0xFFFFFFFF);
            fontRenderer.drawStringWithShadow(
                    "X",
                    valuesPanelX + adaptivePanelWidth - 15,
                    valuesPanelY + 5,
                    HoveringUtil.isHovering(
                            valuesPanelX + adaptivePanelWidth - 15,
                            valuesPanelY + 5,
                            fontRenderer.getStringWidth("X"),
                            fontRenderer.getHeight(),
                            mouseX,
                            mouseY
                    ) ? 0xFFFFFF00 : 0xFFFF0000
            );

            // -------------------------------
            // 绘制每一行的值
            // -------------------------------
            int valueY = valuesPanelY + headerHeight + paddingTop;
            for (Value<?> valueObj : activeModuleValues.values) {
                // 绘制当前行背景
                drawRect(valuesPanelX, valueY, valuesPanelX + adaptivePanelWidth, valueY + rowHeight, 0xFF111111);
                int textY = valueY + (rowHeight - fontRenderer.getHeight()) / 2;
                // 绘制值的名称
                fontRenderer.drawStringWithShadow(valueObj.cnName, valuesPanelX + 5, textY, 0xFFFFFFFF);

                // 计算名称的宽度，后续控件绘制从名称后开始
                int nameWidth = fontRenderer.getStringWidth(valueObj.cnName);
                int offsetX = valuesPanelX + 5 + nameWidth + 5; // 名称+5像素间隔

                if (valueObj instanceof BooleanValue) {
                    BooleanValue boolValue = (BooleanValue) valueObj;
                    String stateText = boolValue.getValue() ? "开启" : "关闭";
                    fontRenderer.drawStringWithShadow(stateText, offsetX, textY, boolValue.getValue() ? 0xFF00FF00 : 0xFFFF0000);
                } else if (valueObj instanceof NumberValue) {
                    NumberValue numValue = (NumberValue) valueObj;
                    String numText = String.valueOf(numValue.getValue());
                    int numTextWidth = fontRenderer.getStringWidth(numText);
                    String minusSymbol = "-";
                    String plusSymbol = "+";
                    int btnWidth = fontRenderer.getStringWidth(minusSymbol);
                    // 确保 offsetX 至少有足够空间展示“-”按钮
                    int minOffsetX = valuesPanelX + btnWidth + 10;
                    int currentOffsetX = Math.max(offsetX, minOffsetX);
                    // 绘制数字文本从 currentOffsetX 开始
                    fontRenderer.drawStringWithShadow(numText, currentOffsetX, textY, 0xFFFFFFFF);
                    // 绘制“-”按钮在左侧，"+”按钮在右侧
                    int minusX = currentOffsetX - btnWidth - 10;
                    int plusX  = currentOffsetX + numTextWidth + 10;
                    fontRenderer.drawStringWithShadow(minusSymbol, minusX, textY, 0xFFFFFFFF);
                    fontRenderer.drawStringWithShadow(plusSymbol, plusX, textY, 0xFFFFFFFF);
                } else if (valueObj instanceof ModeValue) {
                    ModeValue modeValue = (ModeValue) valueObj;
                    String modeText = modeValue.getValue();
                    int modeTextWidth = fontRenderer.getStringWidth(modeText);
                    String leftArrow = "<";
                    String rightArrow = ">";
                    int leftArrowWidth = fontRenderer.getStringWidth(leftArrow);
                    // 绘制左箭头
                    fontRenderer.drawStringWithShadow(leftArrow, offsetX, textY, 0xFFFFFFFF);
                    // 绘制模式文本
                    int modeTextX = offsetX + leftArrowWidth + 5;
                    fontRenderer.drawStringWithShadow(modeText, modeTextX, textY, 0xFFFFFFFF);
                    // 绘制右箭头
                    int rightArrowX = modeTextX + modeTextWidth + 5;
                    fontRenderer.drawStringWithShadow(rightArrow, rightArrowX, textY, 0xFFFFFFFF);
                } else if (valueObj instanceof TextValue) {
                    TextValue textValue = (TextValue) valueObj;
                    String text = textValue.getValue();
                    fontRenderer.drawStringWithShadow(text, offsetX, textY, 0xFFFFFFFF);
                } else if (valueObj instanceof ColorValue) {
                    ColorValue colorValue = (ColorValue) valueObj;
                    // 获取颜色的整型值（含Alpha）
                    int colorInt = colorValue.getValueC();
                    // 定义颜色小块的尺寸
                    int boxWidth = 20;
                    int boxHeight = fontRenderer.getHeight();
                    // 绘制颜色块
                    drawRect(offsetX, textY, offsetX + boxWidth, textY + boxHeight, colorInt);
                    // 绘制颜色块边框（黑色）
                    drawRect(offsetX, textY, offsetX + boxWidth, textY + boxHeight, 0xFF000000);
                    // 绘制颜色的十六进制字符串表示
                    String hexColor = "0x" + Integer.toHexString(colorInt).toUpperCase();
                    fontRenderer.drawStringWithShadow(hexColor, offsetX + boxWidth + 5, textY, 0xFFFFFFFF);
                }
                valueY += rowHeight;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void handleMouseInput() throws IOException {
        int delta = Mouse.getEventDWheel();
        if (delta != 0) {
            scrollY += (delta > 0 ? 20 : -20);
            List<? extends Module> currentModules = isCategoryOne ? ModuleManager.modules : ElementManager.elements;
            int maxScroll = Math.max(0, (moduleHeight + padding) * currentModules.size() - viewHeight);
            if (scrollY > 0) scrollY = 0;
            if (scrollY < -maxScroll) scrollY = -maxScroll;
        }
        super.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        // 处理 Values 面板的拖动与关闭（左键点击面板标题栏区域）
        if (mouseButton == 0 && activeModuleValues != null
                && HoveringUtil.isHovering(valuesPanelX, valuesPanelY, valuesPanelWidth, 20, mouseX, mouseY)) {
            // 检测是否点击了关闭按钮（面板右上角区域）
            if (HoveringUtil.isHovering(valuesPanelX + valuesPanelWidth - 20, valuesPanelY, 20, 20, mouseX, mouseY)) {
                activeModuleValues = null;
                return;
            }
            draggingValues = true;
            valuesDragOffsetX = mouseX - valuesPanelX;
            valuesDragOffsetY = mouseY - valuesPanelY;
            return;
        }

        if (mouseButton == 0) {
            // 判断是否点击标题栏（整个窗口顶部区域）
            if (HoveringUtil.isHovering(startX, startY, guiWidth, 20, mouseX, mouseY)) {
                dragging = true;
                dragX = mouseX - startX;
                dragY = mouseY - startY;
                return;
            }

            // 判断是否点击分类按钮
            int categoryBtnWidth = guiWidth / 2;
            if (HoveringUtil.isHovering(startX, startY + 20, categoryBtnWidth, 20, mouseX, mouseY)) {
                isCategoryOne = true;
                scrollY = 0; // 重置滚动
                return;
            }
            if (HoveringUtil.isHovering(startX + categoryBtnWidth, startY + 20, categoryBtnWidth, 20, mouseX, mouseY)) {
                isCategoryOne = false;
                scrollY = 0; // 重置滚动
                return;
            }

            // 获取当前分类的模块列表，处理左键点击切换模块开关
            List<? extends Module> currentModules = isCategoryOne ? ModuleManager.modules : ElementManager.elements;
            double offsetY = translate.getY();
            int currentY = startY + 50; // 从分类按钮下方开始

            for (Module module : currentModules) {
                if (HoveringUtil.isHovering(startX + 10, (float) (currentY + offsetY), guiWidth - 20, moduleHeight, mouseX, mouseY)) {
                    module.toggle();
                    return;
                }
                currentY += moduleHeight + padding;
            }
        }

        // 右键点击模块时打开对应模块的 Values 面板
        if (mouseButton == 1) {
            List<? extends Module> currentModules = isCategoryOne ? ModuleManager.modules : ElementManager.elements;
            double offsetY = translate.getY();
            int currentY = startY + 50; // 模块列表起始位置

            for (Module module : currentModules) {
                if (HoveringUtil.isHovering(startX + 10, (float) (currentY + offsetY), guiWidth - 20, moduleHeight, mouseX, mouseY)) {
                    activeModuleValues = module;
                    // 初始化 Values 面板位置（可根据需要调整）
                    valuesPanelX = startX + guiWidth + 10;
                    valuesPanelY = startY;
                    return;
                }
                currentY += moduleHeight + padding;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            dragging = false;
            draggingValues = false;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }
}
