package theblockbox.huntersdream.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemBlockWithMaxStackSize extends ItemBlock {

	public ItemBlockWithMaxStackSize(Block block, int maxStackSize) {
		super(block);
		this.setMaxStackSize(maxStackSize);
	}
}
