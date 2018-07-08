package huntersdream.entity.renderer;

import huntersdream.entity.EntityVillagerWerewolf;
import huntersdream.entity.model.ModelWolfman;
import huntersdream.util.Reference;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderVillagerWerewolf extends RenderLiving<EntityVillagerWerewolf> {
	public static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation(Reference.MODID,
			"textures/entity/villager_werewolf.png");
	public final ModelBase ENTITY_MODEL_WOLFMAN;

	public RenderVillagerWerewolf(RenderManager manager) {
		super(manager, new ModelVillager(0.1F), 0.5F);
		this.ENTITY_MODEL_WOLFMAN = new ModelWolfman();
	}

	@Override
	public ModelBase getMainModel() {
		return super.getMainModel();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityVillagerWerewolf entity) {
		if (entity.transformed()) {
			return ModelWolfman.TEXTURE_TRANSFORMED[entity.getTextureIndex()];
		} else {
			return TEXTURE_NORMAL;
		}
	}

	@Override
	protected void applyRotations(EntityVillagerWerewolf entityLiving, float p_77043_2_, float rotationYaw,
			float partialTicks) {
		super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
	}

	// TODO: Add werewolf render
	@Override
	public void doRender(EntityVillagerWerewolf entity, double x, double y, double z, float entityYaw,
			float partialTicks) {
		if (!entity.transformed()) {
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		} else {
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}
	}

	@Override
	public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks) {
		if (!entityIn.getDataManager().get(EntityVillagerWerewolf.TRANSFORMED)) {
			super.doRenderShadowAndFire(entityIn, x, y, z, yaw, partialTicks);
		} else {
			super.doRenderShadowAndFire(entityIn, x, y, z, yaw, partialTicks);
		}
	}
}