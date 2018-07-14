package pixeleyestudios.huntersdream.objects.tools.sword;

import net.minecraft.item.ItemSword;
import pixeleyestudios.huntersdream.Main;
import pixeleyestudios.huntersdream.init.CreativeTabInit;
import pixeleyestudios.huntersdream.init.ItemInit;
import pixeleyestudios.huntersdream.util.interfaces.IEffectiveAgainstWerewolf;
import pixeleyestudios.huntersdream.util.interfaces.IHasModel;

public class ToolPureSilverSword extends ItemSword implements IHasModel, IEffectiveAgainstWerewolf {

	public ToolPureSilverSword() {
		super(ItemInit.TOOL_PURE_SILVER);
		setUnlocalizedName("sword_pure_silver");
		setRegistryName("sword_pure_silver");
		setCreativeTab(CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS);

		ItemInit.ITEMS.add(this);
	}

	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
	}

}
