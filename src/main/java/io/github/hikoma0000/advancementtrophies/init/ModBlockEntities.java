package io.github.hikoma0000.advancementtrophies.init;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.block.entity.TrophyBlockEntity;
import io.github.hikoma0000.advancementtrophies.block.entity.TrophyCrateBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AdvancementTrophies.MOD_ID);

    public static final RegistryObject<BlockEntityType<TrophyBlockEntity>> TROPHY =
            BLOCK_ENTITIES.register("trophy", () ->
                    BlockEntityType.Builder.of(TrophyBlockEntity::new,
                            ModBlocks.IRON_TROPHY.get(),
                            ModBlocks.GOLD_TROPHY.get(),
                            ModBlocks.DIAMOND_TROPHY.get(),
                            ModBlocks.NETHERITE_TROPHY.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<TrophyCrateBlockEntity>> TROPHY_CRATE =
            BLOCK_ENTITIES.register("trophy_crate", () ->
                    BlockEntityType.Builder.of(TrophyCrateBlockEntity::new, ModBlocks.TROPHY_CRATE.get()).build(null));
}