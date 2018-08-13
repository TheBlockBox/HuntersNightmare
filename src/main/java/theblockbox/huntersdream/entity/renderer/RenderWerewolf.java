package theblockbox.huntersdream.entity.renderer;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.entity.model.ModelLycanthropeBiped;
import theblockbox.huntersdream.util.enums.Transformations;

public class RenderWerewolf extends RenderLiving<EntityWerewolf> {

	public RenderWerewolf(RenderManager manager) {
		super(manager, new ModelLycanthropeBiped(), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityWerewolf entity) {
		return Transformations.WEREWOLF.getTextures()[entity.getTextureIndex()];
	}
}