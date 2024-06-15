package gigaherz.configurablecane;

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
import java.util.Random;
import java.util.function.Supplier;

public class ConfigurableThing
{
    private final IntegerProperty ageProperty;
    private final Configurations.ServerConfig.ThingConfig config;
    private final boolean isTop;
    private final Supplier<? extends Block> ownerBlock;
    private final Supplier<? extends Block> mainBlock;
    private final Supplier<? extends Block> topBlock;
    private final boolean stupidEventFiresAbove;

    public ConfigurableThing(Configurations.ServerConfig.ThingConfig config, IntegerProperty ageProperty, boolean isTop, Supplier<? extends Block> ownerBlock, Supplier<? extends Block> mainBlock, Supplier<? extends Block> topBlock, boolean stupidEventFiresAbove)
    {
        this.config = config;
        this.isTop = isTop;
        this.ownerBlock = ownerBlock;
        this.mainBlock = mainBlock;
        this.topBlock = topBlock;
        this.ageProperty = ageProperty;
        this.stupidEventFiresAbove = stupidEventFiresAbove;
    }

    public boolean canPlaceOn(Block blockBelow)
    {
        return blockBelow == mainBlock.get() || blockBelow == topBlock.get();
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
            return randomAge(ctx.getLevel(), topBlock.get().defaultBlockState(), config.maxAgeValue);
        }
        else
        {
            return null;
        }
    }

    private int getStackHeight(Level world, BlockPos pos)
    {
        int stackHeight = 1;
        while (world.getBlockState(pos.below(stackHeight)).getBlock() == mainBlock.get())
        {
            ++stackHeight;
        }
        return stackHeight;
    }

    public boolean randomTick(BlockState state, Level world, BlockPos pos, RandomSource rand)
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

        int maxAge = config.maxAgeValue;
        int maxHeight = config.maxHeightValue;

        if (config.kelpLikeGrowthValue)
        {
            return kelpLikeGrowth(state, world, pos, maxAge, maxHeight, rand);
        }
        else
        {
            return caneLikeGrowth(state, world, pos, maxAge, maxHeight);
        }
    }

    private boolean caneLikeGrowth(BlockState state, Level world, BlockPos pos, int maxAge, int maxHeight)
    {
        if (isTop)
        {
            world.setBlock(pos, mainBlock.get().defaultBlockState(), 2);
            return true;
        }

        if (!world.isEmptyBlock(pos.above()))
            return true;

        if (getStackHeight(world, pos) >= maxHeight)
            return true;

        BlockPos eventPos = stupidEventFiresAbove ? pos.above() : pos;
        if (CommonHooks.canCropGrow(world, eventPos, state, true))
        {
            int age = state.getValue(ageProperty);
            if (age >= maxAge)
            {
                world.setBlockAndUpdate(pos.above(), ownerBlock.get().defaultBlockState());
                world.setBlock(pos, state.setValue(ageProperty, 0), 4);
            }
            else
            {
                world.setBlock(pos, state.setValue(ageProperty, age + 1), 4);
            }
            CommonHooks.fireCropGrowPost(world, pos, state);
        }

        return true;
    }

    private boolean kelpLikeGrowth(BlockState state, Level world, BlockPos pos, int maxAge, int maxHeight, RandomSource rand)
    {
        if (!world.isEmptyBlock(pos.above()))
            return true;

        if (getStackHeight(world, pos) >= maxHeight)
            return true;

        if (isTop)
        {
            int age = state.getValue(ageProperty);
            if (age < maxAge && rand.nextDouble() < config.kelpLikeGrowthChanceValue)
            {
                BlockPos eventPos = stupidEventFiresAbove ? pos.above() : pos;
                if (CommonHooks.canCropGrow(world, eventPos, state, true))
                {
                    int ageGrowthInt = (int) config.kelpLikeAgeChanceValue;
                    double random = config.kelpLikeAgeChanceValue - ageGrowthInt;
                    if (random > 0 && rand.nextDouble() < random)
                        ageGrowthInt++;
                    world.setBlockAndUpdate(pos.above(), state.setValue(ageProperty, age + ageGrowthInt));
                    world.setBlock(pos, mainBlock.get().defaultBlockState().setValue(ageProperty, 15), 2);
                    CommonHooks.fireCropGrowPost(world, pos, state);
                }
            }
        }
        else
        {
            world.setBlock(pos, randomAge(world, topBlock.get().defaultBlockState(), maxAge), 2);
        }

        return true;
    }
}
