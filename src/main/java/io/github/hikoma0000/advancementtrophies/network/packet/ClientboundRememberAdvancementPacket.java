package io.github.hikoma0000.advancementtrophies.network.packet;

import io.github.hikoma0000.advancementtrophies.client.ClientRememberHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundRememberAdvancementPacket {
    private final ResourceLocation advancementId;

    public ClientboundRememberAdvancementPacket(ResourceLocation advancementId) {
        this.advancementId = advancementId;
    }

    public static void encode(ClientboundRememberAdvancementPacket packet, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(packet.advancementId);
    }

    public static ClientboundRememberAdvancementPacket decode(FriendlyByteBuf buffer) {
        return new ClientboundRememberAdvancementPacket(buffer.readResourceLocation());
    }

    public static void handle(ClientboundRememberAdvancementPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientRememberHandler.remember(packet.advancementId)));
        context.setPacketHandled(true);
    }
}
