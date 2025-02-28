package net.fpsboost.screen.musicPlayer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import net.fpsboost.util.Logger;

public class JavaFXInitializer extends Application {
    @Override
    public void start(Stage primaryStage) {
        Logger.warn("JavaFX 线程已启动");
    }

    public static void initialize() {
        // 只有 JavaFX 线程未启动时 才运行 Application.launch()
        if (!Platform.isFxApplicationThread()) {
            new Thread(() -> Application.launch(JavaFXInitializer.class)).start();
        }
    }
}
