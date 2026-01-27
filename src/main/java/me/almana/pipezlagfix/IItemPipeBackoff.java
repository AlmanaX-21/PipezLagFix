package me.almana.pipezlagfix;

import net.minecraft.core.Direction;

public interface IItemPipeBackoff {

    // Getter for next active tick
    long pipezlagfix$getNextActiveTick(Direction side);

    // Setter for next active tick
    void pipezlagfix$setNextActiveTick(Direction side, long tick);

    // Getter for current backoff delay
    int pipezlagfix$getBackoffDelay(Direction side);

    // Setter for current backoff delay
    void pipezlagfix$setBackoffDelay(Direction side, int delay);
}
