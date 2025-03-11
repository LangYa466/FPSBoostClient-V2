package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.optifine.Lang;
import net.minecraft.client.gui.inventory.GuiContainer; // 如果需要可自行调整导入
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;

/**
 * 在原有基础上 添加了搜索框功能。
 */
public class GuiLanguage extends GuiScreen
{
    /** The parent Gui screen */
    protected GuiScreen parentScreen;

    /** The List GuiSlot object reference. */
    private GuiLanguage.List list;

    /** Reference to the GameSettings object. */
    private final GameSettings game_settings_3;

    /** Reference to the LanguageManager object. */
    private final LanguageManager languageManager;

    /**
     * A button which allows the user to determine if the Unicode font should be forced.
     */
    private GuiOptionButton forceUnicodeFontBtn;

    /** The button to confirm the current settings. */
    private GuiOptionButton confirmSettingsBtn;

    /**
     * 用于搜索的文本框
     */
    private GuiTextField searchField;

    public GuiLanguage(GuiScreen screen, GameSettings gameSettingsObj, LanguageManager manager)
    {
        this.parentScreen = screen;
        this.game_settings_3 = gameSettingsObj;
        this.languageManager = manager;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed
     * and when the window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.buttonList.add(this.forceUnicodeFontBtn = new GuiOptionButton(
                100,
                this.width / 2 - 155,
                this.height - 38,
                GameSettings.Options.FORCE_UNICODE_FONT,
                this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT)
        ));
        this.buttonList.add(this.confirmSettingsBtn = new GuiOptionButton(
                6,
                this.width / 2 - 155 + 160,
                this.height - 38,
                I18n.format("gui.done", new Object[0])
        ));

        // 初始化语言列表
        this.list = new GuiLanguage.List(this.mc);
        this.list.registerScrollButtons(7, 8);

