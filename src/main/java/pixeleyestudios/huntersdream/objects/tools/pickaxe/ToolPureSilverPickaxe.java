package pixeleyestudios.huntersdream.objects.tools.pickaxe;

import net.minecraft.item.ItemPickaxe;
import pixeleyestudios.huntersdream.Main;
import pixeleyestudios.huntersdream.init.CreativeTabInit;
import pixeleyestudios.huntersdream.init.ItemInit;
import pixeleyestudios.huntersdream.util.interfaces.IEffectiveAgainstWerewolf;
import pixeleyestudios.huntersdream.util.interfaces.IHasModel;

public class ToolPureSilverPickaxe extends ItemPickaxe implements IHasModel, IEffectiveAgainstWerewolf {

	public ToolPureSilverPickaxe() {
		super(ItemInit.TOOL_PURE_SILVER);
		setUnlocalizedName("pickaxe_pure_silver");
		setRegistryName("pickaxe_pure_silver");
		setCreativeTab(CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS);

		ItemInit.ITEMS.add(this);
	}

	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
	}

}
