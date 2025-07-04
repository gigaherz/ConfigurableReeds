package gigaherz.configurablecane.mixin;

import gigaherz.configurablecane.ConfigurableThing;
import gigaherz.configurablecane.IConfigurable;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(SugarCaneBlock.class)
public class SugarCaneBlockMixin extends Block implements IConfigurable<SugarCaneBlock>
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

    //   public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {

    @Inject(method = "canSurvive(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z",
            at = @At("HEAD"), cancellable = true)
    public void configurableCane$allowSurviveOnTop(BlockState state, LevelReader world, BlockPos pos, CallbackInfoReturnable<Boolean> ci)
    {
        if (configurableCane$getManager() != null && configurableCane$getManager().isValidPosition(world, pos))
        {
            ci.setReturnValue(true);
        }
    }

    // public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random rand) {

    @Inject(method = "randomTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V",
            at = @At("HEAD"), cancellable = true)
    public void configurableCane$customGrowthLogic(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand, CallbackInfo ci)
    {
        if (configurableCane$getManager() != null && configurableCane$getManager().randomTick(state, world, pos, rand))
        {
            ci.cancel();
        }
    }
}
