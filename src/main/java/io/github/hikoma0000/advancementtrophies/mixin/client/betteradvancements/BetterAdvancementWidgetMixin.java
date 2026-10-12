package io.github.hikoma0000.advancementtrophies.mixin.client.betteradvancements;

import io.github.hikoma0000.advancementtrophies.client.gui.TrophyInscribeScreen;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "betteradvancements.common.gui.BetterAdvancementWidget", remap = false)
public abstract class BetterAdvancementWidgetMixin {
    @Shadow
    protected int x;
    @Shadow
    protected int y;

    @Shadow
    public abstract Advancement getAdvancement();

    @Inject(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderFakeItem(Lnet/minecraft/world/item/ItemStack;II)V", shift = At.Shift.AFTER, remap = true))
    private void onDraw(GuiGraphics guiGraphics, int scrollX, int scrollY, CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof TrophyInscribeScreen screen) {
            screen.renderLockedFrame(guiGraphics, getAdvancement(), scrollX + x, scrollY + y);
        }
    }
}
