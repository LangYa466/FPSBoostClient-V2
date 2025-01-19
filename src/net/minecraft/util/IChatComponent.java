package net.minecraft.util;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map.Entry;

/**
 * IChatComponent 接口用于表示聊天组件，可以是文本、翻译、得分等类型。
 * 它提供了获取和设置聊天样式、附加文本或子组件的方法。
 */
public interface IChatComponent extends Iterable<IChatComponent> {

    IChatComponent setChatStyle(ChatStyle style);

    ChatStyle getChatStyle();

    IChatComponent appendText(String text);

    IChatComponent appendSibling(IChatComponent component);

    String getUnformattedTextForChat();

    String getUnformattedText();

    String getFormattedText();

    List<IChatComponent> getSiblings();

    IChatComponent createCopy();

    /**
     * 序列化与反序列化 IChatComponent 对象的工具类
     */
    class Serializer implements JsonDeserializer<IChatComponent>, JsonSerializer<IChatComponent> {

        private static final Gson GSON = new GsonBuilder()
                .registerTypeHierarchyAdapter(IChatComponent.class, new Serializer())
                .registerTypeHierarchyAdapter(ChatStyle.class, new ChatStyle.Serializer())
                .registerTypeAdapterFactory(new EnumTypeAdapterFactory())
                .create();

        /**
         * 将 JSON 元素反序列化为 IChatComponent 对象
         */
        @Override
        public IChatComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return new ChatComponentText(json.getAsString());
            }

            if (json.isJsonArray()) {
                JsonArray jsonArray = json.getAsJsonArray();
                IChatComponent component = null;

                for (JsonElement element : jsonArray) {
                    IChatComponent sibling = deserialize(element, typeOfT, context);
                    if (component == null) {
                        component = sibling;
                    } else {
                        component.appendSibling(sibling);
                    }
                }

                return component;
            }

            if (json.isJsonObject()) {
                JsonObject jsonObject = json.getAsJsonObject();
                IChatComponent component;

                if (jsonObject.has("text")) {
                    component = new ChatComponentText(jsonObject.get("text").getAsString());
                } else if (jsonObject.has("translate")) {
                    component = handleTranslate(jsonObject, context);
                } else if (jsonObject.has("score")) {
                    component = handleScore(jsonObject);
                } else if (jsonObject.has("selector")) {
                    component = new ChatComponentSelector(JsonUtils.getString(jsonObject, "selector"));
                } else {
                    throw new JsonParseException("Unknown component type: " + json);
                }

                if (jsonObject.has("extra")) {
                    JsonArray extraArray = jsonObject.getAsJsonArray("extra");
                    for (JsonElement extraElement : extraArray) {
                        component.appendSibling(deserialize(extraElement, typeOfT, context));
                    }
                }

                component.setChatStyle(context.deserialize(json, ChatStyle.class));
                return component;
            }

            throw new JsonParseException("Don't know how to turn " + json + " into a Component");
        }

        /**
         * 处理翻译类型的聊天组件
         */
        private IChatComponent handleTranslate(JsonObject jsonObject, JsonDeserializationContext context) {
            String key = jsonObject.get("translate").getAsString();
            IChatComponent component;

            if (jsonObject.has("with")) {
                JsonArray withArray = jsonObject.getAsJsonArray("with");
                Object[] args = new Object[withArray.size()];

                for (int i = 0; i < args.length; ++i) {
                    args[i] = deserialize(withArray.get(i), Object.class, context);
                }

                component = new ChatComponentTranslation(key, args);
            } else {
                component = new ChatComponentTranslation(key);
            }

            return component;
        }

        /**
         * 处理得分类型的聊天组件
         */
        private IChatComponent handleScore(JsonObject jsonObject) {
            JsonObject scoreObject = jsonObject.getAsJsonObject("score");
            if (!scoreObject.has("name") || !scoreObject.has("objective")) {
                throw new JsonParseException("A score component needs at least a name and an objective");
            }

            IChatComponent component = new ChatComponentScore(
                    JsonUtils.getString(scoreObject, "name"),
                    JsonUtils.getString(scoreObject, "objective")
            );

            if (scoreObject.has("value")) {
                ((ChatComponentScore) component).setValue(JsonUtils.getString(scoreObject, "value"));
            }

            return component;
        }

        /**
         * 序列化 IChatComponent 对象为 JSON
         */
        @Override
        public JsonElement serialize(IChatComponent component, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();

            if (!component.getChatStyle().isEmpty()) {
                serializeChatStyle(component.getChatStyle(), jsonObject, context);
            }

            if (!component.getSiblings().isEmpty()) {
                JsonArray extraArray = new JsonArray();
                for (IChatComponent sibling : component.getSiblings()) {
                    extraArray.add(serialize(sibling, sibling.getClass(), context));
                }
                jsonObject.add("extra", extraArray);
            }

            if (component instanceof ChatComponentText) {
                jsonObject.addProperty("text", ((ChatComponentText) component).getChatComponentText_TextValue());
            } else if (component instanceof ChatComponentTranslation) {
                ChatComponentTranslation translation = (ChatComponentTranslation) component;
                jsonObject.addProperty("translate", translation.getKey());
                addWithArgument(translation, jsonObject, context);
            } else if (component instanceof ChatComponentScore) {
                serializeScore((ChatComponentScore) component, jsonObject);
            } else if (component instanceof ChatComponentSelector) {
                jsonObject.addProperty("selector", ((ChatComponentSelector) component).getSelector());
            }

            return jsonObject;
        }

        /**
         * 序列化聊天样式
         */
        private void serializeChatStyle(ChatStyle style, JsonObject object, JsonSerializationContext ctx) {
            JsonElement styleElement = ctx.serialize(style);
            if (styleElement.isJsonObject()) {
                JsonObject styleObject = (JsonObject) styleElement;
                for (Entry<String, JsonElement> entry : styleObject.entrySet()) {
                    object.add(entry.getKey(), entry.getValue());
                }
            }
        }

        /**
         * 添加格式化参数到翻译组件的 "with" 字段
         */
        private void addWithArgument(ChatComponentTranslation translation, JsonObject jsonObject, JsonSerializationContext context) {
            if (translation.getFormatArgs() != null && translation.getFormatArgs().length > 0) {
                JsonArray withArray = new JsonArray();
                for (Object arg : translation.getFormatArgs()) {
                    if (arg instanceof IChatComponent) {
                        withArray.add(serialize((IChatComponent) arg, arg.getClass(), context));
                    } else {
                        withArray.add(new JsonPrimitive(String.valueOf(arg)));
                    }
                }
                jsonObject.add("with", withArray);
            }
        }

        /**
         * 序列化得分组件
         */
        private void serializeScore(ChatComponentScore score, JsonObject jsonObject) {
            JsonObject scoreObject = new JsonObject();
            scoreObject.addProperty("name", score.getName());
            scoreObject.addProperty("objective", score.getObjective());
            scoreObject.addProperty("value", score.getUnformattedTextForChat());
            jsonObject.add("score", scoreObject);
        }

        /**
         * 将 IChatComponent 转换为 JSON 字符串
         */
        public static String componentToJson(IChatComponent component) {
            return GSON.toJson(component);
        }

        /**
         * 将 JSON 字符串转换为 IChatComponent 对象
         */
        public static IChatComponent jsonToComponent(String json) {
            return GSON.fromJson(json, IChatComponent.class);
        }
    }
}
