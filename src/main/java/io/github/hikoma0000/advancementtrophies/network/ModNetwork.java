package io.github.hikoma0000.advancementtrophies.network;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.network.packet.ClientboundOpenInscribeScreenPacket;
import io.github.hikoma0000.advancementtrophies.network.packet.ClientboundRememberAdvancementPacket;
import io.github.hikoma0000.advancementtrophies.network.packet.ServerboundInscribeTrophyPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(AdvancementTrophies.MOD_ID, "main"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    private static int packetId = 0;

    private ModNetwork() {
    }

    public static void register() {
        CHANNEL.messageBuilder(ClientboundRememberAdvancementPacket.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientboundRememberAdvancementPacket::decode)
                .encoder(ClientboundRememberAdvancementPacket::encode)
                .consumerMainThread(ClientboundRememberAdvancementPacket::handle)
                .add();
        CHANNEL.messageBuilder(ServerboundInscribeTrophyPacket.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ServerboundInscribeTrophyPacket::decode)
                .encoder(ServerboundInscribeTrophyPacket::encode)
                .consumerMainThread(ServerboundInscribeTrophyPacket::handle)
                .add();
        CHANNEL.messageBuilder(ClientboundOpenInscribeScreenPacket.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientboundOpenInscribeScreenPacket::decode)
                .encoder(ClientboundOpenInscribeScreenPacket::encode)
                .consumerMainThread(ClientboundOpenInscribeScreenPacket::handle)
                .add();
    }

    public static SimpleChannel getChannel() {
        return CHANNEL;
    }

    private static int nextId() {
        return packetId++;
    }
}
