package theblockbox.huntersdream.objects.armor;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.interfaces.effective.IArmorEffectiveAgainstTransformation;
import theblockbox.huntersdream.util.interfaces.effective.ISilverEffectiveAgainstTransformation;

public class SilverArmorBase extends ArmorBase
		implements IArmorEffectiveAgainstTransformation, ISilverEffectiveAgainstTransformation {
	/**
	 * The damage that the werewolf should get when attacking the entity wearing the
	 * armour
	 */
	private final float EFFECTIVENESS;
	private final float PROTECTION;

	public SilverArmorBase(String name, ArmorMaterial materialIn, int renderIndexIn,
			EntityEquipmentSlot equipmentSlotIn, float damageAgainstWerewolf, float protection) {
		super(name, materialIn, renderIndexIn, equipmentSlotIn);
		this.EFFECTIVENESS = damageAgainstWerewolf;
		this.PROTECTION = protection;
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

	@Override
	public float getArmorEffectiveness() {
		return this.EFFECTIVENESS;
	}

	@Override
	public float getProtection() {
		return PROTECTION;
	}

	@Override
	public Transformations[] transformations() {
		return ISilverEffectiveAgainstTransformation.super.transformations();
	}

	@Override
	public boolean effectiveAgainst(Transformations transformation) {
		return IArmorEffectiveAgainstTransformation.super.effectiveAgainst(transformation);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(getTooltipEffectiveness());
		tooltip.add(getTooltipArmorEffective());
	}
}
