package io.github.hikoma0000.advancementtrophies.mixin.client.betteradvancements;

import io.github.hikoma0000.advancementtrophies.client.gui.TrophyInscribeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(targets = "betteradvancements.common.gui.BetterAdvancementsScreen", remap = false)
public class BetterAdvancementsScreenMixin {

    @ModifyArg(method = "renderWindow", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;IIIZ)I"), index = 1)
    private FormattedCharSequence onRenderWindowTitle(FormattedCharSequence title) {
        return Minecraft.getInstance().screen instanceof TrophyInscribeScreen
                ? TrophyInscribeScreen.HINT.getVisualOrderText()
                : title;
    }
}
