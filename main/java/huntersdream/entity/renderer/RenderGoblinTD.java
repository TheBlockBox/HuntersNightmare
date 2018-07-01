package huntersdream.entity.renderer;

import huntersdream.entity.EntityGoblinTD;
import huntersdream.entity.model.ModelGoblinTD;
import huntersdream.util.Reference;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderGoblinTD extends RenderLiving<EntityGoblinTD> {
	public static final ResourceLocation[] TEXTURES = new ResourceLocation[EntityGoblinTD.TEXTURES];

	static {
		for (int i = 0; i < EntityGoblinTD.TEXTURES; i++) {
			TEXTURES[i] = new ResourceLocation(Reference.MODID, "textures/entity/goblin" + i + ".png");
		}
	}

	public RenderGoblinTD(RenderManager manager) {
		super(manager, new ModelGoblinTD(), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityGoblinTD entity) {
		return TEXTURES[entity.getTextureIndex()];
	}

	@Override
	protected void applyRotations(EntityGoblinTD entityLiving, float p_77043_2_, float rotationYaw,
			float partialTicks) {
		super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
	}
}