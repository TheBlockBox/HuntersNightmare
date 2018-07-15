package pixeleyestudios.huntersdream.entity.renderer;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import pixeleyestudios.huntersdream.entity.EntityWerewolf;
import pixeleyestudios.huntersdream.entity.model.ModelWolfman;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper.Transformations;

public class RenderWerewolf extends RenderLiving<EntityWerewolf> {

	public RenderWerewolf(RenderManager manager) {
		super(manager, new ModelWolfman(), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityWerewolf entity) {
		return Transformations.WEREWOLF.TEXTURES[entity.getTextureIndex()];
	}
}