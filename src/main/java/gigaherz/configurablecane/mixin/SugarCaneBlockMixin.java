package gigaherz.configurablecane.mixin;

import gigaherz.configurablecane.AdditionalBlockDataAttachment;
import gigaherz.configurablecane.ConfigurableThing;
import gigaherz.configurablecane.IConfigurable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(SugarCaneBlock.class)
public class SugarCaneBlockMixin extends Block implements IConfigurable
{
    @Unique
    private ConfigurableThing configurablecane$manager;

    @Nullable
    @Override
    public final ConfigurableThing configurableCane$getManager()
    {
        return configurablecane$manager;
    }

    @Override
    public final void configurableCane$setManager(ConfigurableThing manager)
    {
        this.configurablecane$manager = manager;
    }

    public SugarCaneBlockMixin(Properties properties)
    {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        if (configurableCane$getManager() != null)
        {
            BlockState state = configurableCane$getManager().getStateForPlacement(ctx);
            if (state != null)
            {
                return state;
            }
        }
        return super.getStateForPlacement(ctx);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston)
    {
        AdditionalBlockDataAttachment.cleanup(level, pos);
    }

    @Inject(method = "canSurvive(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z",
            at = @At("HEAD"), cancellable = true)
    public void configurableCane$allowSurviveOnTop(BlockState state, LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> ci)
    {
        if (configurableCane$getManager() != null && configurableCane$getManager().isValidPosition(level, pos))
        {
            ci.setReturnValue(true);
        }
    }

    @Inject(method = "randomTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V",
            at = @At("HEAD"), cancellable = true)
    public void configurableCane$customGrowthLogic(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci)
    {
        if (configurableCane$getManager() != null && configurableCane$getManager().randomTick(state, level, pos, random))
        {
            ci.cancel();
        }
    }
}
