package net.fpsboost.screen.alt;

import com.mojang.authlib.exceptions.AuthenticationException;
import net.fpsboost.module.impl.ClientSettings;
import net.fpsboost.screen.alt.altimpl.MicrosoftAlt;
import net.fpsboost.screen.alt.microsoft.GuiMicrosoftLogin;
import net.fpsboost.screen.alt.microsoft.MicrosoftLogin;
import net.fpsboost.util.Logger;
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
        AltManager.Instance.loadAlts();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        String info;
        if (ClientSettings.isChinese) {
            info = "您当前登录的账号: ";
            status = EnumChatFormatting.YELLOW + "等待切换中...";
        } else {
            info = "Your account: ";
            status = EnumChatFormatting.GREEN + "Waiting...";
        }
        try {
            if (microsoftLogin != null) {
                status = microsoftLogin.getStatus();
            }
        } catch (NullPointerException ignored) {
        }

        drawClientBackground();
        FontManager.client().drawCenteredStringWithShadow(EnumChatFormatting.YELLOW + info + mc.getSession().getUsername(), width / 2.0f, height / 2.0f - 10, -1);
        FontManager.client().drawCenteredStringWithShadow(status, width / 2.0f, height / 2.0f, -1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected ResourceLocation actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) { // 返回按钮
            if (runningThread != null) {
                runningThread.interrupt();
            }
            mc.displayGuiScreen(parentScreen);
        } else if (button.id == 2) { // 登录（针对选中的账户）
            if (selectAlt != null) {
                final Thread thread = getThread();
                setRunningThread(thread);
            }
        } else if (button.id == 4) { // 离线登录
            mc.displayGuiScreen(new GuiAltLogin(this) {
                @Override
                public void onLogin(String account, String password) {
                    final Thread thread = new Thread() {
                        @Override
                        public void run() {
                            final AltManager.LoginStatus loginStatus;
                            try {
                                if (ClientSettings.isChinese) {
                                    status = EnumChatFormatting.YELLOW + "登录中...";
                                } else {
                                    status = EnumChatFormatting.GREEN + "Logging in...";
                                }
                                loginStatus = AltManager.loginAlt(account, password);

                                switch (loginStatus) {
                                    case FAILED:
                                        if (ClientSettings.isChinese) {
                                            status = EnumChatFormatting.RED + "登录失败!";
                                        } else {
                                            status = EnumChatFormatting.RED + "Login failed!";
                                        }
                                        break;
                                    case SUCCESS:
                                        String ign = mc.session.getUsername();
                                        if (ClientSettings.isChinese) {
                                            status = EnumChatFormatting.GREEN + "登录成功! " + ign;
                                        } else {
                                            status = EnumChatFormatting.GREEN + "Logged in! " + ign;
                                        }
                                        AltManager.Instance.saveAlts(); // 登录成功后自动保存
                                        break;
                                }
                            } catch (AuthenticationException e) {
                                Logger.error(e.getMessage());
                                if (ClientSettings.isChinese) {
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
        } else if (button.id == 5) { // 微软登录
            mc.displayGuiScreen(new GuiMicrosoftLogin(this));
        } else if (button.id == 7) { // 查看登录过的账户
            mc.displayGuiScreen(new GuiLoggedAccounts(this));
        }
        super.actionPerformed(button);
        return null;
    }

    private Thread getThread() {
        final Thread thread = new Thread(() -> {
            if (ClientSettings.isChinese) {
                status = EnumChatFormatting.YELLOW + "登录中...";
            } else {
                status = EnumChatFormatting.GREEN + "Logging in...";
            }
            switch (selectAlt.getAccountType()) {
                case OFFLINE:
                    Session session1 = new Session(selectAlt.getUserName(), "", "", "mojang");
                    mc.session = session1;
                    if (ClientSettings.isChinese) {
                        status = EnumChatFormatting.GREEN + "登录成功! " + mc.session.getUsername();
                    } else {
                        status = EnumChatFormatting.GREEN + "Logged in! " + mc.session.getUsername();
                    }
                    AltManager.Instance.getAltList().add(session1);
                    AltManager.Instance.saveAlts();
                    break;
                case MICROSOFT: {
                    try {
                        microsoftLogin = new MicrosoftLogin(((MicrosoftAlt) selectAlt).getRefreshToken());

                        while (Minecraft.getMinecraft().running) {
                            if (microsoftLogin.logged) {
                                Session session = new Session(microsoftLogin.getUserName(), microsoftLogin.getUuid(), microsoftLogin.getAccessToken(), "mojang");
                                mc.session = session;
                                if (ClientSettings.isChinese) {
                                    status = EnumChatFormatting.GREEN + "登录成功! " + mc.session.getUsername();
                                } else {
                                    status = EnumChatFormatting.GREEN + "Logged in! " + mc.session.getUsername();
                                }
                                AltManager.Instance.getAltList().add(session);
                                AltManager.Instance.saveAlts();
                                break;
                            }
                        }
                    } catch (Throwable e) {
                        Logger.error(e.getMessage());
                        if (ClientSettings.isChinese) {
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
        return thread;
    }

    @Override
    public void initGui() {
        // 调整按钮布局：离线登录、微软登录、查看账户、返回（自动保存无需按钮）
        if (ClientSettings.isChinese) {
            buttonList.add(new GuiButton(4, this.width / 2 - 150, this.height - 48, 70, 20, "离线登录"));
            buttonList.add(new GuiButton(5, this.width / 2 - 70, this.height - 48, 70, 20, "微软登录"));
            buttonList.add(new GuiButton(7, this.width / 2 + 10, this.height - 48, 70, 20, "查看账户"));
            buttonList.add(new GuiButton(0, this.width / 2 + 90, this.height - 48, 70, 20, "返回"));
            buttonList.add(buttonLogin = new GuiButton(2, -1145141919, -1145141919, 70, 20, "登录"));
        } else {
            buttonList.add(new GuiButton(4, this.width / 2 - 150, this.height - 48, 70, 20, "Offline"));
            buttonList.add(new GuiButton(5, this.width / 2 - 70, this.height - 48, 70, 20, "Microsoft"));
            buttonList.add(new GuiButton(7, this.width / 2 + 10, this.height - 48, 70, 20, "View Accounts"));
            buttonList.add(new GuiButton(0, this.width / 2 + 90, this.height - 48, 70, 20, "Exit"));
            buttonList.add(buttonLogin = new GuiButton(2, -1145141919, -1145141919, 70, 20, "Login"));
        }
        super.initGui();
    }

    public void setRunningThread(Thread runningThread) {
        if (this.runningThread != null) {
            this.runningThread.interrupt();
        }
        this.runningThread = runningThread;
    }

    @Override
    public void onGuiClosed() {
        AltManager.Instance.saveAlts();
        Logger.info("Alt size: {}",AltManager.Instance.getAltList().size());
        super.onGuiClosed();
    }
}
