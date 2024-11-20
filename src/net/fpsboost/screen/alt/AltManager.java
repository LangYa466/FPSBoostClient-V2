package net.fpsboost.screen.alt;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraft.util.StringUtils;

import java.net.Proxy;
import java.util.ArrayList;

@Getter
public class AltManager {
    public static AltManager Instance = new AltManager();

    private final ArrayList<Alt> altList = new ArrayList<>();

    @SuppressWarnings("CaughtExceptionImmediatelyRethrown")
    public static LoginStatus loginAlt(String account, String password) throws AuthenticationException {
        if (StringUtils.isNullOrEmpty(password)) {
            Minecraft.getMinecraft().session = new Session(account, "", "", "mojang");
            return LoginStatus.SUCCESS;
        } else {
            YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
            YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);
            auth.setUsername(account);
            auth.setPassword(password);

            try {
                auth.logIn();
                Minecraft.getMinecraft().session = new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");

                return LoginStatus.SUCCESS;
            } catch (AuthenticationException e) {
                throw e;
            }
        }
    }

    public enum LoginStatus {
        FAILED,
        SUCCESS,
        EXCEPTION {
            private Exception exception;

            public Exception getException() {
                return exception;
            }

            public void setException(Exception exception) {
                this.exception = exception;
            }
        }
    }
}
