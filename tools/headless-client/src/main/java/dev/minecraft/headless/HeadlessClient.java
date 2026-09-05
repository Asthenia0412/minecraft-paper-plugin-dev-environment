package dev.minecraft.headless;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.geysermc.mcprotocollib.auth.SessionService;
import org.geysermc.mcprotocollib.network.ClientSession;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.event.session.DisconnectedEvent;
import org.geysermc.mcprotocollib.network.event.session.SessionAdapter;
import org.geysermc.mcprotocollib.network.factory.ClientNetworkSessionFactory;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.MinecraftConstants;
import org.geysermc.mcprotocollib.protocol.MinecraftProtocol;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundLoginPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatCommandPacket;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class HeadlessClient {
    private static final String HOST = System.getenv().getOrDefault("MC_TEST_HOST", "127.0.0.1");
    private static final int PORT = Integer.parseInt(System.getenv().getOrDefault("MC_TEST_PORT", "25565"));
    private static final String USERNAME = System.getenv().getOrDefault("MC_TEST_USERNAME", "DevPlayer");
    private static final String COMMAND = System.getenv().getOrDefault("MC_TEST_COMMAND", "/devkit status");
    private static final String EXPECTED = System.getenv().getOrDefault("MC_TEST_EXPECTED", "ExamplePlugin status: OK");

    private HeadlessClient() { }

    public static void main(String[] args) throws Exception {
        MinecraftProtocol protocol = new MinecraftProtocol(USERNAME);
        ClientSession client = ClientNetworkSessionFactory.factory()
                .setRemoteSocketAddress(new InetSocketAddress(HOST, PORT))
                .setProtocol(protocol)
                .create();
        client.setFlag(MinecraftConstants.SESSION_SERVICE_KEY, new SessionService());

        CountDownLatch finished = new CountDownLatch(1);
        AtomicBoolean passed = new AtomicBoolean(false);
        client.addListener(new SessionAdapter() {
            @Override
            public void packetReceived(Session session, Packet packet) {
                if (packet instanceof ClientboundLoginPacket) {
                    System.out.println("headless: joined as " + USERNAME);
                    session.send(new ServerboundChatCommandPacket(COMMAND.startsWith("/") ? COMMAND.substring(1) : COMMAND));
                } else if (packet instanceof ClientboundSystemChatPacket chat) {
                    String content = PlainTextComponentSerializer.plainText().serialize(chat.getContent());
                    System.out.println("headless: received=" + content);
                    if (content.contains(EXPECTED)) {
                        passed.set(true);
                        session.disconnect(Component.text("headless test complete"));
                        finished.countDown();
                    }
                }
            }

            @Override
            public void disconnected(DisconnectedEvent event) {
                finished.countDown();
            }
        });
        client.connect();
        if (!finished.await(30, TimeUnit.SECONDS) || !passed.get()) {
            throw new IllegalStateException("Headless integration assertion failed");
        }
        System.out.println("headless: passed");
    }
}
