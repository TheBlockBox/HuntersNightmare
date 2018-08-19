package theblockbox.huntersdream.objects.tools.shovel;

import net.minecraft.item.ItemSpade;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.init.CreativeTabInit;
import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.interfaces.functional.IHasModel;

public class ToolShovel extends ItemSpade implements IHasModel {

	public ToolShovel(String name, ToolMaterial material) {
		super(material);
		setUnlocalizedName(Reference.MODID + "." + name);
		setRegistryName(name);
		setCreativeTab(CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS);

		ItemInit.ITEMS.add(this);
	}

	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
	}

}
