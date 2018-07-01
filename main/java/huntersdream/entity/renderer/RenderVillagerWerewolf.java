package huntersdream.entity.renderer;

import huntersdream.entity.EntityVillagerWerewolf;
import huntersdream.entity.model.ModelWolfman;
import huntersdream.util.Reference;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderVillagerWerewolf extends RenderLiving<EntityVillagerWerewolf> {
	public static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation(Reference.MODID,
			"textures/entity/villager_werewolf.png");
	public static final ResourceLocation TEXTURE_TRANSFORMED[] = new ResourceLocation[3];
	public final ModelBase ENTITY_MODEL_VILLAGER;
	public final ModelBase ENTITY_MODEL_WOLFMAN;
	private ModelBase entityModelCurrent;

	static {
		TEXTURE_TRANSFORMED[0] = new ResourceLocation(Reference.MODID, "textures/entity/werewolf_beta_black.png");
		TEXTURE_TRANSFORMED[1] = new ResourceLocation(Reference.MODID, "textures/entity/werewolf_beta_brown.png");
		TEXTURE_TRANSFORMED[2] = new ResourceLocation(Reference.MODID, "textures/entity/werewolf_beta_white.png");
	}

	public RenderVillagerWerewolf(RenderManager manager) {
		super(manager, new ModelVillager(0.000001F), 0.5F);
		this.ENTITY_MODEL_VILLAGER = super.getMainModel();
		this.ENTITY_MODEL_WOLFMAN = new ModelWolfman();
	}

	@Override
	public ModelBase getMainModel() {
		return entityModelCurrent;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityVillagerWerewolf entity) {
		if (entity.getEntityData().getBoolean("isTransformed")) {
			entityModelCurrent = ENTITY_MODEL_WOLFMAN;
			System.out.println("is transformed");
			return TEXTURE_TRANSFORMED[entity.TEXTURE_INDEX];
		} else {
			entityModelCurrent = ENTITY_MODEL_VILLAGER;
			return TEXTURE_NORMAL;
		}
	}

	@Override
	protected void applyRotations(EntityVillagerWerewolf entityLiving, float p_77043_2_, float rotationYaw,
			float partialTicks) {
		super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
	}
}