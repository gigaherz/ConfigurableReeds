package gigaherz.configurablecane;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;

@Mod(ConfigurableCane.MODID)
public class ConfigurableCane
{
    public static final String MODID = "configurablecane";

    @ObjectHolder("configurablecane:sugar_cane_top")
    public static Block CANE_TOP;

    public ConfigurableCane()
    {

        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addGenericListener(Block.class, this::registerBlocks);
        modEventBus.addGenericListener(Item.class, this::registerItems);
        modEventBus.addListener(this::modConfig);

        modLoadingContext.registerConfig(ModConfig.Type.SERVER, Configurations.SERVER_SPEC);
    }


    public void modConfig(ModConfig.ModConfigEvent event)
    {
        ModConfig config = event.getConfig();
        if (config.getSpec() == Configurations.SERVER_SPEC)
            Configurations.refreshServer();
    }

    public void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(
                new ConfigurableSugarCaneBlock(false, Block.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0).sound(SoundType.PLANT)).setRegistryName(Blocks.SUGAR_CANE.getRegistryName()),
                new ConfigurableSugarCaneBlock(true, Block.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0).sound(SoundType.PLANT)).setRegistryName("sugar_cane_top")
        );
    }

    public void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
                new BlockItem(Blocks.SUGAR_CANE, new Item.Properties().group(Items.SUGAR_CANE.getGroup())).setRegistryName(Items.SUGAR_CANE.getRegistryName())
        );
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ConfigurableCane.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    static class ClientEvents
    {
        @SubscribeEvent
        public static void blockColors(ColorHandlerEvent.Block event)
        {
            event.getBlockColors().register((state, world, pos, tintIndex) ->
                            world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : -1,
                    ConfigurableCane.CANE_TOP
            );
        }
    }
}
