package io.github.hikoma0000.advancementtrophies.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;

public enum TrophyRarity {
    IRON("iron", net.minecraft.world.item.Rarity.COMMON),
    GOLD("gold", net.minecraft.world.item.Rarity.UNCOMMON),
    DIAMOND("diamond", net.minecraft.world.item.Rarity.RARE),
    NETHERITE("netherite", net.minecraft.world.item.Rarity.EPIC);

    private final String name;
    private final net.minecraft.world.item.Rarity itemRarity;

    TrophyRarity(String name, net.minecraft.world.item.Rarity itemRarity) {
        this.name = name;
        this.itemRarity = itemRarity;
    }

    public String getName() {
        return name;
    }

    public net.minecraft.world.item.Rarity getItemRarity() {
        return itemRarity;
    }

    public Style getStyleModifier() {
        return Style.EMPTY.withColor(this.itemRarity.getStyleModifier().apply(Style.EMPTY).getColor());
    }
}