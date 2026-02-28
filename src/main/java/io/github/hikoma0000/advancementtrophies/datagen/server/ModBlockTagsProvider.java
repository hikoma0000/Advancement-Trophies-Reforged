package io.github.hikoma0000.advancementtrophies.datagen.server;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.init.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("removal")
public class ModBlockTagsProvider extends BlockTagsProvider {
    public static final TagKey<Block> TROPHIES = BlockTags
            .create(new ResourceLocation(AdvancementTrophies.MOD_ID, "trophies"));

    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, AdvancementTrophies.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(TROPHIES).add(
                ModBlocks.IRON_TROPHY.get(),
                ModBlocks.GOLD_TROPHY.get(),
                ModBlocks.DIAMOND_TROPHY.get(),
                ModBlocks.NETHERITE_TROPHY.get());
    }
}
