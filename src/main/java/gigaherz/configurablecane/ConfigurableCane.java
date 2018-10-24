package gigaherz.configurablecane;

import net.minecraft.block.Block;
import net.minecraft.block.BlockReed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;

@Mod(modid = ConfigurableCane.MODID)
@Mod.EventBusSubscriber
public class ConfigurableCane
{
    public static final String MODID = "configurablecane";

    @GameRegistry.ObjectHolder(MODID + ":reeds_top")
    public static BlockReed top;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(
                new BlockReedConfigurable(false).setRegistryName(Blocks.REEDS.getRegistryName()),
                new BlockReedConfigurable(true).setRegistryName("reeds_top")
        );
    }

    @Mod.EventBusSubscriber(value= Side.CLIENT, modid=ConfigurableCane.MODID)
    static class ClientEvents
    {
        @SubscribeEvent
        public static void modelsEvent(ModelRegistryEvent event)
        {
            ModelLoader.setCustomStateMapper(ConfigurableCane.top, (new StateMap.Builder()).ignore(BlockReed.AGE).build());
        }

        @SubscribeEvent
        public static void blockColors(ColorHandlerEvent.Block event)
        {
            event.getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) ->
                    worldIn != null && pos != null ? BiomeColorHelper.getGrassColorAtPos(worldIn, pos) : -1, ConfigurableCane.top
            );
        }
    }
}
