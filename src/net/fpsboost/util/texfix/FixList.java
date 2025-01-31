package net.fpsboost.util.texfix;

import java.util.ArrayList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

// LangYa466
// recode in 2025/1/31
public class FixList extends ArrayList<int[][]> {
    private final TextureAtlasSprite sprite;
    private boolean reload = true;

    public FixList(TextureAtlasSprite data) {
        this.sprite = data;
    }

    @Override
    public int size() {
        lazyReload();
        return super.size();
    }

    @Override
    public boolean isEmpty() {
        lazyReload();
        return super.isEmpty();
    }

    @Override
    public int[][] get(int index) {
        lazyReload();
        return super.get(index);
    }

    @Override
    public void clear() {
        this.reload = true;
        super.clear();
    }

    private void lazyReload() {
        if (reload) {
            reload = false;
            TextureFix.reloadTextureData(sprite);
            TextureFix.markForUnload(sprite);
        }
    }
}
