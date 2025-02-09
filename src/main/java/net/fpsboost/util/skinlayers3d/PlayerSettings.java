package net.fpsboost.util.skinlayers3d;

public interface PlayerSettings
{
    CustomizableModelPart getHeadLayers();

    void setupHeadLayers(final CustomizableModelPart p0);

    CustomizableModelPart[] getSkinLayers();

    void setupSkinLayers(final CustomizableModelPart[] p0);
}
