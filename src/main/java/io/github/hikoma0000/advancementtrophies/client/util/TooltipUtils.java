package io.github.hikoma0000.advancementtrophies.client.util;

import io.github.hikoma0000.advancementtrophies.config.client.input.KeyBindings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.List;

public class TooltipUtils {
    public static void addHoldForDetailsTooltip(List<Component> pTooltipComponents) {
        Component keyName = KeyBindings.SHOW_DETAILS_KEY.getTranslatedKeyMessage();
        pTooltipComponents.add(Component.translatableWithFallback(
                "tooltip.advancementtrophies.hold_for_details",
                "§7Hold [§f%s§7] for details",
                keyName));
    }

    public static void addOpenAdvancementTooltip(List<Component> tooltipComponents) {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.options == null) {
            return;
        }
        Component keyName = minecraft.options.keyAdvancements.getTranslatedKeyMessage();
        tooltipComponents.add(Component.translatableWithFallback(
                "tooltip.advancementtrophies.open_advancement",
                "§7Press [§f%s§7] to open advancement",
                keyName));
    }
}