        // 初始化搜索框
        this.searchField = new GuiTextField(1, this.fontRendererObj, this.width / 2 - 100, 5, 200, 20);
        this.searchField.setMaxStringLength(50);
        this.searchField.setText("");
        this.searchField.setFocused(false);
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.list.handleMouseInput();
    }

    /**
     * 当按钮被点击时调用
     */
    protected ResourceLocation actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            switch (button.id)
            {
                case 5:
                    // 原本可能是某个功能的按钮 这里保持原样
                    break;

                case 6:
                    // 点击“Done”/“完成”按钮 返回上一个界面
                    this.mc.displayGuiScreen(this.parentScreen);
                    break;

                case 100:
                    // 强制使用 Unicode 字体的切换按钮
                    if (button instanceof GuiOptionButton)
                    {
                        this.game_settings_3.setOptionValue(((GuiOptionButton)button).returnEnumOptions(), 1);
                        button.displayString = this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
                        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
                        int i = scaledresolution.getScaledWidth();
                        int j = scaledresolution.getScaledHeight();
                        this.setWorldAndResolution(this.mc, i, j);
                    }
                    break;

                default:
                    this.list.actionPerformed(button);
            }
        }
        return null;
    }

    /**
     * 处理键盘输入 主要用于搜索框的输入
     */
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);
        this.searchField.textboxKeyTyped(typedChar, keyCode);
        // 更新搜索结果
        this.list.updateSearch(this.searchField.getText());
    }

    /**
     * 处理鼠标点击 主要用于让搜索框获得/失去焦点
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        // 绘制语言列表
        this.list.drawScreen(mouseX, mouseY, partialTicks);

        // 标题
        this.drawCenteredString(
                this.fontRendererObj,
                I18n.format("options.language", new Object[0]),
                this.width / 2,
                16,
                16777215
        );

        // 语言警告
        this.drawCenteredString(
                this.fontRendererObj,
                "(" + I18n.format("options.languageWarning", new Object[0]) + ")",
                this.width / 2,
                this.height - 56,
                8421504
        );

        // 绘制搜索框
        this.searchField.drawTextBox();

        // 绘制按钮等
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * 语言列表内部类
     */
    class List extends GuiSlot
    {
        /**
         * 完整的语言列表（所有可用语言）
         */
        private final java.util.List<String> allLangCodeList = Lists.newArrayList();

        /**
         * 当前显示在列表中的语言（会随搜索而变化）
         */
        private final java.util.List<String> langCodeList = Lists.newArrayList();

        private final Map<String, Language> languageMap = Maps.newHashMap();

        public List(Minecraft mcIn)
        {
            super(mcIn, GuiLanguage.this.width, GuiLanguage.this.height, 32, GuiLanguage.this.height - 65 + 4, 18);

            // 收集所有语言信息
            for (Language language : GuiLanguage.this.languageManager.getLanguages())
            {
                this.languageMap.put(language.getLanguageCode(), language);
                this.allLangCodeList.add(language.getLanguageCode());
            }
            // 初始时 全部显示
            this.langCodeList.addAll(this.allLangCodeList);
        }

        /**
         * 更新搜索结果 根据搜索文本筛选语言列表
         */
        public void updateSearch(String search)
        {
            this.langCodeList.clear();
            if (search == null || search.trim().isEmpty())
            {
                // 如果没有输入搜索词 显示全部
                this.langCodeList.addAll(this.allLangCodeList);
            }
            else
            {
                String lowerSearch = search.toLowerCase();
                for (String code : this.allLangCodeList)
                {
                    Language language = this.languageMap.get(code);
                    if (language != null)
                    {
                        String langName = language.toString().toLowerCase();
                        // 如果语言名或语言代码包含搜索词  就显示
                        if (langName.contains(lowerSearch) || code.toLowerCase().contains(lowerSearch))
                        {
                            this.langCodeList.add(code);
                        }
                    }
                }
            }
        }

        protected int getSize()
        {
            return this.langCodeList.size();
        }

        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY)
        {
            Language language = (Language)this.languageMap.get(this.langCodeList.get(slotIndex));
            GuiLanguage.this.languageManager.setCurrentLanguage(language);
            GuiLanguage.this.game_settings_3.language = language.getLanguageCode();
//            this.mc.refreshResources();
            this.mc.getLanguageManager().onResourceManagerReload(mc.getResourceManager());

            Lang.resourcesReloaded();
            GuiLanguage.this.fontRendererObj.setUnicodeFlag(GuiLanguage.this.languageManager.isCurrentLocaleUnicode() || GuiLanguage.this.game_settings_3.forceUnicodeFont);
            GuiLanguage.this.fontRendererObj.setBidiFlag(GuiLanguage.this.languageManager.isCurrentLanguageBidirectional());
            GuiLanguage.this.confirmSettingsBtn.displayString = I18n.format("gui.done", new Object[0]);
            GuiLanguage.this.forceUnicodeFontBtn.displayString = GuiLanguage.this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
            GuiLanguage.this.game_settings_3.saveOptions();
        }

        protected boolean isSelected(int slotIndex)
        {
            return this.langCodeList.get(slotIndex).equals(
                    GuiLanguage.this.languageManager.getCurrentLanguage().getLanguageCode()
            );
        }

        protected int getContentHeight()
        {
            return this.getSize() * 18;
        }

        protected void drawBackground()
        {
            GuiLanguage.this.drawDefaultBackground();
        }

        protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn)
        {
            GuiLanguage.this.fontRendererObj.setBidiFlag(true);
            GuiLanguage.this.drawCenteredString(
                    GuiLanguage.this.fontRendererObj,
                    this.languageMap.get(this.langCodeList.get(entryID)).toString(),
                    this.width / 2,
                    p_180791_3_ + 1,
                    16777215
            );
            GuiLanguage.this.fontRendererObj.setBidiFlag(
                    GuiLanguage.this.languageManager.getCurrentLanguage().isBidirectional()
            );
        }
    }
}
