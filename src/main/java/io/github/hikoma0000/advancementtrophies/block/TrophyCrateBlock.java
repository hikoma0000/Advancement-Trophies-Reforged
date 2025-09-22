package io.github.hikoma0000.advancementtrophies.block;

import io.github.hikoma0000.advancementtrophies.block.entity.TrophyCrateBlockEntity;
import io.github.hikoma0000.advancementtrophies.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("removal")
public class TrophyCrateBlock extends BaseEntityBlock {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final ResourceLocation CONTENTS = new ResourceLocation("contents");


    public TrophyCrateBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(OPEN, Boolean.valueOf(false)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPEN);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TrophyCrateBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if(entity instanceof TrophyCrateBlockEntity) {
                NetworkHooks.openScreen(((ServerPlayer)player), (TrophyCrateBlockEntity)entity, pos);
                player.awardStat(Stats.OPEN_BARREL);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof TrophyCrateBlockEntity) {
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.TROPHY_CRATE.get(), TrophyCrateBlockEntity::tick);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        BlockEntity blockentity = level.getBlockEntity(pos);
        return blockentity != null && blockentity.triggerEvent(id, param);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof TrophyCrateBlockEntity trophyCrateBlockEntity) {
            if (!level.isClientSide && player.isCreative() && !trophyCrateBlockEntity.isEmpty()) {
                ItemStack itemstack = new ItemStack(this);
                blockentity.saveToItem(itemstack);
                if (trophyCrateBlockEntity.hasCustomName()) {
                    itemstack.setHoverName(trophyCrateBlockEntity.getCustomName());
                }

                ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
                itementity.setDefaultPickUpDelay();
                level.addFreshEntity(itementity);
            } else {
                trophyCrateBlockEntity.unpackLootTable(player);
            }
        }

        super.playerWillDestroy(level, pos, state, player);
    }


    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity blockentity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockentity instanceof TrophyCrateBlockEntity trophyCrateBlockEntity) {
            params = params.withDynamicDrop(CONTENTS, (consumer) -> {
                for(int i = 0; i < trophyCrateBlockEntity.getContainerSize(); ++i) {
                    consumer.accept(trophyCrateBlockEntity.getItem(i));
                }
            });
        }
        return super.getDrops(state, params);
    }


    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof TrophyCrateBlockEntity) {
                ((TrophyCrateBlockEntity)blockentity).setCustomName(stack.getHoverName());
            }
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack itemstack = new ItemStack(this);
        level.getBlockEntity(pos, ModBlockEntities.TROPHY_CRATE.get()).ifPresent(be -> be.saveToItem(itemstack));
        return itemstack;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }
}