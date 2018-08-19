package theblockbox.huntersdream.objects.blocks;

import net.minecraft.block.BlockCrops;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.init.BlockInit;
import theblockbox.huntersdream.init.CreativeTabInit;
import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.interfaces.functional.IHasModel;

public class BlockCropBase extends BlockCrops implements IHasModel {
	public final Item CROP;

	public BlockCropBase(String name, Item crop) {
		setUnlocalizedName(Reference.MODID + "." + name);
		setRegistryName(name);
		setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC);
		this.CROP = crop;
		BlockInit.BLOCKS.add(this);
		ItemInit.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
	}

	@Override
	protected Item getSeed() {
		return this.CROP;
	}

	@Override
	protected Item getCrop() {
		return this.CROP;
	}

	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
	}
}
