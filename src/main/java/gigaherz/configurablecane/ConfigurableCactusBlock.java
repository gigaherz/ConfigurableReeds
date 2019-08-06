package gigaherz.configurablecane;

import net.minecraft.block.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

class ConfigurableCactusBlock extends CactusBlock
{
    boolean isTop;

    public ConfigurableCactusBlock(boolean isTop, Properties properties)
    {
        super(properties);
        this.isTop = isTop;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx)
    {
        if (Configurations.SERVER.cactus.kelpLikeGrowth.get())
        {
            return randomAge(ctx.getWorld(), ConfigurableCane.CACTUS_TOP.getDefaultState(), Configurations.SERVER.cactus.maxAge.get());
        }
        else
        {
            return super.getStateForPlacement(ctx);
        }
    }

    private BlockState randomAge(World world, BlockState state, int maxAge)
    {
        return state.with(AGE, world.rand.nextInt(maxAge));
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos)
    {
        Block blockBelow = world.getBlockState(pos.down()).getBlock();
        if (blockBelow == Blocks.CACTUS || blockBelow == ConfigurableCane.CACTUS_TOP)
        {
            return true;
        }

        return super.isValidPosition(state, world, pos);
    }

    @Override
    public void tick(BlockState state, World world, BlockPos pos, Random rand)
    {
        if (!Configurations.SERVER.cactus.enabled.get())
        {
            super.tick(state, world, pos, rand);
            return;
        }

        if (!this.isValidPosition(state, world, pos))
        {
            world.destroyBlock(pos, true);
            return;
        }

        int maxAge = Configurations.SERVER.cactus.maxAge.get();
        int maxHeight = Configurations.SERVER.cactus.maxHeight.get();
        boolean kelpLikeGrowth = Configurations.SERVER.cactus.kelpLikeGrowth.get();
        if (isTop)
        {
            if (kelpLikeGrowth && world.isAirBlock(pos.up())
                    && getStackHeight(world, pos) < maxHeight)
            {
                int age = state.get(AGE);
                if (age < maxAge && rand.nextDouble() < Configurations.SERVER.cactus.kelpLikeGrowthChance.get())
                {
                    world.setBlockState(pos.up(), state.with(AGE, age + 1));
                    world.setBlockState(pos, Blocks.CACTUS.getDefaultState().with(AGE, 15), 2);
                }
            }
            else
            {
                world.setBlockState(pos, Blocks.CACTUS.getDefaultState(), 2);
            }
            return;
        }

        if (kelpLikeGrowth)
        {
            if (world.isAirBlock(pos.up()))
            {
                int stackHeight = getStackHeight(world, pos);
                if (stackHeight < maxHeight)
                {
                    world.setBlockState(pos, randomAge(world, ConfigurableCane.CACTUS_TOP.getDefaultState(), maxAge), 2);
                }
            }
        }
        else
        {
            if (world.isAirBlock(pos.up()))
            {
                if (getStackHeight(world, pos) < maxHeight)
                {
                    if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(world, pos, state, true))
                    {
                        int age = state.get(AGE);
                        if (age >= maxAge)
                        {
                            world.setBlockState(pos.up(), this.getDefaultState());
                            world.setBlockState(pos, state.with(AGE, 0), 4);
                        }
                        else
                        {
                            world.setBlockState(pos, state.with(AGE, age + 1), 4);
                        }
                        net.minecraftforge.common.ForgeHooks.onCropsGrowPost(world, pos, state);
                    }
                }
            }
        }
    }

    private int getStackHeight(World world, BlockPos pos)
    {
        int stackHeight = 1;
        while (world.getBlockState(pos.down(stackHeight)).getBlock() == Blocks.CACTUS)
        {
            ++stackHeight;
        }
        return stackHeight;
    }
}
