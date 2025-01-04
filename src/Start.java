import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import cn.langya.Logger;
import net.fpsboost.Client;
import net.minecraft.client.main.Main;

public class Start {
    public static void main(String[] args) throws IOException {
        Logger.setLogLevel(Logger.LogLevel.DEBUG);
        // Jar运行目录
        File directory = new File("");
        Logger.debug("Current directory: {}",directory.getAbsolutePath());
        Client.isDev = true;
        Main.main(concat(new String[]{"--version", "1.8.9","--username","LangYa466", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.8", "--userProperties", "{}"}, args));
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
