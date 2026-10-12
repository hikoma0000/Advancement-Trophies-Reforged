package io.github.hikoma0000.advancementtrophies.network;

import io.github.hikoma0000.advancementtrophies.network.packet.ClientboundOpenInscribeScreenPacket;
import io.github.hikoma0000.advancementtrophies.network.packet.ClientboundRememberAdvancementPacket;
import io.github.hikoma0000.advancementtrophies.network.packet.ServerboundInscribeTrophyPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Set;

public final class ModNetwork {
    private ModNetwork() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                ClientboundRememberAdvancementPacket.TYPE,
                ClientboundRememberAdvancementPacket.STREAM_CODEC,
                ClientboundRememberAdvancementPacket::handle);
        registrar.playToServer(
                ServerboundInscribeTrophyPacket.TYPE,
                ServerboundInscribeTrophyPacket.STREAM_CODEC,
                ServerboundInscribeTrophyPacket::handle);
        registrar.playToClient(
                ClientboundOpenInscribeScreenPacket.TYPE,
                ClientboundOpenInscribeScreenPacket.STREAM_CODEC,
                ClientboundOpenInscribeScreenPacket::handle);
    }

    public static void sendRemember(ServerPlayer player, ResourceLocation advancementId) {
        PacketDistributor.sendToPlayer(player, new ClientboundRememberAdvancementPacket(advancementId));
    }

    public static void sendInscribe(InteractionHand hand, ResourceLocation advancementId) {
        PacketDistributor.sendToServer(new ServerboundInscribeTrophyPacket(hand, advancementId));
    }

    public static void sendOpenInscribeScreen(ServerPlayer player, InteractionHand hand,
            Set<ResourceLocation> inscribableIds) {
        PacketDistributor.sendToPlayer(player, new ClientboundOpenInscribeScreenPacket(hand, inscribableIds));
    }
}
