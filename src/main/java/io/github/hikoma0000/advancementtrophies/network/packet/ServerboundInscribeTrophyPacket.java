package io.github.hikoma0000.advancementtrophies.network.packet;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.init.ModDataComponents;
import io.github.hikoma0000.advancementtrophies.init.ModItems;
import io.github.hikoma0000.advancementtrophies.util.TrophyUtils;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.stream.StreamSupport;

public record ServerboundInscribeTrophyPacket(InteractionHand hand, ResourceLocation advancementId)
        implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerboundInscribeTrophyPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(AdvancementTrophies.MOD_ID, "inscribe_trophy"));

    public static final StreamCodec<FriendlyByteBuf, ServerboundInscribeTrophyPacket> STREAM_CODEC =
            StreamCodec.composite(
                    NeoForgeStreamCodecs.enumCodec(InteractionHand.class),
                    ServerboundInscribeTrophyPacket::hand,
                    ResourceLocation.STREAM_CODEC,
                    ServerboundInscribeTrophyPacket::advancementId,
                    ServerboundInscribeTrophyPacket::new);

    private static final ResourceLocation OBTAIN_TROPHY_ADVANCEMENT_ID = ResourceLocation.fromNamespaceAndPath(
            AdvancementTrophies.MOD_ID, "obtain_trophy");
    private static final ResourceLocation STACK_OVERFLOW_ADVANCEMENT_ID = ResourceLocation.fromNamespaceAndPath(
            AdvancementTrophies.MOD_ID, "stack_overflow");
    private static final String STACK_OVERFLOW_CRITERION = "inscribed";

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ServerboundInscribeTrophyPacket packet, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack stack = player.getItemInHand(packet.hand());
        if (!ModItems.isTrophy(stack) || TrophyUtils.hasTrophyData(stack)) {
            return;
        }

        AdvancementHolder holder = player.server.getAdvancements().get(packet.advancementId());
        if (holder == null) {
            return;
        }

        DisplayInfo display = holder.value().display().orElse(null);
        if (TrophyUtils.getTrophyItem(display) != stack.getItem()) {
            return;
        }

        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
        if (!progress.isDone()) {
            return;
        }

        Date completedDate = StreamSupport.stream(progress.getCompletedCriteria().spliterator(), false)
                .map(criterion -> progress.getCriterion(criterion).getObtained())
                .filter(Objects::nonNull)
                .max(Instant::compareTo)
                .map(Date::from)
                .orElseGet(Date::new);

        stack.set(ModDataComponents.TROPHY_DATA.get(),
                TrophyUtils.createTrophyData(player, holder, display, completedDate));
        player.level().playSound(null, player.blockPosition(), SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT,
                SoundSource.PLAYERS, 1.0F, 1.0F);

        if (OBTAIN_TROPHY_ADVANCEMENT_ID.equals(packet.advancementId())) {
            AdvancementHolder stackOverflow = player.server.getAdvancements().get(STACK_OVERFLOW_ADVANCEMENT_ID);
            if (stackOverflow != null) {
                player.getAdvancements().award(stackOverflow, STACK_OVERFLOW_CRITERION);
            }
        }
    }
}
