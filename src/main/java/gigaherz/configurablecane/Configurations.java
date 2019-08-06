package gigaherz.configurablecane;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

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

    public static class ServerConfig
    {
        public static class ThingConfig
        {
            public final ForgeConfigSpec.BooleanValue enabled;
            public final ForgeConfigSpec.IntValue maxHeight;
            public final ForgeConfigSpec.IntValue maxAge;
            public final ForgeConfigSpec.BooleanValue kelpLikeGrowth;
            public final ForgeConfigSpec.DoubleValue kelpLikeGrowthChance;

            ThingConfig(ForgeConfigSpec.Builder builder, String category, String display) {

                builder.push(category);
                enabled = builder
                        .comment("If set to FALSE, the " + display + " will use the vanilla behaviour.")
                        .translation("text.configurablecane.config."+category+".enable")
                        .define("Enable", true);
                maxHeight = builder
                        .comment("Sets the maximum height the " + display + " can grow to.")
                        .translation("text.configurablecane.config."+category+".max_height")
                        .defineInRange("MaxHeight", 3, 0, 15);
                maxAge = builder
                        .comment("Sets the maximum age value the " + display + " will grow to.")
                        .translation("text.configurablecane.config."+category+".max_age")
                        .defineInRange("MaxAge", 15, 0, 15);
                kelpLikeGrowth = builder
                        .comment("If set to TRUE, the growth system will behave similar to kelp, instead of behaving similar to vanilla " + display + ".")
                        .translation("text.configurablecane.config."+category+".kelp_like_growth")
                        .define("KelpLikeGrowth", false);
                kelpLikeGrowthChance = builder
                        .comment("If KelpLikeGrowth is TRUE, this value indicates the chance the " + display + " will grow.")
                        .translation("text.configurablecane.config."+category+".kelp_like_growth_chance")
                        .defineInRange("KelpLikeGrowthChance", 0.075, 0.0, 1.0);
                builder.pop();
            }
        }

        public final ThingConfig sugarCane;
        public final ThingConfig cactus;

        ServerConfig(ForgeConfigSpec.Builder builder)
        {
            sugarCane = new ThingConfig(builder, "sugar_cane", "sugar cane");
            cactus = new ThingConfig(builder, "cactus", "cactus");
        }
    }
}
