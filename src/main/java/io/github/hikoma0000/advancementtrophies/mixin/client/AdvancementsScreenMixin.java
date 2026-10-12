package io.github.hikoma0000.advancementtrophies.mixin.client;

import io.github.hikoma0000.advancementtrophies.client.gui.TrophyInscribeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AdvancementsScreen.class)
public class AdvancementsScreenMixin {

    @ModifyArg(method = "renderWindow", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I"), index = 1)
    private Component onRenderWindowTitle(Component title) {
        return Minecraft.getInstance().screen instanceof TrophyInscribeScreen ? TrophyInscribeScreen.HINT : title;
    }
}
