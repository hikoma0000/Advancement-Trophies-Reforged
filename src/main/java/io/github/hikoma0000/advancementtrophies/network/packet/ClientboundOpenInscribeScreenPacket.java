package io.github.hikoma0000.advancementtrophies.network.packet;

import io.github.hikoma0000.advancementtrophies.client.gui.TrophyInscribeScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ClientboundOpenInscribeScreenPacket {
    private final InteractionHand hand;
    private final Set<ResourceLocation> inscribableIds;

    public ClientboundOpenInscribeScreenPacket(InteractionHand hand, Set<ResourceLocation> inscribableIds) {
        this.hand = hand;
        this.inscribableIds = inscribableIds;
    }

    public static void encode(ClientboundOpenInscribeScreenPacket packet, FriendlyByteBuf buffer) {
        buffer.writeEnum(packet.hand);
        buffer.writeCollection(packet.inscribableIds, FriendlyByteBuf::writeResourceLocation);
    }

    public static ClientboundOpenInscribeScreenPacket decode(FriendlyByteBuf buffer) {
        return new ClientboundOpenInscribeScreenPacket(buffer.readEnum(InteractionHand.class),
                buffer.readCollection(HashSet::new, FriendlyByteBuf::readResourceLocation));
    }

    public static void handle(ClientboundOpenInscribeScreenPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> TrophyInscribeScreen.open(packet.hand, packet.inscribableIds)));
        context.setPacketHandled(true);
    }
}
