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
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

import java.util.HashSet;
import java.util.Set;

import net.neoforged.neoforge.common.util.TriState;

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

        if (remainder.getCount() < pickedUpStack.getCount()) {
            // クレートに格納できた分がある場合、バニラのピックアップを阻止して増殖を防ぐ
            event.setCanPickup(TriState.FALSE);

            if (remainder.isEmpty()) {
                event.getItemEntity().discard();
            } else {
                event.getItemEntity().getItem().setCount(remainder.getCount());
            }

            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
                    ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
    }

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
