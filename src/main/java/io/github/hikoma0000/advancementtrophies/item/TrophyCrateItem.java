package io.github.hikoma0000.advancementtrophies.item;

import io.github.hikoma0000.advancementtrophies.client.util.TooltipUtils;
import io.github.hikoma0000.advancementtrophies.config.client.input.KeyBindings;
import io.github.hikoma0000.advancementtrophies.init.ModItems;
import io.github.hikoma0000.advancementtrophies.util.TrophyRarity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class TrophyCrateItem extends BlockItem {
    public TrophyCrateItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (isLocked(itemstack) && !canOpen(itemstack, pPlayer)) {
            pPlayer.displayClientMessage(Component.translatable("container.isLocked", itemstack.getHoverName()), true);
            pPlayer.playSound(SoundEvents.CHEST_LOCKED, 1.0F, 1.0F);
            return InteractionResultHolder.fail(itemstack);
        } else if (pPlayer instanceof ServerPlayer serverPlayer) {
            MenuProvider menuProvider = new TrophyCrateItemMenuProvider(itemstack, pHand);
            serverPlayer.openMenu(menuProvider, buf -> buf.writeEnum(pHand));
            pPlayer.awardStat(Stats.OPEN_BARREL);
            pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.BARREL_OPEN, SoundSource.PLAYERS, 0.5F, pLevel.random.nextFloat() * 0.1F + 0.9F);
        }
        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }

    private boolean isLocked(ItemStack pStack) {
        LockCode lock = pStack.get(DataComponents.LOCK);
        return lock != null && !lock.key().isEmpty();
    }

    private boolean canOpen(ItemStack pStack, Player pPlayer) {
        LockCode lock = pStack.getOrDefault(DataComponents.LOCK, LockCode.NO_LOCK);
        return lock.unlocksWith(pPlayer.getMainHandItem()) || lock.unlocksWith(pPlayer.getOffhandItem());
    }


    @Override
    public void onDestroyed(ItemEntity pItemEntity) {
        ItemContainerContents contents = pItemEntity.getItem().get(DataComponents.CONTAINER);
        if (contents != null) {
            for (ItemStack stackInSlot : contents.stream().toList()) {
                if (!stackInSlot.isEmpty()) {
                    pItemEntity.level().addFreshEntity(new ItemEntity(pItemEntity.level(), pItemEntity.getX(), pItemEntity.getY(), pItemEntity.getZ(), stackInSlot));
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("tooltip.advancementtrophies.trophy_crate.description").withStyle(ChatFormatting.GRAY));

        if (KeyBindings.isDetailsKeyDown) {
            ItemContainerContents contents = pStack.get(DataComponents.CONTAINER);
            if (contents != null && !contents.stream().allMatch(ItemStack::isEmpty)) {
                pTooltipComponents.add(Component.literal(""));
                pTooltipComponents.add(Component.translatable("tooltip.advancementtrophies.trophy_crate.contents").withStyle(ChatFormatting.GRAY));
                int iron = 0, gold = 0, diamond = 0, netherite = 0;
                for (ItemStack stackInSlot : contents.stream().toList()) {
                    if (stackInSlot.is(ModItems.IRON_TROPHY.get())) {
                        iron++;
                    } else if (stackInSlot.is(ModItems.GOLD_TROPHY.get())) {
                        gold++;
                    } else if (stackInSlot.is(ModItems.DIAMOND_TROPHY.get())) {
                        diamond++;
                    } else if (stackInSlot.is(ModItems.NETHERITE_TROPHY.get())) {
                        netherite++;
                    }
                }
                if (iron > 0) pTooltipComponents.add(Component.literal("  ").append(Component.translatable("rarity.advancementtrophies.iron")).append(": " + iron).withStyle(TrophyRarity.IRON.getStyleModifier()));
                if (gold > 0) pTooltipComponents.add(Component.literal("  ").append(Component.translatable("rarity.advancementtrophies.gold")).append(": " + gold).withStyle(TrophyRarity.GOLD.getStyleModifier()));
                if (diamond > 0) pTooltipComponents.add(Component.literal("  ").append(Component.translatable("rarity.advancementtrophies.diamond")).append(": " + diamond).withStyle(TrophyRarity.DIAMOND.getStyleModifier()));
                if (netherite > 0) pTooltipComponents.add(Component.literal("  ").append(Component.translatable("rarity.advancementtrophies.netherite")).append(": " + netherite).withStyle(TrophyRarity.NETHERITE.getStyleModifier()));
            }
        } else {
            TooltipUtils.addHoldForDetailsTooltip(pTooltipComponents);
        }
    }

    public static ItemStack addItemToCrate(ItemStack crateStack, ItemStack trophyStack) {
        if (!(crateStack.getItem() instanceof TrophyCrateItem) || !ModItems.isTrophy(trophyStack)) {
            return trophyStack;
        }

        ItemContainerContents contents = crateStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        NonNullList<ItemStack> items = NonNullList.createWithCapacity(contents.getSlots());
        contents.copyInto(items);

        ItemStack remainder = trophyStack.copy();
        for (int i = 0; i < items.size(); i++) {
            if (remainder.isEmpty()) break;
            ItemStack current = items.get(i);
            if (current.isEmpty()) {
                items.set(i, remainder.split(remainder.getMaxStackSize()));
            } else if (ItemStack.isSameItemSameComponents(current, remainder) && current.getCount() < current.getMaxStackSize()) {
                int toAdd = Math.min(remainder.getCount(), current.getMaxStackSize() - current.getCount());
                current.grow(toAdd);
                remainder.shrink(toAdd);
            }
        }

        crateStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
        return remainder;
    }
}