package io.github.hikoma0000.advancementtrophies.datagen.server;

import io.github.hikoma0000.advancementtrophies.init.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Set;
import java.util.function.Supplier;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    public ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.IRON_TROPHY.get());
        this.dropSelf(ModBlocks.GOLD_TROPHY.get());
        this.dropSelf(ModBlocks.DIAMOND_TROPHY.get());
        this.dropSelf(ModBlocks.NETHERITE_TROPHY.get());

        this.add(ModBlocks.TROPHY_CRATE.get(), this::createTrophyCrateDrop);
    }

    private LootTable.Builder createTrophyCrateDrop(Block block) {
        return LootTable.lootTable()
                .withPool(this.applyExplosionCondition(block, LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(block)
                                .apply(CopyComponentsFunction.copyComponents(
                                        CopyComponentsFunction.Source.BLOCK_ENTITY)))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream()
                .map(Supplier::get)
                .map(Block.class::cast)::iterator;
    }
}
