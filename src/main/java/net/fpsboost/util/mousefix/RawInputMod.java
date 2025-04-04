package net.fpsboost.util.mousefix;

import net.fpsboost.Wrapper;
import net.fpsboost.util.Logger;
import net.java.games.input.Controller;
import net.java.games.input.Controller.Type;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Mouse;
import net.minecraft.util.MouseHelper;

public class RawInputMod implements Wrapper {
    private Thread inputThread;
    public boolean isStart;

    public void start() {
        this.isStart = true;
        try {
            mc.mouseHelper = new RawMouseHelper();
            RawInputMod.controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
            this.inputThread = new Thread(() -> {
                while (true) {
                    int i = 0;
                    while ((i < RawInputMod.controllers.length) && (null == mouse)) {
                        if (RawInputMod.controllers[i].getType() == Type.MOUSE) {
                            RawInputMod.controllers[i].poll();
                            final Mouse tempMouse = (Mouse) RawInputMod.controllers[i];
                            if ((0.0 != tempMouse.getX().getPollData()) || (0.0 != tempMouse.getY().getPollData()))
                                RawInputMod.mouse = tempMouse;
                        }
                        i++;
                    }
                    if (null != mouse) {
                        RawInputMod.mouse.poll();
                        RawInputMod.dx += (int) RawInputMod.mouse.getX().getPollData();
                        RawInputMod.dy += (int) RawInputMod.mouse.getY().getPollData();
                        if (null != mc.currentScreen) {
                            RawInputMod.dx = 0;
                            RawInputMod.dy = 0;
                        }
                    }

                    // 防止CPU被榨干了
                    try {
                        Thread.sleep(1);
                    } catch (final InterruptedException e) {
                        Logger.error(e.getMessage());
                    }

                }
            });
            this.inputThread.setName("inputThread");
            this.inputThread.start();
        } catch (final Exception e) {
            // ignored
        }
    }

    public void stop() {
        this.isStart = false;
        this.inputThread.stop();
        mc.mouseHelper = new MouseHelper();
    }

    public static Mouse mouse;
    public static Controller[] controllers;

    // Delta for mouse
    public static int dx;
    public static int dy;
}
