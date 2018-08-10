package theblockbox.huntersdream.util.interfaces.functional;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

@FunctionalInterface
public interface ITransformCreature {
	public EntityLivingBase transformationCreatureReturnTransformed(EntityCreature entity);

	default public void transformCreature(EntityCreature entity) {
		EntityLivingBase returned = this.transformationCreatureReturnTransformed(entity);
		if (returned != null) {
			World world = returned.world;
			returned.setPositionAndUpdate(entity.posX, entity.posY, entity.posZ);
			returned.setHealth(returned.getHealth() / (entity.getMaxHealth() / entity.getHealth()));
			world.removeEntity(entity);
			world.spawnEntity(returned);
		}
	}
}
