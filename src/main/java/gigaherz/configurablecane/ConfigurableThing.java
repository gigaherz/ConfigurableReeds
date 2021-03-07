package gigaherz.configurablecane;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Supplier;

public class ConfigurableThing
{
    private final IntegerProperty ageProperty;
    private final Configurations.ServerConfig.ThingConfig config;
    private final boolean isTop;
    private final Supplier<Block> ownerBlock;
    private final Supplier<Block> mainBlock;
    private final Supplier<Block> topBlock;
    private final boolean stupidEventFiresAbove;

    public ConfigurableThing(Configurations.ServerConfig.ThingConfig config, IntegerProperty ageProperty, boolean isTop, Supplier<Block> ownerBlock, Supplier<Block> mainBlock, Supplier<Block> topBlock, boolean stupidEventFiresAbove)
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

    private BlockState randomAge(World world, BlockState state, int maxAge)
    {
        return state.with(ageProperty, world.rand.nextInt(maxAge));
    }

    public boolean isValidPosition(IWorldReader world, BlockPos pos)
    {
        Block blockBelow = world.getBlockState(pos.down()).getBlock();
        return canPlaceOn(blockBelow);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext ctx)
    {
        if (Configurations.SERVER.cactus.kelpLikeGrowthValue)
        {
            return randomAge(ctx.getWorld(), topBlock.get().getDefaultState(), config.maxAgeValue);
        }
        else
        {
            return null;
        }
    }

    private int getStackHeight(World world, BlockPos pos)
    {
        int stackHeight = 1;
        while (world.getBlockState(pos.down(stackHeight)).getBlock() == mainBlock.get())
        {
            ++stackHeight;
        }
        return stackHeight;
    }

    public boolean randomTick(BlockState state, World world, BlockPos pos, Random rand)
    {
        if (!config.enabledValue)
        {
            return false;
        }

        if (!world.isAreaLoaded(pos, 1))
            return true; // Forge: prevent growing cactus from loading unloaded chunks with block update

        if (!state.isValidPosition(world, pos))
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

    private boolean caneLikeGrowth(BlockState state, World world, BlockPos pos, int maxAge, int maxHeight)
    {
        if (isTop)
        {
            world.setBlockState(pos, mainBlock.get().getDefaultState(), 2);
            return true;
        }

        if (!world.isAirBlock(pos.up()))
            return true;

        if (getStackHeight(world, pos) >= maxHeight)
            return true;

        BlockPos eventPos = stupidEventFiresAbove ? pos.up() : pos;
        if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(world, eventPos, state, true))
        {
            int age = state.get(ageProperty);
            if (age >= maxAge)
            {
                world.setBlockState(pos.up(), ownerBlock.get().getDefaultState());
                world.setBlockState(pos, state.with(ageProperty, 0), 4);
            }
            else
            {
                world.setBlockState(pos, state.with(ageProperty, age + 1), 4);
            }
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(world, pos, state);
        }

        return true;
    }

    private boolean kelpLikeGrowth(BlockState state, World world, BlockPos pos, int maxAge, int maxHeight, Random rand)
    {
        if (!world.isAirBlock(pos.up()))
            return true;

        if (getStackHeight(world, pos) >= maxHeight)
            return true;

        if (isTop)
        {
            int age = state.get(ageProperty);
            if (age < maxAge && rand.nextDouble() < config.kelpLikeGrowthChanceValue)
            {
                BlockPos eventPos = stupidEventFiresAbove ? pos.up() : pos;
                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(world, eventPos, state, true))
                {
                    int ageGrowthInt = (int) config.kelpLikeAgeChanceValue;
                    double random = config.kelpLikeAgeChanceValue - ageGrowthInt;
                    if (random > 0 && rand.nextDouble() < random)
                        ageGrowthInt++;
                    world.setBlockState(pos.up(), state.with(ageProperty, age + ageGrowthInt));
                    world.setBlockState(pos, mainBlock.get().getDefaultState().with(ageProperty, 15), 2);
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(world, pos, state);
                }
            }
        }
        else
        {
            world.setBlockState(pos, randomAge(world, topBlock.get().getDefaultState(), maxAge), 2);
        }

        return true;
    }
}
