package theblockbox.huntersdream.objects.armor;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class SilverArmorBase extends ArmorBase {

	public SilverArmorBase(String name, ArmorMaterial materialIn, int renderIndexIn,
			EntityEquipmentSlot equipmentSlotIn, float damageAgainstWerewolf, float protection) {
		super(name, materialIn, renderIndexIn, equipmentSlotIn);
	}

	@Override
	public boolean isRepairable() {
		return true;
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		int[] repairOredict = OreDictionary.getOreIDs(repair);
		int oredict = OreDictionary.getOreID("ingotSilver");
		for (int i : repairOredict)
			if (i == oredict)
				return true;
		return false;
	}
}
