package net.fpsboost.util.mousefix;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MouseHelper;

public class RawInputMod {
    private Thread inputThread;
    public boolean isStart;

    public void start() {
        isStart = true;
        try {
            Minecraft.getMinecraft().mouseHelper = new RawMouseHelper();
            controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
            inputThread = new Thread(() -> {
                while (true) {
                    int i = 0;
                    while (i < controllers.length && mouse == null) {
                        if (controllers[i].getType() == Controller.Type.MOUSE) {
                            controllers[i].poll();
                            Mouse tempMouse = (Mouse) controllers[i];
                            if (tempMouse.getX().getPollData() != 0.0 || tempMouse.getY().getPollData() != 0.0) {
                                mouse = tempMouse;
                            }
                        }
                        i++;
                    }
                    if (mouse != null) {
                        mouse.poll();
                        dx += (int) mouse.getX().getPollData();
                        dy += (int) mouse.getY().getPollData();
                        if (Minecraft.getMinecraft().currentScreen != null) {
                            dx = 0;
                            dy = 0;
                        }
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });
            inputThread.setName("inputThread");
            inputThread.start();
        } catch (Exception e) {
            // ignored
        }
    }

    public void stop() {
        isStart = false;
        inputThread.stop();
        Minecraft.getMinecraft().mouseHelper = new MouseHelper();
    }

    public static Mouse mouse;
    public static Controller[] controllers;
    
    // Delta for mouse
    public static int dx = 0;
    public static int dy = 0;
}
