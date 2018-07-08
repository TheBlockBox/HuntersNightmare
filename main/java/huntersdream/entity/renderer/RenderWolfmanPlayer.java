package huntersdream.entity.renderer;

import huntersdream.entity.model.ModelWolfman;
import huntersdream.util.helpers.TransformationHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class RenderWolfmanPlayer extends RenderLivingBase<EntityPlayer> {

	public RenderWolfmanPlayer(RenderManager manager) {
		super(manager, new ModelWolfman(), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityPlayer entity) {
		return ModelWolfman.TEXTURE_TRANSFORMED[TransformationHelper.getCap(entity).getTextureIndex()];
	}

	@Override
	protected void applyRotations(EntityPlayer entityLiving, float p_77043_2_, float rotationYaw, float partialTicks) {
		super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
	}
}
