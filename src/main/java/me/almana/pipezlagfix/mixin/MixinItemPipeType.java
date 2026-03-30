package me.almana.pipezlagfix.mixin;

import de.maxhenkel.pipez.blocks.tileentity.PipeLogicTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.types.ItemPipeType;
import me.almana.pipezlagfix.Config;
import me.almana.pipezlagfix.IItemPipeBackoff;
import me.almana.pipezlagfix.TrackingItemHandler;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemPipeType.class)
public class MixinItemPipeType {

    @Unique
    private final ThreadLocal<Boolean> pipezlagfix$success = ThreadLocal.withInitial(() -> false);

    @Inject(method = "insertEqually", at = @At("HEAD"), cancellable = true)
    public void startEqually(PipeLogicTileEntity tileEntity, Direction side,
            List<PipeTileEntity.Connection> connections, ResourceHandler<ItemResource> itemHandler, CallbackInfo ci) {
        pipezlagfix$success.set(false);
        if (pipezlagfix$shouldSuppress(tileEntity, side)) {
            ci.cancel();
        }
    }

    @Inject(method = "insertOrdered", at = @At("HEAD"), cancellable = true)
    public void startOrdered(PipeLogicTileEntity tileEntity, Direction side,
            List<PipeTileEntity.Connection> connections, ResourceHandler<ItemResource> itemHandler, CallbackInfo ci) {
        pipezlagfix$success.set(false);
        if (pipezlagfix$shouldSuppress(tileEntity, side)) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "insertEqually", at = @At("HEAD"), argsOnly = true)
    public ResourceHandler<ItemResource> wrapHandlerEqually(ResourceHandler<ItemResource> handler) {
        return new TrackingItemHandler(handler, () -> pipezlagfix$success.set(true));
    }

    @ModifyVariable(method = "insertOrdered", at = @At("HEAD"), argsOnly = true)
    public ResourceHandler<ItemResource> wrapHandlerOrdered(ResourceHandler<ItemResource> handler) {
        return new TrackingItemHandler(handler, () -> pipezlagfix$success.set(true));
    }

    @Inject(method = "insertEqually", at = @At("RETURN"))
    public void endEqually(PipeLogicTileEntity tileEntity, Direction side, List<PipeTileEntity.Connection> connections,
            ResourceHandler<ItemResource> itemHandler, CallbackInfo ci) {
        pipezlagfix$applyBackoff(tileEntity, side);
    }

    @Inject(method = "insertOrdered", at = @At("RETURN"))
    public void endOrdered(PipeLogicTileEntity tileEntity, Direction side, List<PipeTileEntity.Connection> connections,
            ResourceHandler<ItemResource> itemHandler, CallbackInfo ci) {
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
            backoff.pipezlagfix$setBackoffDelay(side, 0);
            backoff.pipezlagfix$setNextActiveTick(side, 0);
        } else {
            int baseDelay = Config.baseBackoffTicks;
            int maxDelay = Config.maxBackoffTicks;
            int currentDelay = backoff.pipezlagfix$getBackoffDelay(side);

            int newDelay = (currentDelay == 0) ? baseDelay : currentDelay * 2;

            newDelay = Math.min(newDelay, maxDelay);

            backoff.pipezlagfix$setBackoffDelay(side, newDelay);
            backoff.pipezlagfix$setNextActiveTick(side, tileEntity.getLevel().getGameTime() + newDelay);
        }
    }
}
