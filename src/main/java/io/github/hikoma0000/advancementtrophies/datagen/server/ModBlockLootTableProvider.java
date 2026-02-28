package io.github.hikoma0000.advancementtrophies.datagen.server;

import io.github.hikoma0000.advancementtrophies.init.ModBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    public ModBlockLootTableProvider() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.IRON_TROPHY.get());
        this.dropSelf(ModBlocks.GOLD_TROPHY.get());
        this.dropSelf(ModBlocks.DIAMOND_TROPHY.get());
        this.dropSelf(ModBlocks.NETHERITE_TROPHY.get());

        this.add(ModBlocks.TROPHY_CRATE.get(), this::createShulkerBoxDrop);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream()
                .map(RegistryObject::get)::iterator;
    }
}
