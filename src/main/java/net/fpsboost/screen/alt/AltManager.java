package net.fpsboost.screen.alt;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import lombok.Getter;
import net.fpsboost.Wrapper;
import net.fpsboost.config.ConfigManager;
import net.fpsboost.util.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraft.util.StringUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Getter
public class AltManager implements Wrapper {
    public static AltManager Instance = new AltManager();

    public ArrayList<Session> altList = new ArrayList<>();
    private static final String ALT_FILE_PATH = ConfigManager.dir + File.separator + "alts.json";

    @SuppressWarnings("CaughtExceptionImmediatelyRethrown")
    public static LoginStatus loginAlt(String account, String password) throws AuthenticationException {
        if (StringUtils.isNullOrEmpty(password)) {
            mc.session = new Session(account, "", "", "mojang");
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

    public void saveAlts() {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(ALT_FILE_PATH)) {
            String json = gson.toJson(altList);
            String encoded = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
            writer.write(encoded);
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    public void loadAlts() {
        Gson gson = new Gson();
        File file = new File(ALT_FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String encoded = reader.readLine();
            if (encoded != null) {
                String json = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
                Type listType = new TypeToken<List<Session>>() {}.getType();
                altList = gson.fromJson(json, listType);
            }
        } catch (IOException e) {
            Logger.error(e);
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