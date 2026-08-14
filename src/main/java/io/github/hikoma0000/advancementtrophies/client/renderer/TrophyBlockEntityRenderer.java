package io.github.hikoma0000.advancementtrophies.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.hikoma0000.advancementtrophies.block.TrophyBlock;
import io.github.hikoma0000.advancementtrophies.block.entity.TrophyBlockEntity;
import io.github.hikoma0000.advancementtrophies.client.util.RenderUtils;
import io.github.hikoma0000.advancementtrophies.compat.alexsmobs.AlexsMobsCompat;
import io.github.hikoma0000.advancementtrophies.compat.citadel.CitadelCompat;
import io.github.hikoma0000.advancementtrophies.component.TrophyData;
import io.github.hikoma0000.advancementtrophies.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TrophyBlockEntityRenderer implements BlockEntityRenderer<TrophyBlockEntity> {
    private final Minecraft minecraft = Minecraft.getInstance();

    private static final float[] ICON_TRANSLATE = {0.0f, 1.2f, 0.0f};
    private static final float[] ICON_SCALE = {0.5f, 0.5f, 0.5f};

    private static final float[] LABEL_TRANSLATE = {0.0f, 0.1f, 0.2f};
    private static final float[] LABEL_SCALE = {0.01f, -0.01f, 0.01f};
    private static final float MAX_LABEL_WIDTH = 35.0f;


    private static final float CENTER_TRANSLATE = 0.5f;


    public TrophyBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TrophyBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        TrophyData data = pBlockEntity.getTrophyData();

        if (data == null) {
            return;
        }

        Vec3 cameraPos = this.minecraft.gameRenderer.getMainCamera().getPosition();
        if (cameraPos.distanceToSqr(Vec3.atCenterOf(pBlockEntity.getBlockPos())) > 64.0D * 64.0D) {
            return;
        }

        pPoseStack.pushPose();
        pPoseStack.translate(CENTER_TRANSLATE, 0.0, CENTER_TRANSLATE);

        BlockState blockState = pBlockEntity.getBlockState();

        if (data.hasIcon()) {
            pPoseStack.pushPose();
            BlockPos blockPos = pBlockEntity.getBlockPos();
            Vec3 blockCenter = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
            float angle = RenderUtils.getCameraPositionYRotationBillboard(blockCenter, pPartialTick);
            pPoseStack.mulPose(Axis.YP.rotationDegrees(-angle + 180.0f));

            renderIcon(pPoseStack, pBufferSource, pPackedLight, pPackedOverlay, data);
            pPoseStack.popPose();
        }

        if (ClientConfig.SHOW_ACHIEVER_LABEL.get() && data.hasAchiever()) {
            pPoseStack.pushPose();
            float rotation = -blockState.getValue(TrophyBlock.FACING).toYRot();
            pPoseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            pPoseStack.translate(LABEL_TRANSLATE[0], LABEL_TRANSLATE[1], LABEL_TRANSLATE[2]);
            RenderUtils.renderLabel(pPoseStack, pBufferSource, pPackedLight, Component.literal(data.achiever()), MAX_LABEL_WIDTH, LABEL_SCALE);
            pPoseStack.popPose();
        }

        pPoseStack.popPose();
    }

    private void renderIcon(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, TrophyData data) {
        ItemStack iconStack = data.icon();
        if (iconStack.isEmpty()) {
            return;
        }

        ItemRenderer itemRenderer = minecraft.getItemRenderer();

        poseStack.pushPose();
        poseStack.translate(ICON_TRANSLATE[0], ICON_TRANSLATE[1], ICON_TRANSLATE[2]);
        poseStack.scale(ICON_SCALE[0], ICON_SCALE[1], ICON_SCALE[2]);

        if (!CitadelCompat.tryRender(iconStack, ItemDisplayContext.FIXED, poseStack, bufferSource, packedLight, packedOverlay)
                && !AlexsMobsCompat.tryRender(iconStack, ItemDisplayContext.FIXED, poseStack, bufferSource, packedLight, packedOverlay)) {
            itemRenderer.renderStatic(iconStack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, minecraft.level, 0);
        }

        poseStack.popPose();
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}
