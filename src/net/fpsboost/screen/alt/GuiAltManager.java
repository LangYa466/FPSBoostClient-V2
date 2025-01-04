package net.fpsboost.screen.alt;

import cn.langya.Logger;
import net.fpsboost.module.impl.ClientSettings;
import net.fpsboost.screen.alt.altimpl.MicrosoftAlt;
import net.fpsboost.screen.alt.microsoft.GuiMicrosoftLogin;
import net.fpsboost.screen.alt.microsoft.MicrosoftLogin;
import com.mojang.authlib.exceptions.AuthenticationException;
import net.fpsboost.socket.ClientIRC;
import net.fpsboost.util.font.FontManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;

import java.io.IOException;

public class GuiAltManager extends GuiScreen {
    private final GuiScreen parentScreen;

    private GuiButton buttonLogin;
    private GuiButton buttonRemove;
    private volatile String status;
    private volatile MicrosoftLogin microsoftLogin;
    private volatile Thread runningThread;

    private static Alt selectAlt;

    public GuiAltManager(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        String info;
        if (ClientSettings.INSTANCE.cnMode.getValue()) {
            info = "您当前登录的账号: ";
            status = EnumChatFormatting.YELLOW + "等待切换中...";
        }else {
            info = "Your account: ";
            status = EnumChatFormatting.GREEN + "Waiting...";
        }
        try {
            if (microsoftLogin != null) {
                status = microsoftLogin.getStatus();
            }
        } catch (NullPointerException ignored) {
        }

        drawDefaultBackground();
        drawBackground(0);
        FontManager.client().drawCenteredStringWithShadow(EnumChatFormatting.YELLOW + info + mc.getSession().getUsername(), width / 2.0f, height / 2.0f - 10, -1);
        FontManager.client().drawCenteredStringWithShadow(status, width / 2.0f, height / 2.0f, -1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected ResourceLocation actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            if (runningThread != null) {
                runningThread.interrupt();
            }

            mc.displayGuiScreen(parentScreen);
        } else if (button.id == 2) {
            if (selectAlt != null) {
                final Thread thread = new Thread(() -> {
                    if (ClientSettings.INSTANCE.cnMode.getValue()) {
                        status = EnumChatFormatting.YELLOW + "登录中...";
                    } else {
                        status = EnumChatFormatting.GREEN + "Logging in...";
                    }
                    switch (selectAlt.getAccountType()) {
                        case OFFLINE:
                            Minecraft.getMinecraft().session = new Session(selectAlt.getUserName(), "", "", "mojang");
                            if (ClientSettings.INSTANCE.cnMode.getValue()) {
                                status = EnumChatFormatting.GREEN + "登录成功! " + mc.session.getUsername();
                            } else {
                                status = EnumChatFormatting.GREEN + "Logged in! " + mc.session.getUsername();
                            }
                            break;
                        case MICROSOFT: {
                            try {
                                microsoftLogin = new MicrosoftLogin(((MicrosoftAlt) selectAlt).getRefreshToken());

                                while (Minecraft.getMinecraft().running) {
                                    if (microsoftLogin.logged) {
                                        mc.session = new Session(microsoftLogin.getUserName(), microsoftLogin.getUuid(), microsoftLogin.getAccessToken(), "mojang");
                                        if (ClientSettings.INSTANCE.cnMode.getValue()) {
                                            status = EnumChatFormatting.GREEN + "登录成功! " + mc.session.getUsername();
                                        } else {
                                            status = EnumChatFormatting.GREEN + "Logged in! " + mc.session.getUsername();
                                        }
                                        break;
                                    }
                                }
                            } catch (Throwable e) {
                                Logger.error(e.getMessage());
                                if (ClientSettings.INSTANCE.cnMode.getValue()) {
                                    status = EnumChatFormatting.RED + "登录失败! " + e.getClass().getName() + ": " + e.getMessage();
                                } else {
                                    status = EnumChatFormatting.RED + "Login failed! " + e.getClass().getName() + ": " + e.getMessage();
                                }
                            }

                            microsoftLogin = null;

                            break;
                        }
                    }
                }, "AltManager Login Thread");

                thread.setDaemon(true);
                thread.start();

                setRunningThread(thread);
            }
        } else if (button.id == 3) {
            if (selectAlt != null) {
                AltManager.Instance.getAltList().remove(selectAlt);
                selectAlt = null;
            }
        } else if (button.id == 4) {
            mc.displayGuiScreen(new GuiAltLogin(this) {
                @Override
                public void onLogin(String account, String password) {
                    final Thread thread = new Thread() {
                        @Override
                        public void run() {
                            final AltManager.LoginStatus loginStatus;
                            try {
                                if (ClientSettings.INSTANCE.cnMode.getValue()) {
                                    status = EnumChatFormatting.YELLOW + "登录中...";
                                } else {
                                    status = EnumChatFormatting.GREEN + "Logging in...";
                                }
                                loginStatus = AltManager.loginAlt(account, password);

                                switch (loginStatus) {
                                    case FAILED:
                                        if (ClientSettings.INSTANCE.cnMode.getValue()) {
                                            status = EnumChatFormatting.RED + "登录失败!";
                                        } else {
                                            status = EnumChatFormatting.RED + "Login failed!";
                                        }
                                        break;
                                    case SUCCESS:
                                        String ign = mc.session.getUsername();
                                        if (ClientSettings.INSTANCE.cnMode.getValue()) {
                                            status = EnumChatFormatting.GREEN + "登录成功! " + ign;
                                        } else {
                                            status = EnumChatFormatting.GREEN + "Logged in! " + ign;
                                        }
                                        break;
                                }
                            } catch (AuthenticationException e) {
                                Logger.error(e.getMessage());
                                if (ClientSettings.INSTANCE.cnMode.getValue()) {
                                    status = EnumChatFormatting.RED + "登录失败! " + e.getClass().getName() + ": " + e.getMessage();
                                } else {
                                    status = EnumChatFormatting.RED + "Login failed! " + e.getClass().getName() + ": " + e.getMessage();
                                }
                            }

                            interrupt();
                        }
                    };

                    thread.setDaemon(true);
                    thread.start();

                    setRunningThread(thread);
                }
            });
        } else if (button.id == 5) {
            mc.displayGuiScreen(new GuiMicrosoftLogin(this));
        }
        super.actionPerformed(button);
        return null;
    }

