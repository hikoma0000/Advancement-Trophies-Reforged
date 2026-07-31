package io.github.hikoma0000.advancementtrophies.compat.citadel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CitadelCompat {
    private static final ResourceLocation ICON_ITEM_ID = new ResourceLocation("citadel", "icon_item");
    private static final ResourceLocation EFFECT_ITEM_ID = new ResourceLocation("citadel", "effect_item");
    private static final ResourceLocation DEFAULT_ICON_TEXTURE =
            new ResourceLocation("citadel", "textures/gui/book/icon_default.png");

    private static final Map<String, ResourceLocation> LOADED_ICONS = new HashMap<>();

    private static Boolean citadelLoaded = null;
    private static List<MobEffect> mobEffectList = null;

    public static boolean isCitadelLoaded() {
        if (citadelLoaded == null) {
            citadelLoaded = ModList.get().isLoaded("citadel");
        }
        return citadelLoaded;
    }

    public static boolean needsSafeRender(ItemStack stack) {
        if (!isCitadelLoaded() || stack.isEmpty()) {
            return false;
        }
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return ICON_ITEM_ID.equals(id) || EFFECT_ITEM_ID.equals(id);
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
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        BakedModel model = itemRenderer.getModel(stack, null, null, 0);

        poseStack.pushPose();
        model.getTransforms().getTransform(displayContext).apply(false, poseStack);
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        int bright = LightTexture.FULL_BRIGHT;
        if (ICON_ITEM_ID.equals(id)) {
            renderIconItem(stack, poseStack, buffer, bright, packedOverlay);
        } else {
            renderEffectItem(stack, poseStack, buffer, bright, packedOverlay);
        }

        poseStack.popPose();
        return true;
    }

    private static void renderIconItem(
            ItemStack stack,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            int packedOverlay
    ) {
        ResourceLocation texture = DEFAULT_ICON_TEXTURE;
        if (stack.getTag() != null && stack.getTag().contains("IconLocation")) {
            String iconLocationStr = stack.getTag().getString("IconLocation");
            texture = LOADED_ICONS.computeIfAbsent(iconLocationStr, key -> {
                try {
                    return new ResourceLocation(key);
                } catch (Exception e) {
                    return DEFAULT_ICON_TEXTURE;
                }
            });
        }

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 0.5F);
        drawTexturedQuad(poseStack, buffer, texture, 0.0F, 0.0F, 1.0F, 1.0F, packedLight, packedOverlay);
        poseStack.popPose();
    }

    private static void renderEffectItem(
            ItemStack stack,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            int packedOverlay
    ) {
        MobEffect effect;
        if (stack.getTag() != null && stack.getTag().contains("DisplayEffect")) {
            String displayId = stack.getTag().getString("DisplayEffect");
            effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(displayId));
        } else {
            if (mobEffectList == null) {
                mobEffectList = ForgeRegistries.MOB_EFFECTS.getValues().stream().toList();
            }
            int size = mobEffectList.size();
            int time = (int) (Util.getMillis() / 500L);
            effect = size > 0 ? mobEffectList.get(time % size) : null;
        }

        if (effect == null) {
            effect = MobEffects.MOVEMENT_SPEED;
        }

        MobEffectTextureManager textures = Minecraft.getInstance().getMobEffectTextures();
        TextureAtlasSprite sprite = textures.get(effect);

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 0.5F);
        drawTexturedQuad(
                poseStack,
                buffer,
                sprite.atlasLocation(),
                sprite.getU0(),
                sprite.getV0(),
                sprite.getU1(),
                sprite.getV1(),
                packedLight,
                packedOverlay
        );
        poseStack.popPose();
    }

    private static void drawTexturedQuad(
            PoseStack poseStack,
            MultiBufferSource buffer,
            ResourceLocation texture,
            float u0,
            float v0,
            float u1,
            float v1,
            int packedLight,
            int packedOverlay
    ) {
        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));

        vertex(consumer, matrix, 1.0F, 1.0F, 0.0F, u1, v0, packedLight, packedOverlay);
        vertex(consumer, matrix, 0.0F, 1.0F, 0.0F, u0, v0, packedLight, packedOverlay);
        vertex(consumer, matrix, 0.0F, 0.0F, 0.0F, u0, v1, packedLight, packedOverlay);
        vertex(consumer, matrix, 1.0F, 0.0F, 0.0F, u1, v1, packedLight, packedOverlay);
    }

    private static void vertex(
            VertexConsumer consumer,
            Matrix4f matrix,
            float x,
            float y,
            float z,
            float u,
            float v,
            int packedLight,
            int packedOverlay
    ) {
        consumer.vertex(matrix, x, y, z)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(packedOverlay)
                .uv2(packedLight)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
    }
}
