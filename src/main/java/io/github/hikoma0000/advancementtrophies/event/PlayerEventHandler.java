package io.github.hikoma0000.advancementtrophies.event;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.init.ModItems;
import io.github.hikoma0000.advancementtrophies.item.TrophyCrateItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

@EventBusSubscriber(modid = AdvancementTrophies.MOD_ID)
public class PlayerEventHandler {

    @SubscribeEvent
    public static void onPlayerPickupItem(ItemEntityPickupEvent.Pre event) {
        Player player = event.getPlayer();
        ItemEntity itemEntity = event.getItemEntity();
        ItemStack pickedUpStack = itemEntity.getItem();

        if (player.level().isClientSide || !ModItems.isTrophy(pickedUpStack)) {
            return;
        }

        ItemStack remainder = pickedUpStack.copy();
        boolean crateFound = false;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack inventoryStack = player.getInventory().getItem(i);
            if (inventoryStack.getItem() instanceof TrophyCrateItem) {
                crateFound = true;
                remainder = TrophyCrateItem.addItemToCrate(inventoryStack, remainder, player.level().registryAccess());
                if (remainder.isEmpty()) {
                    break;
                }
            }
        }

        if (crateFound && remainder.getCount() < pickedUpStack.getCount()) {
            event.setCanPickup(TriState.FALSE);

            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);

            if (remainder.isEmpty()) {
                itemEntity.discard();
            } else {
                itemEntity.getItem().setCount(remainder.getCount());
            }
        }
    }
}