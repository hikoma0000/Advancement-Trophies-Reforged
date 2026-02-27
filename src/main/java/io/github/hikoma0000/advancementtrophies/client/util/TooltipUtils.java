package io.github.hikoma0000.advancementtrophies.client.util;

import io.github.hikoma0000.advancementtrophies.config.client.input.KeyBindings;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

public class TooltipUtils {
    public static void addHoldForDetailsTooltip(List<Component> pTooltipComponents) {
        Component keyName = KeyBindings.SHOW_DETAILS_KEY.getTranslatedKeyMessage();
        pTooltipComponents.add(
                Component.translatableWithFallback(
                        "tooltip.advancementtrophies.hold_for_details",
                        "Hold [%s] for details",
                        Component.empty().append(keyName).withStyle(ChatFormatting.WHITE))
                        .withStyle(ChatFormatting.GRAY));
    }
}