package io.github.hikoma0000.advancementtrophies.event;

import io.github.hikoma0000.advancementtrophies.init.ModDataComponents;
import io.github.hikoma0000.advancementtrophies.init.ModItems;
import io.github.hikoma0000.advancementtrophies.item.TrophyCrateItem;
import io.github.hikoma0000.advancementtrophies.util.NBTKeys;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

public class AdvancementEventHandler {

    private static final Map<AdvancementType, Supplier<Item>> FRAME_TYPE_TO_TROPHY = Map.of(
            AdvancementType.TASK, ModItems.IRON_TROPHY,
            AdvancementType.GOAL, ModItems.GOLD_TROPHY,
            AdvancementType.CHALLENGE, ModItems.DIAMOND_TROPHY
    );

    @SubscribeEvent
    public void onAdvancementGranted(AdvancementEvent.AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        AdvancementHolder advancementHolder = event.getAdvancement();
        Advancement advancement = advancementHolder.value();
        DisplayInfo display = advancement.display().orElse(null);
        if (display == null || !display.shouldAnnounceChat()) {
            return;
        }

        Supplier<Item> trophyItem = display.isHidden()
                ? ModItems.NETHERITE_TROPHY
                : FRAME_TYPE_TO_TROPHY.get(display.getType());

        if (trophyItem == null) {
            return;
        }

        ItemStack trophyStack = new ItemStack(trophyItem.get());
        CompoundTag nbt = new CompoundTag();

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

        nbt.putString(NBTKeys.ADVANCEMENT_ID, advancementHolder.id().toString());

        HolderLookup.Provider provider = player.level().registryAccess();
        Component advancementTitleComponent = display.getTitle();
        if (advancementTitleComponent.getContents() instanceof TranslatableContents contents) {
            nbt.putString(NBTKeys.ADVANCEMENT_TITLE, contents.getKey());
        } else {
            nbt.putString(NBTKeys.ADVANCEMENT_TITLE_JSON, Component.Serializer.toJson(advancementTitleComponent, provider));
        }

        nbt.putString(NBTKeys.ADVANCEMENT_MOD, advancementHolder.id().getNamespace());

        CompoundTag iconTag = (CompoundTag) display.getIcon().save(provider);
        nbt.put(NBTKeys.ICON, iconTag);

        trophyStack.set(ModDataComponents.TROPHY_DATA.get(), nbt);

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