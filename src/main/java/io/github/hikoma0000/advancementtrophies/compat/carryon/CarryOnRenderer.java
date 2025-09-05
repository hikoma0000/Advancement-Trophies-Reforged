package io.github.hikoma0000.advancementtrophies.compat.carryon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.hikoma0000.advancementtrophies.block.TrophyBlock;
import io.github.hikoma0000.advancementtrophies.config.ClientConfig;
import io.github.hikoma0000.advancementtrophies.util.NBTKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import org.joml.Matrix4f;
import tschipp.carryon.client.render.CarryRenderHelper;
import tschipp.carryon.common.carry.CarryOnData;
import tschipp.carryon.common.carry.CarryOnDataManager;


public class CarryOnRenderer {
    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    private static final float[] ICON_TRANSLATE = {0.0f, 0.75f, 0.0f};
    private static final float[] ICON_SCALE = {0.6f, 0.6f, 0.6f};

    private static final float[] LABEL_TRANSLATE = {0.0f, -0.4f, 0.2f};
    private static final float[] LABEL_SCALE = {0.01f, -0.01f, 0.01f};

    public static void renderInWorld(PoseStack eventPoseStack, float partialTick) {
        if (MINECRAFT.level == null || MINECRAFT.player == null || MINECRAFT.cameraEntity == null) {
            return;
        }

        MultiBufferSource.BufferSource bufferSource = MINECRAFT.renderBuffers().bufferSource();

        for (Player player : MINECRAFT.level.players()) {
            if (MINECRAFT.options.getCameraType().isFirstPerson() && player == MINECRAFT.player && !isFirstPersonModLoaded()) {
                continue;
            }

            CarryOnData carryData = CarryOnDataManager.getCarryData(player);
            if (!carryData.isCarrying(CarryOnData.CarryType.BLOCK)) {
                continue;
            }

            BlockState carriedBlockState = carryData.getBlock();
            if (!(carriedBlockState.getBlock() instanceof TrophyBlock)) {
                continue;
            }

            CompoundTag trophyData = carryData.getNbt().getCompound("tile").getCompound("TrophyData");
            if (trophyData.isEmpty()) {
                continue;
            }

            PoseStack matrix = new PoseStack();
            matrix.last().pose().mul(eventPoseStack.last().pose());

            CarryRenderHelper.applyBlockTransformations(player, partialTick, matrix, carriedBlockState.getBlock());

            int packedLight = MINECRAFT.getEntityRenderDispatcher().getPackedLightCoords(player, partialTick);

            boolean isViewFromBack = player != MINECRAFT.player || CarryRenderHelper.getPerspective() != 0;

            matrix.pushPose();

            if (isViewFromBack) {
                matrix.mulPose(Axis.YP.rotationDegrees(180.0F));
            }

            if (trophyData.contains(NBTKeys.ICON)) {
                matrix.pushPose();
                if (player == MINECRAFT.player) {
                    if (isViewFromBack) {
                        matrix.mulPose(Axis.YP.rotationDegrees(180.0F));
                    }
                } else {
                    matrix.mulPose(Axis.YP.rotationDegrees(180.0F - MINECRAFT.gameRenderer.getMainCamera().getYRot() - CarryRenderHelper.getExactBodyRotationDegrees(player, partialTick)));
                }
                renderIcon(matrix, bufferSource, packedLight, trophyData);
                matrix.popPose();
            }

            if (ClientConfig.SHOW_ACHIEVER_LABEL.get() && trophyData.contains(NBTKeys.ACHIEVER)) {
                renderLabel(matrix, bufferSource, packedLight, trophyData);
            }

            matrix.popPose();

            matrix.popPose();
            matrix.popPose();
        }
        bufferSource.endBatch();
    }

    private static void renderIcon(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CompoundTag nbt) {
        ItemStack iconStack = ItemStack.of(nbt.getCompound(NBTKeys.ICON));
        if (iconStack.isEmpty()) {
            return;
        }

        ItemRenderer itemRenderer = MINECRAFT.getItemRenderer();

        poseStack.pushPose();
        poseStack.translate(ICON_TRANSLATE[0], ICON_TRANSLATE[1], ICON_TRANSLATE[2]);
        poseStack.scale(ICON_SCALE[0], ICON_SCALE[1], ICON_SCALE[2]);

        itemRenderer.renderStatic(iconStack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, MINECRAFT.level, 0);

        poseStack.popPose();
    }

    private static void renderLabel(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CompoundTag nbt) {
        if (!nbt.contains(NBTKeys.ACHIEVER)) {
            return;
        }

        Component labelText = Component.literal(nbt.getString(NBTKeys.ACHIEVER));
        Font font = MINECRAFT.font;

        poseStack.pushPose();
        poseStack.translate(LABEL_TRANSLATE[0], LABEL_TRANSLATE[1], LABEL_TRANSLATE[2]);

        float maxTextWidth = 35.0f;
        float textWidth = font.width(labelText);
        float scaleMultiplier = 1.0f;
        if (textWidth > maxTextWidth) {
            scaleMultiplier = maxTextWidth / textWidth;
        }
        poseStack.scale(LABEL_SCALE[0] * scaleMultiplier, LABEL_SCALE[1], LABEL_SCALE[2]);


        Matrix4f matrix4f = poseStack.last().pose();
        float x = (float) (-font.width(labelText) / 2);

        font.drawInBatch(labelText, x, 0, 0xFFFFFF, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);

        poseStack.popPose();
    }

    private static boolean isFirstPersonModLoaded() {
        return ModList.get().isLoaded("firstperson") || ModList.get().isLoaded("alien_first_person") || ModList.get().isLoaded("realcamera");
    }
}