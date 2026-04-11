package gigaherz.configurablecane;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.common.CommonHooks;

import javax.annotation.Nullable;

public class ConfigurableThing
{
    private final IntegerProperty ageProperty;
    private final Configurations.ServerConfig.ThingConfig config;
    private final Block thisBlock;
    private final boolean stupidEventFiresAbove;
    private final IConfigurable owner;

    public ConfigurableThing(Configurations.ServerConfig.ThingConfig config,
                             IntegerProperty ageProperty,
                             Block block,
                             boolean stupidEventFiresAbove, IConfigurable owner)
    {
        this.config = config;
        this.thisBlock = block;
        this.ageProperty = ageProperty;
        this.stupidEventFiresAbove = stupidEventFiresAbove;
        this.owner = owner;
    }

    public boolean canPlaceOn(Block blockBelow)
    {
        return blockBelow == thisBlock;
    }

    private BlockState randomAge(Level level, BlockState state, int maxAge)
    {
        return state.setValue(ageProperty, level.getRandom().nextInt(maxAge));
    }

    public boolean isValidPosition(LevelReader world, BlockPos pos)
    {
        Block blockBelow = world.getBlockState(pos.below()).getBlock();
        return canPlaceOn(blockBelow);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        if (Configurations.SERVER.cactus.kelpLikeGrowthValue)
        {
            return randomAge(ctx.getLevel(), thisBlock.defaultBlockState(), config.maxAgeValue);
        }
        else
        {
            return null;
        }
    }

    private int getStackHeight(Level level, BlockPos pos, int limit)
    {
        int stackHeight = 1;
        while (stackHeight < limit && level.getBlockState(pos.below(stackHeight)).getBlock() == thisBlock)
        {
            ++stackHeight;
        }
        return stackHeight;
    }

    public boolean randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        if (!config.enabledValue)
        {
            return false;
        }

        if (!level.isAreaLoaded(pos, 1))
            return true; // Forge: prevent growing cactus from loading unloaded chunks with block update

        if (!state.canSurvive(level, pos))
        {
            level.destroyBlock(pos, true);
            return true;
        }

        if (!level.isEmptyBlock(pos.above()))
            return true;

        int maxAge = config.maxAgeValue;
        int maxHeight = config.maxHeightValue;

        if (config.kelpLikeGrowthValue)
        {
            kelpLikeGrowth(state, level, pos, maxAge, maxHeight, rand);
        }
        else
        {
            caneLikeGrowth(state, level, pos, maxAge, maxHeight, rand);
        }

        return true;
    }

    private void caneLikeGrowth(BlockState state, ServerLevel level, BlockPos pos, int maxAge, int maxHeight, RandomSource rand)
    {
        var isTop = AdditionalBlockDataAttachment.isTop(level, pos);
        if (isTop)
        {
            // When cane-like growth is configured, the special "top" block is not needed
            level.setBlock(pos, thisBlock.defaultBlockState(), 2);
            AdditionalBlockDataAttachment.setTop(level, pos, false);
            return;
        }

        BlockPos eventPos = stupidEventFiresAbove ? pos.above() : pos;
        if (CommonHooks.canCropGrow(level, eventPos, state, true))
        {
            int age = state.getValue(ageProperty);
            int stackHeight = getStackHeight(level, pos, maxHeight);
            if (age != 8 || owner.configurableCane$doSpecialGrowth(level, pos, age, stackHeight, rand))
            {
                if (age >= maxAge)
                {
                    if (stackHeight >= maxHeight)
                        return;

                    level.setBlockAndUpdate(pos.above(), thisBlock.defaultBlockState());
                    level.setBlock(pos, state.setValue(ageProperty, 0), 4);
                }
                else
                {
                    level.setBlock(pos, state.setValue(ageProperty, age + 1), 4);
                }
            }
            CommonHooks.fireCropGrowPost(level, pos, state);
        }
    }

    private void kelpLikeGrowth(BlockState state, ServerLevel level, BlockPos pos, int maxAge, int maxHeight, RandomSource rand)
    {
        var isTop = AdditionalBlockDataAttachment.isTop(level, pos);
        if (!isTop)
        {
            // When kelp-like growth is configured, the special "top" block is needed
            level.setBlock(pos, randomAge(level, thisBlock.defaultBlockState(), maxAge), 2);
            AdditionalBlockDataAttachment.setTop(level, pos, true);
            return;
        }

        BlockPos eventPos = stupidEventFiresAbove ? pos.above() : pos;
        if (CommonHooks.canCropGrow(level, eventPos, state, true))
        {
            int age = state.getValue(ageProperty);
            if (age < maxAge && rand.nextDouble() < config.kelpLikeGrowthChanceValue)
            {
                int stackHeight = getStackHeight(level, pos, maxHeight);
                if (owner.configurableCane$doSpecialGrowth(level, pos, age, stackHeight, rand))
                {
                    if (stackHeight >= maxHeight)
                        return;

                    int ageGrowthInt = (int) config.kelpLikeAgeChanceValue;
                    double random = config.kelpLikeAgeChanceValue - ageGrowthInt;
                    if (random > 0 && rand.nextDouble() < random)
                        ageGrowthInt++;
                    level.setBlockAndUpdate(pos.above(), state.setValue(ageProperty, age + ageGrowthInt));
                    AdditionalBlockDataAttachment.setTop(level, pos.above(), true);
                    level.setBlock(pos, thisBlock.defaultBlockState().setValue(ageProperty, 15), 2);
                    AdditionalBlockDataAttachment.setTop(level, pos, false);
                }
            }
            CommonHooks.fireCropGrowPost(level, pos, state);
        }
    }
}
