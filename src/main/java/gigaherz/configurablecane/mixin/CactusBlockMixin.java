package gigaherz.configurablecane.mixin;

import gigaherz.configurablecane.ConfigurableThing;
import gigaherz.configurablecane.IConfigurable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Random;

@Mixin(CactusBlock.class)
public class CactusBlockMixin extends Block implements IConfigurable
{
    private ConfigurableThing manager;

    @Nullable
    @Override
    public final ConfigurableThing getManager()
    {
        return manager;
    }

    @Override
    public final void setManager(ConfigurableThing manager)
    {
        this.manager = manager;
    }

    public CactusBlockMixin(Properties properties)
    {
        super(properties);
    }

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

    //   public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {

    @Inject(method = "canSurvive(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z",
            at = @At("HEAD"), cancellable = true)
    public void canSurviveHook(BlockState state, LevelReader world, BlockPos pos, CallbackInfoReturnable<Boolean> ci)
    {
        if (manager != null && manager.isValidPosition(world, pos))
        {
            ci.setReturnValue(true);
        }
    }

    // public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random rand) {

    @Inject(method = "randomTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Ljava/util/Random;)V",
            at = @At("HEAD"), cancellable = true)
    public void randomTickHook(BlockState state, ServerLevel world, BlockPos pos, Random rand, CallbackInfo ci)
    {
        if (manager != null && manager.randomTick(state, world, pos, rand))
        {
            ci.cancel();
        }
    }
}
