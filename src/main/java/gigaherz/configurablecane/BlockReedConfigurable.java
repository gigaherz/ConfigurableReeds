package gigaherz.configurablecane;

import net.minecraft.block.Block;
import net.minecraft.block.BlockReed;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

class BlockReedConfigurable extends BlockReed
{
    boolean isTop;

    public BlockReedConfigurable(boolean isTop)
    {
        setHardness(0.0F);
        setSoundType(SoundType.PLANT);
        setTranslationKey("reeds");
        disableStats();
        this.isTop = isTop;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        if (Configurations.KelpLikeGrowth)
        {
            return randomAge(world, ConfigurableCane.top.getDefaultState());
        }
        else
        {
            return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);

        }
    }

    private IBlockState randomAge(World world, IBlockState state)
    {
        return state.withProperty(AGE, world.rand.nextInt(Configurations.MaxAge));
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        IBlockState state = worldIn.getBlockState(pos.down());
        Block block = state.getBlock();
        if (block.canSustainPlant(state, worldIn, pos.down(), EnumFacing.UP, this)) return true;

        if (block == Blocks.REEDS || block == ConfigurableCane.top)
        {
            return true;
        }

        return super.canPlaceBlockAt(worldIn, pos);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if(!this.checkForDrop(worldIn, pos, state))
            return;

        if (isTop)
        {
            if (Configurations.KelpLikeGrowth && worldIn.isAirBlock(pos.up())
                    && getStackHeight(worldIn, pos) < Configurations.MaxHeight)
            {
                int age = state.getValue(AGE);
                if(age < Configurations.MaxAge && rand.nextDouble() < Configurations.KelpLikeGrowthChance)
                {
                    worldIn.setBlockState(pos.up(), state.withProperty(AGE, age + 1));
                    worldIn.setBlockState(pos, Blocks.REEDS.getDefaultState().withProperty(AGE, 15), 2);
                }
            }
            else
            {
                worldIn.setBlockState(pos, Blocks.REEDS.getDefaultState(), 2);
            }
            return;
        }

        if(Configurations.KelpLikeGrowth)
        {
            if (worldIn.isAirBlock(pos.up()))
            {
                int stackHeight = getStackHeight(worldIn, pos);
                if (stackHeight < Configurations.MaxHeight)
                {
                    worldIn.setBlockState(pos, randomAge(worldIn, ConfigurableCane.top.getDefaultState()), 2);
                }
            }
        }
        else
        {
            if (worldIn.isAirBlock(pos.up()))
            {
                if (getStackHeight(worldIn, pos) < Configurations.MaxHeight)
                {
                    if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, true))
                    {
                        int age = state.getValue(AGE);
                        if (age >= Configurations.MaxAge)
                        {
                            worldIn.setBlockState(pos.up(), this.getDefaultState());
                            worldIn.setBlockState(pos, state.withProperty(AGE, 0), 4);
                        }
                        else
                        {
                            worldIn.setBlockState(pos, state.withProperty(AGE, age + 1), 4);
                        }
                        net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
                    }
                }
            }
        }
    }

    private int getStackHeight(World worldIn, BlockPos pos)
    {
        int stackHeight;
        for (stackHeight = 1; worldIn.getBlockState(pos.down(stackHeight)).getBlock() == Blocks.REEDS; ++stackHeight)
        {
            ;
        }
        return stackHeight;
    }
}
