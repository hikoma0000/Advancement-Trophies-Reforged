package io.github.hikoma0000.advancementtrophies.item;

import io.github.hikoma0000.advancementtrophies.client.util.TooltipUtils;
import io.github.hikoma0000.advancementtrophies.config.ClientConfig;
import io.github.hikoma0000.advancementtrophies.config.client.input.KeyBindings;
import io.github.hikoma0000.advancementtrophies.init.ModDataComponents;
import io.github.hikoma0000.advancementtrophies.util.NBTKeys;
import io.github.hikoma0000.advancementtrophies.util.TrophyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TrophyItem extends BlockItem {
    public TrophyItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents,
            TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pIsAdvanced);
        CompoundTag nbt = pStack.get(ModDataComponents.TROPHY_DATA.get());

        if (nbt == null || nbt.isEmpty()) {
            pTooltipComponents
                    .add(Component.translatableWithFallback("tooltip.advancementtrophies.empty", "Unclaimed Trophy")
                            .withStyle(ChatFormatting.GRAY));
            return;
        }

        if (KeyBindings.isDetailsKeyDown) {
            if (nbt.contains(NBTKeys.ACHIEVER, Tag.TAG_STRING)) {
                pTooltipComponents.add(Component
                        .translatableWithFallback("tooltip.advancementtrophies.achiever", "Achiever: %s",
                                nbt.getString("achiever"))
                        .withStyle(ChatFormatting.GRAY));
            }
            if (nbt.contains(NBTKeys.DATE, Tag.TAG_COMPOUND)) {
                CompoundTag dateTag = nbt.getCompound(NBTKeys.DATE);
                try {
                    Calendar cal = Calendar.getInstance();
                    cal.set(dateTag.getInt(NBTKeys.YEAR), dateTag.getInt(NBTKeys.MONTH) - 1,
                            dateTag.getInt(NBTKeys.DAY),
                            dateTag.getInt(NBTKeys.HOUR), dateTag.getInt(NBTKeys.MINUTE),
                            dateTag.getInt(NBTKeys.SECOND));
                    SimpleDateFormat sdf = new SimpleDateFormat(ClientConfig.DATE_FORMAT.get());
                    String formattedDate = sdf.format(cal.getTime());
                    pTooltipComponents.add(Component
                            .translatableWithFallback("tooltip.advancementtrophies.date.format", "Date: %s",
                                    formattedDate)
                            .withStyle(ChatFormatting.GRAY));
                } catch (Exception e) {
                    pTooltipComponents
                            .add(Component.translatableWithFallback("tooltip.advancementtrophies.date.invalid",
                                    "Date: Invalid Format").withStyle(ChatFormatting.RED));
                }
            }
            HolderLookup.Provider provider = pContext != null ? pContext.registries() : null;
            Component advancementTitle = TrophyUtils.getAdvancementTitleFromNBT(nbt, provider);
            if (advancementTitle != null) {
                pTooltipComponents.add(Component.literal(""));
                pTooltipComponents.add(
                        Component.translatableWithFallback("tooltip.advancementtrophies.advancement", "Advancement: %s",
                                advancementTitle).withStyle(ChatFormatting.GOLD));
            }
            if (nbt.contains(NBTKeys.ADVANCEMENT_MOD, Tag.TAG_STRING)) {
                pTooltipComponents.add(Component
                        .translatableWithFallback("tooltip.advancementtrophies.mod", "Mod: %s",
                                nbt.getString("advancement_mod"))
                        .withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));
            }
        } else {
            TooltipUtils.addHoldForDetailsTooltip(pTooltipComponents);
        }
    }

    @Override
    public Component getName(ItemStack pStack) {
        CompoundTag nbt = pStack.get(ModDataComponents.TROPHY_DATA.get());
        if (nbt != null) {
            Component advancementTitle = TrophyUtils.getAdvancementTitleFromNBT(nbt, null);
            if (advancementTitle != null) {
                return Component.translatableWithFallback("item.advancementtrophies.trophy.named", "Trophy of %s",
                        advancementTitle);
            }
        }
        return super.getName(pStack);
    }
}