package io.github.hikoma0000.advancementtrophies.init;

import com.google.common.collect.ImmutableSet;
import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.item.TrophyCrateItem;
import io.github.hikoma0000.advancementtrophies.item.TrophyItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, AdvancementTrophies.MOD_ID);

    public static final DeferredHolder<Item, Item> IRON_TROPHY = ITEMS.register("iron_trophy",
            () -> new TrophyItem(ModBlocks.IRON_TROPHY.get(), new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));

    public static final DeferredHolder<Item, Item> GOLD_TROPHY = ITEMS.register("gold_trophy",
            () -> new TrophyItem(ModBlocks.GOLD_TROPHY.get(), new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    public static final DeferredHolder<Item, Item> DIAMOND_TROPHY = ITEMS.register("diamond_trophy",
            () -> new TrophyItem(ModBlocks.DIAMOND_TROPHY.get(), new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    public static final DeferredHolder<Item, Item> NETHERITE_TROPHY = ITEMS.register("netherite_trophy",
            () -> new TrophyItem(ModBlocks.NETHERITE_TROPHY.get(), new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant()));

    public static final DeferredHolder<Item, Item> TROPHY_CRATE = ITEMS.register("trophy_crate",
            () -> new TrophyCrateItem(ModBlocks.TROPHY_CRATE.get(), new Item.Properties().stacksTo(1)));

    private static final Set<DeferredHolder<Item, ? extends Item>> TROPHY_ITEM_SET = ImmutableSet.of(
            IRON_TROPHY, GOLD_TROPHY, DIAMOND_TROPHY, NETHERITE_TROPHY
    );

    public static boolean isTrophy(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return TROPHY_ITEM_SET.stream().anyMatch(trophy -> trophy.get() == stack.getItem());
    }
}