package me.almana.pipezlagfix;

import net.neoforged.neoforge.items.IItemHandler;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TrackingItemHandler implements IItemHandler {
    private final IItemHandler delegate;
    private final Runnable onSuccess;

    public TrackingItemHandler(IItemHandler delegate, Runnable onSuccess) {
        this.delegate = delegate;
        this.onSuccess = onSuccess;
    }

    @Override
    public int getSlots() {
        return delegate.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return delegate.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return delegate.insertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack result = delegate.extractItem(slot, amount, simulate);
        if (!simulate && !result.isEmpty()) {
            onSuccess.run();
        }
        return result;
    }

    @Override
    public int getSlotLimit(int slot) {
        return delegate.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return delegate.isItemValid(slot, stack);
    }
}
