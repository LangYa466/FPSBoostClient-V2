package net.fpsboost.screen.alt.altimpl;

import lombok.Getter;
import net.fpsboost.screen.alt.AccountEnum;
import net.fpsboost.screen.alt.Alt;

@Getter
public class MicrosoftAlt extends Alt {
    private final String refreshToken;

    public MicrosoftAlt(String userName, String refreshToken) {
        super(userName, AccountEnum.MICROSOFT);
        this.refreshToken = refreshToken;
    }
}
