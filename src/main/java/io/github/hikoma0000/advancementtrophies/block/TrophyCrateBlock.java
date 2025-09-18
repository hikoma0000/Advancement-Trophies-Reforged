package io.github.hikoma0000.advancementtrophies.block;

import com.mojang.serialization.MapCodec;
import io.github.hikoma0000.advancementtrophies.block.entity.TrophyCrateBlockEntity;
import io.github.hikoma0000.advancementtrophies.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
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
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class TrophyCrateBlock extends BaseEntityBlock {
    public static final MapCodec<TrophyCrateBlock> CODEC = simpleCodec(TrophyCrateBlock::new);
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    public TrophyCrateBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(OPEN, Boolean.valueOf(false)));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(OPEN);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TrophyCrateBlockEntity(pPos, pState);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof TrophyCrateBlockEntity) {
                pPlayer.openMenu((TrophyCrateBlockEntity) entity, pPos);
                pPlayer.awardStat(Stats.OPEN_BARREL);
                return ItemInteractionResult.CONSUME;
            }
        }
        return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, ModBlockEntities.TROPHY_CRATE.get(), TrophyCrateBlockEntity::tick);
    }

    @Override
    public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (!pLevel.isClientSide && pPlayer.isCreative()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof TrophyCrateBlockEntity be) {
                if (!be.isEmpty()) {
                    ItemStack itemStack = new ItemStack(this);
                    be.saveToItem(itemStack, pLevel.registryAccess());
                    popResource(pLevel, pPos, itemStack);
                }
            }
        }
        return super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader pLevel, BlockPos pPos, BlockState pState) {
        return new ItemStack(this);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            if (pLevel.getBlockEntity(pPos) instanceof TrophyCrateBlockEntity be && !pIsMoving) {
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }


    @Override
    public boolean triggerEvent(BlockState pState, Level pLevel, BlockPos pPos, int pId, int pParam) {
        super.triggerEvent(pState, pLevel, pPos, pId, pParam);
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        return blockentity != null && blockentity.triggerEvent(pId, pParam);
    }
}