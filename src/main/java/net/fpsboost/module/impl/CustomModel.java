package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.util.ColorUtil;
import net.fpsboost.value.impl.ColorValue;
import net.fpsboost.value.impl.ModeValue;
import net.fpsboost.value.impl.NumberValue;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @see net.minecraft.client.model.ModelPlayer#render(Entity, float, float, float, float, float, float)
 */
public class CustomModel extends Module {

    public static final ResourceLocation amongusModel = new ResourceLocation("client/models/amogus.png");
    public static final ResourceLocation rabbitModel = new ResourceLocation("client/models/rabbit.png");

    public static final CustomModel INSTANCE = new CustomModel();
    public static boolean enabled = false;

    public static final ModeValue model = new ModeValue("模型", "Model", "Among Us", "Among Us", "Rabbit");
    private static final ModeValue mogusColorMode = new ModeValue("Among Us颜色模式", "Among Us Color", "自定义", "随机", "彩虹", "自定义");
    private static final ColorValue amongusColor = new ColorValue("Among Us 自定义颜色", "", Color.RED, INSTANCE);
    private static final NumberValue rainbowSpeed = new NumberValue("彩虹速度", "Rainbow Speed", 15, 2, 30, 1);

    public CustomModel() {
        super("Custom Model", "自定义模型");
        this.description = "Renders an custom model on every player";
        this.cnDescription = "为玩家渲染自定义模型";
    }

    @Override
    public void onDisable() {
        super.onDisable();
        enabled = false;
    }

    private static final Map<Object, Color> entityColorMap = new HashMap<>();

    @Override
    public void onEnable() {
        super.onEnable();
        entityColorMap.clear();
        enabled = true;
    }

    @Override
    public void onWorldLoad() {
        entityColorMap.clear();
    }

    public static Color getRandomColor() {
        return new Color(Color.HSBtoRGB((float) Math.random(), (float) (.5 + Math.random() / 2), (float) (.5 + Math.random() / 2f)));
    }

    public static Color getColor(Entity entity) {
        Color color = Color.WHITE;
        switch (mogusColorMode.getValue()) {
            case "随机":
                if (entityColorMap.containsKey(entity)) {
                    color = entityColorMap.get(entity);
                } else {
                    color = getRandomColor();
                    entityColorMap.put(entity, color);
                }
                break;
            case "自定义":
                color = amongusColor.getValue().getColor();
                break;
            case "彩虹":
                color = ColorUtil.rainbow(rainbowSpeed.getValue().intValue(), entity.ticksExisted);
                break;
        }

        return color;
    }

    public static double getYOffset() {
        if (model.getValue().equals("Among Us")) {
            return 0.25;
        }
        return 0;
    }
}
