package gigaherz.configurablecane;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

//@Config(modid= ConfigurableCane.MODID)
class Configurations
{
    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static
    {
        final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    //@Config.RangeInt(min=0,max=15)
    public static int MaxHeight = 3;

    //@Config.RangeInt(min=0,max=15)
    public static int MaxAge = 15;

    public static boolean KelpLikeGrowth = false;

    //@Config.RangeDouble(min=0,max=1)
    public static double KelpLikeGrowthChance = 0.075;

    public static class ServerConfig
    {
        public final ForgeConfigSpec.IntValue maxHeight;
        public final ForgeConfigSpec.IntValue maxAge;
        public final ForgeConfigSpec.BooleanValue kelpLikeGrowth;
        public final ForgeConfigSpec.DoubleValue kelpLikeGrowthChance;

        ServerConfig(ForgeConfigSpec.Builder builder)
        {
            builder.push("general");
            maxHeight = builder
                    .comment("Sets the maximum height the sugarcane can grow to.")
                    .translation("text.configurablecane.config.max_height")
                    .defineInRange("MaxHeight", 3, 0, 15);
            maxAge = builder
                    .comment("Sets the maximum age value the sugarcane will grow to.")
                    .translation("text.configurablecane.config.max_age")
                    .defineInRange("MaxAge", 15, 0, 15);
            kelpLikeGrowth = builder
                    .comment("If set to TRUE, the growth system will behave similar to kelp, instead of behaving similar to vanilla sugarcane.")
                    .translation("text.configurablecane.config.kelp_like_growth")
                    .define("KelpLikeGrowth", false);
            kelpLikeGrowthChance = builder
                    .comment("If KelpLikeGrowth is TRUE, this value indicates the chance the sugarcane will grow.")
                    .translation("text.configurablecane.config.kelp_like_growth_chance")
                    .defineInRange("KelpLikeGrowthChance", 0.075, 0.0, 1.0);
            builder.pop();
        }
    }

    public static void refreshServer()
    {
        MaxHeight = SERVER.maxHeight.get();
        MaxAge = SERVER.maxAge.get();
        KelpLikeGrowth = SERVER.kelpLikeGrowth.get();
        KelpLikeGrowthChance = SERVER.kelpLikeGrowthChance.get();
    }
}
