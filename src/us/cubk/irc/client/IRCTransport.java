package us.cubk.irc.client;

import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;
import us.cubk.irc.packet.IRCPacket;
import us.cubk.irc.packet.implemention.clientbound.*;
import us.cubk.irc.packet.implemention.serverbound.ServerBoundHandshakePacket;
import us.cubk.irc.packet.implemention.serverbound.ServerBoundMessagePacket;
import us.cubk.irc.packet.implemention.serverbound.ServerBoundUpdateCapePacket;
import us.cubk.irc.packet.implemention.serverbound.ServerBoundUpdateIgnPacket;
import us.cubk.irc.processor.IRCProtocol;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IRCTransport {
    private final IRCProtocol protocol = new IRCProtocol();
    private final AioSession session;
    private IRCHandler handler;
    public final Map<String,String> userToIgnMap = new ConcurrentHashMap<>();
    public final Map<String,String> ignToUserMap = new ConcurrentHashMap<>();
    public final Map<String,String> userToCapeMap = new ConcurrentHashMap<>();
    public final Map<String,String> capeURLToUserMap = new ConcurrentHashMap<>();

    public IRCTransport(String host, int port,IRCHandler handler) throws IOException {
        this.handler = handler;
        MessageProcessor<IRCPacket> processor = (session, msg) -> {
            if(msg instanceof ClientBoundDisconnectPacket){
                handler.onDisconnected(((ClientBoundDisconnectPacket) msg).getReason());
            }
            if(msg instanceof ClientBoundConnectedPacket){
                handler.onConnected();
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                Runnable task = this::sendInGameUsername;
                scheduler.scheduleAtFixedRate(task, 5, 5, TimeUnit.SECONDS);
            }
            if(msg instanceof ClientBoundUpdateUserListPacket){
                userToIgnMap.clear();
                userToIgnMap.putAll(((ClientBoundUpdateUserListPacket) msg).getUserMap());
                ignToUserMap.clear();
                userToIgnMap.forEach((user, ign) -> ignToUserMap.put(ign,user));
            }
            if(msg instanceof ClientBoundMessagePacket){
                handler.onMessage(((ClientBoundMessagePacket) msg).getSender(),((ClientBoundMessagePacket) msg).getMessage());
            }
            if(msg instanceof ClientBoundUpdateCapeListPacket){
                handler.getCapes(((ClientBoundUpdateCapeListPacket) msg).getUserMap());
                userToCapeMap.clear();
                userToCapeMap.putAll(((ClientBoundUpdateCapeListPacket) msg).getUserMap());
                capeURLToUserMap.clear();
                userToCapeMap.forEach((user, cape) -> capeURLToUserMap.put(cape,user));
            }
        };
        AioQuickClient client = new AioQuickClient(host, port, protocol, processor);
        session = client.start();
    }

    public void sendPacket(IRCPacket packet){
        try {
            byte[] data = protocol.encode(packet);
            session.writeBuffer().writeInt(data.length);
            session.writeBuffer().write(data);
            session.writeBuffer().flush();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public boolean isUser(String name){
        return ignToUserMap.containsKey(name);
    }

    public String getName(String ign){
        return ignToUserMap.get(ign);
    }

    public String getIgn(String name){
        return userToIgnMap.get(name);
    }

    public String getCapeURL(String name){
        return userToCapeMap.get(name);
    }

    public void sendChat(String message){
        sendPacket(new ServerBoundMessagePacket(message));
    }

    public void sendCapeURL(String capeURL){
        sendPacket(new ServerBoundUpdateCapePacket(capeURL));
    }

    public void sendInGameUsername(String username){
        sendPacket(new ServerBoundUpdateIgnPacket(username));
    }

    public void sendInGameUsername(){
        sendInGameUsername(handler.getInGameUsername());
    }

    public void connect(String username,String token){
        sendPacket(new ServerBoundHandshakePacket(username,token));
    }

    public void setHandler(IRCHandler handler) {
        this.handler = handler;
    }
}
