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
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ColorValue;
import net.fpsboost.value.impl.NumberValue;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LangYa466
 * @since 2/13/2025
 */
public class NewClickGUIScreen extends GuiScreen {
    public static final NewClickGUIScreen INSTANCE = new NewClickGUIScreen();
    private final Translate translate = new Translate(0, 0);
    private double scrollY = 0;
    private final int moduleHeight = 20;
    private final int padding = 5;
    private final FontRenderer fontRenderer = FontManager.client();
    private int guiWidth = 200;
    private int guiHeight = 250; // 给分类按钮和搜索框留空间

    // 使用 DragWindow 管理主窗口拖拽
    private DragWindow mainWindow;
    // 使用 DragWindow 管理 Values 面板拖拽
    private DragWindow valuesPanelWindow;

    // 选中的分类
    private boolean isCategoryOne = true;

    // 模块 Values 面板相关变量
    private Module activeModuleValues = null;
    private boolean init;

    // 用于记录上次绘制时自适应面板的宽度，便于点击区域计算
    private int lastAdaptivePanelWidth = 0;

    // 自定义搜索框
    private SearchBox searchBox;

    // 滚动条拖动状态与鼠标点击偏移量
    private boolean draggingSlider = false;
    private int sliderDragOffset = 0;

    // ColorPickerWindow，当点击 ColorValue 的颜色显示区块时打开
    private ColorPickerWindow colorPickerWindow = null;

    @Override
    public void initGui() {
        if (init) return;
        int startX = (width - guiWidth) / 2;
        int startY = (height - guiHeight) / 2;
        mainWindow = new DragWindow(startX, startY, guiWidth, guiHeight);
        // 初始化自定义搜索框，放置在主窗口内（相对于主窗口左上角）
        searchBox = new SearchBox(startX + 10, startY + 40, guiWidth - 20, 20, fontRenderer);
        init = true;
        super.initGui();
    }

