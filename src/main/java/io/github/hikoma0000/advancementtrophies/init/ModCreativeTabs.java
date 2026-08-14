package io.github.hikoma0000.advancementtrophies.init;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AdvancementTrophies.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ADVANCEMENT_TROPHIES_TAB = CREATIVE_MODE_TABS.register("advancementtrophies_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.GOLD_TROPHY.get()))
                    .title(Component.translatable("creativetabs.advancementtrophies_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.IRON_TROPHY.get());
                        pOutput.accept(ModItems.GOLD_TROPHY.get());
                        pOutput.accept(ModItems.DIAMOND_TROPHY.get());
                        pOutput.accept(ModItems.NETHERITE_TROPHY.get());
                        pOutput.accept(ModItems.TROPHY_CRATE.get());
                    })
                    .build());
}
