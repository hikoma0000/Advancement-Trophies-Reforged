package io.github.hikoma0000.advancementtrophies.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.hikoma0000.advancementtrophies.client.renderer.TrophyItemRenderer;
import io.github.hikoma0000.advancementtrophies.item.TrophyItem;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @Inject(method = "renderItem", at = @At("TAIL"))
    private void onRenderItem(LivingEntity entity, ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource buffer, int seed, CallbackInfo ci) {
        if (itemStack.getItem() instanceof TrophyItem) {
            TrophyItemRenderer.renderFirstPersonIcon(entity, itemStack, displayContext, poseStack, buffer, seed);
        }
    }
}
