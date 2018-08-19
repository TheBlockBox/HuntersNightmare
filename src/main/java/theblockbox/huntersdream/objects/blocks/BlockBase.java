package theblockbox.huntersdream.objects.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.init.BlockInit;
import theblockbox.huntersdream.init.CreativeTabInit;
import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.interfaces.effective.ISilverEffectiveAgainstTransformation;
import theblockbox.huntersdream.util.interfaces.functional.IHasModel;

public class BlockBase extends Block implements IHasModel {

	public BlockBase(String name, Material materialIn, float hardness) {
		this(name, materialIn, hardness, false);
	}

	public BlockBase(String name, Material materialIn, float hardness, boolean silver) {
		super(materialIn);
		setUnlocalizedName(Reference.MODID + "." + name);
		setRegistryName(name);
		setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC);
		BlockInit.BLOCKS.add(this);

		ItemBlock itemBlock;
		if (silver) {
			itemBlock = new ItemBlockSilver(this);
		} else {
			itemBlock = getItemBlock();
		}
		this.setHardness(hardness);

		ItemInit.ITEMS.add(itemBlock.setRegistryName(this.getRegistryName()));
	}

	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
	}

	protected ItemBlock getItemBlock() {
		return new ItemBlock(this);
	}

	private static class ItemBlockSilver extends ItemBlock implements ISilverEffectiveAgainstTransformation {
		public ItemBlockSilver(Block block) {
			super(block);
		}

		@Override
		public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
			super.addInformation(stack, worldIn, tooltip, flagIn);
			tooltip.add(getTooltipEffectiveness());
		}
	}
}
