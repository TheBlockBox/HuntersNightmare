package pixeleyestudios.huntersdream.util.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pixeleyestudios.huntersdream.util.helpers.ChanceHelper;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper.Transformations;
import pixeleyestudios.huntersdream.util.helpers.WerewolfHelper;
import pixeleyestudios.huntersdream.util.interfaces.ITransformation;

@Mod.EventBusSubscriber
public class WerewolfEventHandler {

	@SubscribeEvent
	// use LivingDamage only for removing damage and LivingHurt for damage and
	// damaged resources
	// TODO: Fix some things here
	public static void onEntityHurt(LivingHurtEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		Entity directAttacker = event.getSource().getImmediateSource();
		EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
		ITransformation transformation = TransformationHelper.getITransformation(entity);
		// the attacker's transformation
		ITransformation transformationAtt = TransformationHelper.getITransformation(attacker);

		// when attacker is werewolf, multiply damage
		if (transformationAtt != null) {
			if (transformationAtt.transformed()
					&& (transformationAtt.getTransformation() == Transformations.WEREWOLF)) {
				event.setAmount(event.getAmount() * WerewolfHelper.getWerewolfStrengthMultiplier(attacker));
				if (!attacker.world.isRemote) {
					if (attacker instanceof EntityPlayer) {
						TransformationHelper.addXP((EntityPlayerMP) attacker, 5);
					}
				}
			}
		}

		// if the (attacked) entity is a werewolf
		if (transformation != null) {
			if ((transformation.transformed()) && transformation.getTransformation() == Transformations.WEREWOLF) {

				// and the immediate damage source is effective against the werewolf
				if (WerewolfHelper.effectiveAgainstWerewolf(directAttacker)) {
					event.setAmount(event.getAmount() * WerewolfHelper.getEffectivenessAgainstWerewolf(directAttacker));
				} else {
					event.setAmount(event.getAmount() * WerewolfHelper.getEffectivenessAgainstWerewolf(attacker));
				}
			}
		}
	}

	@SubscribeEvent
	/** When werewolf attacks entity */
	public static void onEntityAttacked(LivingAttackEvent event) {
		if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
			EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();

			if (WerewolfHelper.canInfect(attacker)) {
				if (ChanceHelper.chanceOf(WerewolfHelper.getInfectionPercentage(attacker))) {
					WerewolfHelper.infect(event.getEntityLiving());
				}
			}
		}
	}
}
