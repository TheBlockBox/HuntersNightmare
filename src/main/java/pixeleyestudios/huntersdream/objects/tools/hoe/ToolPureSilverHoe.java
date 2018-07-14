package pixeleyestudios.huntersdream.objects.tools.hoe;

import net.minecraft.item.ItemHoe;
import pixeleyestudios.huntersdream.Main;
import pixeleyestudios.huntersdream.init.CreativeTabInit;
import pixeleyestudios.huntersdream.init.ItemInit;
import pixeleyestudios.huntersdream.util.interfaces.IEffectiveAgainstWerewolf;
import pixeleyestudios.huntersdream.util.interfaces.IHasModel;

public class ToolPureSilverHoe extends ItemHoe implements IHasModel, IEffectiveAgainstWerewolf {

	public ToolPureSilverHoe() {
		super(ItemInit.TOOL_PURE_SILVER);

		setUnlocalizedName("hoe_pure_silver");
		setRegistryName("hoe_pure_silver");
		setCreativeTab(CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS);

		ItemInit.ITEMS.add(this);
	}

	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
	}

}
