package io.github.hikoma0000.advancementtrophies.mixin.client;

import io.github.hikoma0000.advancementtrophies.client.gui.TrophyInscribeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AdvancementTab.class)
public class AdvancementTabMixin {

    @Redirect(method = "drawTooltips", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementWidget;drawHover(Lnet/minecraft/client/gui/GuiGraphics;IIFII)V"))
    private void onDrawHover(AdvancementWidget widget, GuiGraphics guiGraphics, int x, int y, float fade, int width,
            int height) {
        if (Minecraft.getInstance().screen instanceof TrophyInscribeScreen screen
                && !screen.trackHover(((AdvancementWidgetAccessor) widget).advancementtrophies$getAdvancementNode().holder())) {
            return;
        }
        widget.drawHover(guiGraphics, x, y, fade, width, height);
    }
}
