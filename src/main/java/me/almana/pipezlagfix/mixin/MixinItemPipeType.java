package me.almana.pipezlagfix.mixin;

import de.maxhenkel.pipez.blocks.tileentity.PipeLogicTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.types.ItemPipeType;
import me.almana.pipezlagfix.Config;
import me.almana.pipezlagfix.IItemPipeBackoff;
import net.minecraft.core.Direction;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.almana.pipezlagfix.TrackingItemHandler;

import java.util.List;

@Mixin(value = ItemPipeType.class, remap = false)
public class MixinItemPipeType {

    @Unique
    private final ThreadLocal<Boolean> pipezlagfix$success = ThreadLocal.withInitial(() -> false);

    @Inject(method = "insertEqually", at = @At("HEAD"), cancellable = true, remap = false)
    public void startEqually(PipeLogicTileEntity tileEntity, Direction side,
            List<PipeTileEntity.Connection> connections, IItemHandler itemHandler, CallbackInfo ci) {
        pipezlagfix$success.set(false);
        if (pipezlagfix$shouldSuppress(tileEntity, side)) {
            ci.cancel();
        }
    }

    @Inject(method = "insertOrdered", at = @At("HEAD"), cancellable = true, remap = false)
    public void startOrdered(PipeLogicTileEntity tileEntity, Direction side,
            List<PipeTileEntity.Connection> connections, IItemHandler itemHandler, CallbackInfo ci) {
        pipezlagfix$success.set(false);
        if (pipezlagfix$shouldSuppress(tileEntity, side)) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "insertEqually", at = @At("HEAD"), argsOnly = true, remap = false)
    public IItemHandler wrapHandlerEqually(IItemHandler handler) {
        return new TrackingItemHandler(handler, () -> pipezlagfix$success.set(true));
    }

    @ModifyVariable(method = "insertOrdered", at = @At("HEAD"), argsOnly = true, remap = false)
    public IItemHandler wrapHandlerOrdered(IItemHandler handler) {
        return new TrackingItemHandler(handler, () -> pipezlagfix$success.set(true));
    }

    @Inject(method = "insertEqually", at = @At("RETURN"), remap = false)
    public void endEqually(PipeLogicTileEntity tileEntity, Direction side, List<PipeTileEntity.Connection> connections,
            IItemHandler itemHandler, CallbackInfo ci) {
        pipezlagfix$applyBackoff(tileEntity, side);
    }

    @Inject(method = "insertOrdered", at = @At("RETURN"), remap = false)
    public void endOrdered(PipeLogicTileEntity tileEntity, Direction side, List<PipeTileEntity.Connection> connections,
            IItemHandler itemHandler, CallbackInfo ci) {
        pipezlagfix$applyBackoff(tileEntity, side);
    }

    @Unique
    private boolean pipezlagfix$shouldSuppress(PipeLogicTileEntity tileEntity, Direction side) {
        if (tileEntity instanceof IItemPipeBackoff backoff) {
            long nextActive = backoff.pipezlagfix$getNextActiveTick(side);
            return tileEntity.getLevel().getGameTime() < nextActive;
        }
        return false;
    }

    @Unique
    private void pipezlagfix$applyBackoff(PipeLogicTileEntity tileEntity, Direction side) {
        if (!(tileEntity instanceof IItemPipeBackoff backoff)) {
            return;
        }

        if (pipezlagfix$success.get()) {
            // Success: Reset backoff
            backoff.pipezlagfix$setBackoffDelay(side, 0);
            backoff.pipezlagfix$setNextActiveTick(side, 0);
        } else {
            // Failure: Exponential Backoff
            int baseDelay = Config.baseBackoffTicks;
            int maxDelay = Config.maxBackoffTicks;
            int currentDelay = backoff.pipezlagfix$getBackoffDelay(side);

            // Correct initialization: if current is 0, start at base. Else double.
            int newDelay = (currentDelay == 0) ? baseDelay : currentDelay * 2;

            // Apply cap
            newDelay = Math.min(newDelay, maxDelay);

            backoff.pipezlagfix$setBackoffDelay(side, newDelay);
            backoff.pipezlagfix$setNextActiveTick(side, tileEntity.getLevel().getGameTime() + newDelay);
        }
    }
}
