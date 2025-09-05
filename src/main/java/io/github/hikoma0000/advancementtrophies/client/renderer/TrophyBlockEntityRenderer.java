package io.github.hikoma0000.advancementtrophies.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.hikoma0000.advancementtrophies.block.TrophyBlock;
import io.github.hikoma0000.advancementtrophies.block.entity.TrophyBlockEntity;
import io.github.hikoma0000.advancementtrophies.config.ClientConfig;
import io.github.hikoma0000.advancementtrophies.util.NBTKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class TrophyBlockEntityRenderer implements BlockEntityRenderer<TrophyBlockEntity> {
    private final Minecraft minecraft = Minecraft.getInstance();

    private static final float[] ICON_TRANSLATE = {0.0f, 1.2f, 0.0f};
    private static final float[] ICON_SCALE = {0.5f, 0.5f, 0.5f};
    private static final float[] ICON_ROTATION = {0.0f, 180.0f, 0.0f};

    private static final float[] LABEL_TRANSLATE = {0.0f, 0.1f, 0.2f};
    private static final float[] LABEL_SCALE = {0.01f, -0.01f, 0.01f};

    private static final float CENTER_TRANSLATE = 0.5f;


    public TrophyBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TrophyBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        CompoundTag nbt = pBlockEntity.getTrophyData();

        if (nbt == null || nbt.isEmpty()) {
            return;
        }

        pPoseStack.pushPose();
        pPoseStack.translate(CENTER_TRANSLATE, 0.0, CENTER_TRANSLATE);

        BlockState blockState = pBlockEntity.getBlockState();

        if (nbt.contains(NBTKeys.ICON)) {
            pPoseStack.pushPose();

            if (this.minecraft.cameraEntity != null) {
                BlockPos blockPos = pBlockEntity.getBlockPos();
                Vec3 blockCenter = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);

                Vec3 cameraPos = this.minecraft.cameraEntity.getEyePosition(pPartialTick);

                double dx = cameraPos.x() - blockCenter.x();
                double dz = cameraPos.z() - blockCenter.z();

                float angle = (float) (Mth.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;

                pPoseStack.mulPose(Axis.YP.rotationDegrees(-angle));
            }

            renderIcon(pPoseStack, pBufferSource, pPackedLight, pPackedOverlay, nbt);
            pPoseStack.popPose();
        }

        if (ClientConfig.SHOW_ACHIEVER_LABEL.get() && nbt.contains(NBTKeys.ACHIEVER)) {
            pPoseStack.pushPose();
            float rotation = -blockState.getValue(TrophyBlock.FACING).toYRot();
            pPoseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            renderLabel(pPoseStack, pBufferSource, pPackedLight, nbt);
            pPoseStack.popPose();
        }

        pPoseStack.popPose();
    }

    private void renderIcon(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, CompoundTag nbt) {
        ItemStack iconStack = ItemStack.of(nbt.getCompound(NBTKeys.ICON));
        if (iconStack.isEmpty()) {
            return;
        }

        ItemRenderer itemRenderer = minecraft.getItemRenderer();

        poseStack.pushPose();
        poseStack.translate(ICON_TRANSLATE[0], ICON_TRANSLATE[1], ICON_TRANSLATE[2]);
        poseStack.scale(ICON_SCALE[0], ICON_SCALE[1], ICON_SCALE[2]);
        poseStack.mulPose(Axis.XP.rotationDegrees(ICON_ROTATION[0]));
        poseStack.mulPose(Axis.YP.rotationDegrees(ICON_ROTATION[1]));
        poseStack.mulPose(Axis.ZP.rotationDegrees(ICON_ROTATION[2]));

        itemRenderer.renderStatic(iconStack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, minecraft.level, 0);

        poseStack.popPose();
    }

    private void renderLabel(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CompoundTag nbt) {
        if (!nbt.contains(NBTKeys.ACHIEVER)) {
            return;
        }

        Component labelText = Component.literal(nbt.getString(NBTKeys.ACHIEVER));
        Font font = minecraft.font;

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
}