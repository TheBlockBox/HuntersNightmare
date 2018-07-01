package huntersdream.init;

import huntersdream.Main;
import huntersdream.entity.EntityGoblinTD;
import huntersdream.entity.EntityVillagerWerewolf;
import huntersdream.entity.renderer.RenderGoblinTD;
import huntersdream.entity.renderer.RenderVillagerWerewolf;
import huntersdream.util.Reference;
import huntersdream.util.handlers.ConfigHandler;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityInit {
	public static void registerEntities() {
		registerEntity("goblintd", EntityGoblinTD.class, ConfigHandler.goblinID, 20, 29696, 255);
		registerEntity("villagerWerewolf", EntityVillagerWerewolf.class, ConfigHandler.villagerWerewolfID, 15, 41414,
				5252); // TODO: Change egg colour
	}

	public static void registerEntityRenders() {
		RenderingRegistry.registerEntityRenderingHandler(EntityGoblinTD.class, new IRenderFactory<EntityGoblinTD>() {
			@Override
			public Render<? super EntityGoblinTD> createRenderFor(RenderManager manager) {
				return new RenderGoblinTD(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityVillagerWerewolf.class,
				new IRenderFactory<EntityVillagerWerewolf>() {

					@Override
					public Render<? super EntityVillagerWerewolf> createRenderFor(RenderManager manager) {
						return new RenderVillagerWerewolf(manager);
					}
				});
	}

	/*
	 * How to make entities: - register it here - make a new renderer class - make
	 * new texture in textures/entity
	 */

	private static void registerEntity(String name, Class<? extends Entity> entity, int id, int trackingRange,
			int eggColor1, int eggColor2) {
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MODID + ":" + name), entity, name, id,
				Main.instance, trackingRange, 1, true, eggColor1, eggColor2);
	}
}
