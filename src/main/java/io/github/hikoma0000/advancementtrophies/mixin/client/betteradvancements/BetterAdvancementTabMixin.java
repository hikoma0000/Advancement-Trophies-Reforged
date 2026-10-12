package io.github.hikoma0000.advancementtrophies.mixin.client.betteradvancements;

import betteradvancements.common.gui.BetterAdvancementWidget;
import io.github.hikoma0000.advancementtrophies.client.gui.TrophyInscribeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "betteradvancements.common.gui.BetterAdvancementTab", remap = false)
public class BetterAdvancementTabMixin {

    @Redirect(method = "drawToolTips", at = @At(value = "INVOKE", target = "Lbetteradvancements/common/gui/BetterAdvancementWidget;drawHover(Lnet/minecraft/client/gui/GuiGraphics;IIFII)V"))
    private void onDrawHover(BetterAdvancementWidget widget, GuiGraphics guiGraphics, int scrollX, int scrollY,
            float fade, int left, int top) {
        if (Minecraft.getInstance().screen instanceof TrophyInscribeScreen screen
                && !screen.trackHover(widget.getAdvancement())) {
            return;
        }
        widget.drawHover(guiGraphics, scrollX, scrollY, fade, left, top);
    }
}
