package io.github.hikoma0000.advancementtrophies.init;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.inventory.TrophyCrateContainer;
import io.github.hikoma0000.advancementtrophies.inventory.TrophyCrateItemContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContainers {
    public static final DeferredRegister<MenuType<?>> CONTAINERS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, AdvancementTrophies.MOD_ID);

    public static final RegistryObject<MenuType<TrophyCrateContainer>> TROPHY_CRATE_CONTAINER =
            CONTAINERS.register("trophy_crate", () -> IForgeMenuType.create(TrophyCrateContainer::new));

    public static final RegistryObject<MenuType<TrophyCrateItemContainer>> TROPHY_CRATE_ITEM_CONTAINER =
            CONTAINERS.register("trophy_crate_item", () -> IForgeMenuType.create(TrophyCrateItemContainer::new));
}