package gigaherz.configurablecane;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

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

        container.registerConfig(ModConfig.Type.SERVER, Configurations.SERVER_SPEC);
    }

    public void setup(FMLCommonSetupEvent event)
    {
        IConfigurable.initializeCactus();
        IConfigurable.initializeSugarcane();
    }
}
