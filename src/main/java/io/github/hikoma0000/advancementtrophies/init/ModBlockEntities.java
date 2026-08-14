package io.github.hikoma0000.advancementtrophies.init;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.block.entity.TrophyBlockEntity;
import io.github.hikoma0000.advancementtrophies.block.entity.TrophyCrateBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AdvancementTrophies.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrophyBlockEntity>> TROPHY =
            BLOCK_ENTITIES.register("trophy", () ->
                    BlockEntityType.Builder.of(TrophyBlockEntity::new,
                            ModBlocks.IRON_TROPHY.get(),
                            ModBlocks.GOLD_TROPHY.get(),
                            ModBlocks.DIAMOND_TROPHY.get(),
                            ModBlocks.NETHERITE_TROPHY.get()
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrophyCrateBlockEntity>> TROPHY_CRATE =
            BLOCK_ENTITIES.register("trophy_crate", () ->
                    BlockEntityType.Builder.of(TrophyCrateBlockEntity::new, ModBlocks.TROPHY_CRATE.get()).build(null));
}
