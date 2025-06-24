package gigaherz.configurablecane.mixin;

import gigaherz.configurablecane.ConfigurableThing;
import gigaherz.configurablecane.Configurations;
import gigaherz.configurablecane.IConfigurable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(CactusBlock.class)
public abstract class CactusBlockMixin extends Block implements IConfigurable<CactusBlockMixin>
{
    private ConfigurableThing manager;

    @Nullable
    @Override
    public final ConfigurableThing configurableCane$getManager()
    {
        return manager;
    }

    @Override
    public final void configurableCane$setManager(ConfigurableThing manager)
    {
        this.manager = manager;
    }

    public CactusBlockMixin(Properties properties)
    {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        if (manager != null)
        {
            BlockState state = manager.getStateForPlacement(ctx);
            if (state != null)
            {
                return state;
            }
        }
        return super.getStateForPlacement(ctx);
    }

    @Invoker(value="canSurvive")
    protected abstract boolean configurableCane$canSurvive$accessor(BlockState state, LevelReader level, BlockPos pos);

    @Override
    public boolean configurableCane$doSpecialGrowth(ServerLevel level, BlockPos pos, int age, int height, RandomSource rand)
    {
        if (configurableCane$canSurvive$accessor(this.defaultBlockState(), level, pos.above()))
        {
            double chance = (height >= Configurations.SERVER.cactus.flowerChanceHeightValue
                            ? Configurations.SERVER.cactus.flowerChanceExtraValue
                            : Configurations.SERVER.cactus.flowerChanceBaseValue
                    ) + height * Configurations.SERVER.cactus.flowerChancePerLevelBonusValue;
            if (rand.nextDouble() <= chance)
            {
                level.setBlockAndUpdate(pos.above(), Blocks.CACTUS_FLOWER.defaultBlockState());
                return false;
            }
        }
        return true;
    }


    @Inject(method = "canSurvive(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z",
            at = @At(value="INVOKE", ordinal = 1, target="Lnet/minecraft/world/level/LevelReader;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"),
            cancellable = true)
    private void configurableCane$allowSurviveOnTop(BlockState state, LevelReader world, BlockPos pos, CallbackInfoReturnable<Boolean> ci)
    {
        if (manager != null && manager.isValidPosition(world, pos))
        {
            ci.setReturnValue(true);
        }
    }

    @Inject(method = "randomTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V",
            at = @At("HEAD"), cancellable = true)
    public void configurableCane$customGrowthLogic(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand, CallbackInfo ci)
    {
        if (manager != null && manager.randomTick(state, world, pos, rand))
        {
            ci.cancel();
        }
    }
}
