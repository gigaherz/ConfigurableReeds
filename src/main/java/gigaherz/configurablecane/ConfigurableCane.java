package gigaherz.configurablecane;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.references.BlockIds;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Objects;

@Mod(ConfigurableCane.MODID)
public class ConfigurableCane
{
    public static final String MODID = "configurablecane";

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<AdditionalBlockDataAttachment>>
        CHUNK_DATA = ATTACHMENTS.register("data", () -> AttachmentType.builder(AdditionalBlockDataAttachment::new).serialize(AdditionalBlockDataAttachment.CODEC).build());

    public ConfigurableCane(ModContainer container, IEventBus modEventBus)
    {
        ATTACHMENTS.register(modEventBus);

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::registerAliases);

        container.registerConfig(ModConfig.Type.SERVER, Configurations.SERVER_SPEC);
    }

    public void setup(FMLCommonSetupEvent event)
    {
        IConfigurable.initializeCactus();
        IConfigurable.initializeSugarcane();
    }

    public void registerAliases(RegisterEvent event)
    {
        event.register(Registries.BLOCK, helper -> {
            var blockRegistry = Objects.requireNonNull(event.getRegistry(Registries.BLOCK));
            blockRegistry.addAlias(
                    Identifier.fromNamespaceAndPath("configurablecane", "sugar_cane_top"),
                    Objects.requireNonNull(blockRegistry.getKey(Blocks.SUGAR_CANE))
            );
            blockRegistry.addAlias(
                    Identifier.fromNamespaceAndPath("configurablecane", "cactus_top"),
                    Objects.requireNonNull(blockRegistry.getKey(Blocks.CACTUS))
            );
        });
    }
}
