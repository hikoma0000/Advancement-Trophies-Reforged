package io.github.hikoma0000.advancementtrophies.block.entity;

import io.github.hikoma0000.advancementtrophies.component.TrophyData;
import io.github.hikoma0000.advancementtrophies.init.ModBlockEntities;
import io.github.hikoma0000.advancementtrophies.init.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TrophyBlockEntity extends BlockEntity {
    @Nullable
    private TrophyData pendingLegacyData;

    public TrophyBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.TROPHY.get(), pPos, pBlockState);
    }

    @Nullable
    public TrophyData getTrophyData() {
        TrophyData data = this.components().get(ModDataComponents.TROPHY_DATA.get());
        return data != null ? data : this.pendingLegacyData;
    }

    public void setTrophyData(@Nullable TrophyData trophyData) {
        this.pendingLegacyData = null;
        DataComponentPatch.Builder patch = DataComponentPatch.builder();
        if (trophyData == null) {
            patch.remove(ModDataComponents.TROPHY_DATA.get());
        } else {
            patch.set(ModDataComponents.TROPHY_DATA.get(), trophyData);
        }
        this.setComponents(PatchedDataComponentMap.fromPatch(this.components(), patch.build()));
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void saveToItem(ItemStack pStack, HolderLookup.Provider registries) {
        TrophyData data = getTrophyData();
        if (data != null) {
            pStack.set(ModDataComponents.TROPHY_DATA.get(), data);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider registries) {
        migrateLegacyDataIntoComponents();
        super.saveAdditional(pTag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider registries) {
        super.loadAdditional(pTag, registries);
        this.pendingLegacyData = null;
        if (pTag.contains("trophy_data")) {
            var ops = registries.createSerializationContext(NbtOps.INSTANCE);
            this.pendingLegacyData = TrophyData.CODEC.parse(ops, pTag.get("trophy_data")).result().orElse(null);
        }
    }

    private void migrateLegacyDataIntoComponents() {
        if (this.pendingLegacyData == null) {
            return;
        }
        if (this.components().get(ModDataComponents.TROPHY_DATA.get()) == null) {
            DataComponentMap updated = PatchedDataComponentMap.fromPatch(
                    this.components(),
                    DataComponentPatch.builder()
                            .set(ModDataComponents.TROPHY_DATA.get(), this.pendingLegacyData)
                            .build());
            this.setComponents(updated);
        }
        this.pendingLegacyData = null;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        migrateLegacyDataIntoComponents();
        return this.saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
