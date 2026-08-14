package io.github.hikoma0000.advancementtrophies.init;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.inventory.TrophyCrateContainer;
import io.github.hikoma0000.advancementtrophies.inventory.TrophyCrateItemContainer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModContainers {
    public static final DeferredRegister<MenuType<?>> CONTAINERS =
            DeferredRegister.create(Registries.MENU, AdvancementTrophies.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<TrophyCrateContainer>> TROPHY_CRATE_CONTAINER =
            CONTAINERS.register("trophy_crate", () -> IMenuTypeExtension.create(TrophyCrateContainer::new));

    public static final DeferredHolder<MenuType<?>, MenuType<TrophyCrateItemContainer>> TROPHY_CRATE_ITEM_CONTAINER =
            CONTAINERS.register("trophy_crate_item", () -> IMenuTypeExtension.create(TrophyCrateItemContainer::new));
}
