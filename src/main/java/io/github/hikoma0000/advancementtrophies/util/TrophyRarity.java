package io.github.hikoma0000.advancementtrophies.util;

import net.minecraft.world.item.Rarity;

public enum TrophyRarity {
    IRON("iron", Rarity.COMMON),
    GOLD("gold", Rarity.UNCOMMON),
    DIAMOND("diamond", Rarity.RARE),
    NETHERITE("netherite", Rarity.EPIC);

    private final String name;
    private final Rarity itemRarity;

    TrophyRarity(String name, Rarity itemRarity) {
        this.name = name;
        this.itemRarity = itemRarity;
    }

    public String getName() {
        return name;
    }

    public Rarity getItemRarity() {
        return itemRarity;
    }
}