package io.github.hikoma0000.advancementtrophies.block.entity;

import io.github.hikoma0000.advancementtrophies.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        if (!trophyData.isEmpty()) {
            CompoundTag components = new CompoundTag();
            components.put("advancementtrophies:trophy_data", trophyData.copy());
            pTag.put("components", components);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        if (pTag.contains("components", CompoundTag.TAG_COMPOUND)) {
            CompoundTag componentsTag = pTag.getCompound("components");
            if (componentsTag.contains("advancementtrophies:trophy_data", CompoundTag.TAG_COMPOUND)) {
                this.trophyData = componentsTag.getCompound("advancementtrophies:trophy_data").copy();
                return;
            }
        }
        CompoundTag customData = pTag.copy();
        customData.remove("id");
        customData.remove("x");
        customData.remove("y");
        customData.remove("z");
        customData.remove("keepPacked");
        this.trophyData = customData;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        if (!this.trophyData.isEmpty()) {
            CompoundTag components = new CompoundTag();
            components.put("advancementtrophies:trophy_data", this.trophyData.copy());
            tag.put("components", components);
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        if (pTag != null) {
            this.loadAdditional(pTag, pRegistries);
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
