package theblockbox.huntersdream.objects.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import theblockbox.huntersdream.util.interfaces.effective.ISilverEffectiveAgainstTransformation;

public class BlockPureSilver extends BlockBase implements ISilverEffectiveAgainstTransformation {

	public BlockPureSilver(String name) {
		super(name, Material.IRON);
	}

	@Override
	public ItemBlock getItemBlock() {
		return new ItemBlockPureSilver(this);
	}

	public static class ItemBlockPureSilver extends ItemBlock implements ISilverEffectiveAgainstTransformation {

		public ItemBlockPureSilver(Block block) {
			super(block);
		}

	}
}
