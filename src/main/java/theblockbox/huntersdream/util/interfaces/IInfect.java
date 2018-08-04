package theblockbox.huntersdream.util.interfaces;

import net.minecraft.entity.EntityLivingBase;

@FunctionalInterface
public interface IInfect {
	public void infectEntity(EntityLivingBase entity);
}
