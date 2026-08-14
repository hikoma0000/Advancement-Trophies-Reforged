package io.github.hikoma0000.advancementtrophies.event;

import io.github.hikoma0000.advancementtrophies.compat.curios.CuriosCompat;
import io.github.hikoma0000.advancementtrophies.compat.sophisticatedbackpacks.SophisticatedBackpacksCompat;
import io.github.hikoma0000.advancementtrophies.init.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

import java.util.HashSet;
import java.util.Set;

public class PlayerEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerPickupItem(ItemEntityPickupEvent.Pre event) {
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
        remainder = SophisticatedBackpacksCompat.insertIntoCratesDeep(playerInvHandler, remainder, visited);
        remainder = CuriosCompat.checkCurios(player, remainder, visited);

        if (remainder.getCount() < pickedUpStack.getCount()) {
            event.setCanPickup(TriState.FALSE);
            event.getItemEntity().discard();

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
}
