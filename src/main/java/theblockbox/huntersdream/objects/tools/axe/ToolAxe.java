package theblockbox.huntersdream.objects.tools.axe;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.init.CreativeTabInit;
import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.interfaces.functional.IHasModel;

public class ToolAxe extends ItemTool implements IHasModel {

	private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG,
			Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN, Blocks.LIT_PUMPKIN, Blocks.MELON_BLOCK, Blocks.LADDER,
			Blocks.WOODEN_BUTTON, Blocks.WOODEN_PRESSURE_PLATE);

	public ToolAxe(String name, ToolMaterial material) {
		super(material, EFFECTIVE_ON);
		setUnlocalizedName(Reference.MODID + "." + name);
		setRegistryName(name);
		setCreativeTab(CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS);
		this.setHarvestLevel(name, material.getHarvestLevel());

		ItemInit.ITEMS.add(this);
	}

	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
	}

	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state) {
		Material material = state.getMaterial();
		return material != Material.WOOD && material != Material.PLANTS && material != Material.VINE
				? super.getDestroySpeed(stack, state)
				: this.efficiency;
	}
}
