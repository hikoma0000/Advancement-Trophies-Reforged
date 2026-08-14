package io.github.hikoma0000.advancementtrophies.event;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.block.TrophyBlock;
import io.github.hikoma0000.advancementtrophies.block.entity.TrophyBlockEntity;
import io.github.hikoma0000.advancementtrophies.component.TrophyData;
import io.github.hikoma0000.advancementtrophies.network.ModNetwork;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TrophyBrushHandler {
    private static final int BRUSH_TICK_INTERVAL = 10;
    private static final int BRUSH_TICK_OFFSET = 5;
    private static final int BRUSH_USE_DURATION = 200;
    private static final int POLISH_THRESHOLD = 3;
    private static final int PARTICLE_COUNT = 10;
    private static final double BRUSH_REACH = 5.0D;

    private static final ResourceLocation POLISH_ADVANCEMENT_ID = ResourceLocation.fromNamespaceAndPath(
            AdvancementTrophies.MOD_ID, "polish_trophy");
    private static final String POLISH_CRITERION = "polished";

    private static final Map<UUID, BrushSession> SESSIONS = new HashMap<>();

    @SubscribeEvent
    public static void onUseItemTick(LivingEntityUseItemEvent.Tick event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) {
            return;
        }

        ItemStack stack = event.getItem();
        if (!(stack.getItem() instanceof BrushItem)) {
            return;
        }

        int remaining = event.getDuration();
        if (remaining < 0) {
            return;
        }

        int usedTicks = BRUSH_USE_DURATION - remaining + 1;
        if (usedTicks % BRUSH_TICK_INTERVAL != BRUSH_TICK_OFFSET) {
            return;
        }

        Level level = player.level();
        HitResult hitResult = player.pick(BRUSH_REACH, 0.0F, false);
        if (!(hitResult instanceof BlockHitResult blockHit) || hitResult.getType() != HitResult.Type.BLOCK) {

            clearSession(player);
            return;
        }

        BlockPos pos = blockHit.getBlockPos();
        if (!(level.getBlockState(pos).getBlock() instanceof TrophyBlock)) {
            clearSession(player);
            return;
        }

        if (level.isClientSide) {
            spawnPolishParticles(level, pos);
            return;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof TrophyBlockEntity trophyBlockEntity)) {
            clearSession(player);
            return;
        }

        TrophyData trophyData = trophyBlockEntity.getTrophyData();
        if (trophyData == null || !trophyData.hasAdvancementId()) {
            clearSession(player);
            return;
        }

        ResourceLocation advancementId = trophyData.advancementId();

        BrushSession session = SESSIONS.computeIfAbsent(player.getUUID(), id -> new BrushSession());

        if (!pos.equals(session.pos)) {
            session.pos = pos.immutable();
            session.brushCount = 0;
        }

        session.brushCount++;
        if (session.brushCount < POLISH_THRESHOLD) {
            return;
        }

        session.brushCount = 0;

        ModNetwork.sendRemember(serverPlayer, advancementId);

        awardPolishAdvancement(serverPlayer);
    }

    @SubscribeEvent
    public static void onUseItemStop(LivingEntityUseItemEvent.Stop event) {
        if (event.getEntity() instanceof Player player) {
            clearSession(player);
        }
    }

    @SubscribeEvent
    public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player) {
            clearSession(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        SESSIONS.remove(event.getEntity().getUUID());
    }

    private static void awardPolishAdvancement(ServerPlayer player) {
        AdvancementHolder advancement = player.server.getAdvancements().get(POLISH_ADVANCEMENT_ID);
        if (advancement == null) {
            return;
        }
        player.getAdvancements().award(advancement, POLISH_CRITERION);
    }

    private static void spawnPolishParticles(Level level, BlockPos pos) {
        double centerX = pos.getX() + 0.5D;
        double centerY = pos.getY() + 0.5D;
        double centerZ = pos.getZ() + 0.5D;

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double ox = (level.random.nextDouble() - 0.5D) * 0.9D;
            double oy = (level.random.nextDouble() - 0.5D) * 0.9D;
            double oz = (level.random.nextDouble() - 0.5D) * 0.9D;
            level.addParticle(ParticleTypes.WAX_OFF, centerX + ox, centerY + oy, centerZ + oz, 0.0D, 0.02D, 0.0D);
        }
    }

    private static void clearSession(Player player) {
        BrushSession session = SESSIONS.get(player.getUUID());
        if (session != null) {
            session.brushCount = 0;
            session.pos = null;
        }
    }

    private static final class BrushSession {
        private BlockPos pos;
        private int brushCount;
    }
}
