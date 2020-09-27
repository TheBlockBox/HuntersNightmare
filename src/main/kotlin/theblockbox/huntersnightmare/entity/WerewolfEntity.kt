package theblockbox.huntersnightmare.entity

import net.minecraft.entity.CreatureEntity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.monster.MonsterEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.pathfinding.GroundPathNavigator
import net.minecraft.world.World
import theblockbox.huntersnightmare.api.init.TransformationInit
import theblockbox.huntersnightmare.api.transformation.TransformationHelper.getTransformation

// TODO: Should we make this more similar to normal wolves? (like the water stuff)
// TODO: Add capability, animations, translation, AI and other general functionality
// TODO: Should this attack normal wolves?
class WerewolfEntity(entityType: EntityType<WerewolfEntity>, world: World) : MonsterEntity(entityType, world) {
    override fun registerGoals() {
        super.registerGoals()
        // TODO: Break doors only in hard difficulty?
        goalSelector.addGoal(2, MeleeAttackGoal(this, 1.0, false))
        goalSelector.addGoal(5, MoveThroughVillageGoal(this, 1.0, true, 4) { true })
        goalSelector.addGoal(6, WaterAvoidingRandomWalkingGoal(this, 1.0))
        goalSelector.addGoal(7, LookAtGoal(this, PlayerEntity::class.java, 8.0f))
        goalSelector.addGoal(7, LookRandomlyGoal(this))
        (getNavigator() as GroundPathNavigator).setBreakDoors(true)
        goalSelector.addGoal(1, BreakDoorGoal(this) { true })

        targetSelector.addGoal(1, HurtByTargetGoal(this))
        targetSelector.addGoal(2, NearestAttackableTargetGoal(this, CreatureEntity::class.java, 10, true, false) {
            it.getTransformation() != TransformationInit.werewolf.get()
        })
    }

    override fun registerAttributes() {
        super.registerAttributes()
        getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).baseValue = 0.3
        getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).baseValue = 8.0
        getAttribute(SharedMonsterAttributes.MAX_HEALTH).baseValue = 40.0
        getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).baseValue = 0.5
    }
}