    /**
     * 根据搜索框内容过滤模块列表
     */
    private List<Module> getFilteredModules() {
        List<? extends Module> rawModules = isCategoryOne ? ModuleManager.modules : ElementManager.elements;
        List<Module> filtered = new ArrayList<>();
        String query = searchBox != null ? searchBox.getText().toLowerCase() : "";
        for (Module m : rawModules) {
            if (query.isEmpty() || m.cnName.toLowerCase().contains(query) || m.cnDescription.toLowerCase().contains(query)) {
                filtered.add(m);
            }
        }
        return filtered;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // 如果有 ColorPicker 窗口，则优先处理其拖拽与绘制
        if (colorPickerWindow != null) {
            colorPickerWindow.drag(mouseX, mouseY);
        }
        // 主窗口拖拽处理
        if (mainWindow != null) {
            mainWindow.drag(mouseX, mouseY);
        }
        // 更新 Values 面板拖拽时的位置
        if (activeModuleValues != null && valuesPanelWindow != null) {
            valuesPanelWindow.drag(mouseX, mouseY);
        }

        // 滚动条拖拽
        if (draggingSlider) {
            List<Module> filteredModules = getFilteredModules();
            int modulesViewHeight = guiHeight - 60;
            int totalContentHeight = (moduleHeight + padding) * filteredModules.size();
            int maxScroll = Math.max(0, totalContentHeight - modulesViewHeight);
            float sliderPositionRatio = getSliderPositionRatio(mouseY, modulesViewHeight, totalContentHeight);
            scrollY = -sliderPositionRatio * maxScroll;
        }

        translate.interpolate(0, scrollY, 0.2f);
        double offsetY = translate.getY();

        int startX = mainWindow.getX();
        int startY = mainWindow.getY();

        // 更新搜索框位置，确保跟随主窗口拖动
        if (searchBox != null) {
            searchBox.setPosition(startX + 10, startY + 40);
            searchBox.setWidth(guiWidth - 20);
        }

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

        // 绘制自定义搜索框
        if (searchBox != null) {
            searchBox.draw();
        }

        // 定义模块列表区域的起始位置和高度（不包含标题、分类按钮和搜索框区域）
        int moduleListStartY = startY + 60;
        int modulesViewHeight = guiHeight - 60;

        // 获取过滤后的模块列表
        List<Module> filteredModules = getFilteredModules();

        // 开启裁剪，限制滚动区域（仅模块列表区域）
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        new Scissor(startX, moduleListStartY, guiWidth, modulesViewHeight).doScissor();

        int currentY = moduleListStartY + 5; // 添加上边距
        for (Module module : filteredModules) {
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

        // 绘制右侧滚动条（仅当模块列表超出可视区域时显示）
        int totalContentHeight = (moduleHeight + padding) * filteredModules.size();
        int maxScroll = Math.max(0, totalContentHeight - modulesViewHeight);
        if (totalContentHeight > modulesViewHeight && maxScroll > 0) {
            int scrollbarWidth = 6;
            // 定义滚动条区域，放在主窗口右边，离边缘2像素
            int scrollbarX = startX + guiWidth - scrollbarWidth - 2;

            // 绘制滚动条背景（深灰色）
            drawRect(scrollbarX, moduleListStartY, scrollbarX + scrollbarWidth, moduleListStartY + modulesViewHeight, 0xFF555555);

            // 计算滑块高度与位置（保证最小高度为10像素）
            float viewRatio = (float) modulesViewHeight / totalContentHeight;
            int sliderHeight = Math.max((int) (modulesViewHeight * viewRatio), 10);
            float sliderPositionRatio = (float) (-scrollY) / maxScroll;
            int sliderY = moduleListStartY + (int) ((modulesViewHeight - sliderHeight) * sliderPositionRatio);

            // 绘制滑块（浅灰色）
            drawRect(scrollbarX, sliderY, scrollbarX + scrollbarWidth, sliderY + sliderHeight, 0xFFAAAAAA);
        }

        // 绘制模块 Values 面板（如果已打开）
        if (activeModuleValues != null) {
            int headerHeight = 20;
            int paddingTop = 5;
            int paddingBottom = 5;
            int rowHeight = Math.max(15, fontRenderer.getHeight() + 4);

            String headerText = "设置: " + activeModuleValues.cnName;
            int headerTextWidth = fontRenderer.getStringWidth(headerText);
            int closeButtonWidth = fontRenderer.getStringWidth("X") + 10;

            int computedMaxValueWidth = 0;
            for (Value<?> valueObj : activeModuleValues.values) {
                int lineWidth = 5;
                int nameWidth = fontRenderer.getStringWidth(valueObj.cnName);
                lineWidth += nameWidth + 5;
                if (valueObj instanceof BooleanValue) {
                    String stateText = ((BooleanValue) valueObj).getValue() ? "开启(按我修改)" : "关闭(按我修改)";
                    int stateTextWidth = fontRenderer.getStringWidth(stateText);
                    lineWidth += stateTextWidth + 5;
                } else if (valueObj instanceof ColorValue) {
                    int boxWidth = 20;
                    String hexColor = "0x" + Integer.toHexString(((ColorValue) valueObj).getValueC()).toUpperCase();
                    int hexTextWidth = fontRenderer.getStringWidth(hexColor);
                    lineWidth += boxWidth + 5 + hexTextWidth + 5;
                } else if (valueObj instanceof NumberValue) {
                    NumberValue numberValue = (NumberValue) valueObj;
                    String valueText = String.format("%.2f", numberValue.getValue());
                    int btnWidth = fontRenderer.getStringWidth("[-]") + 4;
                    int valueTextWidth = fontRenderer.getStringWidth(valueText);
                    // 计算：减按钮 + 间隔 + 数值文本 + 间隔 + 加按钮 + 间隔
                    lineWidth += btnWidth + 5 + valueTextWidth + 5 + btnWidth + 5;
                }
                computedMaxValueWidth = Math.max(computedMaxValueWidth, lineWidth);
            }

            int calculatedWidth = Math.max(headerTextWidth + closeButtonWidth + 10, computedMaxValueWidth);
            lastAdaptivePanelWidth = Math.max(100, calculatedWidth);

            int valuesCount = activeModuleValues.values != null ? activeModuleValues.values.size() : 0;
            int contentHeight = valuesCount * rowHeight;
            int adaptivePanelHeight = Math.max(100, headerHeight + paddingTop + contentHeight + paddingBottom);

            if (valuesPanelWindow == null) {
                valuesPanelWindow = new DragWindow(startX + guiWidth + 10, startY, lastAdaptivePanelWidth, adaptivePanelHeight);
            }
            drawRect(valuesPanelWindow.getX(), valuesPanelWindow.getY(), valuesPanelWindow.getX() + lastAdaptivePanelWidth, valuesPanelWindow.getY() + adaptivePanelHeight, 0xCC000000);
            drawRect(valuesPanelWindow.getX(), valuesPanelWindow.getY(), valuesPanelWindow.getX() + lastAdaptivePanelWidth, valuesPanelWindow.getY() + headerHeight, 0xFF222222);

            fontRenderer.drawStringWithShadow(headerText, valuesPanelWindow.getX() + 5, valuesPanelWindow.getY() + 5, 0xFFFFFFFF);
            fontRenderer.drawStringWithShadow(
                    "X",
                    valuesPanelWindow.getX() + lastAdaptivePanelWidth - 15,
                    valuesPanelWindow.getY() + 5,
                    HoveringUtil.isHovering(
                            valuesPanelWindow.getX() + lastAdaptivePanelWidth - 15,
                            valuesPanelWindow.getY() + 5,
                            fontRenderer.getStringWidth("X"),
                            fontRenderer.getHeight(),
                            mouseX,
                            mouseY
                    ) ? 0xFFFFFF00 : 0xFFFF0000
            );

            int valueY = valuesPanelWindow.getY() + headerHeight + paddingTop;
            for (Value<?> valueObj : activeModuleValues.values) {
                drawRect(valuesPanelWindow.getX(), valueY, valuesPanelWindow.getX() + lastAdaptivePanelWidth, valueY + rowHeight, 0xFF111111);
                int textY = valueY + (rowHeight - fontRenderer.getHeight()) / 2;
                fontRenderer.drawStringWithShadow(valueObj.cnName, valuesPanelWindow.getX() + 5, textY, 0xFFFFFFFF);

                int nameWidth = fontRenderer.getStringWidth(valueObj.cnName);
                int offsetX = valuesPanelWindow.getX() + 5 + nameWidth + 5;

                if (valueObj instanceof BooleanValue) {
                    BooleanValue boolValue = (BooleanValue) valueObj;
                    String stateText = boolValue.getValue() ? "开启(按我修改)" : "关闭(按我修改)";
                    fontRenderer.drawStringWithShadow(stateText, offsetX, textY, boolValue.getValue() ? 0xFF00FF00 : 0xFFFF0000);
                } else if (valueObj instanceof ColorValue) {
                    ColorValue colorValue = (ColorValue) valueObj;
                    int colorInt = colorValue.getValueC();
                    int boxWidth = 20;
                    int boxHeight = fontRenderer.getHeight();
                    // 绘制颜色显示区块
                    drawRect(offsetX, textY, offsetX + boxWidth, textY + boxHeight, colorInt);
                    // 边框
                    drawRect(offsetX, textY, offsetX + boxWidth, textY + 1, 0xFF000000);
                    drawRect(offsetX, textY, offsetX + 1, textY + boxHeight, 0xFF000000);
                    drawRect(offsetX + boxWidth - 1, textY, offsetX + boxWidth, textY + boxHeight, 0xFF000000);
                    drawRect(offsetX, textY + boxHeight - 1, offsetX + boxWidth, textY + boxHeight, 0xFF000000);
                    // 同时绘制16进制颜色值
                    String hexColor = "0x" + Integer.toHexString(colorInt).toUpperCase();
                    fontRenderer.drawStringWithShadow(hexColor, offsetX + boxWidth + 5, textY, 0xFFFFFFFF);
                } else if (valueObj instanceof NumberValue) {
                    NumberValue numberValue = (NumberValue) valueObj;
                    String valueText = String.format("%.2f", numberValue.getValue());
                    int btnWidth = fontRenderer.getStringWidth("[-]") + 4;
                    int btnHeight = fontRenderer.getHeight() + 3;
                    // 绘制减号按钮
                    drawRect(offsetX, textY, offsetX + btnWidth, textY + btnHeight, 0xFF444444);
                    fontRenderer.drawStringWithShadow("[-]", offsetX + 2, textY, 0xFFFFFFFF);
                    // 绘制当前数值
                    int valueTextWidth = fontRenderer.getStringWidth(valueText);
                    int valueX = offsetX + btnWidth + 5;
                    fontRenderer.drawStringWithShadow(valueText, valueX, textY, 0xFFFFFFFF);
                    // 绘制加号按钮
                    int plusX = valueX + valueTextWidth + 5;
                    drawRect(plusX, textY, plusX + btnWidth, textY + btnHeight, 0xFF444444);
                    fontRenderer.drawStringWithShadow("[+]", plusX + 2, textY, 0xFFFFFFFF);
                }
                valueY += rowHeight;
            }
        }

        // 如果 ColorPicker 窗口存在，则绘制在最上层
        if (colorPickerWindow != null) {
            colorPickerWindow.drawScreen(mouseX, mouseY, partialTicks);
            if (colorPickerWindow.isClosed()) {
                colorPickerWindow = null;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private float getSliderPositionRatio(int mouseY, int modulesViewHeight, int totalContentHeight) {
        int moduleListStartY = mainWindow.getY() + 60;
        float viewRatio = (float) modulesViewHeight / totalContentHeight;
        int sliderHeight = Math.max((int) (modulesViewHeight * viewRatio), 10);
        int sliderNewY = mouseY - sliderDragOffset;
        // 限制 sliderNewY 不超出模块列表区域
        if (sliderNewY < moduleListStartY) {
            sliderNewY = moduleListStartY;
        }
        if (sliderNewY > moduleListStartY + modulesViewHeight - sliderHeight) {
            sliderNewY = moduleListStartY + modulesViewHeight - sliderHeight;
        }
        float sliderPositionRatio = (float) (sliderNewY - moduleListStartY) / (modulesViewHeight - sliderHeight);
        return sliderPositionRatio;
    }

    @Override
    public void handleMouseInput() throws IOException {
        int delta = Mouse.getEventDWheel();
        if (delta != 0) {
            scrollY += (delta > 0 ? 20 : -20);
            List<Module> filteredModules = getFilteredModules();
            int modulesViewHeight = guiHeight - 60;
            int totalContentHeight = (moduleHeight + padding) * filteredModules.size();
            int maxScroll = Math.max(0, totalContentHeight - modulesViewHeight);
            if (scrollY > 0) scrollY = 0;
            if (scrollY < -maxScroll) scrollY = -maxScroll;
        }
        super.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        // 如果 ColorPicker 窗口存在，则先转发鼠标点击给它
        if (colorPickerWindow != null) {
            if (colorPickerWindow.mouseClicked(mouseX, mouseY, mouseButton)) {
                return;
            }
        }

        // 优先处理搜索框点击
        if (mouseButton == 0 && searchBox != null && searchBox.mouseClicked(mouseX, mouseY)) {
            return;
        }

        // 检测是否点击滚动条滑块区域（优先处理）
        if (mouseButton == 0) {
            List<Module> filteredModules = getFilteredModules();
            int totalContentHeight = (moduleHeight + padding) * filteredModules.size();
            int modulesViewHeight = guiHeight - 60;
            int maxScroll = Math.max(0, totalContentHeight - modulesViewHeight);
            if (totalContentHeight > modulesViewHeight && maxScroll > 0) {
                int startX = mainWindow.getX();
                int moduleListStartY = mainWindow.getY() + 60;
                int scrollbarWidth = 6;
                int scrollbarX = startX + guiWidth - scrollbarWidth - 2;
                float viewRatio = (float) modulesViewHeight / totalContentHeight;
                int sliderHeight = Math.max((int) (modulesViewHeight * viewRatio), 10);
                float sliderPositionRatio = (float) (-scrollY) / maxScroll;
                int sliderY = moduleListStartY + (int) ((modulesViewHeight - sliderHeight) * sliderPositionRatio);
                if (HoveringUtil.isHovering(scrollbarX, sliderY, scrollbarWidth, sliderHeight, mouseX, mouseY)) {
                    draggingSlider = true;
                    sliderDragOffset = mouseY - sliderY;
                    return;
                }
            }
        }

        // 处理 Values 面板标题区域拖动与关闭
        if (mouseButton == 0 && activeModuleValues != null && valuesPanelWindow != null
                && HoveringUtil.isHovering(valuesPanelWindow.getX(), valuesPanelWindow.getY(), lastAdaptivePanelWidth, 20, mouseX, mouseY)) {
            // 判断是否点击关闭按钮
            if (HoveringUtil.isHovering(valuesPanelWindow.getX() + lastAdaptivePanelWidth - 20, valuesPanelWindow.getY(), 20, 20, mouseX, mouseY)) {
                activeModuleValues = null;
                valuesPanelWindow = null;
                return;
            }
            valuesPanelWindow.startDrag(mouseX, mouseY);
            return;
        }

        if (mouseButton == 0) {
            // 检查窗口拖动
            if (HoveringUtil.isHovering(mainWindow.getX(), mainWindow.getY(), guiWidth, 20, mouseX, mouseY)) {
                mainWindow.startDrag(mouseX, mouseY);
                return;
            }
            // 分类按钮
            int categoryBtnWidth = guiWidth / 2;
            if (HoveringUtil.isHovering(mainWindow.getX(), mainWindow.getY() + 20, categoryBtnWidth, 20, mouseX, mouseY)) {
                isCategoryOne = true;
                scrollY = 0;
                return;
            }
            if (HoveringUtil.isHovering(mainWindow.getX() + categoryBtnWidth, mainWindow.getY() + 20, categoryBtnWidth, 20, mouseX, mouseY)) {
                isCategoryOne = false;
                scrollY = 0;
                return;
            }
            // 模块列表点击（左键切换开关）
            int moduleListStartY = mainWindow.getY() + 60;
            double offsetY = translate.getY();
            int currentY = moduleListStartY + 5;
            List<Module> filteredModules = getFilteredModules();
            for (Module module : filteredModules) {
                if (HoveringUtil.isHovering(mainWindow.getX() + 10, (float) (currentY + offsetY), guiWidth - 20, moduleHeight, mouseX, mouseY)) {
                    module.toggle();
                    return;
                }
                currentY += moduleHeight + padding;
            }
        }

        // 右键打开 Values 面板
        if (mouseButton == 1) {
            int moduleListStartY = mainWindow.getY() + 60;
            double offsetY = translate.getY();
            int currentY = moduleListStartY + 5;
            List<Module> filteredModules = getFilteredModules();
            for (Module module : filteredModules) {
                if (HoveringUtil.isHovering(mainWindow.getX() + 10, (float) (currentY + offsetY), guiWidth - 20, moduleHeight, mouseX, mouseY)) {
                    activeModuleValues = module;
                    // 当右键打开 Values 面板时，初始化其拖拽窗口
                    valuesPanelWindow = new DragWindow(mainWindow.getX() + guiWidth + 10, mainWindow.getY(), lastAdaptivePanelWidth, 100);
                    return;
                }
                currentY += moduleHeight + padding;
            }
        }

        // 处理 Values 面板中各个 value 的点击事件
        if (mouseButton == 0 && activeModuleValues != null && valuesPanelWindow != null) {
            int headerHeight = 20;
            int paddingTop = 5;
            int rowHeight = Math.max(15, fontRenderer.getHeight() + 4);
            if (mouseY >= valuesPanelWindow.getY() + headerHeight) {
                int rowIndex = (mouseY - valuesPanelWindow.getY() - headerHeight - paddingTop) / rowHeight;
                if (rowIndex >= 0 && rowIndex < activeModuleValues.values.size()) {
                    Value<?> valueObj = activeModuleValues.values.get(rowIndex);
                    int rowY = valuesPanelWindow.getY() + headerHeight + paddingTop + rowIndex * rowHeight;
                    int textY = rowY + (rowHeight - fontRenderer.getHeight()) / 2;
                    int nameWidth = fontRenderer.getStringWidth(valueObj.cnName);
                    int offsetX = valuesPanelWindow.getX() + 5 + nameWidth + 5;
                    if (valueObj instanceof BooleanValue) {
                        BooleanValue boolValue = (BooleanValue) valueObj;
                        String stateText = boolValue.getValue() ? "开启(按我修改)" : "关闭(按我修改)";
                        int stateTextWidth = fontRenderer.getStringWidth(stateText);
                        if (HoveringUtil.isHovering(offsetX, textY, stateTextWidth, fontRenderer.getHeight(), mouseX, mouseY)) {
                            boolValue.toggle();
                            return;
                        }
                    } else if (valueObj instanceof ColorValue) {
                        // 如果点击了颜色显示区块，则打开 ColorPicker 窗口
                        ColorValue colorValue = (ColorValue) valueObj;
                        int boxWidth = 20;
                        int boxHeight = fontRenderer.getHeight();
                        if (HoveringUtil.isHovering(offsetX, textY, boxWidth, boxHeight, mouseX, mouseY)) {
                            colorPickerWindow = new ColorPickerWindow(colorValue, mainWindow.getX() + guiWidth + 50, mainWindow.getY());
                            return;
                        }
                    } else if (valueObj instanceof NumberValue) {
                        NumberValue numberValue = (NumberValue) valueObj;
                        int btnWidth = fontRenderer.getStringWidth("[-]") + 4;
                        int btnHeight = fontRenderer.getHeight() + 3;
                        String valueText = String.format("%.2f", numberValue.getValue());
                        int valueTextWidth = fontRenderer.getStringWidth(valueText);
                        // 计算加号按钮区域：offsetX + (减按钮宽度 + 间隔 + 数值文本宽度 + 间隔)
                        int plusX = offsetX + btnWidth + 5 + valueTextWidth + 5;
                        if (HoveringUtil.isHovering(offsetX, textY, btnWidth, btnHeight, mouseX, mouseY)) {
                            numberValue.setValue(numberValue.getValue() - numberValue.incValue);
                            return;
                        } else if (HoveringUtil.isHovering(plusX, textY, btnWidth, btnHeight, mouseX, mouseY)) {
                            numberValue.setValue(numberValue.getValue() + numberValue.incValue);
                            return;
                        }
                    }
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (colorPickerWindow != null) {
            colorPickerWindow.mouseReleased(mouseX, mouseY, state);
        }
        if (state == 0) {
            if (mainWindow != null) {
                mainWindow.stopDrag();
            }
            if (valuesPanelWindow != null) {
                valuesPanelWindow.stopDrag();
            }
            draggingSlider = false;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (searchBox != null) {
            searchBox.keyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }
}
