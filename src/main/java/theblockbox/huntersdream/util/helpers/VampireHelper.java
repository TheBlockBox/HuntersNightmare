package theblockbox.huntersdream.util.helpers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.common.capabilities.Capability;
import theblockbox.huntersdream.event.TransformationXPEvent.TransformationXPSentReason;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.exceptions.WrongTransformationException;
import theblockbox.huntersdream.util.interfaces.transformation.IVampire;

public class VampireHelper {
	public static final Capability<IVampire> CAPABILITY_VAMPIRE = CapabilitiesInit.CAPABILITY_VAMPIRE;

	public static void drinkBlood(EntityLivingBase vampire, EntityLivingBase drinkFrom) {
		if (isVampire(vampire)) {
			drinkFrom.attackEntityFrom(new EntityDamageSource("vampireDrankBlood", vampire), 1F);

			if (drinkFrom.isEntityUndead()) {
				vampire.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 100, 2));
			}
			if (vampire instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) vampire;
				TransformationHelper.addXP(player, 2, TransformationXPSentReason.VAMPIRE_DRANK_BLOOD);
				getIVampire(player).incrementBlood();
			}
		} else {
			throw new WrongTransformationException("Only vampires can drink blood",
					TransformationHelper.getTransformation(vampire));
		}
	}

	public static IVampire getIVampire(EntityPlayer player) {
		return player.getCapability(CAPABILITY_VAMPIRE, null);
	}

	public static boolean isVampire(EntityLivingBase entity) {
		return TransformationHelper.getTransformation(entity) == Transformations.VAMPIRE;
	}
}
