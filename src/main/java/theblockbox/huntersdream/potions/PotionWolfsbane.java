package theblockbox.huntersdream.potions;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.helpers.TransformationHelper;

public class PotionWolfsbane extends PotionBase {
	public static final HashMap<EntityLivingBase, Integer> APPLIED_AT_TIME = new HashMap<>();

	public PotionWolfsbane() {
		super(false, 14811307, 0, "wolfsbane");
	}

	@Override
	public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn,
			AbstractAttributeMap attributeMapIn, int amplifier) {
		// not actually applying attribute modifiers
		entityLivingBaseIn.addPotionEffect(new PotionEffect(
				(TransformationHelper.getTransformation(entityLivingBaseIn) == Transformations.WEREWOLF)
						? MobEffects.HUNGER
						: MobEffects.POISON,
				100, 2, false, false));
		super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return Lists.newArrayList(new ItemStack(Items.MILK_BUCKET));
	}
}
