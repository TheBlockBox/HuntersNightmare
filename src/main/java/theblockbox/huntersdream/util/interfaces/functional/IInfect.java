package theblockbox.huntersdream.util.interfaces.functional;

import net.minecraft.entity.EntityLivingBase;

@FunctionalInterface
public interface IInfect {
	public void infectEntity(EntityLivingBase entity);
}
