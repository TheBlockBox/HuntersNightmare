package theblockbox.huntersdream.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.init.BlockInit;
import theblockbox.huntersdream.init.CreativeTabInit;
import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.interfaces.functional.IHasModel;

public class BlockBase extends Block implements IHasModel {

	public BlockBase(String name, Material materialIn, float hardness) {
		super(materialIn);
		setTranslationKey(Reference.MODID + "." + name);
		setRegistryName(name);
		setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC);
		BlockInit.BLOCKS.add(this);
		this.setHardness(hardness);

		ItemInit.ITEMS.add(getItemBlock().setRegistryName(this.getRegistryName()));
	}

	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
	}

	protected ItemBlock getItemBlock() {
		return new ItemBlock(this);
	}
}
