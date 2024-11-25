package us.cubk.irc.client;

import java.util.Map;

public interface IRCHandler {
    void onMessage(String sender,String message);
    void onDisconnected(String message);
    void onConnected();
    String getInGameUsername();
    void getCapes(Map<String,String> capeMap);
}
