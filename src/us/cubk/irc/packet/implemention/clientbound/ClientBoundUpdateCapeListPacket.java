package us.cubk.irc.packet.implemention.clientbound;

import us.cubk.irc.packet.IRCPacket;
import us.cubk.irc.packet.annotations.ProtocolField;

import java.util.Map;

/**
 * @author LangYa
 * @since 2024/11/25 12:53
 */
public class ClientBoundUpdateCapeListPacket implements IRCPacket {
    @ProtocolField("c")
    private final Map<String,String> capeMap;

    public ClientBoundUpdateCapeListPacket(Map<String, String> userMap) {
        this.capeMap = userMap;
    }

    public Map<String, String> getUserMap() {
        return capeMap;
    }
}
