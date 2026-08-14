package io.github.hikoma0000.advancementtrophies.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.hikoma0000.advancementtrophies.compat.alexsmobs.AlexsMobsCompat;
import io.github.hikoma0000.advancementtrophies.compat.citadel.CitadelCompat;
import io.github.hikoma0000.advancementtrophies.component.TrophyData;
import io.github.hikoma0000.advancementtrophies.init.ModDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TrophyItemRenderer {
    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    private static final float[] ICON_TRANSLATE = {0.0f, 0.325f, 0.0f};
    private static final float[] ICON_SCALE = {0.2f, 0.2f, 0.2f};

    private static final float POSITION_FOLLOW_SPEED = 50.0f;
    private static final float ROTATION_FOLLOW_SPEED = 50.0f;

    private static final FollowState FOLLOW = new FollowState();

    public static void renderFirstPersonIcon(LivingEntity entity, ItemStack itemStack,
            ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer,
            int packedLight) {
        if (displayContext != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                && displayContext != ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            return;
        }

        TrophyData data = itemStack.get(ModDataComponents.TROPHY_DATA.get());
        if (data == null || !data.hasIcon()) {
            return;
        }

        ItemStack iconStack = data.icon();
        if (iconStack.isEmpty()) {
            return;
        }

        float dt = getDeltaSeconds();

        poseStack.pushPose();

        Matrix4f modelView = new Matrix4f(RenderSystem.getModelViewMatrix());
        Matrix4f pose = poseStack.last().pose();
        Matrix4f view = new Matrix4f(modelView).mul(pose);
        view.translate(ICON_TRANSLATE[0], ICON_TRANSLATE[1], ICON_TRANSLATE[2]);

        float scaleX = columnLength(view, 0);
        float scaleY = columnLength(view, 1);
        float scaleZ = columnLength(view, 2);

        Vector3f tipPos = new Vector3f(view.m30(), view.m31(), view.m32());
        FOLLOW.smoothPosition(tipPos, dt);

        float depth = Math.max(0.05f, Math.abs(FOLLOW.pos.z));
        float targetYaw = (float) Math.atan2(-FOLLOW.pos.x, depth) + (float) Math.PI;
        float targetPitch = (float) Math.atan2(-FOLLOW.pos.y, depth);
        FOLLOW.smoothRotation(targetYaw, targetPitch, dt);

        setIdentityRotation(view, scaleX, scaleY, scaleZ);
        view.m30(FOLLOW.pos.x);
        view.m31(FOLLOW.pos.y);
        view.m32(FOLLOW.pos.z);

        pose.set(new Matrix4f(modelView).invert().mul(view));
        poseStack.last().normal().identity();

        poseStack.mulPose(Axis.YP.rotation(FOLLOW.yaw));
        poseStack.mulPose(Axis.XP.rotation(FOLLOW.pitch));
        poseStack.scale(ICON_SCALE[0], ICON_SCALE[1], ICON_SCALE[2]);

        ItemRenderer itemRenderer = MINECRAFT.getItemRenderer();
        if (!CitadelCompat.tryRender(iconStack, ItemDisplayContext.FIXED, poseStack, buffer, packedLight,
                OverlayTexture.NO_OVERLAY)
                && !AlexsMobsCompat.tryRender(iconStack, ItemDisplayContext.FIXED, poseStack, buffer, packedLight,
                        OverlayTexture.NO_OVERLAY)) {
            itemRenderer.renderStatic(
                    iconStack,
                    ItemDisplayContext.FIXED,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    buffer,
                    entity.level(),
                    entity.getId());
        }

        poseStack.popPose();
    }

    private static float getDeltaSeconds() {
        float deltaTicks = MINECRAFT.getTimer().getGameTimeDeltaTicks();
        return Mth.clamp(deltaTicks / 20.0f, 0.0f, 0.05f);
    }

    private static void setIdentityRotation(Matrix4f matrix, float scaleX, float scaleY, float scaleZ) {
        matrix.m00(scaleX);
        matrix.m01(0.0f);
        matrix.m02(0.0f);
        matrix.m10(0.0f);
        matrix.m11(scaleY);
        matrix.m12(0.0f);
        matrix.m20(0.0f);
        matrix.m21(0.0f);
        matrix.m22(scaleZ);
    }

    private static float columnLength(Matrix4f matrix, int column) {
        return switch (column) {
            case 0 -> (float) Math.sqrt(
                    matrix.m00() * matrix.m00() + matrix.m10() * matrix.m10() + matrix.m20() * matrix.m20());
            case 1 -> (float) Math.sqrt(
                    matrix.m01() * matrix.m01() + matrix.m11() * matrix.m11() + matrix.m21() * matrix.m21());
            default -> (float) Math.sqrt(
                    matrix.m02() * matrix.m02() + matrix.m12() * matrix.m12() + matrix.m22() * matrix.m22());
        };
    }

    private static final class FollowState {
        private static final float SNAP_DISTANCE_SQR = 1.0f;

        private final Vector3f pos = new Vector3f();
        private float yaw;
        private float pitch;
        private boolean initialized;
        private boolean justSnapped;

        private void smoothPosition(Vector3f target, float dt) {
            if (!initialized || pos.distanceSquared(target) > SNAP_DISTANCE_SQR) {
                pos.set(target);
                initialized = true;
                justSnapped = true;
                return;
            }
            justSnapped = false;
            float t = 1.0f - (float) Math.exp(-POSITION_FOLLOW_SPEED * dt);
            pos.lerp(target, t);
        }

        private void smoothRotation(float targetYaw, float targetPitch, float dt) {
            if (justSnapped) {
                yaw = targetYaw;
                pitch = targetPitch;
                return;
            }
            float t = 1.0f - (float) Math.exp(-ROTATION_FOLLOW_SPEED * dt);
            yaw = Mth.lerp(t, yaw, targetYaw);
            pitch = Mth.lerp(t, pitch, targetPitch);
        }
    }
}
