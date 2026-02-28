package io.github.hikoma0000.advancementtrophies.item;

import io.github.hikoma0000.advancementtrophies.block.entity.TrophyCrateBlockEntity;
import io.github.hikoma0000.advancementtrophies.client.util.TooltipUtils;
import io.github.hikoma0000.advancementtrophies.config.client.input.KeyBindings;
import io.github.hikoma0000.advancementtrophies.init.ModItems;
import io.github.hikoma0000.advancementtrophies.util.NBTKeys;
import io.github.hikoma0000.advancementtrophies.util.TrophyRarity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

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
        } else if (!pLevel.isClientSide()) {
            MenuProvider menuProvider = new TrophyCrateItemMenuProvider(itemstack, pHand);
            NetworkHooks.openScreen((ServerPlayer) pPlayer, menuProvider, buf -> buf.writeEnum(pHand));
            pPlayer.awardStat(Stats.OPEN_BARREL);
            pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.BARREL_OPEN,
                    SoundSource.PLAYERS, 0.5F, pLevel.random.nextFloat() * 0.1F + 0.9F);
        }
        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }

    private boolean isLocked(ItemStack pStack) {
        CompoundTag compoundtag = pStack.getTagElement(NBTKeys.BLOCK_ENTITY_TAG);
        return compoundtag != null && compoundtag.contains(NBTKeys.LOCK, 8)
                && !compoundtag.getString(NBTKeys.LOCK).isEmpty();
    }

    private boolean canOpen(ItemStack pStack, Player pPlayer) {
        CompoundTag compoundtag = pStack.getTagElement(NBTKeys.BLOCK_ENTITY_TAG);
        if (compoundtag == null || !compoundtag.contains(NBTKeys.LOCK, 8)) {
            return true;
        }
        String s = compoundtag.getString(NBTKeys.LOCK);
        return s.isEmpty()
                || pPlayer.isHolding(item -> item.hasCustomHoverName() && item.getHoverName().getString().equals(s));
    }

    @Override
    public void onDestroyed(ItemEntity pItemEntity) {
        CompoundTag blockEntityTag = pItemEntity.getItem().getTagElement(NBTKeys.BLOCK_ENTITY_TAG);
        if (blockEntityTag != null) {
            NonNullList<ItemStack> items = NonNullList.withSize(TrophyCrateBlockEntity.CONTAINER_SIZE, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(blockEntityTag, items);
            for (ItemStack stackInSlot : items) {
                if (!stackInSlot.isEmpty()) {
                    pItemEntity.level().addFreshEntity(new ItemEntity(pItemEntity.level(), pItemEntity.getX(),
                            pItemEntity.getY(), pItemEntity.getZ(), stackInSlot));
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
            TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("tooltip.advancementtrophies.trophy_crate.description")
                .withStyle(ChatFormatting.GRAY));

        if (KeyBindings.isDetailsKeyDown) {
            CompoundTag blockEntityTag = pStack.getTagElement(NBTKeys.BLOCK_ENTITY_TAG);
            if (blockEntityTag != null) {
                NonNullList<ItemStack> items = NonNullList.withSize(TrophyCrateBlockEntity.CONTAINER_SIZE,
                        ItemStack.EMPTY);
                ContainerHelper.loadAllItems(blockEntityTag, items);

                if (items.stream().anyMatch(item -> !item.isEmpty())) {
                    pTooltipComponents.add(Component.literal(""));
                    pTooltipComponents.add(Component.translatable("tooltip.advancementtrophies.trophy_crate.contents")
                            .withStyle(ChatFormatting.GRAY));
                    java.util.Map<TrophyRarity, Integer> counts = new java.util.EnumMap<>(TrophyRarity.class);
                    for (ItemStack stackInSlot : items) {
                        if (stackInSlot.isEmpty())
                            continue;
                        TrophyRarity rarity = ModItems.getTrophyRarity(stackInSlot.getItem());
                        if (rarity != null) {
                            counts.put(rarity, counts.getOrDefault(rarity, 0) + stackInSlot.getCount());
                        }
                    }

                    for (TrophyRarity rarity : TrophyRarity.values()) {
                        int count = counts.getOrDefault(rarity, 0);
                        if (count > 0) {
                            pTooltipComponents
                                    .add(Component
                                            .translatableWithFallback("tooltip.advancementtrophies.trophy_crate.amount",
                                                    "  %s: %s",
                                                    Component.translatable(
                                                            "rarity.advancementtrophies." + rarity.getName()),
                                                    count)
                                            .withStyle(rarity.getItemRarity().color));
                        }
                    }
                }
            }
        } else {
            TooltipUtils.addHoldForDetailsTooltip(pTooltipComponents);
        }
    }

    public static ItemStack addItemToCrate(ItemStack crateStack, ItemStack trophyStack) {
        if (!(crateStack.getItem() instanceof TrophyCrateItem) || !ModItems.isTrophy(trophyStack)) {
            return trophyStack;
        }

        CompoundTag blockEntityTag = crateStack.getOrCreateTagElement(NBTKeys.BLOCK_ENTITY_TAG);
        NonNullList<ItemStack> items = NonNullList.withSize(TrophyCrateBlockEntity.CONTAINER_SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(blockEntityTag, items);

        ItemStack remainder = trophyStack.copy();
        for (int i = 0; i < items.size(); i++) {
            if (remainder.isEmpty())
                break;
            ItemStack current = items.get(i);
            if (current.isEmpty()) {
                items.set(i, remainder.split(remainder.getMaxStackSize()));
            } else if (ItemStack.isSameItemSameTags(current, remainder)
                    && current.getCount() < current.getMaxStackSize()) {
                int toAdd = Math.min(remainder.getCount(), current.getMaxStackSize() - current.getCount());
                current.grow(toAdd);
                remainder.shrink(toAdd);
            }
        }
        ContainerHelper.saveAllItems(blockEntityTag, items);
        return remainder;
    }
}