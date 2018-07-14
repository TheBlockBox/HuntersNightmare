package pixeleyestudios.huntersdream.objects.tools.axe;

import net.minecraft.item.ItemAxe;
import pixeleyestudios.huntersdream.Main;
import pixeleyestudios.huntersdream.init.CreativeTabInit;
import pixeleyestudios.huntersdream.init.ItemInit;
import pixeleyestudios.huntersdream.util.interfaces.IEffectiveAgainstWerewolf;
import pixeleyestudios.huntersdream.util.interfaces.IHasModel;

public class ToolPureSilverAxe extends ItemAxe implements IHasModel, IEffectiveAgainstWerewolf {

	public ToolPureSilverAxe() {
		super(ItemInit.TOOL_PURE_SILVER);
		setUnlocalizedName("axe_pure_silver");
		setRegistryName("axe_pure_silver");
		setCreativeTab(CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS);

		ItemInit.ITEMS.add(this);
	}

	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
	}
}
