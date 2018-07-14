package pixeleyestudios.huntersdream.objects.tools.shovel;

import net.minecraft.item.ItemSpade;
import pixeleyestudios.huntersdream.Main;
import pixeleyestudios.huntersdream.init.CreativeTabInit;
import pixeleyestudios.huntersdream.init.ItemInit;
import pixeleyestudios.huntersdream.util.interfaces.IEffectiveAgainstWerewolf;
import pixeleyestudios.huntersdream.util.interfaces.IHasModel;

public class ToolPureSilverShovel extends ItemSpade implements IHasModel, IEffectiveAgainstWerewolf {

	public ToolPureSilverShovel() {
		super(ItemInit.TOOL_PURE_SILVER);

		setUnlocalizedName("shovel_pure_silver");
		setRegistryName("shovel_pure_silver");
		setCreativeTab(CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS);

		ItemInit.ITEMS.add(this);
	}

	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
	}

}
