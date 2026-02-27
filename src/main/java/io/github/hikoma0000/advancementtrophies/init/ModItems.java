package io.github.hikoma0000.advancementtrophies.init;

import com.google.common.collect.ImmutableSet;
import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.item.TrophyCrateItem;
import io.github.hikoma0000.advancementtrophies.item.TrophyItem;
import io.github.hikoma0000.advancementtrophies.util.TrophyRarity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class ModItems {
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM,
                        AdvancementTrophies.MOD_ID);

        public static final Supplier<Item> IRON_TROPHY = ITEMS.register("iron_trophy",
                        () -> new TrophyItem(ModBlocks.IRON_TROPHY.get(),
                                        new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));

        public static final Supplier<Item> GOLD_TROPHY = ITEMS.register("gold_trophy",
                        () -> new TrophyItem(ModBlocks.GOLD_TROPHY.get(),
                                        new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

        public static final Supplier<Item> DIAMOND_TROPHY = ITEMS.register("diamond_trophy",
                        () -> new TrophyItem(ModBlocks.DIAMOND_TROPHY.get(),
                                        new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

        public static final Supplier<Item> NETHERITE_TROPHY = ITEMS.register("netherite_trophy",
                        () -> new TrophyItem(ModBlocks.NETHERITE_TROPHY.get(),
                                        new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant()));

        public static final Supplier<Item> TROPHY_CRATE = ITEMS.register("trophy_crate",
                        () -> new TrophyCrateItem(ModBlocks.TROPHY_CRATE.get(), new Item.Properties().stacksTo(1)));

        private static final Set<Supplier<Item>> TROPHY_ITEM_SET = ImmutableSet.of(
                        IRON_TROPHY, GOLD_TROPHY, DIAMOND_TROPHY, NETHERITE_TROPHY);

        private static Map<Supplier<Item>, TrophyRarity> trophyRarityMap;

        private static Map<Supplier<Item>, TrophyRarity> getTrophyRarityMap() {
                if (trophyRarityMap == null) {
                        trophyRarityMap = Map.of(
                                        IRON_TROPHY, TrophyRarity.IRON,
                                        GOLD_TROPHY, TrophyRarity.GOLD,
                                        DIAMOND_TROPHY, TrophyRarity.DIAMOND,
                                        NETHERITE_TROPHY, TrophyRarity.NETHERITE);
                }
                return trophyRarityMap;
        }

        public static boolean isTrophy(ItemStack stack) {
                if (stack.isEmpty()) {
                        return false;
                }
                return TROPHY_ITEM_SET.stream().anyMatch(trophy -> trophy.get() == stack.getItem());
        }

        @Nullable
        public static TrophyRarity getTrophyRarity(Item item) {
                for (Map.Entry<Supplier<Item>, TrophyRarity> entry : getTrophyRarityMap().entrySet()) {
                        if (entry.getKey().get() == item) {
                                return entry.getValue();
                        }
                }
                return null;
        }
}