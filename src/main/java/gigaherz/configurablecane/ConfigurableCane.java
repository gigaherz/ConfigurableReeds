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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(ConfigurableCane.MODID)
public class ConfigurableCane
{
    public static final String MODID = "configurablecane";

    public static final DeferredRegister<Block> VANILLA_BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, "minecraft");
    public static final DeferredRegister<Item> VANILLA_ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, "minecraft");
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Block> SUGAR_CANE_REPLACEMENT = VANILLA_BLOCKS.register("sugar_cane",
            () -> new ConfigurableSugarCaneBlock(false,
                    Block.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0).sound(SoundType.PLANT)
            )
    );

    public static final RegistryObject<Block> SUGAR_CANE_TOP = BLOCKS.register("sugar_cane_top",
            () -> new ConfigurableSugarCaneBlock(true,
                    Block.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0).sound(SoundType.PLANT).lootFrom(Blocks.SUGAR_CANE)
            )
    );

    @SuppressWarnings("unused")
    public static final RegistryObject<Item> SUGAR_CANE_ITEM_REPLACEMENT = VANILLA_ITEMS.register("sugar_cane",
            () -> new BlockItem(Blocks.SUGAR_CANE, new Item.Properties().group(Items.SUGAR_CANE.getGroup()))
    );

    public static RegistryObject<Block> CACTUS_REPLACEMENT = VANILLA_BLOCKS.register("cactus",
            () -> new ConfigurableCactusBlock(false,
                    Block.Properties.create(Material.CACTUS).tickRandomly().hardnessAndResistance(0.4F).sound(SoundType.CLOTH)
            )
    );

    public static final RegistryObject<Block> CACTUS_TOP = BLOCKS.register("cactus_top",
            () -> new ConfigurableCactusBlock(true,
                    Block.Properties.create(Material.CACTUS).tickRandomly().hardnessAndResistance(0.4F).sound(SoundType.CLOTH).lootFrom(Blocks.CACTUS)
            )
    );

    @SuppressWarnings("unused")
    public static final RegistryObject<Item> CACTUS_ITEM_REPLACEMENT = VANILLA_ITEMS.register("cactus",
            () -> new BlockItem(Blocks.CACTUS, new Item.Properties().group(Items.CACTUS.getGroup()))
    );

    public ConfigurableCane()
    {

        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        VANILLA_BLOCKS.register(modEventBus);
        VANILLA_ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);

        modLoadingContext.registerConfig(ModConfig.Type.SERVER, Configurations.SERVER_SPEC);
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ConfigurableCane.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    static class ClientEvents
    {
        @SubscribeEvent
        public static void blockColors(ColorHandlerEvent.Block event)
        {
            event.getBlockColors().register((state, world, pos, tintIndex) ->
                            world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : -1,
                    ConfigurableCane.SUGAR_CANE_TOP.get()
            );
        }
    }
}
