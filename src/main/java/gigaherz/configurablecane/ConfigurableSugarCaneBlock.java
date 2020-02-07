package gigaherz.configurablecane;

import net.minecraft.block.BlockState;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

class ConfigurableSugarCaneBlock extends SugarCaneBlock
{
    private final ConfigurableThing manager;

    public ConfigurableSugarCaneBlock(boolean isTop, Properties properties)
    {
        super(properties);
        manager = new ConfigurableThing(
                Configurations.SERVER.sugarCane,
                isTop,
                () -> this,
                ConfigurableCane.SUGAR_CANE_REPLACEMENT,
                ConfigurableCane.SUGAR_CANE_TOP
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
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand)
    {
        if (!manager.tick(state, world, pos, rand))
            super.tick(state, world, pos, rand);
    }
}
