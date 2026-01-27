package me.almana.pipezlagfix.mixin;

import de.maxhenkel.pipez.blocks.tileentity.PipeLogicTileEntity;
import me.almana.pipezlagfix.IItemPipeBackoff;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PipeLogicTileEntity.class)
public class MixinPipeLogicTileEntity implements IItemPipeBackoff {

    @Unique
    private final long[] pipezlagfix$nextActiveTick = new long[6];

    @Unique
    private final int[] pipezlagfix$backoffDelay = new int[6];

    @Override
    public long pipezlagfix$getNextActiveTick(Direction side) {
        return pipezlagfix$nextActiveTick[side.ordinal()];
    }

    @Override
    public void pipezlagfix$setNextActiveTick(Direction side, long tick) {
        pipezlagfix$nextActiveTick[side.ordinal()] = tick;
    }

    @Override
    public int pipezlagfix$getBackoffDelay(Direction side) {
        return pipezlagfix$backoffDelay[side.ordinal()];
    }

    @Override
    public void pipezlagfix$setBackoffDelay(Direction side, int delay) {
        pipezlagfix$backoffDelay[side.ordinal()] = delay;
    }
}
