package net.fpsboost.util.skinlayers3d;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;

import java.util.List;

public class CustomizableModelPart {
    public float x;
    public float y;
    public float z;
    public boolean visible;
    private final List<CustomizableCube> cubes;

    public CustomizableModelPart(final List<CustomizableCube> list) {
        this.visible = true;
        this.cubes = list;
    }

    public void copyFrom(final ModelBox modelPart) {
        this.x = modelPart.posX1;
        this.y = modelPart.posY1;
        this.z = modelPart.posZ1;
    }

    public void setPos(final float f, final float g, final float h) {
        this.x = f;
        this.y = g;
        this.z = h;
    }

    public void render(final boolean redTint) {
        if (!this.visible) {
            return;
        }
        GlStateManager.pushMatrix();
        this.translateAndRotate();
        this.compile(redTint);
        GlStateManager.popMatrix();
    }

    public void translateAndRotate() {
        GlStateManager.translate(this.x / 16.0f, this.y / 16.0f, this.z / 16.0f);
    }

    private void compile(final boolean redTint) {
        for (final CustomizableCube cube : this.cubes) {
            cube.render(Tessellator.getInstance().getWorldRenderer(), redTint);
        }
    }
}
