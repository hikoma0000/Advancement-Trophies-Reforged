package io.github.hikoma0000.advancementtrophies.compat.alexsmobs;

import com.github.alexthe666.alexsmobs.item.ItemTabIcon;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class AlexsMobsCompat {
    private static final ResourceLocation TAB_ICON_ID = ResourceLocation.fromNamespaceAndPath("alexsmobs", "tab_icon");

    private static final float TARGET_SIZE = 1.300f;
    private static final float ENTITY_YAW = 220.0f;
    private static final float Y_OFFSET = -0.500f;

    private static final Map<String, Entity> CACHED_ENTITIES = new HashMap<>();
    private static final Set<EntityType<?>> BLOCKED_TYPES = new HashSet<>();

    private static Boolean alexsMobsLoaded = null;

    private AlexsMobsCompat() {
    }

    public static boolean isAlexsMobsLoaded() {
        if (alexsMobsLoaded == null) {
            alexsMobsLoaded = ModList.get().isLoaded("alexsmobs");
        }
        return alexsMobsLoaded;
    }

    public static boolean needsSafeRender(ItemStack stack) {
        if (!isAlexsMobsLoaded() || stack.isEmpty()) {
            return false;
        }
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (!TAB_ICON_ID.equals(id)) {
            return false;
        }
        return ItemTabIcon.hasCustomEntityDisplay(stack);
    }

    public static boolean tryRender(
            ItemStack stack,
            ItemDisplayContext displayContext,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            int packedOverlay
    ) {
        if (!needsSafeRender(stack)) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null) {
            return false;
        }

        EntityType<?> entityType = ItemTabIcon.getEntityType(stack);
        if (entityType == null || BLOCKED_TYPES.contains(entityType)) {
            return false;
        }

        Entity entity = getOrCreateEntity(entityType, level);
        if (entity == null) {
            return false;
        }

        float scale = resolveScale(stack, entity, TARGET_SIZE);

        poseStack.pushPose();
        poseStack.translate(0.0F, Y_OFFSET, 0.0F);
        poseStack.scale(scale, scale, scale);

        float oldYRot = entity.getYRot();
        float oldXRot = entity.getXRot();
        float oldBodyRot = 0.0F;
        float oldBodyRotO = 0.0F;
        float oldHeadRot = 0.0F;
        float oldHeadRotO = 0.0F;
        LivingEntity living = entity instanceof LivingEntity livingEntity ? livingEntity : null;
        if (living != null) {
            oldBodyRot = living.yBodyRot;
            oldBodyRotO = living.yBodyRotO;
            oldHeadRot = living.yHeadRot;
            oldHeadRotO = living.yHeadRotO;
        }

        entity.setYRot(ENTITY_YAW);
        entity.setXRot(0.0F);
        if (living != null) {
            living.yBodyRot = ENTITY_YAW;
            living.yBodyRotO = ENTITY_YAW;
            living.yHeadRot = ENTITY_YAW;
            living.yHeadRotO = ENTITY_YAW;
        }

        EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();
        dispatcher.setRenderShadow(false);
        try {
            dispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 0.0F, poseStack, buffer, packedLight);
        } catch (Exception ignored) {
            BLOCKED_TYPES.add(entityType);
            CACHED_ENTITIES.remove(entityType.getDescriptionId());
            poseStack.popPose();
            restoreEntityRotation(entity, living, oldYRot, oldXRot, oldBodyRot, oldBodyRotO, oldHeadRot, oldHeadRotO);
            dispatcher.setRenderShadow(true);
            return false;
        }
        dispatcher.setRenderShadow(true);

        restoreEntityRotation(entity, living, oldYRot, oldXRot, oldBodyRot, oldBodyRotO, oldHeadRot, oldHeadRotO);
        poseStack.popPose();
        return true;
    }

    private static Entity getOrCreateEntity(EntityType<?> entityType, Level level) {
        String key = entityType.getDescriptionId();
        Entity cached = CACHED_ENTITIES.get(key);
        if (cached != null) {
            return cached;
        }
        try {
            Entity created = entityType.create(level);
            if (created == null) {
                BLOCKED_TYPES.add(entityType);
                return null;
            }
            CACHED_ENTITIES.put(key, created);
            return created;
        } catch (Exception ignored) {
            BLOCKED_TYPES.add(entityType);
            return null;
        }
    }

    private static float resolveScale(ItemStack stack, Entity entity, float targetSize) {
        float nbtScale = ItemTabIcon.getDisplayMobScale(stack);
        if (nbtScale > 0.0F) {
            return nbtScale;
        }
        float width = entity.getBbWidth();
        float height = entity.getBbHeight();
        float maxDim = Math.max(width, height);
        if (maxDim <= 0.0F) {
            return 1.0F;
        }
        return targetSize / maxDim;
    }

    private static void restoreEntityRotation(
            Entity entity,
            LivingEntity living,
            float yRot,
            float xRot,
            float bodyRot,
            float bodyRotO,
            float headRot,
            float headRotO
    ) {
        entity.setYRot(yRot);
        entity.setXRot(xRot);
        if (living != null) {
            living.yBodyRot = bodyRot;
            living.yBodyRotO = bodyRotO;
            living.yHeadRot = headRot;
            living.yHeadRotO = headRotO;
        }
    }
}
