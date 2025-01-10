package net.fpsboost.util.mousefix;

import cn.langya.Logger;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MouseHelper;

import java.util.Arrays;
import java.util.Optional;

public class RawInputMod {
    private Thread inputThread;
    public boolean isStart;

    public void start() {
        isStart = true;
        try {
            Minecraft.getMinecraft().mouseHelper = new RawMouseHelper();
            controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
            inputThread = new Thread(this::inputLoop);
            inputThread.setName("inputThread");
            inputThread.start();
        } catch (Exception e) {
            Logger.error("Error starting input thread: {}", e.getMessage());
        }
    }

    private void inputLoop() {
        while (isStart) {
            Optional<Mouse> optionalMouse = Arrays.stream(controllers)
                    .filter(controller -> controller.getType() == Controller.Type.MOUSE)
                    .map(controller -> (Mouse) controller)
                    .filter(mouse -> mouse.getX().getPollData() != 0.0 || mouse.getY().getPollData() != 0.0)
                    .findFirst();

            optionalMouse.ifPresent(tempMouse -> {
                mouse = tempMouse;
                mouse.poll();
                dx += (int) mouse.getX().getPollData();
                dy += (int) mouse.getY().getPollData();

                if (Minecraft.getMinecraft().currentScreen != null) {
                    dx = 0;
                    dy = 0;
                }
            });

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Logger.error("Input thread interrupted: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stop() {
        isStart = false;
        if (inputThread != null && inputThread.isAlive()) {
            inputThread.interrupt();
        }
        Minecraft.getMinecraft().mouseHelper = new MouseHelper();
    }

    public static Mouse mouse;
    public static Controller[] controllers;

    // Delta for mouse
    public static int dx = 0;
    public static int dy = 0;
}
