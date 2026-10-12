package io.github.hikoma0000.advancementtrophies.item;

import io.github.hikoma0000.advancementtrophies.block.entity.TrophyBlockEntity;
import io.github.hikoma0000.advancementtrophies.client.util.TooltipUtils;
import io.github.hikoma0000.advancementtrophies.config.ClientConfig;
import io.github.hikoma0000.advancementtrophies.config.client.input.KeyBindings;
import io.github.hikoma0000.advancementtrophies.network.ModNetwork;
import io.github.hikoma0000.advancementtrophies.network.packet.ClientboundOpenInscribeScreenPacket;
import io.github.hikoma0000.advancementtrophies.util.NBTKeys;
import io.github.hikoma0000.advancementtrophies.util.TrophyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TrophyItem extends BlockItem {
    public TrophyItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer,
            ItemStack pStack, BlockState pState) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof TrophyBlockEntity trophyBlockEntity) {
            CompoundTag nbt = pStack.getTag();
            if (nbt != null) {
                trophyBlockEntity.setTrophyData(nbt.copy());
            }
        }
        return super.updateCustomBlockEntityTag(pPos, pLevel, pPlayer, pStack, pState);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if (TrophyUtils.hasTrophyData(stack)) {
            return super.use(pLevel, pPlayer, pUsedHand);
        }
        if (pPlayer instanceof ServerPlayer serverPlayer) {
            Set<ResourceLocation> inscribableIds = serverPlayer.server.getAdvancements().getAllAdvancements().stream()
                    .filter(advancement -> TrophyUtils.getTrophyItem(advancement.getDisplay()) == this)
                    .filter(advancement -> serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone())
                    .map(Advancement::getId)
                    .collect(Collectors.toSet());
            ModNetwork.getChannel().send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new ClientboundOpenInscribeScreenPacket(pUsedHand, inscribableIds));
        }
        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide);
    }

    @Override
    public boolean canGrindstoneRepair(ItemStack stack) {
        return TrophyUtils.hasTrophyData(stack);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
            TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        CompoundTag nbt = pStack.getTag();
        if (!TrophyUtils.hasTrophyData(pStack)) {
            pTooltipComponents
                    .add(Component.translatableWithFallback("tooltip.advancementtrophies.empty.hint",
                            "Use to inscribe a completed advancement").withStyle(ChatFormatting.YELLOW));
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
            if (nbt.contains(NBTKeys.ADVANCEMENT_MOD, Tag.TAG_STRING)) {
                String modId = nbt.getString(NBTKeys.ADVANCEMENT_MOD);
                if (!modId.isEmpty()) {
                    String displayName = TrophyUtils.resolveModDisplayName(modId);
                    pTooltipComponents.add(Component
                            .translatableWithFallback("tooltip.advancementtrophies.mod", "Mod: %s", displayName)
                            .withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));
                }
            }

        } else {
            TooltipUtils.addHoldForDetailsTooltip(pTooltipComponents);
        }

        if (nbt.contains(NBTKeys.ADVANCEMENT_ID, Tag.TAG_STRING) && pLevel != null && pLevel.isClientSide) {
            TooltipUtils.addOpenAdvancementTooltip(pTooltipComponents);
        }
    }



    @Override
    public Component getName(ItemStack pStack) {
        if (!TrophyUtils.hasTrophyData(pStack)) {
            return Component.translatableWithFallback("item.advancementtrophies.trophy.blank", "Blank %s",
                    super.getName(pStack));
        }
        Component advancementTitle = TrophyUtils.getAdvancementTitleFromNBT(pStack.getTag());
        if (advancementTitle != null) {
            return Component.translatableWithFallback("item.advancementtrophies.trophy.named", "Trophy of %s",
                    advancementTitle);
        }
        return super.getName(pStack);
    }
}