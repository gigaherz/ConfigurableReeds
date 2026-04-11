package gigaherz.configurablecane;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.SugarCaneBlock;

import javax.annotation.Nullable;

public interface IConfigurable
{
    @Nullable
    ConfigurableThing configurableCane$getManager();

    void configurableCane$setManager(ConfigurableThing manager);

    default boolean configurableCane$doSpecialGrowth(ServerLevel level, BlockPos pos, int age, int height, RandomSource rand)
    {
        return true;
    }

    static void initializeCactus()
    {
        var conf = ((IConfigurable) Blocks.CACTUS);
        conf.configurableCane$setManager(new ConfigurableThing(Configurations.SERVER.cactus, CactusBlock.AGE, Blocks.CACTUS, true, conf));
    }

    static void initializeSugarcane()
    {
        var conf = ((IConfigurable) Blocks.SUGAR_CANE);
        conf.configurableCane$setManager(new ConfigurableThing(Configurations.SERVER.sugarCane, SugarCaneBlock.AGE, Blocks.SUGAR_CANE, false, conf));
    }
}
