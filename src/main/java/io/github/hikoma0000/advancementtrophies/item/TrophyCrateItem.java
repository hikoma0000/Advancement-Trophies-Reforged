package io.github.hikoma0000.advancementtrophies.item;

import io.github.hikoma0000.advancementtrophies.client.util.TooltipUtils;
import io.github.hikoma0000.advancementtrophies.config.client.input.KeyBindings;
import io.github.hikoma0000.advancementtrophies.init.ModBlockEntities;
import io.github.hikoma0000.advancementtrophies.init.ModItems;
import io.github.hikoma0000.advancementtrophies.util.NBTKeys;
import io.github.hikoma0000.advancementtrophies.util.TrophyRarity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TrophyCrateItem extends BlockItem {
    public TrophyCrateItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        if (!pLevel.isClientSide()) {
            MenuProvider menuProvider = new TrophyCrateItemMenuProvider(pPlayer.getItemInHand(pHand), pHand);
            ((ServerPlayer) pPlayer).openMenu(menuProvider, buf -> buf.writeEnum(pHand));
            pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.BARREL_OPEN, SoundSource.PLAYERS, 0.5F, pLevel.random.nextFloat() * 0.1F + 0.9F);
        }
        return InteractionResultHolder.sidedSuccess(pPlayer.getItemInHand(pHand), pLevel.isClientSide());
    }

    @Override
    public void onDestroyed(ItemEntity pItemEntity) {
        ItemStack stack = pItemEntity.getItem();
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data != null) {
            CompoundTag nbt = data.copyTag();
            if (nbt.contains(NBTKeys.ITEMS)) {
                ItemStackHandler handler = new ItemStackHandler(27);
                ListTag tagList = nbt.getList(NBTKeys.ITEMS, 10);
                for (int i = 0; i < tagList.size(); i++) {
                    CompoundTag itemTags = tagList.getCompound(i);
                    int slot = itemTags.getByte("Slot") & 255;
                    if (slot >= 0 && slot < handler.getSlots()) {
                        handler.setStackInSlot(slot, ItemStack.parseOptional(pItemEntity.level().registryAccess(), itemTags));
                    }
                }
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack stackInSlot = handler.getStackInSlot(i);
                    if (!stackInSlot.isEmpty()) {
                        pItemEntity.level().addFreshEntity(new ItemEntity(pItemEntity.level(), pItemEntity.getX(), pItemEntity.getY(), pItemEntity.getZ(), stackInSlot));
                    }
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("tooltip.advancementtrophies.trophy_crate.description").withStyle(ChatFormatting.GRAY));

        if (KeyBindings.isDetailsKeyDown) {
            CustomData data = pStack.get(DataComponents.BLOCK_ENTITY_DATA);
            if (data != null && pContext.registries() != null) {
                CompoundTag nbt = data.copyTag();
                if (nbt.contains(NBTKeys.ITEMS)) {
                    ItemStackHandler handler = new ItemStackHandler(27);
                    ListTag tagList = nbt.getList(NBTKeys.ITEMS, 10);
                    for (int i = 0; i < tagList.size(); i++) {
                        CompoundTag itemTags = tagList.getCompound(i);
                        int slot = itemTags.getByte("Slot") & 255;
                        if (slot >= 0 && slot < handler.getSlots()) {
                            handler.setStackInSlot(slot, ItemStack.parseOptional(pContext.registries(), itemTags));
                        }
                    }

                    int iron = 0, gold = 0, diamond = 0, netherite = 0;
                    boolean hasItems = false;
                    for (int i = 0; i < handler.getSlots(); i++) {
                        ItemStack stackInSlot = handler.getStackInSlot(i);
                        if (!stackInSlot.isEmpty()) hasItems = true;

                        if (stackInSlot.getItem() == ModItems.IRON_TROPHY.get()) {
                            iron++;
                        } else if (stackInSlot.getItem() == ModItems.GOLD_TROPHY.get()) {
                            gold++;
                        } else if (stackInSlot.getItem() == ModItems.DIAMOND_TROPHY.get()) {
                            diamond++;
                        } else if (stackInSlot.getItem() == ModItems.NETHERITE_TROPHY.get()) {
                            netherite++;
                        }
                    }

                    if (hasItems) {
                        pTooltipComponents.add(Component.literal(""));
                        pTooltipComponents.add(Component.translatable("tooltip.advancementtrophies.trophy_crate.contents").withStyle(ChatFormatting.GRAY));
                        if (iron > 0) pTooltipComponents.add(Component.literal("  ").append(Component.translatable("rarity.advancementtrophies.iron")).append(": " + iron).withStyle(TrophyRarity.IRON.getItemRarity().getStyleModifier()));
                        if (gold > 0) pTooltipComponents.add(Component.literal("  ").append(Component.translatable("rarity.advancementtrophies.gold")).append(": " + gold).withStyle(TrophyRarity.GOLD.getItemRarity().getStyleModifier()));
                        if (diamond > 0) pTooltipComponents.add(Component.literal("  ").append(Component.translatable("rarity.advancementtrophies.diamond")).append(": " + diamond).withStyle(TrophyRarity.DIAMOND.getItemRarity().getStyleModifier()));
                        if (netherite > 0) pTooltipComponents.add(Component.literal("  ").append(Component.translatable("rarity.advancementtrophies.netherite")).append(": " + netherite).withStyle(TrophyRarity.NETHERITE.getItemRarity().getStyleModifier()));
                    }
                }
            }
        } else {
            TooltipUtils.addHoldForDetailsTooltip(pTooltipComponents);
        }
    }

    public static ItemStack addItemToCrate(ItemStack crateStack, ItemStack trophyStack, HolderLookup.Provider registries) {
        if (!(crateStack.getItem() instanceof TrophyCrateItem) || !ModItems.isTrophy(trophyStack)) {
            return trophyStack;
        }

        CustomData data = crateStack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
        CompoundTag nbt = data.copyTag();

        ItemStackHandler handler = new ItemStackHandler(27);
        if (nbt.contains(NBTKeys.ITEMS)) {
            ListTag tagList = nbt.getList(NBTKeys.ITEMS, 10);
            for (int i = 0; i < tagList.size(); i++) {
                CompoundTag itemTags = tagList.getCompound(i);
                int slot = itemTags.getByte("Slot") & 255;
                if (slot >= 0 && slot < handler.getSlots()) {
                    handler.setStackInSlot(slot, ItemStack.parseOptional(registries, itemTags));
                }
            }
        }

        ItemStack remainder = trophyStack.copy();
        for (int i = 0; i < handler.getSlots(); i++) {
            if (remainder.isEmpty()) break;
            remainder = handler.insertItem(i, remainder, false);
        }

        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty()) {
                CompoundTag itemTag = (CompoundTag) handler.getStackInSlot(i).save(registries);
                itemTag.putByte("Slot", (byte) i);
                nbtTagList.add(itemTag);
            }
        }
        nbt.put(NBTKeys.ITEMS, nbtTagList);
        nbt.putString("id", ModBlockEntities.TROPHY_CRATE.getId().toString());
        crateStack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(nbt));
        return remainder;
    }
}