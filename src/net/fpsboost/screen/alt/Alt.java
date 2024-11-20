package net.fpsboost.screen.alt;

import lombok.Getter;

@Getter
public abstract class Alt {
    private final String userName;
    private final AccountEnum accountType;

    public Alt(String userName,AccountEnum accountType) {
        this.userName = userName;
        this.accountType = accountType;
    }
}
