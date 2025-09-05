package io.github.hikoma0000.advancementtrophies.init;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.block.TrophyBlock;
import io.github.hikoma0000.advancementtrophies.block.TrophyCrateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, AdvancementTrophies.MOD_ID);

    public static final RegistryObject<Block> IRON_TROPHY = BLOCKS.register("iron_trophy",
            () -> new TrophyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 5.0F).sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<Block> GOLD_TROPHY = BLOCKS.register("gold_trophy",
            () -> new TrophyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 5.0F).sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<Block> DIAMOND_TROPHY = BLOCKS.register("diamond_trophy",
            () -> new TrophyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 5.0F).sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<Block> NETHERITE_TROPHY = BLOCKS.register("netherite_trophy",
            () -> new TrophyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 1200.0F).sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<Block> TROPHY_CRATE = BLOCKS.register("trophy_crate",
            () -> new TrophyCrateBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD)));
}