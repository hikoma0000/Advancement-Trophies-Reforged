package io.github.hikoma0000.advancementtrophies.network;

import io.github.hikoma0000.advancementtrophies.network.packet.ClientboundRememberAdvancementPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetwork {
    private ModNetwork() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                ClientboundRememberAdvancementPacket.TYPE,
                ClientboundRememberAdvancementPacket.STREAM_CODEC,
                ClientboundRememberAdvancementPacket::handle);
    }

    public static void sendRemember(ServerPlayer player, ResourceLocation advancementId) {
        PacketDistributor.sendToPlayer(player, new ClientboundRememberAdvancementPacket(advancementId));
    }
}
