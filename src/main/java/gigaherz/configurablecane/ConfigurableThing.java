package gigaherz.configurablecane;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;

import javax.annotation.Nullable;

public class ConfigurableThing
{
    private final IntegerProperty ageProperty;
    private final Configurations.ServerConfig.ThingConfig config;
    private final boolean isTop;
    private final Block ownerBlock;
    private final Block mainBlock;
    private final Block topBlock;
    private final boolean stupidEventFiresAbove;
    private final IConfigurable owner;

    public ConfigurableThing(Configurations.ServerConfig.ThingConfig config,
                             IntegerProperty ageProperty,
                             boolean isTop,
                             Block ownerBlock,
                             Block mainBlock,
                             Block topBlock,
                             boolean stupidEventFiresAbove, IConfigurable owner)
    {
        this.config = config;
        this.isTop = isTop;
        this.ownerBlock = ownerBlock;
        this.mainBlock = mainBlock;
        this.topBlock = topBlock;
        this.ageProperty = ageProperty;
        this.stupidEventFiresAbove = stupidEventFiresAbove;
        this.owner = owner;
    }

    public boolean canPlaceOn(Block blockBelow)
    {
        return blockBelow == mainBlock || blockBelow == topBlock;
    }

    private BlockState randomAge(Level world, BlockState state, int maxAge)
    {
        return state.setValue(ageProperty, world.random.nextInt(maxAge));
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
            return randomAge(ctx.getLevel(), topBlock.defaultBlockState(), config.maxAgeValue);
        }
        else
        {
            return null;
        }
    }

    private int getStackHeight(Level world, BlockPos pos)
    {
        int stackHeight = 1;
        while (world.getBlockState(pos.below(stackHeight)).getBlock() == mainBlock)
        {
            ++stackHeight;
        }
        return stackHeight;
    }

    public boolean randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand)
    {
        if (!config.enabledValue)
        {
            return false;
        }

        if (!world.isAreaLoaded(pos, 1))
            return true; // Forge: prevent growing cactus from loading unloaded chunks with block update

        if (!state.canSurvive(world, pos))
        {
            world.destroyBlock(pos, true);
            return true;
        }

        if (!world.isEmptyBlock(pos.above()))
            return true;

        int maxAge = config.maxAgeValue;
        int maxHeight = config.maxHeightValue;

        if (config.kelpLikeGrowthValue)
        {
            kelpLikeGrowth(state, world, pos, maxAge, maxHeight, rand);
        }
        else
        {
            caneLikeGrowth(state, world, pos, maxAge, maxHeight, rand);
        }

        return true;
    }

    private void caneLikeGrowth(BlockState state, ServerLevel world, BlockPos pos, int maxAge, int maxHeight, RandomSource rand)
    {
        if (isTop)
        {
            // When cane-like growth is configured, the special "top" block is not needed
            world.setBlock(pos, mainBlock.defaultBlockState(), 2);
            return;
        }

        BlockPos eventPos = stupidEventFiresAbove ? pos.above() : pos;
        if (CommonHooks.canCropGrow(world, eventPos, state, true))
        {
            int age = state.getValue(ageProperty);
            if (age != 8 || owner.configurableCane$doSpecialGrowth(world, pos, age, getStackHeight(world, pos), rand))
            {
                if (getStackHeight(world, pos) >= maxHeight)
                    return;

                if (age >= maxAge)
                {
                    world.setBlockAndUpdate(pos.above(), ownerBlock.defaultBlockState());
                    world.setBlock(pos, state.setValue(ageProperty, 0), 4);
                }
                else
                {
                    world.setBlock(pos, state.setValue(ageProperty, age + 1), 4);
                }
            }
            CommonHooks.fireCropGrowPost(world, pos, state);
        }
    }

    private void kelpLikeGrowth(BlockState state, ServerLevel world, BlockPos pos, int maxAge, int maxHeight, RandomSource rand)
    {
        if (!isTop)
        {
            // When kelp-like growth is configured, the special "top" block is needed
            world.setBlock(pos, randomAge(world, topBlock.defaultBlockState(), maxAge), 2);
            return;
        }

        BlockPos eventPos = stupidEventFiresAbove ? pos.above() : pos;
        if (CommonHooks.canCropGrow(world, eventPos, state, true))
        {
            int age = state.getValue(ageProperty);
            if (age < maxAge && rand.nextDouble() < config.kelpLikeGrowthChanceValue)
            {
                if (owner.configurableCane$doSpecialGrowth(world, pos, age, getStackHeight(world, pos), rand))
                {
                    if (getStackHeight(world, pos) >= maxHeight)
                        return;

                    int ageGrowthInt = (int) config.kelpLikeAgeChanceValue;
                    double random = config.kelpLikeAgeChanceValue - ageGrowthInt;
                    if (random > 0 && rand.nextDouble() < random)
                        ageGrowthInt++;
                    world.setBlockAndUpdate(pos.above(), state.setValue(ageProperty, age + ageGrowthInt));
                    world.setBlock(pos, mainBlock.defaultBlockState().setValue(ageProperty, 15), 2);
                }
            }
            CommonHooks.fireCropGrowPost(world, pos, state);
        }
    }
}
