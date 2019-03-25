package theblockbox.huntersdream.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.util.EnumHand;
import theblockbox.huntersdream.api.helpers.ChanceHelper;
import theblockbox.huntersdream.api.helpers.WerewolfHelper;

public class EntityAIWerewolfAttack extends EntityAIAttackMelee {

    public EntityAIWerewolfAttack(EntityCreature creature, double speedIn, boolean useLongMemory) {
        super(creature, speedIn, useLongMemory);
        WerewolfHelper.validateIsWerewolf(creature);
    }

    @Override
    protected void checkAndPerformAttack(EntityLivingBase enemy, double distToEnemySqr) {
        if (distToEnemySqr <= this.getAttackReachSqr(enemy) && this.attackTick <= 0) {
            this.attackTick = 20;
            this.attacker.swingArm(EnumHand.MAIN_HAND);
            this.attacker.attackEntityAsMob(enemy);
            // TODO: Needed?
            WerewolfHelper.setLastAttackBite(this.attacker, ChanceHelper.chanceOf(this.attacker, 25));
        }
    }
}
