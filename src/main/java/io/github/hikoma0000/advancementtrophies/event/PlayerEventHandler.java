package io.github.hikoma0000.advancementtrophies.event;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.init.ModItems;
import io.github.hikoma0000.advancementtrophies.item.TrophyCrateItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Mod.EventBusSubscriber(modid = AdvancementTrophies.MOD_ID)
public class PlayerEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerPickupItem(EntityItemPickupEvent event) {
        Player player = event.getEntity();
        ItemStack pickedUpStack = event.getItem().getItem();

        if (player.level().isClientSide || !ModItems.isTrophy(pickedUpStack)) {
            return;
        }

        ItemStack remainder = pickedUpStack.copy();
        Set<IItemHandler> visited = new HashSet<>();

        IItemHandler playerInvHandler = new InvWrapper(player.getInventory());
        remainder = insertIntoCratesDeep(playerInvHandler, remainder, visited);

        if (!remainder.isEmpty() && ModList.get().isLoaded("curios")) {
            remainder = checkCurios(player, remainder, visited);
        }

        if (remainder.getCount() < pickedUpStack.getCount()) {
            event.setCanceled(true);
            event.getItem().discard();

            if (!remainder.isEmpty()) {
                if (!player.getInventory().add(remainder)) {
                    player.drop(remainder, false);
                }
            }
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP,
                    SoundSource.PLAYERS, 0.2F,
                    ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
    }

    private static ItemStack checkCurios(Player player, ItemStack remainder, Set<IItemHandler> visited) {
        AtomicReference<ItemStack> currentRemainder = new AtomicReference<>(remainder);
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            IItemHandler curiosHandler = handler.getEquippedCurios();
            currentRemainder.set(insertIntoCratesDeep(curiosHandler, currentRemainder.get(), visited));
        });
        return currentRemainder.get();
    }

    private static ItemStack insertIntoCratesDeep(IItemHandler handler, ItemStack stack, Set<IItemHandler> visited) {
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
                slotStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(nestedHandler -> {
                });

                IItemHandler nestedHandler = slotStack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
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