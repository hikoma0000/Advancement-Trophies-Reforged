package io.github.hikoma0000.advancementtrophies.event;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.compat.curios.CuriosCompat;
import io.github.hikoma0000.advancementtrophies.compat.sophisticatedbackpacks.SophisticatedBackpacksCompat;
import io.github.hikoma0000.advancementtrophies.init.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.HashSet;
import java.util.Set;

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
        remainder = SophisticatedBackpacksCompat.insertIntoCratesDeep(playerInvHandler, remainder, visited);
        remainder = CuriosCompat.checkCurios(player, remainder, visited);

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
}