package io.github.hikoma0000.advancementtrophies.network.packet;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.client.gui.TrophyInscribeScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashSet;
import java.util.Set;

public record ClientboundOpenInscribeScreenPacket(InteractionHand hand, Set<ResourceLocation> inscribableIds)
        implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundOpenInscribeScreenPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(AdvancementTrophies.MOD_ID, "open_inscribe_screen"));

    public static final StreamCodec<FriendlyByteBuf, ClientboundOpenInscribeScreenPacket> STREAM_CODEC =
            StreamCodec.composite(
                    NeoForgeStreamCodecs.enumCodec(InteractionHand.class),
                    ClientboundOpenInscribeScreenPacket::hand,
                    ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new)),
                    ClientboundOpenInscribeScreenPacket::inscribableIds,
                    ClientboundOpenInscribeScreenPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientboundOpenInscribeScreenPacket packet, IPayloadContext context) {
        TrophyInscribeScreen.open(packet.hand(), packet.inscribableIds());
    }
}
