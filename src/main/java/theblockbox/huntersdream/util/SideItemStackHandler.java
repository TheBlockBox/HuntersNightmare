package theblockbox.huntersdream.util;

import java.util.function.IntPredicate;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class SideItemStackHandler implements IItemHandler {
	private final IItemHandler delegate;
	private final IntPredicate shouldInsertIntoSlot;
	private final IntPredicate shouldExtractFromSlot;

	public SideItemStackHandler(IItemHandler delegate, IntPredicate shouldInsertIntoSlot,
			IntPredicate shouldExtractFromSlot) {
		this.delegate = delegate;
		this.shouldInsertIntoSlot = shouldInsertIntoSlot;
		this.shouldExtractFromSlot = shouldExtractFromSlot;
	}

	@Override
	public int getSlots() {
		return this.delegate.getSlots();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return this.delegate.getStackInSlot(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (shouldInsertIntoSlot.test(slot))
			return this.delegate.insertItem(slot, stack, simulate);
		else
			return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (shouldExtractFromSlot.test(slot))
			return this.delegate.extractItem(slot, amount, simulate);
		else
			return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) {
		return this.delegate.getSlotLimit(slot);
	}
}