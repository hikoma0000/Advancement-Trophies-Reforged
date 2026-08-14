package io.github.hikoma0000.advancementtrophies.compat.curios;

import io.github.hikoma0000.advancementtrophies.compat.sophisticatedbackpacks.SophisticatedBackpacksCompat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.items.IItemHandler;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class CuriosCompat {

    public static ItemStack checkCurios(Player player, ItemStack remainder, Set<IItemHandler> visited) {
        if (!ModList.get().isLoaded("curios")) {
            return remainder;
        }
        AtomicReference<ItemStack> currentRemainder = new AtomicReference<>(remainder);
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            IItemHandler curiosHandler = handler.getEquippedCurios();
            currentRemainder.set(SophisticatedBackpacksCompat.insertIntoCratesDeep(curiosHandler, currentRemainder.get(), visited));
        });
        return currentRemainder.get();
    }
}
