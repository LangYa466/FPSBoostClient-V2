package net.fpsboost.util;

import com.google.gson.JsonObject;
import net.fpsboost.Wrapper;
import net.minecraft.util.IChatComponent;

/**
 * @author LangYa
 * @since 2024/9/1 19:05
 */
public class ChatUtil implements Wrapper {

    public static void addMessageWithClient(String message) {
        if (mc.thePlayer == null) return;
        JsonObject object = new JsonObject();
        object.addProperty("text", message);
        mc.thePlayer.addChatMessage(IChatComponent.Serializer.jsonToComponent(object.toString()));
    }
}
