package gigaherz.configurablecane;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.world.item.Item;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(ConfigurableCane.MODID)
public class ConfigurableCane
{
    public static final String MODID = "configurablecane";

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Block> SUGAR_CANE_TOP = BLOCKS.register("sugar_cane_top",
            () -> new SugarCaneBlock(Block.Properties.copy(Blocks.SUGAR_CANE))
    );

    public static final RegistryObject<Block> CACTUS_TOP = BLOCKS.register("cactus_top",
            () -> new CactusBlock(Block.Properties.copy(Blocks.CACTUS))
    );

    public ConfigurableCane()
    {

        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);

        modLoadingContext.registerConfig(ModConfig.Type.SERVER, Configurations.SERVER_SPEC);
    }

    public void setup(FMLCommonSetupEvent event)
    {
        IConfigurable.initializeCactus(Blocks.CACTUS, false);
        IConfigurable.initializeCactus(CACTUS_TOP.get(), true);
        IConfigurable.initializeSugarcane(Blocks.SUGAR_CANE, false);
        IConfigurable.initializeSugarcane(SUGAR_CANE_TOP.get(), true);
    }

    public void clientSetup(FMLClientSetupEvent event)
    {
        ItemBlockRenderTypes.setRenderLayer(CACTUS_TOP.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(SUGAR_CANE_TOP.get(), RenderType.cutout());
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ConfigurableCane.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    static class ClientEvents
    {
        @SubscribeEvent
        public static void blockColors(ColorHandlerEvent.Block event)
        {
            event.getBlockColors().register((state, world, pos, tintIndex) ->
                            world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : -1,
                    ConfigurableCane.SUGAR_CANE_TOP.get()
            );
        }
    }
}
