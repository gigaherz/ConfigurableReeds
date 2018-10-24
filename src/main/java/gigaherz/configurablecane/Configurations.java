package gigaherz.configurablecane;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid= ConfigurableCane.MODID)
class Configurations
{
    @Config.RangeInt(min=0,max=15)
    public static int MaxHeight = 3;

    @Config.RangeInt(min=0,max=15)
    public static int MaxAge = 15;

    public static boolean KelpLikeGrowth = false;

    @Config.RangeDouble(min=0,max=1)
    public static double KelpLikeGrowthChance = 0.075;

    @Mod.EventBusSubscriber(modid = ConfigurableCane.MODID)
    private static class EventHandler
    {
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if (event.getModID().equals(ConfigurableCane.MODID))
            {
                ConfigManager.sync(ConfigurableCane.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
