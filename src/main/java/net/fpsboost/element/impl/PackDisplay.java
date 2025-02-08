package net.fpsboost.element.impl;

import java.awt.*;
import java.util.List;

import net.fpsboost.util.Logger;
import net.fpsboost.element.Element;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.util.font.FontManager;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ColorValue;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.ResourceLocation;

/**
 * @author LangYa466
 * @since 1/31/2025
 */
public class PackDisplay extends Element {
	public static final PackDisplay INSTANCE = new PackDisplay();

	private IResourcePack pack;
	private ResourceLocation currentPack;
	private final ResourcePackRepository resourcePackRepository = mc.getResourcePackRepository();
	private List<ResourcePackRepository.Entry> packs = resourcePackRepository.getRepositoryEntries();
	private final BooleanValue bg = new BooleanValue("背景","Background",true);
	private final ColorValue colorValue = new ColorValue("背景颜色","Background Color",new Color(0,0,0,80), this);
	private final ColorValue textColorValue = new ColorValue("文本颜色","Text Color",Color.white, this);
    private final BooleanValue betterFont = new BooleanValue("更好的字体","BetterFont",true);
	public static boolean init;

	public PackDisplay() {
        super("PackDisplay","材质包显示");
		this.cnDescription = "显示你正在用的材质包";
		this.description = "Show the material bag you are using";
    }

	@Override
	public void onDraw() {
		if (!init) {
			this.loadTexture();
			init = true;
		}

		FontRenderer fr;
		if (betterFont.getValue()) fr = FontManager.client(); else fr = mc.fontRendererObj;

		width = 46 + fr.getStringWidth(this.convertNormalText(pack.getPackName()));
		height = 38;
		if (pack == null) pack = this.getCurrentPack();

		if (bg.getValue()) RenderUtil.drawRect(0, 0, width, height, colorValue.getValueC());
		RenderUtil.drawImage(this.currentPack, 4.5F, 4.5F, 29, 29);
		fr.drawString(this.convertNormalText(pack.getPackName()), 40, 29 / 2, textColorValue.getValueC());

		super.onDraw();
	}

	public void onSwitchTexture() {
		packs = resourcePackRepository.getRepositoryEntries();
		pack = this.getCurrentPack();
		this.loadTexture();
	}

	private String convertNormalText(String text) {
		if (text == null || text.isEmpty()) return ""; // 死吗 NPE
		if (!text.contains(".zip")) return text;
		StringBuilder sb = new StringBuilder(text);

		String[] patterns = {"\\u00a7" + "1", "\\u00a7" + "2", "\\u00a7" + "3", "\\u00a7" + "4",
				"\\u00a7" + "5", "\\u00a7" + "6", "\\u00a7" + "7", "\\u00a7" + "8",
				"\\u00a7" + "9", "\\u00a7" + "a", "\\u00a7" + "b", "\\u00a7" + "c",
				"\\u00a7" + "d", "\\u00a7" + "e", "\\u00a7" + "f", "\\u00a7" + "g",
				"\\u00a7" + "k", "\\u00a7" + "l", "\\u00a7" + "m", "\\u00a7" + "n",
				"\\u00a7" + "o", "\\u00a7" + "r"};

		for (String pattern : patterns) {
			int index;
			while ((index = sb.indexOf(pattern)) != -1) {
				sb.delete(index, index + pattern.length());
			}
		}

		int zipIndex;
		while ((zipIndex = sb.indexOf(".zip")) != -1) {
			sb.delete(zipIndex, zipIndex + ".zip".length());
		}

		return sb.toString();
	}

	private void loadTexture() {
		DynamicTexture dynamicTexture = null;
		try {
			dynamicTexture = new DynamicTexture(getCurrentPack().getPackImage());
		} catch (Exception e) {
			dynamicTexture = TextureUtil.missingTexture;
		} finally {
			this.currentPack = mc.getTextureManager().getDynamicTextureLocation("texturepackicon", dynamicTexture);
		}
	}

	private IResourcePack getCurrentPack() {
		if (!packs.isEmpty()) return packs.get(packs.size() - 1).getResourcePack();
		return mc.mcDefaultResourcePack;
	}
}