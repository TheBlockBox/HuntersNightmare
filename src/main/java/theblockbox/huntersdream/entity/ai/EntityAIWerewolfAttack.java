package theblockbox.huntersdream.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.util.EnumHand;
import theblockbox.huntersdream.util.helpers.ChanceHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;

public class EntityAIWerewolfAttack extends EntityAIAttackMelee {

	public EntityAIWerewolfAttack(EntityCreature creature, double speedIn, boolean useLongMemory) {
		super(creature, speedIn, useLongMemory);
		WerewolfHelper.validateIsWerewolf(creature);
	}

	@Override
	protected void checkAndPerformAttack(EntityLivingBase enemy, double distToEnemySqr) {
		if (distToEnemySqr <= this.getAttackReachSqr(enemy) && this.attackTick <= 0) {
			this.attackTick = 20;
			// TODO: Was it 15 or 25 percent?
			if (ChanceHelper.chanceOf(this.attacker, 15)) {
				// TODO: Add bite animation here
				WerewolfHelper.setLastAttackBite(this.attacker, true);
			} else {
				this.attacker.swingArm(EnumHand.MAIN_HAND);
				WerewolfHelper.setLastAttackBite(this.attacker, false);
			}
			this.attacker.attackEntityAsMob(enemy);
		}
	}
}
