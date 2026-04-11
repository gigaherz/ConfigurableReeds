package gigaherz.configurablecane;

import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

@Mod(ConfigurableCane.MODID)
public class ConfigurableCane
{
    public static final String MODID = "configurablecane";

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

    public static final DeferredBlock<SugarCaneBlock> SUGAR_CANE_TOP = BLOCKS.registerBlock("sugar_cane_top",
            props -> new SugarCaneBlock(props.overrideLootTable(Blocks.SUGAR_CANE.getLootTable()).overrideDescription("block.minecraft.sugar_cane")){
                @Deprecated
                @Override
                protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData)
                {
                    return Items.SUGAR_CANE.getDefaultInstance();
                }
            },
            () -> Block.Properties.ofFullCopy(Blocks.SUGAR_CANE)
    );

    public static final DeferredBlock<CactusBlock> CACTUS_TOP = BLOCKS.registerBlock("cactus_top",
            props -> new CactusBlock(props.overrideLootTable(Blocks.CACTUS.getLootTable()).overrideDescription("block.minecraft.cactus")){
                @Deprecated
                @Override
                protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData)
                {
                    return Items.CACTUS.getDefaultInstance();
                }
            },
            () -> Block.Properties.ofFullCopy(Blocks.CACTUS)
    );

    public ConfigurableCane(ModContainer container, IEventBus modEventBus)
    {
        BLOCKS.register(modEventBus);

        modEventBus.addListener(this::setup);

        container.registerConfig(ModConfig.Type.SERVER, Configurations.SERVER_SPEC);
    }

    public void setup(FMLCommonSetupEvent event)
    {
        IConfigurable.initializeCactus(Blocks.CACTUS, false);
        IConfigurable.initializeCactus(CACTUS_TOP.get(), true);
        IConfigurable.initializeSugarcane(Blocks.SUGAR_CANE, false);
        IConfigurable.initializeSugarcane(SUGAR_CANE_TOP.get(), true);
    }

    @EventBusSubscriber(value = Dist.CLIENT, modid = ConfigurableCane.MODID)
    public static class ClientEvents
    {
        @SubscribeEvent
        public static void blockColors(RegisterColorHandlersEvent.BlockTintSources event)
        {
            event.register(List.of(BlockTintSources.sugarCane()),ConfigurableCane.SUGAR_CANE_TOP.get());
        }
    }
}
