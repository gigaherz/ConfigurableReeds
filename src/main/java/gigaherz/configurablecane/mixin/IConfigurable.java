package gigaherz.configurablecane.mixin;

import gigaherz.configurablecane.ConfigurableCane;
import gigaherz.configurablecane.ConfigurableThing;
import gigaherz.configurablecane.Configurations;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.SugarCaneBlock;

public interface IConfigurable
{
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
                ConfigurableCane.CACTUS_TOP
        ));
    }

    static void initializeSugarcane(Block block, boolean isTop)
    {
        ((IConfigurable) block).setManager(new ConfigurableThing(
                Configurations.SERVER.sugarCane,
                SugarCaneBlock.AGE,
                isTop,
                () -> block,
                () -> Blocks.SUGAR_CANE,
                ConfigurableCane.SUGAR_CANE_TOP
        ));
    }
}