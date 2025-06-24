package gigaherz.configurablecane;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.SugarCaneBlock;

import javax.annotation.Nullable;

public interface IConfigurable<T extends Block>
{
    @Nullable
    ConfigurableThing configurableCane$getManager();

    void configurableCane$setManager(ConfigurableThing manager);

    default boolean configurableCane$doSpecialGrowth(ServerLevel level, BlockPos pos, int age, int height, RandomSource rand)
    {
        return true;
    }

    @SuppressWarnings("unchecked")
    static <B extends Block> void initializeCactus(Block block, boolean isTop)
    {
        ((IConfigurable<B>) block).configurableCane$setManager(new ConfigurableThing(
                Configurations.SERVER.cactus,
                CactusBlock.AGE,
                isTop,
                block,
                Blocks.CACTUS,
                ConfigurableCane.CACTUS_TOP.get(),
                true, ((IConfigurable<B>) block)));
    }

    @SuppressWarnings("unchecked")
    static <B extends Block> void initializeSugarcane(Block block, boolean isTop)
    {
        ((IConfigurable<B>) block).configurableCane$setManager(new ConfigurableThing(
                Configurations.SERVER.sugarCane,
                SugarCaneBlock.AGE,
                isTop,
                block,
                Blocks.SUGAR_CANE,
                ConfigurableCane.SUGAR_CANE_TOP.get(),
                false, ((IConfigurable<B>) block)));
    }
}
