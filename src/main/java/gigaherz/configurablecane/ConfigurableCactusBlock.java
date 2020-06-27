package gigaherz.configurablecane;

import net.minecraft.block.BlockState;
import net.minecraft.block.CactusBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

class ConfigurableCactusBlock extends CactusBlock
{
    private final ConfigurableThing manager;

    public ConfigurableCactusBlock(boolean isTop, Properties properties)
    {
        super(properties);
        manager = new ConfigurableThing(
                Configurations.SERVER.cactus,
                CactusBlock.AGE,
                isTop,
                () -> this,
                ConfigurableCane.CACTUS_REPLACEMENT,
                ConfigurableCane.CACTUS_TOP
        );
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx)
    {
        return manager.getStateForPlacement(ctx).orElseGet(() -> super.getStateForPlacement(ctx));
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos)
    {
        return manager.isValidPosition(world, pos)
                || super.isValidPosition(state, world, pos);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand)
    {
        if (!manager.randomTick(state, world, pos, rand))
            super.randomTick(state, world, pos, rand);
    }
}
