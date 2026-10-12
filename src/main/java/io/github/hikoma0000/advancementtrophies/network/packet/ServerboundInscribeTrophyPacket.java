package io.github.hikoma0000.advancementtrophies.network.packet;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.init.ModItems;
import io.github.hikoma0000.advancementtrophies.util.TrophyUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.Date;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

public class ServerboundInscribeTrophyPacket {
    private static final ResourceLocation OBTAIN_TROPHY_ADVANCEMENT_ID = new ResourceLocation(
            AdvancementTrophies.MOD_ID, "obtain_trophy");
    private static final ResourceLocation STACK_OVERFLOW_ADVANCEMENT_ID = new ResourceLocation(
            AdvancementTrophies.MOD_ID, "stack_overflow");
    private static final String STACK_OVERFLOW_CRITERION = "inscribed";

    private final InteractionHand hand;
    private final ResourceLocation advancementId;

    public ServerboundInscribeTrophyPacket(InteractionHand hand, ResourceLocation advancementId) {
        this.hand = hand;
        this.advancementId = advancementId;
    }

    public static void encode(ServerboundInscribeTrophyPacket packet, FriendlyByteBuf buffer) {
        buffer.writeEnum(packet.hand);
        buffer.writeResourceLocation(packet.advancementId);
    }

    public static ServerboundInscribeTrophyPacket decode(FriendlyByteBuf buffer) {
        return new ServerboundInscribeTrophyPacket(buffer.readEnum(InteractionHand.class),
                buffer.readResourceLocation());
    }

    public static void handle(ServerboundInscribeTrophyPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            ItemStack stack = player.getItemInHand(packet.hand);
            if (!ModItems.isTrophy(stack) || TrophyUtils.hasTrophyData(stack)) {
                return;
            }

            Advancement advancement = player.server.getAdvancements().getAdvancement(packet.advancementId);
            if (advancement == null || TrophyUtils.getTrophyItem(advancement.getDisplay()) != stack.getItem()) {
                return;
            }

            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
            if (!progress.isDone()) {
                return;
            }

            Date completedDate = StreamSupport.stream(progress.getCompletedCriteria().spliterator(), false)
                    .map(criterion -> progress.getCriterion(criterion).getObtained())
                    .filter(Objects::nonNull)
                    .max(Date::compareTo)
                    .orElseGet(Date::new);

            TrophyUtils.writeTrophyData(stack.getOrCreateTag(), player, advancement, completedDate);
            player.level().playSound(null, player.blockPosition(), SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT,
                    SoundSource.PLAYERS, 1.0F, 1.0F);

            if (OBTAIN_TROPHY_ADVANCEMENT_ID.equals(packet.advancementId)) {
                Advancement stackOverflow = player.server.getAdvancements().getAdvancement(STACK_OVERFLOW_ADVANCEMENT_ID);
                if (stackOverflow != null) {
                    player.getAdvancements().award(stackOverflow, STACK_OVERFLOW_CRITERION);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
