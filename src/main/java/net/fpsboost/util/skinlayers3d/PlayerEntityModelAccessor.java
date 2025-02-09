package net.fpsboost.util.skinlayers3d;

public interface PlayerEntityModelAccessor
{
    boolean hasThinArms();

    HeadLayerFeatureRenderer getHeadLayer();

    BodyLayerFeatureRenderer getBodyLayer();
}
