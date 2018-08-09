package theblockbox.huntersdream.objects.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.init.BlockInit;
import theblockbox.huntersdream.init.CreativeTabInit;
import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.util.interfaces.effective.ISilverEffectiveAgainstTransformation;
import theblockbox.huntersdream.util.interfaces.functional.IHasModel;

public class BlockBase extends Block implements IHasModel {

	public BlockBase(String name, Material materialIn) {
		this(name, materialIn, false);
	}

	public BlockBase(String name, Material materialIn, boolean silver) {
		super(materialIn);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC);
		BlockInit.BLOCKS.add(this);

		ItemBlock itemBlock;
		if (silver) {
			itemBlock = new ItemBlockSilver(this);
		} else {
			itemBlock = new ItemBlock(this);
		}

		ItemInit.ITEMS.add(itemBlock.setRegistryName(this.getRegistryName()));
	}

	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
	}

	private static class ItemBlockSilver extends ItemBlock implements ISilverEffectiveAgainstTransformation {
		public ItemBlockSilver(Block block) {
			super(block);
		}
	}
}
