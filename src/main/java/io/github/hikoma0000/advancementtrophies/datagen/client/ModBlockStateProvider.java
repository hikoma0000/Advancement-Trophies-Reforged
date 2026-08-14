package io.github.hikoma0000.advancementtrophies.datagen.client;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.block.TrophyCrateBlock;
import io.github.hikoma0000.advancementtrophies.init.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, AdvancementTrophies.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        trophyBlock(ModBlocks.IRON_TROPHY);
        trophyBlock(ModBlocks.GOLD_TROPHY);
        trophyBlock(ModBlocks.DIAMOND_TROPHY);
        trophyBlock(ModBlocks.NETHERITE_TROPHY);

        ModelFile crateClosed = models().getExistingFile(modLoc("block/trophy_crate"));
        ModelFile crateOpen = models().getExistingFile(modLoc("block/trophy_crate_open"));

        getVariantBuilder(ModBlocks.TROPHY_CRATE.get()).forAllStates(state -> {
            boolean isOpen = state.getValue(TrophyCrateBlock.OPEN);
            return ConfiguredModel.builder()
                    .modelFile(isOpen ? crateOpen : crateClosed)
                    .build();
        });
    }

    private void trophyBlock(DeferredHolder<Block, Block> blockHolder) {
        Block block = blockHolder.get();
        ModelFile model = models().getExistingFile(modLoc("block/" + blockHolder.getId().getPath()));
        horizontalBlock(block, model);
    }
}
