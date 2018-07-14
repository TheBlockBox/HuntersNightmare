package pixeleyestudios.huntersdream.util.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
	public static void onEntityHurt(LivingHurtEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		Entity directAttacker = event.getSource().getImmediateSource();

		ITransformation transformation = TransformationHelper.getITransformation(entity);

		if (transformation == null)
			return;

		// 1 damage = 1/2 hearts
		if (transformation.getTransformation() == Transformations.WEREWOLF && transformation.transformed()) {

			if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
				EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();

				if (WerewolfHelper.effectiveAgainstWerewolf(attacker.getHeldItemMainhand())
						|| WerewolfHelper.effectiveAgainstWerewolf(attacker)
						|| WerewolfHelper.effectiveAgainstWerewolf(directAttacker)) {
					event.setAmount(event.getAmount() * 2); // multiply damage by 2
					return;
				}
			}

			event.setAmount(event.getAmount() / 20); // reduce damage by 20
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