    @Override
    public void initGui() {
        if (ClientSettings.INSTANCE.cnMode.getValue()) {
            buttonList.add(new GuiButton(4, this.width / 2 - 120, this.height - 48, 70, 20, "离线登录"));
            buttonList.add(new GuiButton(5, this.width / 2 - 40, this.height - 48, 70, 20, "微软登录"));
            this.buttonList.add(new GuiButton(0, this.width / 2 + 40, this.height - 48, 70, 20, "返回"));
            buttonList.add(buttonLogin = new GuiButton(2, -1145141919, -1145141919, 70, 20, "登录"));
            buttonList.add(buttonRemove = new GuiButton(3, -1145141919, -1145141919, 70, 20, "删除"));
        }else {
            buttonList.add(new GuiButton(4, this.width / 2 - 120, this.height - 48, 70, 20, "Offline"));
            buttonList.add(new GuiButton(5, this.width / 2 - 40, this.height - 48, 70, 20, "Microsoft"));
            this.buttonList.add(new GuiButton(0, this.width / 2 + 40, this.height - 48, 70, 20, "Exit"));
            buttonList.add(buttonLogin = new GuiButton(2, -1145141919, -1145141919, 70, 20, "Login"));
            buttonList.add(buttonRemove = new GuiButton(3, -1145141919, -1145141919, 70, 20, "Remove"));
        }


        super.initGui();
    }

    public void setRunningThread(Thread runningThread) {
        if (this.runningThread != null) {
            this.runningThread.interrupt();
        }

        this.runningThread = runningThread;
    }
}
