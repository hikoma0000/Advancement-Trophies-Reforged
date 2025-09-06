package io.github.hikoma0000.advancementtrophies.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class RenderUtils {
    public static float getCameraPositionYRotationBillboard(Vec3 targetPos, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.cameraEntity == null) {
            return 0.0F;
        }
        Vec3 cameraPos = minecraft.cameraEntity.getEyePosition(partialTick);
        double dx = cameraPos.x() - targetPos.x();
        double dz = cameraPos.z() - targetPos.z();
        return (float) (Mth.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
    }

    public static void renderLabel(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, Component labelText, float maxTextWidth, float[] scale) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        poseStack.pushPose();

        float textWidth = font.width(labelText);
        float scaleMultiplier = 1.0f;
        if (textWidth > maxTextWidth) {
            scaleMultiplier = maxTextWidth / textWidth;
        }
        poseStack.scale(scale[0] * scaleMultiplier, scale[1], scale[2]);

        Matrix4f matrix4f = poseStack.last().pose();
        float x = (float) (-font.width(labelText) / 2);

        font.drawInBatch(labelText, x, 0, 0xFFFFFF, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);

        poseStack.popPose();
    }

}