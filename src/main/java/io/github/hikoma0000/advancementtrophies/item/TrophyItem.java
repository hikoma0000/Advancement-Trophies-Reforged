package io.github.hikoma0000.advancementtrophies.item;

import io.github.hikoma0000.advancementtrophies.block.entity.TrophyBlockEntity;
import io.github.hikoma0000.advancementtrophies.config.ClientConfig;
import io.github.hikoma0000.advancementtrophies.config.client.input.KeyBindings;
import io.github.hikoma0000.advancementtrophies.util.NBTKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TrophyItem extends BlockItem {
    public TrophyItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof TrophyBlockEntity trophyBlockEntity) {
            CompoundTag nbt = pStack.getTag();
            if (nbt != null) {
                trophyBlockEntity.setTrophyData(nbt.copy());
            }
        }
        return super.updateCustomBlockEntityTag(pPos, pLevel, pPlayer, pStack, pState);
    }

    @Nullable
    private Component getAdvancementTitleFromNBT(CompoundTag nbt) {
        if (nbt.contains(NBTKeys.ADVANCEMENT_TITLE, Tag.TAG_STRING)) {
            return Component.translatableWithFallback(nbt.getString(NBTKeys.ADVANCEMENT_TITLE), nbt.getString(NBTKeys.ADVANCEMENT_TITLE));
        }
        if (nbt.contains(NBTKeys.ADVANCEMENT_TITLE_JSON, Tag.TAG_STRING)) {
            try {
                return Component.Serializer.fromJson(nbt.getString(NBTKeys.ADVANCEMENT_TITLE_JSON));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        CompoundTag nbt = pStack.getTag();
        if (nbt == null) {
            pTooltipComponents.add(Component.translatableWithFallback("tooltip.advancementtrophies.empty", "Unclaimed Trophy").withStyle(ChatFormatting.GRAY));
            return;
        }

        if (KeyBindings.isDetailsKeyDown) {
            if (nbt.contains(NBTKeys.ACHIEVER, Tag.TAG_STRING)) {
                pTooltipComponents.add(Component.translatableWithFallback("tooltip.advancementtrophies.achiever", "Achiever: %s", nbt.getString("achiever"))
                        .withStyle(ChatFormatting.GRAY));
            }
            if (nbt.contains(NBTKeys.DATE, Tag.TAG_COMPOUND)) {
                CompoundTag dateTag = nbt.getCompound(NBTKeys.DATE);
                try {
                    Calendar cal = Calendar.getInstance();
                    cal.set(dateTag.getInt(NBTKeys.YEAR), dateTag.getInt(NBTKeys.MONTH) - 1, dateTag.getInt(NBTKeys.DAY),
                            dateTag.getInt(NBTKeys.HOUR), dateTag.getInt(NBTKeys.MINUTE), dateTag.getInt(NBTKeys.SECOND));
                    SimpleDateFormat sdf = new SimpleDateFormat(ClientConfig.DATE_FORMAT.get());
                    String formattedDate = sdf.format(cal.getTime());
                    pTooltipComponents.add(Component.translatableWithFallback("tooltip.advancementtrophies.date.format", "Date: %s", formattedDate)
                            .withStyle(ChatFormatting.GRAY));
                } catch (Exception e) {
                    pTooltipComponents.add(Component.literal("Date: Invalid Format").withStyle(ChatFormatting.RED));
                }
            }
            Component advancementTitle = getAdvancementTitleFromNBT(nbt);
            if (advancementTitle != null) {
                pTooltipComponents.add(Component.literal(""));
                pTooltipComponents.add(Component.translatableWithFallback("tooltip.advancementtrophies.advancement", "Advancement: %s",
                        advancementTitle).withStyle(ChatFormatting.GOLD));
            }
            if (nbt.contains(NBTKeys.ADVANCEMENT_MOD, Tag.TAG_STRING)) {
                pTooltipComponents.add(Component.translatableWithFallback("tooltip.advancementtrophies.mod", "Mod: %s", nbt.getString("advancement_mod"))
                        .withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));
            }
        } else {
            Component keyName = KeyBindings.SHOW_DETAILS_KEY.getTranslatedKeyMessage();
            pTooltipComponents.add(Component.translatableWithFallback("tooltip.advancementtrophies.hold_for_details", "§7Hold [§f%s§7] for details", keyName));
        }
    }

    @Override
    public Component getName(ItemStack pStack) {
        CompoundTag nbt = pStack.getTag();
        if (nbt != null) {
            Component advancementTitle = getAdvancementTitleFromNBT(nbt);
            if (advancementTitle != null) {
                return Component.translatableWithFallback("item.advancementtrophies.trophy.named", "Trophy of %s", advancementTitle);
            }
        }
        return super.getName(pStack);
    }
}