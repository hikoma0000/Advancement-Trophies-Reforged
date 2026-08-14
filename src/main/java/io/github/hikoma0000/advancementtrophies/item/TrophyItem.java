package io.github.hikoma0000.advancementtrophies.item;

import io.github.hikoma0000.advancementtrophies.client.util.TooltipUtils;
import io.github.hikoma0000.advancementtrophies.component.TrophyData;
import io.github.hikoma0000.advancementtrophies.component.TrophyDate;
import io.github.hikoma0000.advancementtrophies.config.ClientConfig;
import io.github.hikoma0000.advancementtrophies.config.client.input.KeyBindings;
import io.github.hikoma0000.advancementtrophies.init.ModDataComponents;
import io.github.hikoma0000.advancementtrophies.util.TrophyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TrophyItem extends BlockItem {
    public TrophyItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents,
            TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pIsAdvanced);
        TrophyData data = pStack.get(ModDataComponents.TROPHY_DATA.get());
        if (data == null) {
            pTooltipComponents
                    .add(Component.translatableWithFallback("tooltip.advancementtrophies.empty", "Unclaimed Trophy")
                            .withStyle(ChatFormatting.GRAY));
            return;
        }

        if (KeyBindings.isDetailsKeyDown) {
            if (data.hasAchiever()) {
                pTooltipComponents.add(Component
                        .translatableWithFallback("tooltip.advancementtrophies.achiever", "Achiever: %s",
                                data.achiever())
                        .withStyle(ChatFormatting.GRAY));
            }
            TrophyDate date = data.date();
            try {
                Calendar cal = Calendar.getInstance();
                cal.set(date.year(), date.month() - 1, date.day(),
                        date.hour(), date.minute(), date.second());
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
            if (data.advancementMod() != null && !data.advancementMod().isEmpty()) {
                String displayName = TrophyUtils.resolveModDisplayName(data.advancementMod());
                pTooltipComponents.add(Component.translatableWithFallback(
                                "tooltip.advancementtrophies.mod", "Mod: %s",
                                displayName)
                        .withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));
            }
        } else {
            TooltipUtils.addHoldForDetailsTooltip(pTooltipComponents);
        }

        Level level = pContext.level();
        if (data.hasAdvancementId() && level != null && level.isClientSide) {
            TooltipUtils.addOpenAdvancementTooltip(pTooltipComponents);
        }
    }

    @Override
    public Component getName(ItemStack pStack) {
        Component advancementTitle = TrophyUtils.getAdvancementTitle(pStack);
        if (advancementTitle != null) {
            return Component.translatableWithFallback("item.advancementtrophies.trophy.named", "Trophy of %s",
                    advancementTitle);
        }
        return super.getName(pStack);
    }
}
