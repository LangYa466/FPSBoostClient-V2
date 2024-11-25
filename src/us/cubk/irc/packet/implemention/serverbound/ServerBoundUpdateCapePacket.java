package us.cubk.irc.packet.implemention.serverbound;

import us.cubk.irc.packet.IRCPacket;
import us.cubk.irc.packet.annotations.ProtocolField;

/**
 * @author LangYa
 * @since 2024/11/25 12:48
 */
public class ServerBoundUpdateCapePacket implements IRCPacket {
    @ProtocolField("u")
    private final String url;

    public ServerBoundUpdateCapePacket(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
