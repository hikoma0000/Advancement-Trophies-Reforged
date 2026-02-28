package io.github.hikoma0000.advancementtrophies.event;

import io.github.hikoma0000.advancementtrophies.init.ModItems;
import io.github.hikoma0000.advancementtrophies.item.TrophyCrateItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;

import java.util.HashSet;
import java.util.Set;

public class PlayerEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItem(ItemEntityPickupEvent.Pre event) {
        Player player = event.getPlayer();
        ItemStack pickedUpStack = event.getItemEntity().getItem();

        if (player.level().isClientSide
                || event.getItemEntity().hasPickUpDelay()
                || !ModItems.isTrophy(pickedUpStack)) {
            return;
        }

        ItemStack remainder = pickedUpStack.copy();
        Set<IItemHandler> visited = new HashSet<>();

        IItemHandler playerInvHandler = new InvWrapper(player.getInventory());
        remainder = insertIntoCratesDeep(playerInvHandler, remainder, visited);

        if (!remainder.isEmpty()) {
            remainder = insertIntoCratesInBackpacks(player, remainder, visited);
        }

        if (remainder.getCount() < pickedUpStack.getCount()) {
            event.getItemEntity().setItem(remainder);
            event.setCanPickup(TriState.FALSE);

            if (remainder.isEmpty()) {
                event.getItemEntity().discard();
            }

            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
                    ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
    }

    private static ItemStack insertIntoCratesInBackpacks(Player player, ItemStack stack, Set<IItemHandler> visited) {
        ItemStack[] remainderRef = { stack };
        PlayerInventoryProvider.get().runOnBackpacks(player, (backpack, handlerName, identifier, slot) -> {
            IItemHandler invHandler = BackpackWrapper.fromStack(backpack).getInventoryHandler();
            remainderRef[0] = insertIntoCratesDeep(invHandler, remainderRef[0], visited);
            return remainderRef[0].isEmpty();
        });
        return remainderRef[0];
    }

    public static ItemStack insertIntoCratesDeep(IItemHandler handler, ItemStack stack, Set<IItemHandler> visited) {
        if (!visited.add(handler) || stack.isEmpty()) {
            return stack;
        }

        ItemStack remainder = stack.copy();

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack slotStack = handler.getStackInSlot(i);
            if (slotStack.getItem() instanceof TrophyCrateItem) {
                ItemStack crateStack = slotStack.copy();
                int prevCount = remainder.getCount();
                remainder = TrophyCrateItem.addItemToCrate(crateStack, remainder);
                if (remainder.getCount() < prevCount && handler instanceof IItemHandlerModifiable modifiable) {
                    modifiable.setStackInSlot(i, crateStack);
                }
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
