package theblockbox.huntersdream.entity.renderer;

import java.util.Optional;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.entity.model.ModelLycanthropeAlex;
import theblockbox.huntersdream.entity.model.ModelLycanthropeAlexCrouched;
import theblockbox.huntersdream.entity.model.ModelLycanthropeSteve;
import theblockbox.huntersdream.entity.model.ModelLycanthropeSteveCrouched;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;

public abstract class RenderLycanthrope<T extends EntityLivingBase> extends RenderLivingBase<T> {
	protected ModelLycanthropeAlex modelAlex = new ModelLycanthropeAlex();
	protected ModelLycanthropeAlexCrouched modelAlexCrouched = new ModelLycanthropeAlexCrouched();
	protected ModelLycanthropeSteve modelSteve;
	protected ModelLycanthropeSteveCrouched modelSteveCrouched = new ModelLycanthropeSteveCrouched();

	public RenderLycanthrope(RenderManager manager) {
		super(manager, new ModelLycanthropeSteve(), 0.5F);
		// assign this so the model that the steve model is always the same
		this.modelSteve = (ModelLycanthropeSteve) this.mainModel;
	}

	@Override
	public ResourceLocation getEntityTexture(T entity) {
		Optional<ITransformation> transformation = TransformationHelper.getITransformation(entity);
		return Transformation.WEREWOLF.getTextures()[transformation.isPresent() ? transformation.get().getTextureIndex()
				: 0];
	}

	@Override
	public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
		// set correct model
		this.mainModel = WerewolfHelper.shouldUseSneakingModel(entity)
				? (RenderLycanthrope.isAlex(entity) ? this.modelAlexCrouched : this.modelSteveCrouched)
				: (RenderLycanthrope.isAlex(entity) ? this.modelAlex : this.modelSteve);
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	public static boolean isAlex(Entity entity) {
		if (entity instanceof AbstractClientPlayer) {
			return "slim".equals(((AbstractClientPlayer) entity).getSkinType());
		} else if (entity instanceof EntityWerewolf) {
			return ((EntityWerewolf) entity).usesAlexSkin();
		} else {
			return false;
		}
	}
}