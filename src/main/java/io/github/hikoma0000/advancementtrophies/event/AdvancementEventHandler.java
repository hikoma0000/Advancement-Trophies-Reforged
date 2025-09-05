package io.github.hikoma0000.advancementtrophies.event;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.init.ModItems;
import io.github.hikoma0000.advancementtrophies.item.TrophyCrateItem;
import io.github.hikoma0000.advancementtrophies.util.NBTKeys;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Mod.EventBusSubscriber(modid = AdvancementTrophies.MOD_ID)
public class AdvancementEventHandler {

    private static final Map<FrameType, RegistryObject<Item>> FRAME_TYPE_TO_TROPHY = Map.of(
            FrameType.TASK, ModItems.IRON_TROPHY,
            FrameType.GOAL, ModItems.GOLD_TROPHY,
            FrameType.CHALLENGE, ModItems.DIAMOND_TROPHY
    );

    @SubscribeEvent
    public static void onAdvancementGranted(AdvancementEvent.AdvancementEarnEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }

        Advancement advancement = event.getAdvancement();
        DisplayInfo display = advancement.getDisplay();
        if (display == null || !display.shouldAnnounceChat()) {
            return;
        }

        RegistryObject<Item> trophyItem = display.isHidden()
                ? ModItems.NETHERITE_TROPHY
                : FRAME_TYPE_TO_TROPHY.get(display.getFrame());

        if (trophyItem == null) {
            return;
        }

        ItemStack trophyStack = new ItemStack(trophyItem.get());
        CompoundTag nbt = trophyStack.getOrCreateTag();

        nbt.putString(NBTKeys.ACHIEVER, player.getName().getString());

        CompoundTag dateTag = new CompoundTag();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        dateTag.putInt(NBTKeys.YEAR, cal.get(Calendar.YEAR));
        dateTag.putInt(NBTKeys.MONTH, cal.get(Calendar.MONTH) + 1);
        dateTag.putInt(NBTKeys.DAY, cal.get(Calendar.DAY_OF_MONTH));
        dateTag.putInt(NBTKeys.HOUR, cal.get(Calendar.HOUR_OF_DAY));
        dateTag.putInt(NBTKeys.MINUTE, cal.get(Calendar.MINUTE));
        dateTag.putInt(NBTKeys.SECOND, cal.get(Calendar.SECOND));
        nbt.put(NBTKeys.DATE, dateTag);

        nbt.putString(NBTKeys.ADVANCEMENT_ID, advancement.getId().toString());

        Component advancementTitleComponent = display.getTitle();
        if (advancementTitleComponent.getContents() instanceof TranslatableContents contents) {
            nbt.putString(NBTKeys.ADVANCEMENT_TITLE, contents.getKey());
        } else {
            nbt.putString(NBTKeys.ADVANCEMENT_TITLE_JSON, Component.Serializer.toJson(advancementTitleComponent));
        }

        nbt.putString(NBTKeys.ADVANCEMENT_MOD, advancement.getId().getNamespace());

        CompoundTag iconTag = new CompoundTag();
        display.getIcon().save(iconTag);
        nbt.put(NBTKeys.ICON, iconTag);

        ItemStack trophyRemainder = trophyStack.copy();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack inventoryStack = player.getInventory().getItem(i);
            if (inventoryStack.getItem() instanceof TrophyCrateItem) {
                trophyRemainder = TrophyCrateItem.addItemToCrate(inventoryStack, trophyRemainder);
                if (trophyRemainder.isEmpty()) {
                    break;
                }
            }
        }

        if (!trophyRemainder.isEmpty()) {
            if (!player.getInventory().add(trophyRemainder)) {
                player.drop(trophyRemainder, false);
                player.sendSystemMessage(Component.translatableWithFallback("message.advancementtrophies.inventory_full", "Your inventory is full! The trophy has been dropped nearby."));
            }
        }
    }
}