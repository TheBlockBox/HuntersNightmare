package pixeleyestudios.huntersdream.util.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pixeleyestudios.huntersdream.entity.EntityWerewolf;
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
		EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
		ITransformation transformation = TransformationHelper.getITransformation(entity);
		// the attacker's transformation
		ITransformation transformationAtt = TransformationHelper.getITransformation(attacker);

		if (transformationAtt != null) {
			if ((transformationAtt.getTransformation() == Transformations.WEREWOLF)
					&& transformationAtt.transformed()) {
				event.setAmount(event.getAmount() * 8);
				if (!attacker.world.isRemote) {
					if (attacker instanceof EntityPlayer) {
						TransformationHelper.addXP((EntityPlayerMP) attacker, 5);
					}

					if (attacker instanceof EntityWerewolf) {
						EntityWerewolf werewolf = (EntityWerewolf) attacker;
						if (werewolf.getEntityName().startsWith("player")) {
							// (EntityPlayer) world
							// .getPlayerEntityByName(entityName.substring(6, entityName.length()))
							TransformationHelper.addXP((EntityPlayerMP) WerewolfHelper.getPlayer(werewolf), 5);
						}
					}
				}
			}
		}

		// 1 damage = 1/2 hearts
		// deal more or less damage
		if (transformation != null) {
			if ((transformation.getTransformation() == Transformations.WEREWOLF) && transformation.transformed()) {

				if (event.getSource().getTrueSource() instanceof EntityLivingBase) {

					if (WerewolfHelper.effectiveAgainstWerewolf(attacker.getHeldItemMainhand())
							|| WerewolfHelper.effectiveAgainstWerewolf(attacker)
							|| WerewolfHelper.effectiveAgainstWerewolf(directAttacker)
							|| WerewolfHelper.effectiveAgainstWerewolf(event.getSource().getImmediateSource())) {
						event.setAmount(event.getAmount() * 2); // multiply damage by 2
						return;
					}
				}

				event.setAmount(event.getAmount() / 20); // reduce damage by 20
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
