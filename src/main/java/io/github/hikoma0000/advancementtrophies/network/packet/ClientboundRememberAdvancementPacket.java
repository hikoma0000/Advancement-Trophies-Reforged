package io.github.hikoma0000.advancementtrophies.network.packet;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.client.ClientRememberHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundRememberAdvancementPacket(ResourceLocation advancementId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundRememberAdvancementPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(AdvancementTrophies.MOD_ID, "remember_advancement"));

    public static final StreamCodec<? super RegistryFriendlyByteBuf, ClientboundRememberAdvancementPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC,
                    ClientboundRememberAdvancementPacket::advancementId,
                    ClientboundRememberAdvancementPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientboundRememberAdvancementPacket packet, IPayloadContext context) {
        ClientRememberHandler.remember(packet.advancementId());
    }
}
