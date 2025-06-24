package gigaherz.configurablecane;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Configurations
{
    public static final ServerConfig SERVER;
    public static final ModConfigSpec SERVER_SPEC;

    static
    {
        final Pair<ServerConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    @EventBusSubscriber(modid=ConfigurableCane.MODID)
    public static class ConfigEvents
    {
        @SubscribeEvent
        public static void modConfigLoad(ModConfigEvent.Loading event)
        {
            reloadConfig(event);
        }

        @SubscribeEvent
        public static void modConfigReload(ModConfigEvent.Reloading event)
        {
            reloadConfig(event);
        }

        private static void reloadConfig(ModConfigEvent event)
        {
            if(event.getConfig().getSpec() == SERVER_SPEC)
            {
                SERVER.cactus.updateCachedValues();
                SERVER.sugarCane.updateCachedValues();
            }
        }
    }

    public static class ServerConfig
    {
        public static class ThingConfig
        {
            private final ModConfigSpec.BooleanValue enabled;
            private final ModConfigSpec.IntValue maxHeight;
            private final ModConfigSpec.IntValue maxAge;
            private final ModConfigSpec.BooleanValue kelpLikeGrowth;
            private final ModConfigSpec.DoubleValue kelpLikeGrowthChance;
            private final ModConfigSpec.DoubleValue kelpLikeAgeChance;

            public boolean enabledValue;
            public int maxHeightValue;
            public int maxAgeValue;
            public boolean kelpLikeGrowthValue;
            public double kelpLikeGrowthChanceValue;
            public double kelpLikeAgeChanceValue;

            public void updateCachedValues()
            {
                enabledValue = enabled.get();
                maxHeightValue = maxHeight.get();
                maxAgeValue = maxAge.get();
                kelpLikeGrowthValue = kelpLikeGrowth.get();
                kelpLikeGrowthChanceValue = kelpLikeGrowthChance.get();
                kelpLikeAgeChanceValue = kelpLikeAgeChance.get();
            }

            ThingConfig(ModConfigSpec.Builder builder, String category, String display)
            {

                builder.push(category);
                enabled = builder
                        .comment("If set to FALSE, the " + display + " will use the vanilla behaviour.")
                        .translation("text.configurablecane.config." + category + ".enable")
                        .define("Enable", true);
                maxHeight = builder
                        .comment("Sets the maximum height the " + display + " can grow to.")
                        .translation("text.configurablecane.config." + category + ".max_height")
                        .defineInRange("MaxHeight", 3, 0, 255);
                maxAge = builder
                        .comment("Sets the maximum age value the " + display + " will grow to.")
                        .translation("text.configurablecane.config." + category + ".max_age")
                        .defineInRange("MaxAge", 15, 0, 15);
                kelpLikeGrowth = builder
                        .comment("If set to TRUE, the growth system will behave similar to kelp, instead of behaving similar to vanilla " + display + ".")
                        .translation("text.configurablecane.config." + category + ".kelp_like_growth")
                        .define("KelpLikeGrowth", false);
                kelpLikeGrowthChance = builder
                        .comment("If KelpLikeGrowth is TRUE, this value indicates the chance the " + display + " will grow.")
                        .translation("text.configurablecane.config." + category + ".kelp_like_growth_chance")
                        .defineInRange("KelpLikeGrowthChance", 0.075, 0.0, 1.0);
                kelpLikeAgeChance = builder
                        .comment("If KelpLikeGrowth is TRUE, this value indicates the chance the top " + display + " age will increase when growing.")
                        .translation("text.configurablecane.config." + category + ".kelp_like_growth_chance")
                        .defineInRange("KelpLikeAgeChance", 0.75, 0.0, Double.MAX_VALUE);
                additional(builder);
                builder.pop();
            }

            protected void additional(ModConfigSpec.Builder builder)
            {

            }
        }

        public static class CactusThingConfig extends ThingConfig
        {
            private ModConfigSpec.DoubleValue flowerChancePerLevelBonus;
            private ModConfigSpec.IntValue flowerChanceHeight;
            private ModConfigSpec.DoubleValue flowerChanceExtra;
            private ModConfigSpec.DoubleValue flowerChanceBase;

            public double flowerChancePerLevelBonusValue;
            public int flowerChanceHeightValue;
            public double flowerChanceExtraValue;
            public double flowerChanceBaseValue;

            CactusThingConfig(ModConfigSpec.Builder builder, String category, String display)
            {
                super(builder, category, display);
            }

            @Override
            protected void additional(ModConfigSpec.Builder builder)
            {
                flowerChancePerLevelBonus = builder
                        .comment("When non-zero, the specified value is added to the chance of flowers to grow on the cactus.")
                        .translation("text.configurablecane.config.cactus.flower_chance_per_level_bonus")
                        .defineInRange("flowerChancePerLevelBonus", 0, 0, Double.MAX_VALUE);
                flowerChanceHeight = builder
                        .comment("The height at which cactus is more likely to generate flowers. The vanilla default is 3.")
                        .translation("text.configurablecane.config.cactus.flower_chance_per_level_bonus")
                        .defineInRange("flowerChanceHeightValue", 3, 0, 255);
                flowerChanceExtra = builder
                        .comment("The chance for a flower to grow in blocks at or above the height. The vanilla default is 0.25.")
                        .translation("text.configurablecane.config.cactus.flower_chance_per_level_bonus")
                        .defineInRange("flowerChanceExtraValue", 0.25, 0, Double.MAX_VALUE);
                flowerChanceBase = builder
                        .comment("The chance for a flower to grow in blocks below the height. The vanilla default is 0.1.")
                        .translation("text.configurablecane.config.cactus.flower_chance_per_level_bonus")
                        .defineInRange("flowerChanceBaseValue", 0.1, 0, Double.MAX_VALUE);
            }

            @Override
            public void updateCachedValues()
            {
                super.updateCachedValues();
                flowerChancePerLevelBonusValue = flowerChancePerLevelBonus.getAsDouble();
                flowerChanceHeightValue = flowerChanceHeight.getAsInt();
                flowerChanceExtraValue = flowerChanceExtra.getAsDouble();
                flowerChanceBaseValue = flowerChanceBase.getAsDouble();
            }
        }

        public final ThingConfig sugarCane;
        public final CactusThingConfig cactus;

        ServerConfig(ModConfigSpec.Builder builder)
        {
            sugarCane = new ThingConfig(builder, "sugar_cane", "sugar cane");
            cactus = new CactusThingConfig(builder, "cactus", "cactus");
        }
    }

}
