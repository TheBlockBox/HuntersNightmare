package theblockbox.huntersdream.entity.renderer;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.entity.model.ModelLycanthropeBiped;
import theblockbox.huntersdream.entity.model.ModelLycanthropeQuadruped;
import theblockbox.huntersdream.init.TransformationInit;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;

public abstract class RenderLycantrophe<T extends EntityLivingBase> extends RenderLivingBase<T> {
	public static final ModelLycanthropeBiped MODEL_BIPED = new ModelLycanthropeBiped();
	public static final ModelLycanthropeQuadruped MODEL_QUADRUPED = new ModelLycanthropeQuadruped();

	public RenderLycantrophe(RenderManager manager) {
		super(manager, new ModelLycanthropeBiped(), 0.5F);
	}

	@Override
	public ResourceLocation getEntityTexture(T entity) {
		if (entity instanceof EntityWerewolf) {
			return TransformationInit.WEREWOLF.getTextures()[((EntityWerewolf) entity).getTextureIndex()];
		} else if (entity instanceof EntityPlayer) {
			return TransformationInit.WEREWOLF.getTextures()[TransformationHelper.getCap((EntityPlayer) entity)
					.getTextureIndex()];
		} else {
			return null;
		}
	}

	@Override
	public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
		this.mainModel = (entity.isSprinting() || entity.isSneaking()) ? MODEL_QUADRUPED : MODEL_BIPED;
		if (mainModel == MODEL_BIPED)
			if (!GeneralHelper.canEntityExpandHeight(entity, ModelLycanthropeBiped.HEIGHT))
				mainModel = MODEL_QUADRUPED;

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
}