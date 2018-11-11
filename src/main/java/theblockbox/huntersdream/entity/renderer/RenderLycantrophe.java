package theblockbox.huntersdream.entity.renderer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.entity.model.ModelLycanthropeAlex;
import theblockbox.huntersdream.entity.model.ModelLycanthropeAlexCrouched;
import theblockbox.huntersdream.entity.model.ModelLycanthropeSteve;
import theblockbox.huntersdream.entity.model.ModelLycanthropeSteveCrouched;
import theblockbox.huntersdream.init.TransformationInit;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;

public abstract class RenderLycantrophe<T extends EntityLivingBase> extends RenderLivingBase<T> {
	protected ModelLycanthropeAlex modelAlex = new ModelLycanthropeAlex();
	protected ModelLycanthropeAlexCrouched modelAlexCrouched = new ModelLycanthropeAlexCrouched();
	protected ModelLycanthropeSteve modelSteve;
	protected ModelLycanthropeSteveCrouched modelSteveCrouched = new ModelLycanthropeSteveCrouched();

	public RenderLycantrophe(RenderManager manager) {
		super(manager, new ModelLycanthropeSteve(), 0.5F);
		// assign this so the model that the steve model is always the same
		this.modelSteve = (ModelLycanthropeSteve) this.mainModel;
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
		// set correct model
		this.mainModel = WerewolfHelper.shouldUseSneakingModel(entity)
				? (isAlex(entity) ? this.modelAlexCrouched : this.modelSteveCrouched)
				: (isAlex(entity) ? this.modelAlex : this.modelSteve);
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	public static boolean isAlex(Entity entity) {
		if (entity instanceof AbstractClientPlayer) {
			return ((AbstractClientPlayer) entity).getSkinType().equals("slim");
		} else if (entity instanceof EntityWerewolf) {
			return ((EntityWerewolf) entity).usesAlexSkin();
		} else {
			return false;
		}
	}
}