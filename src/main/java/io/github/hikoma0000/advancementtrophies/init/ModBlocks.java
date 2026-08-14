package io.github.hikoma0000.advancementtrophies.init;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.block.TrophyBlock;
import io.github.hikoma0000.advancementtrophies.block.TrophyCrateBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, AdvancementTrophies.MOD_ID);

    public static final DeferredHolder<Block, Block> IRON_TROPHY = BLOCKS.register("iron_trophy",
            () -> new TrophyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 5.0F).sound(SoundType.METAL).noOcclusion()));

    public static final DeferredHolder<Block, Block> GOLD_TROPHY = BLOCKS.register("gold_trophy",
            () -> new TrophyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 5.0F).sound(SoundType.METAL).noOcclusion()));

    public static final DeferredHolder<Block, Block> DIAMOND_TROPHY = BLOCKS.register("diamond_trophy",
            () -> new TrophyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 5.0F).sound(SoundType.METAL).noOcclusion()));

    public static final DeferredHolder<Block, Block> NETHERITE_TROPHY = BLOCKS.register("netherite_trophy",
            () -> new TrophyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 1200.0F).sound(SoundType.METAL).noOcclusion()));

    public static final DeferredHolder<Block, Block> TROPHY_CRATE = BLOCKS.register("trophy_crate",
            () -> new TrophyCrateBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD)));
}
