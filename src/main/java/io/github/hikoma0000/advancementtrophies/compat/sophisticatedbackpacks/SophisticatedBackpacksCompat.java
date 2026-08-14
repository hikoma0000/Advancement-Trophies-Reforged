package io.github.hikoma0000.advancementtrophies.compat.sophisticatedbackpacks;

import io.github.hikoma0000.advancementtrophies.item.TrophyCrateItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Set;

public class SophisticatedBackpacksCompat {

    public static ItemStack insertIntoCratesDeep(IItemHandler handler, ItemStack stack, Set<IItemHandler> visited) {
        if (!visited.add(handler) || stack.isEmpty()) {
            return stack;
        }

        ItemStack remainder = stack.copy();

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack slotStack = handler.getStackInSlot(i);
            if (slotStack.getItem() instanceof TrophyCrateItem) {
                remainder = TrophyCrateItem.addItemToCrate(slotStack, remainder);
                if (remainder.isEmpty()) {
                    return remainder;
                }
            }
        }

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack slotStack = handler.getStackInSlot(i);
            if (!slotStack.isEmpty() && !(slotStack.getItem() instanceof TrophyCrateItem)) {
                IItemHandler nestedHandler = slotStack.getCapability(Capabilities.ItemHandler.ITEM);
                if (nestedHandler != null) {
                    remainder = insertIntoCratesDeep(nestedHandler, remainder, visited);
                    if (remainder.isEmpty()) {
                        return remainder;
                    }
                }
            }
        }

        return remainder;
    }
}
