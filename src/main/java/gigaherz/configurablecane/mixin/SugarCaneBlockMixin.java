package gigaherz.configurablecane.mixin;

import gigaherz.configurablecane.ConfigurableThing;
import gigaherz.configurablecane.IConfigurable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

@Mixin(SugarCaneBlock.class)
public class SugarCaneBlockMixin extends Block implements IConfigurable
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

    public SugarCaneBlockMixin(Properties properties)
    {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx)
    {
        if (getManager() != null)
        {
            BlockState state = getManager().getStateForPlacement(ctx);
            if (state != null)
            {
                return state;
            }
        }
        return super.getStateForPlacement(ctx);
    }

    @Inject(method = "isValidPosition", at = @At("HEAD"), cancellable = true)
    public void isValidPosition(BlockState state, IWorldReader world, BlockPos pos, CallbackInfoReturnable<Boolean> ci)
    {
        if (getManager() != null && getManager().isValidPosition(world, pos))
        {
            ci.setReturnValue(true);
        }
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand, CallbackInfo ci)
    {
        if (getManager() != null && getManager().randomTick(state, world, pos, rand))
        {
            ci.cancel();
        }
    }
}
