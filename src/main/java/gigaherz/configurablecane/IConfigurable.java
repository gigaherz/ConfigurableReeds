package gigaherz.configurablecane;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.SugarCaneBlock;

import javax.annotation.Nullable;

public interface IConfigurable
{
    @Nullable
    ConfigurableThing getManager();

    void setManager(ConfigurableThing manager);

    static void initializeCactus(Block block, boolean isTop)
    {
        ((IConfigurable) block).setManager(new ConfigurableThing(
                Configurations.SERVER.cactus,
                CactusBlock.AGE,
                isTop,
                () -> block,
                () -> Blocks.CACTUS,
                ConfigurableCane.CACTUS_TOP,
                true));
    }

    static void initializeSugarcane(Block block, boolean isTop)
    {
        ((IConfigurable) block).setManager(new ConfigurableThing(
                Configurations.SERVER.sugarCane,
                SugarCaneBlock.AGE,
                isTop,
                () -> block,
                () -> Blocks.SUGAR_CANE,
                ConfigurableCane.SUGAR_CANE_TOP,
                false));
    }
}
