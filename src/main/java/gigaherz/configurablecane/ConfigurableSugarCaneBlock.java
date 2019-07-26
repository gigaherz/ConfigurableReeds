package gigaherz.configurablecane;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

class ConfigurableSugarCaneBlock extends SugarCaneBlock
{
    boolean isTop;

    public ConfigurableSugarCaneBlock(boolean isTop, Properties properties)
    {
        super(properties);
        this.isTop = isTop;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx)
    {
        if (Configurations.KelpLikeGrowth)
        {
            return randomAge(ctx.getWorld(), ConfigurableCane.CANE_TOP.getDefaultState());
        }
        else
        {
            return super.getStateForPlacement(ctx);
        }
    }

    private BlockState randomAge(World world, BlockState state)
    {
        return state.with(AGE, world.rand.nextInt(Configurations.MaxAge));
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos)
    {
        Block blockBelow = world.getBlockState(pos.down()).getBlock();
        if (blockBelow == Blocks.SUGAR_CANE || blockBelow == ConfigurableCane.CANE_TOP)
        {
            return true;
        }

        return super.isValidPosition(state, world, pos);
    }

    @Override
    public void tick(BlockState state, World world, BlockPos pos, Random rand)
    {
        if (!this.isValidPosition(state, world, pos))
        {
            world.destroyBlock(pos, true);
            return;
        }

        if (isTop)
        {
            if (Configurations.KelpLikeGrowth && world.isAirBlock(pos.up())
                    && getStackHeight(world, pos) < Configurations.MaxHeight)
            {
                int age = state.get(AGE);
                if (age < Configurations.MaxAge && rand.nextDouble() < Configurations.KelpLikeGrowthChance)
                {
                    world.setBlockState(pos.up(), state.with(AGE, age + 1));
                    world.setBlockState(pos, Blocks.SUGAR_CANE.getDefaultState().with(AGE, 15), 2);
                }
            }
            else
            {
                world.setBlockState(pos, Blocks.SUGAR_CANE.getDefaultState(), 2);
            }
            return;
        }

        if (Configurations.KelpLikeGrowth)
        {
            if (world.isAirBlock(pos.up()))
            {
                int stackHeight = getStackHeight(world, pos);
                if (stackHeight < Configurations.MaxHeight)
                {
                    world.setBlockState(pos, randomAge(world, ConfigurableCane.CANE_TOP.getDefaultState()), 2);
                }
            }
        }
        else
        {
            if (world.isAirBlock(pos.up()))
            {
                if (getStackHeight(world, pos) < Configurations.MaxHeight)
                {
                    if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(world, pos, state, true))
                    {
                        int age = state.get(AGE);
                        if (age >= Configurations.MaxAge)
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
        while (world.getBlockState(pos.down(stackHeight)).getBlock() == Blocks.SUGAR_CANE)
        {
            ++stackHeight;
        }
        return stackHeight;
    }
}
