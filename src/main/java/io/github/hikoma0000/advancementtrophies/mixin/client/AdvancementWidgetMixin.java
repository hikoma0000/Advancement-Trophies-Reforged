package io.github.hikoma0000.advancementtrophies.mixin.client;

import io.github.hikoma0000.advancementtrophies.client.gui.TrophyInscribeScreen;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AdvancementWidget.class)
public class AdvancementWidgetMixin {
    @Shadow
    @Final
    private AdvancementNode advancementNode;
    @Shadow
    @Final
    private int x;
    @Shadow
    @Final
    private int y;

    @Inject(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderFakeItem(Lnet/minecraft/world/item/ItemStack;II)V", shift = At.Shift.AFTER))
    private void onDraw(GuiGraphics guiGraphics, int scrollX, int scrollY, CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof TrophyInscribeScreen screen) {
            screen.renderLockedFrame(guiGraphics, advancementNode.holder(), scrollX + x, scrollY + y);
        }
    }
}
