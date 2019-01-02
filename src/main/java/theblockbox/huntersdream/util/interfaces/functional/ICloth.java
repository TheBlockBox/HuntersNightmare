package theblockbox.huntersdream.util.interfaces.functional;

import net.minecraft.item.ItemStack;

/** An interface all clothes implement. Currently unused. */
@FunctionalInterface
public interface ICloth {
	/**
	 * Returns true if the given ItemStack can be put in the slot in the clothing
	 * tab with the given ClothType (the ItemStack's item is always the one this
	 * method is in)
	 */
	public boolean isOfClothType(ItemStack stack, ICloth.ClothType type);

	public enum ClothType {
		HAT, SHIRT, TROUSERS, SHOE, MASK, COAT, GLOVE, CLOAK, ACCESSIORE, BACKPACK
	}
}
