package io.github.hikoma0000.advancementtrophies.block.entity;

import io.github.hikoma0000.advancementtrophies.init.ModBlockEntities;
import io.github.hikoma0000.advancementtrophies.util.NBTKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TrophyBlockEntity extends BlockEntity {
    private CompoundTag trophyData = new CompoundTag();

    public TrophyBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.TROPHY.get(), pPos, pBlockState);
    }

    public CompoundTag getTrophyData() {
        return trophyData;
    }

    public void setTrophyData(CompoundTag trophyData) {
        this.trophyData = trophyData;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void saveToItem(ItemStack pStack) {
        if (this.trophyData != null && !this.trophyData.isEmpty()) {
            pStack.getOrCreateTag().merge(this.trophyData);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("TrophyData", trophyData);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.trophyData = pTag.getCompound("TrophyData");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}